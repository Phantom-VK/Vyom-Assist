package com.swag.vyom.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions


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
                onResult(true)

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