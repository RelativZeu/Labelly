package com.example.labelly_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.labelly_application.ui.auth.CreateAccountScreen
import com.example.labelly_application.ui.auth.LoginScreen
import com.example.labelly_application.ui.theme.Labelly_ApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Labelly_ApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // State pentru a ține evidența paginii curente
    var currentScreen by remember { mutableStateOf("login") }

    when (currentScreen) {
        "login" -> {
            LoginScreen(
                onLoginClick = {
                    // TODO: Implementează logica de login
                    println("Login clicked!")
                },
                onCreateAccountClick = {
                    // Navighează la pagina de creare cont
                    currentScreen = "create_account"
                },
                onForgotPasswordClick = {
                    // TODO: Navighează la pagina de resetare parolă
                    currentScreen = "reset_password"
                    println("Forgot password clicked!")
                }
            )
        }

        "create_account" -> {
            CreateAccountScreen(
                onCreateClick = {
                    // TODO: Implementează logica de creare cont
                    println("Account created!")
                    // Poți naviga înapoi la login sau la main screen
                    currentScreen = "login"
                },
                onBackToLoginClick = {
                    // Navighează înapoi la login
                    currentScreen = "login"
                }
            )
        }

        "reset_password" -> {
            // TODO: Implementează ResetPasswordScreen
            // Pentru acum, afișează un placeholder și permite întoarcerea
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                androidx.compose.material3.Text(
                    text = "Reset Password Screen - Coming Soon!\nTap to go back",
                    modifier = Modifier.fillMaxSize(),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}