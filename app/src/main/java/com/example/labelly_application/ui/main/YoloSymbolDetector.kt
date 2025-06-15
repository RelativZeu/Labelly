
/*package com.example.labelly_application.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

data class Detection(
    val boundingBox: RectF,
    val label: String,
    val confidence: Float,
    val symbolKey: String,
    val category: String
)

class YoloSymbolDetector(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val inputSize = 640
    private val numClasses = 31

    // Maparea claselor tale din Python
    private val labelMap = mapOf(
        0 to Triple("do_not_wash", "washing", "Nu se spală"),
        1 to Triple("do_not_bleach", "bleaching", "Nu se înălbește"),
        2 to Triple("iron_low", "ironing", "Călcare temperatură joasă"),
        3 to Triple("dry_clean_P", "dry_cleaning", "Curățare chimică P"),
        4 to Triple("no_tumble_dry", "drying", "Nu se usucă în uscător"),
        5 to Triple("no_wash", "washing", "Nu se spală"),
        6 to Triple("no_bleach", "bleaching", "Nu se înălbește"),
        7 to Triple("iron_110", "ironing", "Călcare max 110°C"),
        8 to Triple("wash_40", "washing", "Spălare 40°C"),
        9 to Triple("iron_150", "ironing", "Călcare max 150°C"),
        10 to Triple("dry_clean_P_normal", "dry_cleaning", "Curățare chimică P"),
        11 to Triple("wash_40", "washing", "Spălare 40°C"),
        12 to Triple("no_bleach", "bleaching", "Nu se înălbește"),
        13 to Triple("no_tumble_dry", "drying", "Nu se usucă în uscător"),
        14 to Triple("iron_150", "ironing", "Călcare max 150°C"),
        15 to Triple("dry_clean_P", "dry_cleaning", "Curățare chimică P"),
        16 to Triple("wash_30", "washing", "Spălare 30°C"),
        17 to Triple("iron_110", "ironing", "Călcare max 110°C"),
        18 to Triple("no_dry_clean", "dry_cleaning", "Nu se curăță chimic"),
        19 to Triple("dry_clean_P_mild", "dry_cleaning", "Curățare chimică delicată"),
        20 to Triple("hand_wash", "washing", "Spălare manuală"),
        21 to Triple("bleach_ok", "bleaching", "Se poate înălbi"),
        22 to Triple("tumble_dry_low", "drying", "Uscare uscător 60°C"),
        23 to Triple("wash_mild_30", "washing", "Spălare delicată 30°C"),
        24 to Triple("wash_mild_40", "washing", "Spălare delicată 40°C"),
        25 to Triple("no_iron", "ironing", "Nu se calcă"),
        26 to Triple("wash_mild_40", "washing", "Spălare delicată 40°C"),
        27 to Triple("bleach_special", "bleaching", "Înălbitor special"),
        28 to Triple("tumble_dry_normal", "drying", "Uscare normală 80°C"),
        29 to Triple("iron_200", "ironing", "Călcare max 200°C"),
        30 to Triple("no_wash", "washing", "Nu se spală")
    )

    private val symbolIcons = mapOf(
        "do_not_wash" to "🚫",
        "no_wash" to "🚫",
        "wash_30" to "30°",
        "wash_40" to "40°",
        "wash_mild_30" to "30°",
        "wash_mild_40" to "40°",
        "hand_wash" to "🤲",
        "do_not_bleach" to "⊗",
        "no_bleach" to "⊗",
        "bleach_ok" to "△",
        "bleach_special" to "△̸",
        "no_tumble_dry" to "⊠",
        "tumble_dry_low" to "●",
        "tumble_dry_normal" to "●●",
        "iron_low" to "•",
        "iron_110" to "•",
        "iron_150" to "••",
        "iron_200" to "•••",
        "no_iron" to "⊗",
        "no_dry_clean" to "⊗",
        "dry_clean_P" to "Ⓟ",
        "dry_clean_P_normal" to "Ⓟ",
        "dry_clean_P_mild" to "Ⓟ"
    )

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val model = FileUtil.loadMappedFile(context, "yolo_care_symbols.tflite")
            interpreter = Interpreter(model)
            Log.d("YOLO", "Model loaded successfully")
        } catch (e: Exception) {
            Log.e("YOLO", "Error loading model", e)
        }
    }

    suspend fun detectSymbols(imageUri: Uri): List<Detection> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            Log.d("YOLO", "Image loaded: ${bitmap.width}x${bitmap.height}")

            val processedImage = preprocessImage(bitmap)
            val detections = runInference(processedImage)
            return@withContext postProcess(detections)
        } catch (e: Exception) {
            Log.e("YOLO", "Error detecting symbols", e)
            return@withContext emptyList()
        }
    }

    private fun preprocessImage(bitmap: Bitmap): TensorImage {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputSize, inputSize, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()

        var tensorImage = TensorImage.fromBitmap(bitmap)
        tensorImage = imageProcessor.process(tensorImage)
        return tensorImage
    }

    private fun runInference(tensorImage: TensorImage): Array<FloatArray> {
        val interpreter = this.interpreter ?: return emptyArray()

        val output = Array(1) { Array(35) { FloatArray(8400) } }
        interpreter.run(tensorImage.buffer, output)

        // LOG-URI CORECTE
        Log.d("YOLO", "Model output shape: [1, 35, 8400]")
        Log.d("YOLO", "First few coord values: [${output[0][0][0]}, ${output[0][1][0]}, ${output[0][2][0]}, ${output[0][3][0]}]")

        // Găsește max confidence din prima detecție
        val firstDetectionClasses = FloatArray(31) { output[0][it + 4][0] }
        Log.d("YOLO", "Max confidence in first detection: ${firstDetectionClasses.maxOrNull()}")
        Log.d("YOLO", "All confidences in first detection: ${firstDetectionClasses.joinToString(", ")}")

        // Transpune din [1, 35, 8400] în [8400, 35]
        val transposed = Array(8400) { FloatArray(35) }
        for (i in 0 until 35) {
            for (j in 0 until 8400) {
                transposed[j][i] = output[0][i][j]
            }
        }
        return transposed
    }

    private fun postProcess(rawOutput: Array<FloatArray>): List<Detection> {
        val detections = mutableListOf<Detection>()
        val confidenceThreshold = 0.25f

        Log.d("YOLO", "Processing ${rawOutput.size} detections")
        var validDetections = 0

        for (i in rawOutput.indices) {
            val prediction = rawOutput[i]

            // Pentru YOLOv8
            val x = prediction[0]
            val y = prediction[1]
            val w = prediction[2]
            val h = prediction[3]

            // Găsește clasa cu cea mai mare probabilitate
            var maxProb = 0f
            var maxIndex = -1

            for (j in 4 until prediction.size) {
                if (prediction[j] > maxProb) {
                    maxProb = prediction[j]
                    maxIndex = j - 4
                }
            }

            if (maxProb > 0.1f) { // Log chiar și confidence-uri mici
                Log.d("YOLO", "Detection $i: class=$maxIndex, confidence=$maxProb")
            }

            if (maxProb > confidenceThreshold) {
                validDetections++

                val left = (x - w / 2) * inputSize
                val top = (y - h / 2) * inputSize
                val right = (x + w / 2) * inputSize
                val bottom = (y + h / 2) * inputSize

                val symbolInfo = labelMap[maxIndex]
                if (symbolInfo != null) {
                    val (key, category, description) = symbolInfo
                    val icon = symbolIcons[key] ?: "?"

                    detections.add(
                        Detection(
                            boundingBox = RectF(left, top, right, bottom),
                            label = "$icon $description",
                            confidence = maxProb,
                            symbolKey = key,
                            category = category
                        )
                    )
                }
            }
        }

        Log.d("YOLO", "Found $validDetections valid detections above threshold $confidenceThreshold")
        return detections
    }

    fun close() {
        interpreter?.close()
    }
}*/
package com.example.labelly_application.ui.main

