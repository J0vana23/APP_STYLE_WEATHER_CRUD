package com.example.styleweather.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.styleweather.AuthState
import com.example.styleweather.AuthViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun EditarPerfilPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val user = authViewModel.getCurrentUser()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.observeAsState()
    val db = FirebaseFirestore.getInstance()

    // 🔹 Carrega nome e email do Firestore
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            try {
                val doc = db.collection("users").document(uid).get().await()
                name = doc.getString("name") ?: ""
                email = doc.getString("email") ?: user.email.orEmpty()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // 🔹 BOTÃO VOLTAR – AGORA NO TOPO
            TextButton(onClick = { navController.popBackStack() }) {
                Text("< VOLTAR", color = Color(0xFF5F2B87))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 🔹 CENTRALIZA APENAS O CARD
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .border(2.dp, Color(0xFF5F2B87), RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "PERFIL",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF5F2B87)
                        )

                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .fillMaxWidth(0.5f)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF5F2B87), Color(0xFFA563FF))
                                    ),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🔹 Nome
                        Text(
                            text = "Nome:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nome completo") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = TextStyle(color = Color.Black)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔹 Email (somente leitura)
                        Text(
                            text = "Email:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Email cadastrado") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF3E8FF), RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = TextStyle(color = Color.DarkGray),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFF5F2B87),
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔹 Nova senha
                        Text(
                            text = "Nova senha:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Senha") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = TextStyle(color = Color.Black),
                            visualTransformation = PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.height(26.dp))

                        // 🔘 Botão Salvar
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF5F2B87), Color(0xFFA563FF))
                                    )
                                )
                                .clickable {
                                    authViewModel.updateProfile(name, password)

                                    // salva no firestore também
                                    user?.uid?.let { uid ->
                                        db.collection("users").document(uid)
                                            .update("name", name)
                                    }
                                }
                                .padding(vertical = 12.dp)
                                .fillMaxWidth(0.9f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Salvar alterações",
                                color = Color.White,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 🔹 Estados
                        when (authState) {
                            is AuthState.Loading -> CircularProgressIndicator(color = Color(0xFF5F2B87))
                            is AuthState.Error -> Text(
                                text = (authState as AuthState.Error).message,
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                            is AuthState.ProfileUpdated -> Text(
                                text = "Perfil atualizado com sucesso!",
                                color = Color(0xFF5F2B87),
                                fontSize = 14.sp
                            )
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
