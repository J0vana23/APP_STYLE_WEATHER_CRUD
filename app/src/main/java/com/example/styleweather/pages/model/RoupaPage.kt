package com.example.styleweather.pages.model

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.styleweather.AuthViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch

@Composable
fun RoupaPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    roupaId: String // "new" para criar, id para editar
) {
    val roupas by authViewModel.roupas.observeAsState(emptyList())
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var nome by remember { mutableStateOf("") }
    var estacao by remember { mutableStateOf("") }

    val purpleGradient = Brush.horizontalGradient(listOf(Color(0xFF5F2B87), Color(0xFFA563FF)))

    LaunchedEffect(roupas, roupaId) {
        if (roupaId != "new") {
            val encontrada = roupas.firstOrNull { it.id == roupaId }
            if (encontrada != null) {
                nome = encontrada.nome
                estacao = encontrada.estacao
            } else {
                nome = ""
                estacao = ""
            }
        } else {
            nome = ""
            estacao = ""
        }
    }

    // ✅ Fundo branco da tela
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent // mantém branco do Surface
        ) { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (roupaId == "new") "Adicionar nova peça" else "Editar peça",
                    fontSize = 20.sp,
                    color = Color(0xFF5F2B87)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome da peça") },
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

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = estacao,
                    onValueChange = { estacao = it },
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

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(purpleGradient)
                        .clickable {
                            if (nome.isBlank()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Digite o nome da peça")
                                }
                                return@clickable
                            }

                            if (roupaId == "new") {
                                authViewModel.adicionarRoupa(nome, estacao,
                                    onComplete = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Peça adicionada")
                                            navController.popBackStack()
                                        }
                                    },
                                    onError = { msg ->
                                        scope.launch { snackbarHostState.showSnackbar("Erro: $msg") }
                                    })
                            } else {
                                authViewModel.editarRoupa(roupaId, nome, estacao,
                                    onComplete = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Peça atualizada")
                                            navController.popBackStack()
                                        }
                                    },
                                    onError = { msg ->
                                        scope.launch { snackbarHostState.showSnackbar("Erro: $msg") }
                                    })
                            }
                        }
                        .padding(vertical = 14.dp)
                        .fillMaxWidth(0.9f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (roupaId == "new") "Salvar" else "Atualizar",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
