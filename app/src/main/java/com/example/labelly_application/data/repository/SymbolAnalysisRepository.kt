package com.example.labelly_application.data.repository

import android.content.Context
import android.net.Uri
import com.example.labelly_application.CareSymbol
import com.example.labelly_application.ui.main.YoloSymbolDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SymbolAnalysisRepository(private val context: Context) {

    private val yoloDetector = YoloSymbolDetector(context)

    suspend fun analyzeImage(imageUri: Uri): Result<List<CareSymbol>> = withContext(Dispatchers.IO) {
        try {
            // Detectează simbolurile folosind YOLO
            val detections = yoloDetector.detectSymbols(imageUri)

            // Convertește detecțiile în CareSymbol pentru UI
            val careSymbols = detections.map { detection ->
                CareSymbol(
                    key = detection.symbolKey,
                    category = detection.category,
                    icon = getSymbolIcon(detection.symbolKey),
                    description = getSymbolDescription(detection.symbolKey),
                    confidence = detection.confidence
                )
            }

            // Sortează după categorie și confidence
            val sortedSymbols = careSymbols
                .sortedWith(compareBy({ it.category }, { -it.confidence }))

            Result.success(sortedSymbols)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getSymbolIcon(symbolKey: String): String {
        return when (symbolKey) {
            "wash_30" -> "30°"
            "wash_40" -> "40°"
            "wash_60" -> "60°"
            "wash_95" -> "95°"
            "hand_wash" -> "🤲"
            "no_wash" -> "🚫"
            "bleach_allowed" -> "△"
            "no_bleach" -> "🚫△"
            "non_chlorine_bleach" -> "△̸"
            "tumble_dry_normal" -> "◯"
            "tumble_dry_low" -> "◯•"
            "tumble_dry_high" -> "◯••"
            "no_tumble_dry" -> "🚫◯"
            "line_dry" -> "│"
            "flat_dry" -> "═"
            "iron_low" -> "•"
            "iron_medium" -> "••"
            "iron_high" -> "•••"
            "no_iron" -> "🚫"
            "no_steam" -> "⚡"
            "dry_clean" -> "○"
            "no_dry_clean" -> "🚫○"
            "dry_clean_petroleum" -> "P"
            "gentle_dry_clean" -> "F"
            else -> "?"
        }
    }

    private fun getSymbolDescription(symbolKey: String): String {
        return when (symbolKey) {
            "wash_30" -> "Spălare la 30°C"
            "wash_40" -> "Spălare la 40°C"
            "wash_60" -> "Spălare la 60°C"
            "wash_95" -> "Spălare la 95°C"
            "hand_wash" -> "Spălare manuală"
            "no_wash" -> "Nu se spală"
            "bleach_allowed" -> "Se poate înălbi"
            "no_bleach" -> "Nu se înălbește"
            "non_chlorine_bleach" -> "Doar înălbitor fără clor"
            "tumble_dry_normal" -> "Uscare normală"
            "tumble_dry_low" -> "Uscare la temperatură mică"
            "tumble_dry_high" -> "Uscare la temperatură înaltă"
            "no_tumble_dry" -> "Nu se usucă la uscător"
            "line_dry" -> "Uscare pe sârmă"
            "flat_dry" -> "Uscare pe orizontală"
            "iron_low" -> "Călcare la temperatură mică"
            "iron_medium" -> "Călcare la temperatură medie"
            "iron_high" -> "Călcare la temperatură înaltă"
            "no_iron" -> "Nu se calcă"
            "no_steam" -> "Fără abur"
            "dry_clean" -> "Curățare chimică"
            "no_dry_clean" -> "Nu se curăță chimic"
            "dry_clean_petroleum" -> "Curățare cu solvent pe bază de petrol"
            "gentle_dry_clean" -> "Curățare chimică delicată"
            else -> "Simbol necunoscut"
        }
    }

    fun close() {
        yoloDetector.close()
    }
}