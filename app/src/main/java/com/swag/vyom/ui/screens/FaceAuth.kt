package com.swag.vyom.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.telecom.VideoProfile.isVideo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.ui.components.FaceDetectionCameraPreview
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.SkyBlue
import com.swag.vyom.utils.FaceMetrics
import com.swag.vyom.utils.LivenessChallenge
import com.swag.vyom.utils.LivenessDetectionService
import com.swag.vyom.utils.LivenessState
import com.swag.vyom.viewmodels.AuthViewModel
import com.swag.vyom.viewmodels.CameraViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun FaceAuth(navController: NavHostController, authVM: AuthViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val sharedPreferencesHelper = remember { SharedPreferencesHelper(context) }

    // Liveness detection service
    val livenessService = remember { LivenessDetectionService(context) }
    val livenessState by livenessService.livenessState.collectAsState()
    val currentChallenge by livenessService.currentChallenge.collectAsState()

    // State variables
    var isFaceDetected by remember { mutableStateOf(false) }
    var isAuthComplete by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var showCameraScreen by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Start liveness detection on first composition
    LaunchedEffect(Unit) {
        livenessService.startLivenessDetection()
    }

    // Handle liveness check states
    LaunchedEffect(livenessState) {
        when (livenessState) {
            is LivenessState.Failed -> {
                delay(500)
                livenessService.reset()
                livenessService.startLivenessDetection()
            }
            is LivenessState.Success -> {
                isAuthComplete = true
            }
            else -> {}
        }
    }

    // Handle captured image for face authentication
    LaunchedEffect(capturedImageUri) {
        capturedImageUri?.let { uri ->
            isProcessing = true
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                } ?: throw Exception("Failed to decode image")

                val storedImageUrl = sharedPreferencesHelper.getUserImageLink() ?: ""
                if (storedImageUrl.isEmpty()) {
                    Toast.makeText(context, "No registered face found", Toast.LENGTH_SHORT).show()
                    return@let
                }

                authVM.faceAuth(bitmap, storedImageUrl) { success ->
                    isProcessing = false
                    if (success) {
                        navController.navigate("home_screen") {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Face authentication failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                isProcessing = false
                Toast.makeText(
                    context,
                    "Error processing image: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Show camera dialog when needed
    if (showCameraScreen) {
        Dialog(onDismissRequest = { showCameraScreen = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = Color.White
            ) {
                CameraScreen(
                    cameraVM = CameraViewModel(),
                    userID = sharedPreferencesHelper.getid() ?: 0,
                    onMediaCaptured = { uri, isVideo ->
                        if (!isVideo) {
                            capturedImageUri = uri
                            showCameraScreen = false
                        }
                    }
                )
            }
        }
    }

    // Show loading screen while processing
    if (isProcessing) {
        Dialog(onDismissRequest = { /* Prevent dismissal */ }) {
            Surface(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = AppRed,
                        strokeWidth = 6.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Verifying Face...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Main UI Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status indicator
            Text(
                text = when {
                    !isFaceDetected -> "Position Your Face in Frame"
                    isAuthComplete -> "Liveness Check Complete!"
                    livenessState is LivenessState.Failed -> "Authentication Failed"
                    else -> currentChallenge?.let { challenge ->
                        when (challenge) {
                            LivenessChallenge.TURN_LEFT -> "Please turn your head to the right"
                            LivenessChallenge.TURN_RIGHT -> "Please turn your head to the left"
                        }
                    } ?: "Preparing verification system..."
                },
                color = when {
                    !isFaceDetected -> Color.Gray
                    isAuthComplete -> Color.Green
                    livenessState is LivenessState.Failed -> Color.Red
                    else -> AppRed
                },
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress indicator
            LinearProgressIndicator(
                progress = {
                    when (livenessState) {
                        is LivenessState.Initializing -> 0.1f
                        is LivenessState.ChallengeInProgress -> {
                            val state = livenessState as LivenessState.ChallengeInProgress
                            (state.currentIndex + 1).toFloat() / (state.challenges.size + 1).toFloat()
                        }
                        is LivenessState.Success -> 1f
                        is LivenessState.Failed -> 0f
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = AppRed,
                trackColor = Color.LightGray,
                strokeCap = StrokeCap.Round,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Camera preview with face detection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 2.dp,
                        color = when {
                            isAuthComplete -> Color.Green
                            livenessState is LivenessState.Failed -> Color.Red
                            else -> AppRed
                        },
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                FaceDetectionCameraPreview { faceDetected, leftTurn, rightTurn, _, _, faceDistance ->
                    isFaceDetected = faceDetected
                    if (faceDetected) {
                        livenessService.processFaceMetrics(
                            FaceMetrics(
                                faceDetected = faceDetected,
                                headEulerAngleY = if (leftTurn) -20f else if (rightTurn) 20f else 0f,
                                faceDistance = faceDistance
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            if (isAuthComplete) {
                Button(
                    onClick = { showCameraScreen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Authenticate Face")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Debug button - skip face auth
                Button(
                    onClick = {
                        navController.navigate("home_screen") {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Skip Face Auth (Debug)")
                }
            } else if (livenessState is LivenessState.Failed) {
                Button(
                    onClick = {
                        livenessService.reset()
                        livenessService.startLivenessDetection()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Retry Liveness Check")
                }
            }

            // Challenge progress display
            if (livenessState is LivenessState.ChallengeInProgress) {
                val state = livenessState as LivenessState.ChallengeInProgress
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Challenge ${state.currentIndex + 1} of ${state.challenges.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SkyBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    state.challenges.forEachIndexed { index, _ ->
                        val isCompleted = index < state.currentIndex
                        val isCurrent = index == state.currentIndex

                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> Color.Green
                                        isCurrent -> AppRed
                                        else -> Color.LightGray
                                    }
                                )
                        )
                        if (index < state.challenges.size - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }
}