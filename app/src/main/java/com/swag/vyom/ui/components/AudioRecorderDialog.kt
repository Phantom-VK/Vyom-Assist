package com.swag.vyom.ui.components

import android.R.attr.enabled
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.swag.vyom.R
import com.swag.vyom.audioPlayer.AndroidAudioPlayer
import com.swag.vyom.audioRecorder.AndroidAudioRecorder


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AudioRecorderDialog(
    userID: Int?,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit // Callback with recorded file path
) {
    val context = LocalContext.current


    var audioFilePath by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    val mediaRecorder by lazy { AndroidAudioRecorder(context) }
    val mediaPlayer by lazy { AndroidAudioPlayer(context) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Audio Recorder", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly

                ){
                    // Record Button
                    IconButton(
                        onClick = {
                            if (!isRecording) {
                                audioFilePath = "${context.cacheDir.absolutePath}/recorded_audio${userID}.mp3"
                                mediaRecorder.startRecording(audioFilePath)
                                isRecording = true
                            }
                        },
                        enabled = !isRecording
                    ) {
                        if (!isRecording) {
                            Icon(
                                painter = painterResource(id = R.drawable.record_icon),
                                contentDescription = "Record"
                            )
                        }else{
                            Icon(
                                painter = painterResource(id = R.drawable.recording_icon),
                                contentDescription = "Recording"
                            )
                        }
                    }

                    // Stop Recording
                    IconButton(
                        onClick = {
                            if (isRecording) {
                                mediaRecorder.stopRecording()
                                isRecording = false
                            }
                        },
                        enabled = isRecording
                    ) {
                        Icon(painter = painterResource(id = R.drawable.stop_icon), contentDescription = "Stop Recording")
                    }

                    // Play Audio
                    IconButton(
                        onClick = {
                            if (!isPlaying && audioFilePath.isNotEmpty()) {
                                mediaPlayer.playFile(audioFilePath) {
                                    isPlaying = false
                                }
                                isPlaying = true
                            }
                        },
                        enabled = !isPlaying && audioFilePath.isNotEmpty()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play Audio")
                    }

                    // Delete Audio
                    IconButton(
                        onClick = {
                            mediaPlayer.deleteAudioFile(audioFilePath)
                            audioFilePath = ""
                        },
                        enabled = audioFilePath.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Audio")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (audioFilePath.isNotEmpty()) {
                            onSubmit(audioFilePath)
                            onDismiss()
                            isRecording = false
                        }
                    },
                    enabled = audioFilePath.isNotEmpty()
                ) {
                    Text("Submit")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun PreviewRecorderDialog(){
    AudioRecorderDialog(userID = 1, onDismiss = { /*TODO*/ }, onSubmit = { /*TODO*/ })
}