import android.content.Context
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

data class Detection(
    val boundingBox: RectF,
    val label: String,
    val confidence: Float,
    val symbolKey: String,
    val category: String
)

class YoloSymbolDetector(private val context: Context) {

    private var mockModel: Boolean = true

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            // Pentru test - folosim mock model
            Log.d("YOLO", "Mock model loaded for testing")
            mockModel = true
        } catch (e: Exception) {
            Log.e("YOLO", "Error loading model", e)
        }
    }

    suspend fun detectSymbols(imageUri: Uri): List<Detection> = withContext(Dispatchers.IO) {
        try {
            Log.d("YOLO", "Starting mock symbol detection...")

            // Simulăm procesarea imaginii
            delay(2000) // 2 secunde pentru a simula procesarea

            // Mock pentru test - returnează simboluri fake
            Log.d("YOLO", "Mock detection completed - returning test symbols")

            return@withContext listOf(
                Detection(
                    boundingBox = RectF(100f, 100f, 200f, 200f),
                    label = "🤲 Spălare manuală",
                    confidence = 0.95f,
                    symbolKey = "hand_wash",
                    category = "washing"
                ),
                Detection(
                    boundingBox = RectF(300f, 100f, 400f, 200f),
                    label = "30° Spălare 30°C",
                    confidence = 0.88f,
                    symbolKey = "wash_30",
                    category = "washing"
                ),
                Detection(
                    boundingBox = RectF(100f, 300f, 200f, 400f),
                    label = "⊗ Nu se înălbește",
                    confidence = 0.92f,
                    symbolKey = "no_bleach",
                    category = "bleaching"
                ),
                Detection(
                    boundingBox = RectF(300f, 300f, 400f, 400f),
                    label = "•• Călcare temperatură medie",
                    confidence = 0.85f,
                    symbolKey = "iron_150",
                    category = "ironing"
                ),
                Detection(
                    boundingBox = RectF(100f, 500f, 200f, 600f),
                    label = "● Uscare uscător 60°C",
                    confidence = 0.91f,
                    symbolKey = "tumble_dry_low",
                    category = "drying"
                )
            )
        } catch (e: Exception) {
            Log.e("YOLO", "Error in mock detection", e)
            return@withContext emptyList()
        }
    }

    fun close() {
        Log.d("YOLO", "Mock detector closed")
    }
}