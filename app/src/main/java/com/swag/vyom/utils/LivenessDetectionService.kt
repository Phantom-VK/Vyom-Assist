package com.swag.vyom.utils


import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class LivenessDetectionService(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val _livenessState = MutableStateFlow<LivenessState>(LivenessState.Initializing)
    val livenessState = _livenessState.asStateFlow()
    private val _currentChallenge = MutableStateFlow<LivenessChallenge?>(null)
    val currentChallenge = _currentChallenge.asStateFlow()
    private val faceMetricsHistory = mutableListOf<FaceMetrics>()

    private var validationCounter = 0
    private val VALIDATION_THRESHOLD = 2 // Frames required to confirm an action

    private var blinkCounter = 0
    private var eyeOpenCounter = 0
    private val BLINK_HOLD_THRESHOLD = 2 // Number of frames eyes must stay closed
    private val EYE_OPEN_RESET_THRESHOLD = 1 // Ensure eyes open before next challenge

    fun startLivenessDetection() {
        _livenessState.value = LivenessState.Initializing
        faceMetricsHistory.clear()
        validationCounter = 0
        val selectedChallenges = listOf(
            LivenessChallenge.TURN_LEFT,
            LivenessChallenge.TURN_RIGHT,
            LivenessChallenge.BLINK
        )
        scope.launch {
            delay(1000)
            _livenessState.value = LivenessState.ChallengeInProgress(0, selectedChallenges)
            _currentChallenge.value = selectedChallenges.first()
        }
    }

    fun processFaceMetrics(metrics: FaceMetrics) {
        if (!metrics.faceDetected) {
            validationCounter = 0
            blinkCounter = 0
            eyeOpenCounter = 0
            return
        }
        faceMetricsHistory.add(metrics)
        if (faceMetricsHistory.size > 10) faceMetricsHistory.removeAt(0)

        val currentState = _livenessState.value
        if (currentState is LivenessState.ChallengeInProgress) {
            val currentIndex = currentState.currentIndex
            val currentChallenge = currentState.challenges[currentIndex]
            val completed = when (currentChallenge) {
                LivenessChallenge.TURN_LEFT -> metrics.headEulerAngleY < -15
                LivenessChallenge.TURN_RIGHT -> metrics.headEulerAngleY > 15
                LivenessChallenge.BLINK -> {
                    if (metrics.blinkProbability < 0.3f) {
                        blinkCounter++ // Increase blink counter if eyes are closed
                        eyeOpenCounter = 0 // Reset eye-open counter
                    } else {
                        eyeOpenCounter++ // Increase counter if eyes are open
                    }

                    // Confirm blink only if eyes were closed for enough frames and then reopened
                    blinkCounter >= BLINK_HOLD_THRESHOLD && eyeOpenCounter >= EYE_OPEN_RESET_THRESHOLD
                }
            }

            if (completed) {
                validationCounter++
                if (validationCounter >= VALIDATION_THRESHOLD) {
                    validationCounter = 0
                    blinkCounter = 0
                    eyeOpenCounter = 0
                    val nextIndex = currentIndex + 1
                    if (nextIndex < currentState.challenges.size) {
                        _livenessState.value = LivenessState.ChallengeInProgress(nextIndex, currentState.challenges)
                        _currentChallenge.value = currentState.challenges[nextIndex]
                    } else {
                        _livenessState.value = if (checkFaceConsistency()) LivenessState.Success else LivenessState.Failed("Spoofing detected")
                    }
                }
            } else {
                validationCounter = 0
            }
        }
    }

    private fun checkFaceConsistency(): Boolean {
        if (faceMetricsHistory.size < 5) return false
        val distances = faceMetricsHistory.map { it.faceDistance }
        val avgDistance = distances.average()
        val distanceVariance = distances.map { abs(it - avgDistance) }.average()
        return distanceVariance in 0.05f..0.2f
    }

    fun reset() {
        _livenessState.value = LivenessState.Initializing
        _currentChallenge.value = null
        faceMetricsHistory.clear()
        validationCounter = 0
    }
}

data class FaceMetrics(
    val faceDetected: Boolean,
    val headEulerAngleY: Float,
    val blinkProbability: Float,
    val faceDistance: Float
)

enum class LivenessChallenge {
    TURN_LEFT, TURN_RIGHT, BLINK
}

sealed class LivenessState {
    object Initializing : LivenessState()
    data class ChallengeInProgress(val currentIndex: Int, val challenges: List<LivenessChallenge>) : LivenessState()
    object Success : LivenessState()
    data class Failed(val reason: String) : LivenessState()
}