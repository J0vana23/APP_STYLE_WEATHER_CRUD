package com.example.styleweather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.styleweather.pages.model.Roupa
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AuthViewModel : ViewModel() {

    private val TAG = "AuthViewModel"

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _roupas = MutableLiveData<List<Roupa>>(emptyList())
    val roupas: LiveData<List<Roupa>> = _roupas

    private var roupasListener: ListenerRegistration? = null

    // -------------------------------------------------------------------------
    // LOGIN / SIGNUP
    // -------------------------------------------------------------------------

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = AuthState.Authenticated
                carregarRoupas()
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Erro ao fazer login.")
            }
    }

    fun signup(name: String, email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = auth.currentUser ?: return@addOnSuccessListener
                val userData = mapOf("name" to name, "email" to email, "uid" to user.uid)

                db.collection("users")
                    .document(user.uid)
                    .set(userData)
                    .addOnSuccessListener {
                        auth.signOut()
                        _authState.value = AuthState.SignedUp
                    }
                    .addOnFailureListener { e ->
                        _authState.value = AuthState.Error(e.message ?: "Erro ao salvar usuário.")
                    }
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Erro ao criar conta.")
            }
    }

    fun signOut() {
        roupasListener?.remove()
        roupasListener = null
        _roupas.value = emptyList()
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun getCurrentUser() = auth.currentUser

    // -------------------------------------------------------------------------
    // ATUALIZAR PERFIL  (RESTaurado conforme você pediu)
    // -------------------------------------------------------------------------

    fun updateProfile(newName: String, newPassword: String?) {
        val user = auth.currentUser
        if (user == null) {
            _authState.value = AuthState.Error("Usuário não logado.")
            return
        }

        val updates = mutableMapOf<String, Any>(
            "name" to newName,
            "email" to (user.email ?: "")
        )

        // Atualiza no Firestore
        db.collection("users")
            .document(user.uid)
            .update(updates)
            .addOnSuccessListener {

                // Atualiza senha (se foi enviada)
                if (!newPassword.isNullOrEmpty()) {
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            _authState.value = AuthState.ProfileUpdated
                        }
                        .addOnFailureListener {
                            _authState.value =
                                AuthState.Error("Nome atualizado, mas erro ao atualizar senha.")
                        }
                } else {
                    _authState.value = AuthState.ProfileUpdated
                }
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("Erro ao atualizar perfil.")
            }
    }

    // -------------------------------------------------------------------------
    // ROUPAS  (CRUD completo)
    // -------------------------------------------------------------------------

    fun carregarRoupas(onError: ((String) -> Unit)? = null) {
        val user = auth.currentUser ?: run {
            _roupas.value = emptyList()
            return
        }

        roupasListener?.remove()

        roupasListener = db.collection("users")
            .document(user.uid)
            .collection("roupas")
            .orderBy("criadaEm")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError?.invoke(error.message ?: "Erro ao carregar roupas")
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    _roupas.value = emptyList()
                    return@addSnapshotListener
                }

                val lista = snapshot.documents.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        val nome = doc.getString("nome") ?: ""
                        val estacao = doc.getString("estacao") ?: ""
                        val criadaAny = doc.get("criadaEm")

                        val criadaEm = when (criadaAny) {
                            is Long -> criadaAny
                            is Double -> criadaAny.toLong()
                            is Number -> criadaAny.toLong()
                            else -> null
                        }

                        Roupa(id, nome, estacao, criadaEm)
                    } catch (e: Exception) {
                        Log.e(TAG, "Erro ao converter roupa: ${e.message}")
                        null
                    }
                }

                _roupas.value = lista
            }
    }

    fun adicionarRoupa(
        nome: String,
        estacao: String,
        onComplete: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val user = auth.currentUser ?: return onError("Usuário não logado")

        val roupa = mapOf(
            "nome" to nome,
            "estacao" to estacao,
            "criadaEm" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(user.uid)
            .collection("roupas")
            .add(roupa)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { e -> onError(e.message ?: "Erro ao adicionar roupa") }
    }

    fun editarRoupa(
        idRoupa: String,
        nome: String,
        estacao: String,
        onComplete: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val user = auth.currentUser ?: return onError("Usuário não logado")

        db.collection("users")
            .document(user.uid)
            .collection("roupas")
            .document(idRoupa)
            .update("nome", nome, "estacao", estacao)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { e -> onError(e.message ?: "Erro ao editar roupa") }
    }

    fun excluirRoupa(
        idRoupa: String,
        onComplete: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val user = auth.currentUser ?: return onError("Usuário não logado")

        db.collection("users")
            .document(user.uid)
            .collection("roupas")
            .document(idRoupa)
            .delete()
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { e -> onError(e.message ?: "Erro ao excluir roupa") }
    }

    override fun onCleared() {
        roupasListener?.remove()
        super.onCleared()
    }
}

// -------------------------------------------------------------------------
//  ESTADOS DO LOGIN / PERFIL / ROUPAS
// -------------------------------------------------------------------------

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    object SignedUp : AuthState()
    object ProfileUpdated : AuthState()   // <-- RESTAURADO
    data class Error(val message: String) : AuthState()
}
