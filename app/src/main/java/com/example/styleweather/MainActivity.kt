package com.example.styleweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.styleweather.ui.theme.theme.StyleWeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StyleWeatherTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                // observa se está logado ou não
                val authState by authViewModel.authState.observeAsState(AuthState.Unauthenticated)

                Surface {
                    MyAppNavigation(navController = navController, authViewModel = authViewModel)
                }
            }
        }
    }
}
