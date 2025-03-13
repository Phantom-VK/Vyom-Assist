package com.swag.vyom.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swag.vyom.R
import com.swag.vyom.ui.components.FaceDetectionCameraPreview
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.SkyBlue

@Composable
fun FaceAuth(navController: NavHostController) {
    // Get screen dimensions to make UI responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top header card
        RoundedCornerCard(screenHeight)

        // Auth section with face detection
        Column(modifier = Modifier.offset(0.dp, -80.dp)) {
            IntegratedFaceAuthSection(navController)
        }
    }
}

@Composable
fun RoundedCornerCard(screenHeight:Dp) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight / 3),
        shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp),
        colors = CardDefaults.cardColors(containerColor = AppRed)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.attherate),
                contentDescription = "Bank Logo",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Union Bank of India",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Face Authentication",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun IntegratedFaceAuthSection(navController: NavHostController) {
    // States for face detection tasks
    var isLeftTurnCompleted by remember { mutableStateOf(false) }
    var isRightTurnCompleted by remember { mutableStateOf(false) }
    var isFaceDetected by remember { mutableStateOf(false) }

    // Calculate authentication progress
    val authProgress = remember(isLeftTurnCompleted, isRightTurnCompleted, isFaceDetected) {
        var progress = 0f
        if (isFaceDetected) progress += 0.33f
        if (isLeftTurnCompleted) progress += 0.33f
        if (isRightTurnCompleted) progress += 0.34f
        progress
    }

    // Check if authentication is complete
    val isAuthComplete = isLeftTurnCompleted && isRightTurnCompleted

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
                text = if (isFaceDetected) "Face Detected" else "Position Your Face",
                color = if (isFaceDetected) Color.Green else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress indicator
            LinearProgressIndicator(
                progress = authProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = AppRed,
                trackColor = Color.LightGray
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
                        color = if (isAuthComplete) Color.Green else AppRed,
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                FaceDetectionCameraPreview { faceDetected, leftTurn, rightTurn ->
                    isFaceDetected = faceDetected
                    isLeftTurnCompleted = isLeftTurnCompleted || leftTurn
                    isRightTurnCompleted = isRightTurnCompleted || rightTurn
                }

                // Overlay face guide
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                        .border(
                            width = 2.dp,
                            color = if (isFaceDetected) Color.Green.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Instructions and checkboxes
            Text(
                text = "Complete Face Authentication Steps",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = SkyBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isLeftTurnCompleted,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(
                        checkedColor = AppRed,
                        uncheckedColor = Color.Gray
                    )
                )
                Text(
                    text = "Turn Head Left",
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Checkbox(
                    checked = isRightTurnCompleted,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(
                        checkedColor = AppRed,
                        uncheckedColor = Color.Gray
                    )
                )
                Text(
                    text = "Turn Head Right",
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Authentication button
            Button(
                onClick = {
                    if (isAuthComplete) {
                        navController.navigate("home_screen") {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAuthComplete) AppRed else Color.Gray,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                enabled = isAuthComplete
            ) {
                Text(
                    text = if (isAuthComplete) "Authenticate" else "Complete All Steps",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Help section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.attherate),
                    contentDescription = "Support Logo",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Need Help? Write to us at",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "customercare@unionbankofindia.bank",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SkyBlue
                    )
                }
            }
        }
    }
}



