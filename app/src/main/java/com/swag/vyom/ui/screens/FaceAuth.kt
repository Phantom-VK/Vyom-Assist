package com.swag.vyom.ui.screens


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.ui.components.FaceDetectionCameraPreview
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.SkyBlue
import com.swag.vyom.utils.FaceMetrics
import com.swag.vyom.utils.LivenessChallenge
import com.swag.vyom.utils.LivenessDetectionService
import com.swag.vyom.utils.LivenessState
import com.swag.vyom.utils.checkFaceAndSpoof
import com.swag.vyom.viewmodels.AuthViewModel
import com.swag.vyom.viewmodels.CameraViewModel
import com.swag.vyom.viewmodels.TicketViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FaceAuth(navController: NavHostController, authVM: AuthViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Remember liveness detection service
    val livenessService = remember { LivenessDetectionService(context) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }


    // Collect states
    val livenessState by livenessService.livenessState.collectAsState()
    val currentChallenge by livenessService.currentChallenge.collectAsState()

    // Face metrics state
    var isFaceDetected by remember { mutableStateOf(false) }
    var isAuthComplete by remember { mutableStateOf(false) }
    var showCameraScreen by remember { mutableStateOf(false) }
    val cameraVM by lazy { CameraViewModel() }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pf by lazy { SharedPreferencesHelper(context) }
    var isFaceAuthSuccess by remember { mutableStateOf<Boolean?>(null) }

    if (showCameraScreen) {
        Dialog(
            onDismissRequest = {
                showCameraScreen = false
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                CameraScreen(
                    cameraVM = cameraVM,
                    userID = pf.getid()!!,
                ) { uri, isVideo ->

                   capturedBitmap = BitmapFactory.decodeFile(uri.path)



                    val imageUrl = pf.getUserImageLink()
                    if (imageUrl != null) {
                        capturedBitmap?.let { it ->
                            authVM.faceAuth(it, imageUrl){ isMatch ->
                                if (isMatch) {
                                    Toast.makeText(context, "Face Matched", Toast.LENGTH_LONG).show()
                                    Log.d("FaceAuth", "Face matched successfully")
                                    navController.navigate("home_screen") {
                                        popUpTo("splash_screen") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "Face Not Matched", Toast.LENGTH_LONG).show()
                                    Log.e("FaceAuth", "Face not matched")
                                    capturedImageUri = null
                                    cameraVM.clearPhoto()
                                }
                                showCameraScreen = false
                            }
                        }
                    } else {
                        Toast.makeText(context, "User Image not Found", Toast.LENGTH_LONG).show()
                        showCameraScreen = false
                    }
                }
            }
        }
    }

    // Success state handler
    LaunchedEffect(livenessState) {
        if (livenessState is LivenessState.Success) {
            isAuthComplete = true
        }
    }

    // Start liveness detection on first composition
    LaunchedEffect(Unit) {
        livenessService.startLivenessDetection()
    }

    // Challenge instructions
    val getChallengeText = { challenge: LivenessChallenge? ->
        when (challenge) {
            LivenessChallenge.TURN_LEFT -> "Please turn your head to the right"
            LivenessChallenge.TURN_RIGHT -> "Please turn your head to the left"
            LivenessChallenge.SMILE -> "Smile for the camera!"
            null -> "Preparing verification system..."
        }
    }

    // UI Progress calculation
    val getProgressValue = {
        when (livenessState) {
            is LivenessState.Initializing -> 0.1f
            is LivenessState.ChallengeInProgress -> {
                val state = livenessState as LivenessState.ChallengeInProgress
                (state.currentIndex + 1).toFloat() / (state.challenges.size + 1).toFloat()
            }

            is LivenessState.Success -> 1f
            is LivenessState.Failed -> 0.1f
        }
    }

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
            // Status indicator with clear instructions
            Text(
                text = when {
                    !isFaceDetected -> "Position Your Face in Frame"
                    isAuthComplete -> "Liveliness check Successful!"
                    livenessState is LivenessState.Failed -> "Authentication Failed"
                    else -> getChallengeText(currentChallenge)
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
                progress = { getProgressValue() },
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
                FaceDetectionCameraPreview { faceDetected, leftTurn, rightTurn, _, smileDetected, faceDistance ->
                    isFaceDetected = faceDetected

                    // Process face metrics - now using smile detection instead of blink
                    if (faceDetected) {
                        livenessService.processFaceMetrics(
                            FaceMetrics(
                                faceDetected = faceDetected,
                                headEulerAngleY = if (leftTurn) -20f else if (rightTurn) 20f else 0f,
                                smileProbability = if(smileDetected) 0.8f else 0.2f,
                                faceDistance = faceDistance
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Challenge progress display with better visual feedback
            if (livenessState is LivenessState.ChallengeInProgress) {
                val state = livenessState as LivenessState.ChallengeInProgress
                Text(
                    text = "Challenge ${state.currentIndex + 1} of ${state.challenges.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SkyBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Visual indicators for completed challenges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    state.challenges.forEachIndexed { index, challenge ->
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

            Spacer(modifier = Modifier.height(20.dp))

// Authentication button with clearer states
            Button(
                onClick = {

                    showCameraScreen = true
//                    if (isAuthComplete) {
//
//
//
//
//                    } else if (livenessState is LivenessState.Failed) {
//                        coroutineScope.launch {
//                            livenessService.reset()
//                            delay(500)
//                            livenessService.startLivenessDetection()
//                        }
//                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isAuthComplete -> AppRed
                        livenessState is LivenessState.Failed -> Color.Gray
                        else -> Color.Gray
                    },
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                enabled =true
//                isAuthComplete || livenessState is LivenessState.Failed
            ) {
                Text(
                    text = when {
                        isAuthComplete -> "Continue"
                        livenessState is LivenessState.Failed -> "Try Again"
                        else -> "Complete All Challenges"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Add helpful instruction text
            if (!isAuthComplete && livenessState !is LivenessState.Failed) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Follow the instructions to verify liveliness check",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}