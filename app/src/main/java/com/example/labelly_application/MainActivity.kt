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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState


// Data class pentru simbolurile de îngrijire
data class CareSymbol(
    val key: String,
    val category: String,
    val icon: String,
    val description: String,
    val confidence: Float
)

// Data class pentru categorii
data class SymbolCategory(
    val name: String,
    val displayName: String,
    val icon: String,
    val symbols: List<CareSymbol>
)

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
                },
                onCorrectClick = {
                    // Navighează la pagina cu explicații
                    currentScreen = "symbol_explanations"
                },
                onIncorrectClick = {
                    // Navighează la pagina de selecție manuală
                    currentScreen = "manual_symbol_selection"
                }
            )
        }

        "symbol_explanations" -> {
            // TODO: Implementează pagina cu explicații pentru simboluri
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Symbol Explanations Screen - To be implemented")
            }
        }

        "manual_symbol_selection" -> {
            // TODO: Implementează pagina de selecție manuală a simbolurilor
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Manual Symbol Selection Screen - To be implemented")
            }
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
    onScanAgain: () -> Unit,
    onCorrectClick: () -> Unit = { println("Navigate to ExplanationScreen") },
    onIncorrectClick: () -> Unit = { println("Navigate to ManualSelectionScreen") }
) {
    var analysisComplete by remember { mutableStateOf(false) }
    var detectedSymbols by remember { mutableStateOf<List<CareSymbol>>(emptyList()) }

    val mockDetectedSymbols = listOf(
        CareSymbol("wash_30", "washing", "🧺", "Spălare la 30°C", 0.85f),
        CareSymbol("no_bleach", "bleaching", "△", "Nu se înălbește", 0.91f),
        CareSymbol("no_tumble_dry", "drying", "◯", "Nu se usucă la uscător", 0.92f),
        CareSymbol("iron_low", "ironing", "🔥", "Călcare la temperatură mică", 0.78f),
        CareSymbol("dry_clean", "dry_cleaning", "○", "Curățare chimică", 0.88f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = if (analysisComplete) "Rezultate Analiză" else "Etichetă Capturată",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            // Image display (reduced size when analysis is complete)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (analysisComplete) 250.dp else 400.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Captured Label",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Results section
            if (analysisComplete) {
                // Card cu simbolurile detectate (cu padding bottom pentru a nu acoperi butoanele)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    AnalysisResultsSection(
                        detectedSymbols = detectedSymbols
                    )
                }
            } else {
                // Initial buttons
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(16.dp)
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onScanAgain,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Scanează Din Nou")
                            }

                            Button(
                                onClick = {
                                    // Simulează analiza
                                    detectedSymbols = mockDetectedSymbols
                                    analysisComplete = true
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
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

        // Întrebarea și butoanele fixate jos (afișate doar când analiza e completă)
        if (analysisComplete && detectedSymbols.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sunt corect selectate simbolurile?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Buton NU
                        OutlinedButton(
                            onClick = onIncorrectClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935))
                        ) {
                            Text(
                                text = "✕ NU",
                                color = Color(0xFFE53935),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Buton DA
                        Button(
                            onClick = onCorrectClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF66BB6A)
                            )
                        ) {
                            Text(
                                text = "✓ DA",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisResultsSection(
    detectedSymbols: List<CareSymbol>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header cu icon și titlu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎯",
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Simboluri Detectate",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
        }

        if (detectedSymbols.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "❌ Nu am detectat simboluri clare în imagine",
                    fontSize = 14.sp,
                    color = Color(0xFFE57373),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Lista de simboluri cu scroll
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                   // .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 200.dp) // Spațiu pentru butoanele fixe
            ) {
                itemsIndexed(detectedSymbols) { index, symbol ->
                    CompactSymbolItem(symbol = symbol)

                    // Afișează un separator sau indicator după primele 2 simboluri
                    if (index == 1 && detectedSymbols.size > 2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "↓ Scroll pentru mai multe simboluri ↓",
                                fontSize = 12.sp,
                                color = Color(0xFF9E9E9E),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CompactSymbolItem(symbol: CareSymbol) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon simbol
            Card(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.icon,
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Detalii simbol
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = symbol.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )

                Text(
                    text = "Categorie: ${getCategoryDisplayName(symbol.category)}",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }

            // Procentaj încredere
            Text(
                text = "${(symbol.confidence * 100).toInt()}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF66BB6A)
            )
        }
    }
}

@Composable
fun DetectedSymbolItem(symbol: CareSymbol) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Symbol icon
            Text(
                text = symbol.icon,
                fontSize = 24.sp,
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Symbol info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = symbol.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF424242)
                )

                Text(
                    text = "Categorie: ${getCategoryDisplayName(symbol.category)}",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            // Confidence score
            Text(
                text = "${(symbol.confidence * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    symbol.confidence > 0.8f -> Color(0xFF4CAF50)
                    symbol.confidence > 0.6f -> Color(0xFFFF9800)
                    else -> Color(0xFFE57373)
                },
                modifier = Modifier
                    .background(
                        when {
                            symbol.confidence > 0.8f -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            symbol.confidence > 0.6f -> Color(0xFFFF9800).copy(alpha = 0.1f)
                            else -> Color(0xFFE57373).copy(alpha = 0.1f)
                        },
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

fun getCategoryDisplayName(category: String): String {
    return when (category) {
        "washing" -> "Spălare"
        "bleaching" -> "Înălbire"
        "drying" -> "Uscare"
        "ironing" -> "Călcare"
        "dry_cleaning" -> "Curățare chimică"
        else -> category.capitalize()
    }
}