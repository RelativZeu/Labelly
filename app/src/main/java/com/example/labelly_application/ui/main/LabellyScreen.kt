package com.example.labelly_application.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CameraAlt
//import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labelly_application.R

@Composable
fun LabellyScreen(
    onCameraClick: () -> Unit,
    onSymbolsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp), // ajustează după nevoie
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bottle), // imaginea ta cu sticla
                    contentDescription = "Bottle background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "MY LABELLY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue,
                    modifier = Modifier
                        .offset(x = 40.dp, y = 80.dp) // poziționare mai exactă
                )
            }


            // Main Content Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Alege o opțiune",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Camera Button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clickable { onCameraClick() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF59D) // Light yellow
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.camera), // Imaginea camera.png
                                contentDescription = "Camera",
                                tint = Color(0xFF8D6E63),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Fotografiază eticheta",
                                    fontSize = 12.sp,
                                    color = Color(0xFF5D4037),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Deschide camera",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Symbols Legend Button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clickable { onSymbolsClick() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF59D) // Light yellow
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.hand), // Imaginea hand.png (pentru touch)
                                contentDescription = "Symbols",
                                tint = Color(0xFF8D6E63),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Alege simbolurile",
                                    fontSize = 12.sp,
                                    color = Color(0xFF5D4037),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Legendă simboluri",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Bottom Illustration
                    Image(
                        painter = painterResource(id = R.drawable.spalat), // Placeholder - vom înlocui cu ilustrația corectă
                        contentDescription = "Laundry Illustration",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Fit,
                        alpha = 0.8f
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Logout Button (optional)
                    TextButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Deconectează-te",
                            color = Color(0xFF757575),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}