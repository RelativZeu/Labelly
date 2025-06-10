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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labelly_application.ui.auth.CreateAccountScreen
import com.example.labelly_application.ui.auth.LoginScreen
import com.example.labelly_application.ui.auth.ResetPassword
import com.example.labelly_application.ui.main.LabellyScreen
import com.example.labelly_application.ui.theme.Labelly_ApplicationTheme
import com.example.labelly_application.viewmodel.AuthViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

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
    val authViewModel: AuthViewModel = viewModel()
    val uiState by authViewModel.uiState.collectAsState()

    // State pentru navigare
    var currentScreen by remember { mutableStateOf("login") }

    // Verifică dacă utilizatorul este deja logat
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            currentScreen = "labelly_main"
        }
    }

    when (currentScreen) {
        "login" -> {
            LoginScreenWithFirebase(
                authViewModel = authViewModel,
                onNavigateToCreateAccount = { currentScreen = "create_account" },
                onNavigateToResetPassword = { currentScreen = "reset_password" },
                onLoginSuccess = { currentScreen = "labelly_main" }
            )
        }

        "create_account" -> {
            CreateAccountScreenWithFirebase(
                authViewModel = authViewModel,
                onNavigateToLogin = { currentScreen = "login" },
                onCreateSuccess = { currentScreen = "labelly_main" }
            )
        }

        "reset_password" -> {
            ResetPasswordScreenWithFirebase(
                authViewModel = authViewModel,
                onNavigateToLogin = { currentScreen = "login" }
            )
        }

        "labelly_main" -> {
            LabellyScreen(
                onCameraClick = {
                    println("Camera clicked!")
                },
                onSymbolsClick = {
                    println("Symbols legend clicked!")
                },
                onLogoutClick = {
                    authViewModel.logout {
                        currentScreen = "login"
                    }
                }
            )
        }
    }
}

@Composable
fun LoginScreenWithFirebase(
    authViewModel: AuthViewModel,
    onNavigateToCreateAccount: () -> Unit,
    onNavigateToResetPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current // NOU!

    // ÎNLOCUIEȘTE vechiul LaunchedEffect cu acesta:
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            authViewModel.clearMessages()
        }
    }

    // ADAUGĂ și pentru mesaje de succes:
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { successMsg ->
            Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
            authViewModel.clearMessages()
        }
    }

    LoginScreen(
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onLoginClick = {
            authViewModel.login(email, password) {
                onLoginSuccess()
            }
        },
        onCreateAccountClick = onNavigateToCreateAccount,
        onForgotPasswordClick = onNavigateToResetPassword
    )
}

@Composable
fun CreateAccountScreenWithFirebase(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onCreateSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            println("Register Error: $it")
        }
    }

    CreateAccountScreen(
        username = username,
        email = email,
        password = password,
        onUsernameChange = { username = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onCreateClick = {
            authViewModel.register(email, password, username) {
                onCreateSuccess()
            }
        },
        onBackToLoginClick = onNavigateToLogin
    )
}

@Composable
fun ResetPasswordScreenWithFirebase(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            if (it.contains("Email de resetare")) {
                onNavigateToLogin()
            }
        }
    }

    ResetPassword(
        email = email,
        onEmailChange = { email = it },
        onResetClick = {
            authViewModel.resetPassword(email) {
                onNavigateToLogin()
            }
        },
        onBackToLoginClick = onNavigateToLogin
    )
}