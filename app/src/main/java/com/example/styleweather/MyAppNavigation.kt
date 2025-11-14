package com.example.styleweather

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.styleweather.pages.*
import com.example.styleweather.pages.model.Roupa
import com.example.styleweather.pages.model.RoupaPage
import com.example.styleweather.screens.ConfiguracoesPage

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // 🟣 Tela de Login
        composable("login") {
            LoginPage(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // 🟣 Tela de Cadastro
        composable("signup") {
            SignupPage(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // 🟣 Tela Home
        composable("home") {
            HomePage(
                modifier = modifier,
                navController = navController
            )
        }
// 🟣 Tela de Configurações (PRECISA do AuthViewModel)
        composable("configuracoes") {
            ConfiguracoesPage(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // 🟣 (Opcional) Tela de Editar Perfil
        composable("editarPerfil") {
            EditarPerfilPage(
                modifier = modifier,
                navController = navController
            )
        }

        // 🟣 (Opcional) Tela de Roupa — rota com parâmetro id
        composable("roupa/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "new"
            RoupaPage(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel,
                roupaId = id
            )
        }

    }
}
