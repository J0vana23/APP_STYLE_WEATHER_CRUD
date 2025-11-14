package com.example.styleweather.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.styleweather.R
import com.example.styleweather.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.text.AnnotatedString

import androidx.compose.material3.OutlinedTextFieldDefaults


@Composable
fun HomePage(
    navController: NavController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    val user = authViewModel.getCurrentUser()
    val db = FirebaseFirestore.getInstance()

    // Estado do popup
    var showDialog by remember { mutableStateOf(false) }
    var nomePeca by remember { mutableStateOf("") }
    var estacaoPeca by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // Logo Responsiva
            Image(
                painter = painterResource(id = R.drawable.logo_responsiva),
                contentDescription = "Logo",
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CARD ROXO (Adicionar roupa)
            // CARD ROXO (Adicionar roupa) COM GRADIENTE
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(150.dp)
                    .clickable { showDialog = true },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF5F2B87),   // Roxo escuro
                                    Color(0xFFA563FF)    // Roxo claro
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.manequim),
                            contentDescription = "Manequim",
                            modifier = Modifier.size(80.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "+ adicionar nova peça de roupa",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            val clipboard = LocalClipboardManager.current

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Agora também disponível na versão web!",
                fontSize = 16.sp,
                color = Color(0xFF5F2B87)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "styleweather.vercel.app (toque para copiar)",
                fontSize = 14.sp,
                color = Color(0xFFA563FF),
                modifier = Modifier.clickable {
                    clipboard.setText(AnnotatedString("https://styleweather.vercel.app/"))
                }
            )



            Spacer(modifier = Modifier.height(40.dp))
        }

        // Menu inferior
        BottomNavigationBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // 🔹 Dialog de adicionar roupa
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    user?.uid?.let { uid ->

                        val roupa = hashMapOf(
                            "nome" to nomePeca,
                            "estacao" to estacaoPeca,
                            "criadaEm" to System.currentTimeMillis()
                        )

                        db.collection("users")
                            .document(uid)
                            .collection("roupas")
                            .add(roupa)

                        // limpa
                        nomePeca = ""
                        estacaoPeca = ""
                        showDialog = false
                    }
                }) {
                    Text("Salvar", color = Color(0xFF5F2B87))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            title = {
                Text("Adicionar nova peça", color = Color(0xFF5F2B87))
            },
            text = {
                Column {

                    // Campo "Nome da peça"
                    OutlinedTextField(
                        value = nomePeca,
                        onValueChange = { nomePeca = it },
                        label = { Text("Nome da peça") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color.Gray,          // borda opcional
                            unfocusedBorderColor = Color.Gray,        // borda opcional
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo "Estação"
                    OutlinedTextField(
                        value = estacaoPeca,
                        onValueChange = { estacaoPeca = it },
                        label = { Text("Estação") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Color.Black
                        )
                    )

                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF5F2B87), Color(0xFFA563FF))
                ),
            )
            .padding(horizontal = 40.dp),
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController.navigate("editarPerfil") }
            ) {
                Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(36.dp))
                Text("Perfil", color = Color.White, fontSize = 14.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController.navigate("home") }
            ) {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White, modifier = Modifier.size(36.dp))
                Text("Home", color = Color.White, fontSize = 14.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController.navigate("configuracoes") }
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = Color.White, modifier = Modifier.size(36.dp))
                Text("Config.", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
