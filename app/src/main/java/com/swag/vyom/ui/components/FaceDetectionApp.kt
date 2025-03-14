package com.swag.vyom.ui.components

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

@Composable
fun FaceDetectionCameraPreview(
    onFaceDetectionResult: (
        faceDetected: Boolean,
        leftTurn: Boolean,
        rightTurn: Boolean,
        blinkDetected: Boolean,
        smileDetected: Boolean,
        faceDistance: Float
    ) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Remember last frame timestamp to limit processing rate
    val lastProcessedTimestamp = remember { mutableLongStateOf(0L) }
    val processingRateLimit = 100L // ms between frames to process

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { previewView ->
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up camera selector - use front camera for face auth
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            // Configure preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

            // Configure image analysis with rate limiting
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        val currentTime = System.currentTimeMillis()

                        // Process only if enough time has passed since last frame
                        if (currentTime - lastProcessedTimestamp.longValue >= processingRateLimit) {
                            processImageForFaceDetection(
                                imageProxy,
                                onFaceDetectionResult
                            )
                            lastProcessedTimestamp.longValue = currentTime
                        } else {
                            // Close the image if not processing it
                            imageProxy.close()
                        }
                    }
                }

            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

            } catch (e: Exception) {
                Log.e("CameraPreview", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
fun processImageForFaceDetection(
    imageProxy: ImageProxy,
    onFaceDetectionResult: (
        faceDetected: Boolean,
        leftTurn: Boolean,
        rightTurn: Boolean,
        blinkDetected: Boolean,
        smileDetected: Boolean,
        faceDistance: Float
    ) -> Unit
) {
    val mediaImage = imageProxy.image ?: run {
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.25f)
        .enableTracking()
        .build()

    val detector = FaceDetection.getClient(options)
    detector.process(image)
        .addOnSuccessListener { faces ->
            if (faces.isNotEmpty()) {
                val face = faces[0]

                // Head rotation detection
                val headEulerAngleY = face.headEulerAngleY // Left-right rotation
                val leftTurn = headEulerAngleY < -15
                val rightTurn = headEulerAngleY > 15

                // Eye blinking detection
                val leftEyeOpenProb = face.leftEyeOpenProbability ?: 0f
                val rightEyeOpenProb = face.rightEyeOpenProbability ?: 0f
                val blinkDetected = (leftEyeOpenProb < 0.5f || rightEyeOpenProb < 0.5f)

                // Smile detection
                val smileProb = face.smilingProbability ?: 0f
                val smileDetected = smileProb > 0.7f

                // Calculate face distance (approximation based on face size)
                val faceDistance = 1f / (face.boundingBox.width().toFloat() / image.width.toFloat())

                // Return enhanced face detection results
                onFaceDetectionResult(
                    true,
                    leftTurn,
                    rightTurn,
                    blinkDetected,
                    smileDetected,
                    faceDistance
                )
            } else {
                // No face detected
                onFaceDetectionResult(false, false, false, false, false, 0f)
            }
        }
        .addOnFailureListener {
            // Handle failure
            onFaceDetectionResult(false, false, false, false, false, 0f)
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}