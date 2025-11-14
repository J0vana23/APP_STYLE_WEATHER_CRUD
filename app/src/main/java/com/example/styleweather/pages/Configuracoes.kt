package com.example.styleweather.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.styleweather.AuthViewModel
import com.example.styleweather.pages.model.Roupa
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun ConfiguracoesPage(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val roupas by authViewModel.roupas.observeAsState(emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Carrega roupas ao abrir a tela
    LaunchedEffect(Unit) {
        authViewModel.carregarRoupas { msg ->
            scope.launch { snackbarHostState.showSnackbar("Erro ao carregar roupas: $msg") }
        }
    }

    val purpleGradient = Brush.horizontalGradient(listOf(Color(0xFF5F2B87), Color(0xFFA563FF)))

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Scaffold(
            containerColor = Color.White,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable { navController.popBackStack() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color(0xFF5F2B87))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("VOLTAR", color = Color(0xFF5F2B87), fontSize = 18.sp)
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CONFIGURAÇÕES",
                            fontSize = 32.sp,
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
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Roupas cadastradas:", fontSize = 18.sp, color = Color(0xFF160421))
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Lista de roupas
                items(roupas) { roupa ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(purpleGradient, shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Column {
                                Text("Peça: ${roupa.nome}", color = Color.White, fontSize = 18.sp)
                                Text("Estação: ${roupa.estacao}", color = Color.White, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = {
                                        try {
                                            navController.navigate("roupa/${roupa.id}")
                                        } catch (e: Exception) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Tela de edição não encontrada.")
                                            }
                                        }
                                    }) {
                                        Text("Editar", color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(onClick = {
                                        authViewModel.excluirRoupa(
                                            roupa.id,
                                            onComplete = {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Peça excluída")
                                                }
                                            },
                                            onError = { err ->
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Erro ao excluir: $err")
                                                }
                                            }
                                        )
                                    }) {
                                        Text("Excluir", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }

                // Botão "Sair da conta" grande, cinza
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(55.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.Gray)
                            .clickable {
                                authViewModel.signOut()
                                navController.navigate("login") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Sair da conta",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
