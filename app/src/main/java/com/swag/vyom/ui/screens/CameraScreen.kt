package com.swag.vyom.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.util.Range
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController.IMAGE_CAPTURE
import androidx.camera.view.CameraController.VIDEO_CAPTURE
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.swag.vyom.ui.components.CameraPreview
import com.swag.vyom.ui.components.PhotoBottomSheetContent
import com.swag.vyom.viewmodels.CameraViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    cameraVM: CameraViewModel,
    userID: Int,
    onMediaCaptured:  (Uri, Boolean) -> Unit // Uri and isVideo flag
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val selector = QualitySelector.fromOrderedList(
        listOf(Quality.LOWEST, Quality.SD),
        FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
    )


    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(IMAGE_CAPTURE or VIDEO_CAPTURE)
            // Set the QualitySelector here, after use cases are enabled
            videoCaptureQualitySelector = selector
            videoCaptureTargetFrameRate = Range(15, 28)


        }
    }
    val bitmap = cameraVM.bitmap.collectAsState().value
    val isRecording = cameraVM.isRecording.collectAsState().value

    // Recording variables
    var recording: Recording? by remember { mutableStateOf(null) }
    var remainingTime by remember { mutableIntStateOf(30) }

    // State to control bottom sheet visibility
    var showBottomSheet by remember { mutableStateOf(false) }

    // Timer for recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (remainingTime > 0) {
                delay(1000) // 1 second
                remainingTime--
            }
            if (isRecording) {
                recording?.stop()
                cameraVM.stopRecording()
            }
        } else {
            remainingTime = 30 // Reset timer
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = BottomSheetDefaults.HiddenShape ,
        sheetContent = {
            bitmap?.let {
                PhotoBottomSheetContent(
                    bitmaps = listOf(it),
                    modifier = Modifier.fillMaxWidth(),
                    onDelete = {
                        cameraVM.clearPhoto()
                        showBottomSheet = false // Hide bottom sheet after deletion
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E3A8A), Color(0xFF1E40AF)) // Blue gradient
                    )
                )
        ) {

            CameraPreview(
                controller = controller,
                modifier = Modifier.fillMaxSize()
            )

            // Camera switch button
            CameraSwitchButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else CameraSelector.DEFAULT_BACK_CAMERA
                }
            )

            // Recording indicator and timer
            if (isRecording) {
                RecordingIndicator(remainingTime = remainingTime)
            }

            // Bottom action bar
            BottomActionBar(
                isRecording = isRecording,
                onPhotoCapture = {
                    if (!isRecording) {
                        takePhoto(
                            controller = controller,
                            onPhotoTaken = { bitmap ->
                                cameraVM.onTakePhoto(bitmap)
                                savePhotoToCache(context, bitmap, userID)?.let { uri ->
                                    onMediaCaptured(uri, false) // isVideo = false
                                    showBottomSheet = true // Show bottom sheet for photo
                                    scope.launch {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            },
                            context = context
                        )
                        Toast.makeText(
                            context,
                            "Image Captured Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                onVideoRecord = {
                    if (isRecording) {
                        // Stop recording if already recording
                        recording?.stop()
                        cameraVM.stopRecording()
                    } else {
                        // Start new recording if not recording
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        val outputFile = File(context.filesDir, "query_recording_${timeStamp}_$userID.mp4")

                        recording = controller.startRecording(
                            FileOutputOptions.Builder(outputFile).build(),
                           AudioConfig.create(true),
                            ContextCompat.getMainExecutor(context),
                        ) { event ->
                            when (event) {
                                is VideoRecordEvent.Finalize -> {
                                    if (!event.hasError()) {
                                        // Only pass URI back on successful recording
                                        onMediaCaptured(outputFile.toUri(), true) // isVideo = true
                                        Toast.makeText(
                                            context,
                                            "Video Captured Successfully",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Video capture failed",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    cameraVM.stopRecording()
                                }
                            }
                        }

                        cameraVM.startRecording()
                    }
                }
            )
        }
    }
}

@Composable
private fun CameraSwitchButton(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) { // Add a Box as a container
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.TopEnd) // Now align is valid here
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "Switch camera",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun RecordingIndicator(remainingTime: Int) {
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .background(Color.Red, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FiberManualRecord,
                contentDescription = "Recording",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Recording: ${remainingTime}s",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BottomActionBar(
    isRecording: Boolean,
    onPhotoCapture: () -> Unit,
    onVideoRecord: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Photo capture button
            CaptureButton(
                icon = Icons.Default.PhotoCamera,
                contentDescription = "Take photo",
                onClick = onPhotoCapture,
                isEnabled = !isRecording
            )

            // Video recording button
            CaptureButton(
                icon = if (isRecording) Icons.Default.Stop else Icons.Default.Videocam,
                contentDescription = if (isRecording) "Stop Recording" else "Record Video",
                onClick = onVideoRecord,
                backgroundColor = if (isRecording) Color.Red else Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun CaptureButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    backgroundColor: Color = Color.White.copy(alpha = 0.3f)
) {
    IconButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(50))
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isEnabled) Color.White else Color.Gray,
            modifier = Modifier.size(32.dp)
        )
    }
}




fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    context: Context
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        }
    )
}

fun savePhotoToCache(context: Context, bitmap: Bitmap, userID: Int): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "JPEG_${timeStamp}_user${userID}"
    val storageDir = context.cacheDir
    val file = File.createTempFile(fileName, ".jpg", storageDir)

    return try {
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        Uri.fromFile(file)
    } catch (e: Exception) {
        Log.e("Camera", "Error saving photo: ", e)
        null
    }
}

fun deleteFile(context: Context, uri: Uri?) {
    val file = File(uri?.path)
    if (file.exists()) {
        file.delete()
        Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun ShowCameraScreen(){
    CameraScreen(
        cameraVM = CameraViewModel(),
        userID = 0,
        onMediaCaptured = {uri, isVideo ->}
    )
}