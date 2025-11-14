package com.example.styleweather.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.styleweather.AuthState
import com.example.styleweather.AuthViewModel
import com.example.styleweather.R
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SignupPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.observeAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(16.dp)
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
                    text = "CADASTRO",
                    fontSize = 36.sp,
                    fontFamily = FontFamily.Default,
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

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.manequim),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .clip(CircleShape)
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Nome:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth() // ocupa a mesma largura da caixa
                        .padding(bottom = 3.dp), // pequeno espaço antes do campo
                    textAlign = TextAlign.Start // garante alinhamento à esquerda
                )
                // Campos
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(color = Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Email:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth() // ocupa a mesma largura da caixa
                        .padding(bottom = 3.dp), // pequeno espaço antes do campo
                    textAlign = TextAlign.Start // garante alinhamento à esquerda
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(color = Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Senha:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth() // ocupa a mesma largura da caixa
                        .padding(bottom = 3.dp), // pequeno espaço antes do campo
                    textAlign = TextAlign.Start // garante alinhamento à esquerda
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

                Spacer(modifier = Modifier.height(24.dp))

                // Botão Criar Conta
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF5F2B87), Color(0xFFA563FF))
                            )
                        )
                        .clickable {
                            authViewModel.signup(name, email, password)
                        }
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(0.9f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Criar conta",
                        color = Color.White,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { navController.navigate("login") }) {
                    Text(
                        text = "Já possui cadastro? Entrar",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                // 🔁 Feedback visual
                when (authState) {
                    is AuthState.Loading -> {
                        CircularProgressIndicator(color = Color(0xFF5F2B87))
                    }

                    is AuthState.Error -> {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }

                    is AuthState.SignedUp -> {
                        // ✅ Após cadastro, redireciona para LOGIN
                        LaunchedEffect(Unit) {
                            navController.navigate("login") {
                                popUpTo("signup") { inclusive = true }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}
