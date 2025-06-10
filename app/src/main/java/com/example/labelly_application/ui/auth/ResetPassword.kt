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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labelly_application.R

@Composable
fun ResetPassword(
    email: String,
    onEmailChange: (String) -> Unit,
    onResetClick: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F4FF)) // Very light purple background
        ) {
            // Top Section - Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(Color(0xFFE8E0FF)) // Light purple
            ) {
                // Reset Password Illustration with phone
                Image(
                    painter = painterResource(id = R.drawable.passw), // Imaginea cu telefonul și parola
                    contentDescription = "Reset Password Illustration",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Bottom Section - Form
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Background cu iconițele transparente
                Image(
                    painter = painterResource(id = R.drawable.create_acc),
                    contentDescription = "Background Icons",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.08f // Foarte transparent pentru fundal
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "RESET PASSWORD",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A1B9A),
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Enter your email to receive a password reset link",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF757575),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Email Field
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF8A80) // Light coral/pink
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color(0xFF8B0000),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (email.isEmpty()) {
                                    Text(
                                        text = "YOUR EMAIL ADDRESS",
                                        color = Color(0xFF8B0000),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
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
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Send Reset Email Button
                    Button(
                        onClick = onResetClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF7043) // Orange-red color
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "SEND RESET EMAIL",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Back to Login Link
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REMEMBER YOUR PASSWORD? ",
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