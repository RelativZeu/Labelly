package com.example.labelly_application.ui.explanations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.labelly_application.CareSymbol
import com.example.labelly_application.R

@Composable
fun SymbolExplanationsScreen(
    detectedSymbols: List<CareSymbol>,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "Ghid de Îngrijire",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = onSaveClick,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Save",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Summary card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✅",
                                fontSize = 32.sp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Analiza completă!",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    text = "${detectedSymbols.size} simboluri detectate",
                                    fontSize = 14.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                        }
                    }
                }
            }

            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Grupează simbolurile pe categorii
                val groupedSymbols = detectedSymbols.groupBy { it.category }

                groupedSymbols.forEach { (category, symbols) ->
                    item {
                        CategorySection(
                            category = category,
                            symbols = symbols
                        )
                    }
                }

                // Recomandări generale
                item {
                    GeneralRecommendationsCard()
                }

                // Spațiu la final
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Bottom action button
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(
                text = "💾 Salvează Ghidul de Îngrijire",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CategorySection(
    category: String,
    symbols: List<CareSymbol>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Category header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categoryIcon = when(category) {
                    "washing" -> "🧺"
                    "bleaching" -> "🧪"
                    "drying" -> "🌀"
                    "ironing" -> "♨️"
                    "dry_cleaning" -> "🧽"
                    else -> "📋"
                }

                Text(
                    text = categoryIcon,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = getCategoryDisplayName(category),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Symbols in this category
            symbols.forEach { symbol ->
                SymbolExplanationItem(symbol = symbol)
                if (symbol != symbols.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SymbolExplanationItem(symbol: CareSymbol) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Symbol image
        Card(
            modifier = Modifier.size(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getDrawableResourceForSymbol(symbol.key)),
                    contentDescription = symbol.description,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = symbol.description,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = getDetailedExplanation(symbol.key),
                fontSize = 13.sp,
                color = Color(0xFF616161),
                lineHeight = 18.sp
            )

            // Confidence indicator dacă este mai mic de 100%
            if (symbol.confidence < 1.0f) {
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Acuratețe: ${(symbol.confidence * 100).toInt()}%",
                        fontSize = 11.sp,
                        color = Color(0xFFFF8F00),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GeneralRecommendationsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡",
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Sfaturi Generale",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF57C00)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val tips = listOf(
                "Sortează întotdeauna rufele după culoare și material",
                "Verifică buzunarele înainte de spălare",
                "Închide fermoarele și nasturii înainte de spălare",
                "Întoarce hainele pe dos pentru a proteja culorile",
                "Nu supraîncărca mașina de spălat"
            )

            tips.forEach { tip ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "• ",
                        fontSize = 13.sp,
                        color = Color(0xFF795548)
                    )
                    Text(
                        text = tip,
                        fontSize = 13.sp,
                        color = Color(0xFF795548),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// Funcție helper pentru a obține drawable resource ID-ul bazat pe symbol key
fun getDrawableResourceForSymbol(symbolKey: String): Int {
    return when (symbolKey) {
        // Washing symbols
        "wash_30" -> R.drawable.wash_30
        "wash_40" -> R.drawable.wash_40
        "wash_60" -> R.drawable.wash_60
        "wash_95" -> R.drawable.wash_95
        "hand_wash" -> R.drawable.hand_wash
        "no_wash" -> R.drawable.no_wash

        // Bleaching symbols
        "bleach_allowed" -> R.drawable.bleach_allowed
        "no_bleach" -> R.drawable.no_bleach
        "non_chlorine_bleach" -> R.drawable.non_chlorine_bleach

        // Drying symbols
        "tumble_dry_normal" -> R.drawable.tumble_dry_normal
        "tumble_dry_low" -> R.drawable.tumble_dry_low
        "tumble_dry_high" -> R.drawable.tumble_dry_high
        "no_tumble_dry" -> R.drawable.no_tumble_dry
        "line_dry" -> R.drawable.line_dry
        "flat_dry" -> R.drawable.flat_dry

        // Ironing symbols
        "iron_low" -> R.drawable.iron_low
        "iron_medium" -> R.drawable.iron_medium
        "iron_high" -> R.drawable.iron_high
        "no_iron" -> R.drawable.no_iron
        "no_steam" -> R.drawable.no_steam

        // Dry cleaning symbols
        "dry_clean" -> R.drawable.dry_clean
        "no_dry_clean" -> R.drawable.no_dry_clean
        "dry_clean_petroleum" -> R.drawable.dry_clean_petroleum
        "gentle_dry_clean" -> R.drawable.gentle_dry_clean

        // Default fallback
        else -> R.drawable.front // sau orice imagine default
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

fun getDetailedExplanation(symbolKey: String): String {
    return when (symbolKey) {
        "wash_30" -> "Spălați la maxim 30°C. Ideal pentru materiale delicate și haine colorate care pot decolora."
        "wash_40" -> "Spălați la maxim 40°C. Potrivit pentru bumbac și materiale mixte moderat murdare."
        "wash_60" -> "Spălați la maxim 60°C. Pentru rufe albe și foarte murdare."
        "wash_95" -> "Spălați la maxim 95°C. Pentru rufe foarte rezistente, albe și foarte murdare."
        "hand_wash" -> "Spălați doar manual, cu apă călduță și detergent delicat. Nu frecați prea tare."
        "no_wash" -> "Nu spălați cu apă. Necesită curățare chimică profesională."

        "bleach_allowed" -> "Se poate folosi înălbitor. Urmați instrucțiunile de pe produs."
        "no_bleach" -> "Nu folosiți înălbitor sau detergenți cu clor. Poate deteriora materialul."
        "non_chlorine_bleach" -> "Se poate folosi doar înălbitor fără clor (pe bază de oxigen)."

        "tumble_dry_normal" -> "Uscați la temperatură normală. Pentru majoritatea materialelor."
        "tumble_dry_low" -> "Uscați la temperatură scăzută. Pentru materiale sensibile la căldură."
        "tumble_dry_high" -> "Uscați la temperatură înaltă. Pentru materiale groase și rezistente."
        "no_tumble_dry" -> "Nu uscați în uscător. Uscați natural pentru a evita deformarea."
        "line_dry" -> "Uscați pe sârmă sau umeraș. Ideal pentru majoritatea materialelor."
        "flat_dry" -> "Uscați pe o suprafață plană. Pentru articole care se pot deforma (lână, etc.)."

        "iron_low" -> "Călcați la temperatură mică (max 110°C). Pentru materiale sintetice."
        "iron_medium" -> "Călcați la temperatură medie (max 150°C). Pentru lână și amestecuri."
        "iron_high" -> "Călcați la temperatură înaltă (max 200°C). Pentru bumbac și in."
        "no_iron" -> "Nu călcați. Materialul se poate topi sau deteriora."
        "no_steam" -> "Călcați fără abur. Aburul poate deteriora acest material."

        "dry_clean" -> "Curățare chimică permisă. Duceți la curățătorie profesională."
        "no_dry_clean" -> "Nu curățați chimic. Poate deteriora materialul."
        "dry_clean_petroleum" -> "Curățare chimică cu solvent pe bază de petrol. Specificați la curățătorie."
        "gentle_dry_clean" -> "Curățare chimică delicată. Informați curățătoria despre necesitatea unui tratament blând."

        else -> "Urmați instrucțiunile specifice pentru acest simbol."
    }
}