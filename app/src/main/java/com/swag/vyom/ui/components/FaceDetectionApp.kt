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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executor

@Composable
fun FaceDetectionCameraPreview(
    processingRateLimit: Long = 100L, // Made configurable with default value
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }

    // Remember last frame timestamp to limit processing rate
    val lastProcessedTimestamp = remember { mutableLongStateOf(0L) }

    // Setup face detector options once
    val faceDetectorOptions = remember {
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.25f)
            .enableTracking()
            .build()
    }

    // Create detector instance once
    val faceDetector = remember { FaceDetection.getClient(faceDetectorOptions) }

    // Clean up resources when the composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            faceDetector.close()
        }
    }

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
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Create analyzer
            val imageAnalyzer = createImageAnalyzer(
                mainExecutor,
                faceDetector,
                lastProcessedTimestamp,
                processingRateLimit, // Pass the parameter here
                onFaceDetectionResult
            )

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
        }, mainExecutor)
    }
}

private fun createImageAnalyzer(
    executor: Executor,
    faceDetector: com.google.mlkit.vision.face.FaceDetector,
    lastProcessedTimestamp: androidx.compose.runtime.MutableState<Long>,
    processingRateLimit: Long, // Use the parameter here
    onFaceDetectionResult: (Boolean, Boolean, Boolean, Boolean, Boolean, Float) -> Unit
): ImageAnalysis {
    return ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
            it.setAnalyzer(executor) { imageProxy ->
                val currentTime = System.currentTimeMillis()

                // Process only if enough time has passed since last frame
                if (currentTime - lastProcessedTimestamp.value >= processingRateLimit) {
                    processImageForFaceDetection(
                        imageProxy,
                        faceDetector,
                        onFaceDetectionResult
                    )
                    lastProcessedTimestamp.value = currentTime
                } else {
                    // Close the image if not processing it
                    imageProxy.close()
                }
            }
        }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageForFaceDetection(
    imageProxy: ImageProxy,
    detector: com.google.mlkit.vision.face.FaceDetector,
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

                // Smile detection - improved threshold
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
            Log.e("FaceDetection", "Detection failed", it)
            onFaceDetectionResult(false, false, false, false, false, 0f)
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}