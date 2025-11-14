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
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.navigation.NavController
import com.example.styleweather.AuthState
import com.example.styleweather.AuthViewModel
import com.example.styleweather.R
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.observeAsState(AuthState.Unauthenticated)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LOGIN",
                    fontSize = 36.sp,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF5F2B87)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .fillMaxWidth(0.3f)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF5F2B87), Color(0xFFA563FF))
                            )
                        )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = R.drawable.usuario),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .clip(CircleShape)
                        .height(150.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Campos Email e Senha
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Email:", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Digite seu email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle(color = Color.Black)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Senha:", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Digite sua senha") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle(color = Color.Black),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Botão Entrar com ação Firebase
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF5F2B87), Color(0xFFA563FF))
                            )
                        )
                        .clickable {
                            authViewModel.login(email, password)
                        }
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(0.9f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Entrar",
                        color = Color.White,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Exibição do estado (erro, carregando, sucesso)
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

                    AuthState.Authenticated -> {
                        LaunchedEffect(Unit) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    else -> {}
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Link para Signup
                TextButton(onClick = { navController.navigate("signup") }) {
                    Text(
                        text = "Não possui cadastro? Cadastre-se!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
