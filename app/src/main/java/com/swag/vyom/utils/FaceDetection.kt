package com.swag.vyom.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.common.InputImage
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import androidx.core.graphics.scale
import androidx.core.graphics.get


/**
 * Function to check if an image contains a human face and whether it is a spoof.
 *
 * @param context The application context.
 * @param uri The URI of the image to check.
 * @param onResult Callback with the result:
 *                 - `true` if a human face is detected and it is not a spoof.
 *                 - `false` if no face is detected or the face is a spoof.
 */
fun checkFaceAndSpoof(context: Context, imagePath: String, onResult: (Boolean) -> Unit) {
    // Step 1: Convert Uri to Bitmap
    val bitmap = BitmapFactory.decodeFile(imagePath)
    if (bitmap == null) {
        Log.e("FaceDetection", "Failed to convert Uri to Bitmap")
        onResult(false) // Notify failure
        return
    }

    // Step 2: Configure face detector
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .build()

    val detector = FaceDetection.getClient(options)

    // Step 3: Create InputImage from Bitmap
    val image = InputImage.fromBitmap(bitmap, 0)

    // Step 4: Perform face detection
    detector.process(image)
        .addOnSuccessListener { faces ->
            if (faces.isNotEmpty()) {
                Log.d("FaceDetection", "Face detected: ${faces.size} faces found")
//                // Step 5: Check for spoof (placeholder for spoof detection logic)
//                val isSpoof = checkForSpoof(
//                    bitmap,
//                    context = context
//                ) // Implement spoof detection logic
//                if (isSpoof) {
//                    Log.d("FaceDetection", "Spoof detected")
//                    onResult(false) // Notify that the face is a spoof
//                } else {
//                    Log.d("FaceDetection", "No spoof detected")
//                    onResult(true) // Notify that a valid face is detected
//                }
            } else {
                Log.d("FaceDetection", "No face detected")
                onResult(false) // Notify that no face is detected
            }
        }
        .addOnFailureListener { e ->
            Log.e("FaceDetection", "Face detection failed", e)
            onResult(false) // Notify failure
        }
}


/**
 * Helper function to convert Uri to Bitmap.
 */

//private fun checkForSpoof(bitmap: Bitmap, context: Context): Boolean {
//    // Load the TensorFlow Lite model
//    val modelFile = loadModelFile(context, "spoof_detection_model.tflite")
//    val interpreter = Interpreter(modelFile)
//
//    // Preprocess the bitmap (resize, normalize, etc.)
//    val inputBuffer = preprocessBitmap(bitmap)
//
//    // Run inference
//    val outputBuffer = ByteBuffer.allocateDirect(1 * 4) // Adjust based on model output
//    interpreter.run(inputBuffer, outputBuffer)
//
//    // Interpret the result
//    val isSpoof = outputBuffer.getFloat(0) > 0.5f // Adjust threshold as needed
//    return isSpoof
//}

private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
    val fileDescriptor = context.assets.openFd(modelPath)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
}

private fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
    // Resize bitmap to model input size
    val resizedBitmap = bitmap.scale(224, 224)

    // Convert bitmap to ByteBuffer
    val byteBuffer = ByteBuffer.allocateDirect(224 * 224 * 3 * 4) // Adjust based on model input
    byteBuffer.order(ByteOrder.nativeOrder())
    for (y in 0 until 224) {
        for (x in 0 until 224) {
            val pixel = resizedBitmap[x, y]
            byteBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // Red
            byteBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)  // Green
            byteBuffer.putFloat((pixel and 0xFF) / 255.0f)        // Blue
        }
    }
    return byteBuffer
}