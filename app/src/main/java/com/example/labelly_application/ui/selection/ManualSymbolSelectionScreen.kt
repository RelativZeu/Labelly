package com.example.labelly_application.ui.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labelly_application.CareSymbol

// Data pentru toate simbolurile disponibile
data class SymbolOption(
    val key: String,
    val icon: String,
    val description: String,
    val category: String
)

@Composable
fun ManualSymbolSelectionScreen(
    onBackClick: () -> Unit,
    onConfirmSelection: (List<CareSymbol>) -> Unit
) {
    var selectedSymbols by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Toate simbolurile disponibile grupate pe categorii
    val symbolCategories = remember { getAllSymbolCategories() }

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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
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
                            text = "Selectează Simbolurile",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👆",
                                fontSize = 24.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = "Atinge pentru a selecta",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    text = "${selectedSymbols.size} simboluri selectate",
                                    fontSize = 12.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                        }
                    }
                }
            }

            // Symbol categories
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                symbolCategories.forEach { category ->
                    item {
                        CategorySelectionSection(
                            categoryName = category.key,
                            categoryIcon = category.value.first,
                            symbols = category.value.second,
                            selectedSymbols = selectedSymbols,
                            onSymbolToggle = { symbolKey ->
                                selectedSymbols = if (selectedSymbols.contains(symbolKey)) {
                                    selectedSymbols - symbolKey
                                } else {
                                    selectedSymbols + symbolKey
                                }
                            }
                        )
                    }
                }
            }
        }

        // Bottom confirmation button
        val context = LocalContext.current
        Button(
            onClick = {
                val selected = symbolCategories.values
                    .flatMap { it.second }
                    .filter { selectedSymbols.contains(it.key) }
                    .map {
                        CareSymbol(
                            key = it.key,
                            category = it.category,
                            icon = it.icon,
                            description = it.description,
                            confidence = 1.0f // 100% sigur pentru selecție manuală
                        )
                    }

                val selectedCategoryCount = selected.map { it.category }.toSet().size
                if (selectedCategoryCount < 5) {
                    android.widget.Toast.makeText(
                        context,
                        "Selectează câte un simbol din fiecare categorie!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onConfirmSelection(selected)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedSymbols.isNotEmpty())
                    Color(0xFF4CAF50) else Color(0xFF9E9E9E)
            ),
            enabled = selectedSymbols.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Confirmă Selecția",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CategorySelectionSection(
    categoryName: String,
    categoryIcon: String,
    symbols: List<SymbolOption>,
    selectedSymbols: Set<String>,
    onSymbolToggle: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Category header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryIcon,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = getCategoryDisplayName(categoryName),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Symbol grid
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                symbols.chunked(2).forEach { rowSymbols ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSymbols.forEach { symbol ->
                            SymbolSelectionItem(
                                symbol = symbol,
                                isSelected = selectedSymbols.contains(symbol.key),
                                onToggle = { onSymbolToggle(symbol.key) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Dacă e un singur element pe rând, adaugă spațiu gol
                        if (rowSymbols.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SymbolSelectionItem(
    symbol: SymbolOption,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color(0xFF4CAF50) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(0xFF4CAF50).copy(alpha = 0.1f)
            else Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Selection indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Symbol icon
            Text(
                text = symbol.icon,
                fontSize = 32.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Symbol description
            Text(
                text = symbol.description,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
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

fun getAllSymbolCategories(): Map<String, Pair<String, List<SymbolOption>>> {
    return mapOf(
        "washing" to ("🧺" to listOf(
            SymbolOption("wash_30", "30°", "Spălare la 30°C", "washing"),
            SymbolOption("wash_40", "40°", "Spălare la 40°C", "washing"),
            SymbolOption("wash_60", "60°", "Spălare la 60°C", "washing"),
            SymbolOption("wash_95", "95°", "Spălare la 95°C", "washing"),
            SymbolOption("hand_wash", "🤲", "Spălare manuală", "washing"),
            SymbolOption("no_wash", "🚫", "Nu se spală", "washing")
        )),

        "bleaching" to ("🧪" to listOf(
            SymbolOption("bleach_allowed", "△", "Se poate înălbi", "bleaching"),
            SymbolOption("no_bleach", "🚫△", "Nu se înălbește", "bleaching"),
            SymbolOption("non_chlorine_bleach", "△̸", "Doar înălbitor fără clor", "bleaching")
        )),

        "drying" to ("🌀" to listOf(
            SymbolOption("tumble_dry_normal", "◯", "Uscare normală", "drying"),
            SymbolOption("tumble_dry_low", "◯•", "Uscare la temp. mică", "drying"),
            SymbolOption("tumble_dry_high", "◯••", "Uscare la temp. înaltă", "drying"),
            SymbolOption("no_tumble_dry", "🚫◯", "Nu se usucă la uscător", "drying"),
            SymbolOption("line_dry", "│", "Uscare pe sârmă", "drying"),
            SymbolOption("flat_dry", "═", "Uscare pe orizontală", "drying")
        )),

        "ironing" to ("♨️" to listOf(
            SymbolOption("iron_low", "•", "Călcare temp. mică", "ironing"),
            SymbolOption("iron_medium", "••", "Călcare temp. medie", "ironing"),
            SymbolOption("iron_high", "•••", "Călcare temp. înaltă", "ironing"),
            SymbolOption("no_iron", "🚫", "Nu se calcă", "ironing"),
            SymbolOption("no_steam", "⚡", "Fără abur", "ironing")
        )),

        "dry_cleaning" to ("🧽" to listOf(
            SymbolOption("dry_clean", "○", "Curățare chimică", "dry_cleaning"),
            SymbolOption("no_dry_clean", "🚫○", "Nu se curăță chimic", "dry_cleaning"),
            SymbolOption("dry_clean_petroleum", "P", "Curățare cu solvent pe bază de petrol", "dry_cleaning"),
            SymbolOption("gentle_dry_clean", "F", "Curățare chimică delicată", "dry_cleaning")
        ))
    )
}