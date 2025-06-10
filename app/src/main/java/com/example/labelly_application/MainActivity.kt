package com.example.labelly_application

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.labelly_application.ui.auth.CreateAccountScreen
import com.example.labelly_application.ui.auth.LoginScreen
import com.example.labelly_application.ui.auth.ResetPassword
import com.example.labelly_application.ui.camera.CameraScreen
import com.example.labelly_application.ui.main.LabellyScreen
import com.example.labelly_application.ui.theme.Labelly_ApplicationTheme
import com.example.labelly_application.viewmodel.AuthViewModel

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
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

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
                    currentScreen = "camera"
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

        "camera" -> {
            CameraScreen(
                onBackClick = {
                    currentScreen = "labelly_main"
                },
                onPhotoTaken = { uri ->
                    capturedImageUri = uri
                    currentScreen = "photo_result"
                }
            )
        }

        "photo_result" -> {
            PhotoResultScreen(
                imageUri = capturedImageUri,
                onBackClick = {
                    currentScreen = "labelly_main"
                },
                onScanAgain = {
                    currentScreen = "camera"
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
    val context = LocalContext.current

    // Toast pentru erori
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            authViewModel.clearMessages()
        }
    }

    // Toast pentru succes
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
    val context = LocalContext.current

    // Toast pentru erori
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            authViewModel.clearMessages()
        }
    }

    // Toast pentru succes
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { successMsg ->
            Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
            authViewModel.clearMessages()
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
    val context = LocalContext.current

    // Toast pentru erori
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            authViewModel.clearMessages()
        }
    }

    // Toast pentru succes
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { successMsg ->
            Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
            authViewModel.clearMessages()
            if (successMsg.contains("Email de resetare")) {
                onNavigateToLogin()
            }
        }
    }

    ResetPassword(
        email = email,
        onEmailChange = { email = it },
        onResetClick = {
            authViewModel.resetPassword(email) {
                // Success callback este gestionat de LaunchedEffect de mai sus
            }
        },
        onBackToLoginClick = onNavigateToLogin
    )
}

@Composable
fun PhotoResultScreen(
    imageUri: Uri?,
    onBackClick: () -> Unit,
    onScanAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Etichetă Capturată",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.width(48.dp))
        }

        // Image display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                androidx.compose.foundation.Image(
                    painter = coil.compose.rememberAsyncImagePainter(imageUri),
                    contentDescription = "Captured Label",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            } else {
                Card(
                    modifier = Modifier.padding(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "❌ Nu s-a putut încărca imaginea",
                        color = Color.White,
                        modifier = Modifier.padding(24.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "🔍 Analiza Etichetei",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "✅ Imaginea a fost capturată cu succes!",
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "🚀 Următorul pas: Implementarea recunoașterii automate a simbolurilor de îngrijire",
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onScanAgain,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2196F3)
                        )
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scanează Din Nou")
                    }

                    Button(
                        onClick = {
                            // TODO: Implementează analiza simbolurilor
                            println("🔍 Începe analiza simbolurilor pentru: $imageUri")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text("🔍 Analizează", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🏠 Înapoi la Meniul Principal", color = Color(0xFF757575))
                }
            }
        }
    }
}