package com.example.labelly_application.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labelly_application.R

@Composable
fun CreateAccountScreen(
    username: String,
    email: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F8FF)) // Light blue background
        ) {
            // Top Section - Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(Color(0xFFE6F3FF)) // Very light blue
            ) {
                // Create Account Illustration
                Image(
                    painter = painterResource(id = R.drawable.create_acc), // Imaginea cu ilustrația
                    contentDescription = "Create Account Illustration",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Bottom Section - Form cu fundal transparent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Background cu iconițele de curățenie cu transparență
                Image(
                    painter = painterResource(id = R.drawable.create_acc),
                    contentDescription = "Background Icons",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.1f // Mai transparentă pentru fundal
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(16.dp))

                    // Username Field
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF9999) // Coral red color
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Username",
                                tint = Color(0xFF8B0000),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (username.isEmpty()) {
                                    Text(
                                        text = "USERNAME",
                                        color = Color(0xFF8B0000),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                BasicTextField(
                                    value = username,
                                    onValueChange = onUsernameChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = Color(0xFF8B0000),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF9999) // Coral red color
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color(0xFF8B0000),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (email.isEmpty()) {
                                    Text(
                                        text = "YOUR E-MAIL",
                                        color = Color(0xFF8B0000),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                BasicTextField(
                                    value = email,
                                    onValueChange = onEmailChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = Color(0xFF8B0000),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF9999) // Coral red color
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = Color(0xFF8B0000),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                if (password.isEmpty()) {
                                    Text(
                                        text = "PASSWORD",
                                        color = Color(0xFF8B0000),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                BasicTextField(
                                    value = password,
                                    onValueChange = onPasswordChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = Color(0xFF8B0000),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            TextButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = if (passwordVisible) "Hide" else "Show",
                                    color = Color(0xFF8B0000),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Create Button ca Card clickable
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable {
                                println("🔥🔥🔥 CARD CLICKED!")
                                println("Username: '$username'")
                                println("Email: '$email'")
                                println("Password: '$password'")
                                onCreateClick()
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFB19CD9) // Light purple
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "CREATE ACCOUNT",
                                color = Color(0xFF4A148C),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Back to Login Link
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ALREADY HAVE AN ACCOUNT? ",
                            color = Color(0xFF757575),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "SIGN IN",
                            color = Color(0xFF2196F3),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.clickable { onBackToLoginClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}