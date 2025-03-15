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

    private var smileCounter = 0
    private var neutralFaceCounter = 0
    private val SMILE_HOLD_THRESHOLD = 2 // Number of frames smile must be maintained
    private val NEUTRAL_FACE_RESET_THRESHOLD = 0 // Ensure neutral face before next challenge

    fun startLivenessDetection() {
        _livenessState.value = LivenessState.Initializing
        faceMetricsHistory.clear()
        validationCounter = 0
        smileCounter = 0
        neutralFaceCounter = 0

        // Get all available challenges
        val allChallenges = LivenessChallenge.entries

        // Randomly select and shuffle challenges
        val selectedChallenges = allChallenges.shuffled().take(3)

        scope.launch {
            delay(1000)
            _livenessState.value = LivenessState.ChallengeInProgress(0, selectedChallenges)
            _currentChallenge.value = selectedChallenges.first()
        }
    }

    fun processFaceMetrics(metrics: FaceMetrics) {
        if (!metrics.faceDetected) {
            validationCounter = 0
            smileCounter = 0
            neutralFaceCounter = 0
            return
        }

        // Keep history for consistency check
        faceMetricsHistory.add(metrics)
        if (faceMetricsHistory.size > 10) faceMetricsHistory.removeAt(0)

        val currentState = _livenessState.value
        if (currentState is LivenessState.ChallengeInProgress) {
            val currentIndex = currentState.currentIndex
            val currentChallenge = currentState.challenges[currentIndex]

            val completed = when (currentChallenge) {
                LivenessChallenge.TURN_LEFT -> metrics.headEulerAngleY < -15
                LivenessChallenge.TURN_RIGHT -> metrics.headEulerAngleY > 15
                LivenessChallenge.SMILE -> {
                    if (metrics.smileProbability > 0.7f) {
                        smileCounter++ // Increase smile counter if smiling
                        neutralFaceCounter = 0 // Reset neutral face counter
                    } else {
                        neutralFaceCounter++ // Increase counter if not smiling
                        if (smileCounter > 0) smileCounter-- // Gradually decrease smile counter
                    }

                    // Confirm smile only if maintained for enough frames
                    smileCounter >= SMILE_HOLD_THRESHOLD && neutralFaceCounter >= NEUTRAL_FACE_RESET_THRESHOLD
                }
            }

            if (completed) {
                validationCounter++
                if (validationCounter >= VALIDATION_THRESHOLD) {
                    moveToNextChallenge(currentState)
                }
            } else {
                validationCounter = max(0, validationCounter - 1) // Gracefully decrease counter
            }
        }
    }

    private fun moveToNextChallenge(currentState: LivenessState.ChallengeInProgress) {
        validationCounter = 0
        smileCounter = 0
        neutralFaceCounter = 0

        val nextIndex = currentState.currentIndex + 1
        if (nextIndex < currentState.challenges.size) {
            _livenessState.value = LivenessState.ChallengeInProgress(nextIndex, currentState.challenges)
            _currentChallenge.value = currentState.challenges[nextIndex]
        } else {
            _livenessState.value = if (checkFaceConsistency()) {
                LivenessState.Success
            } else {
                LivenessState.Failed("Verification failed - inconsistent face metrics")
            }
        }
    }

    private fun checkFaceConsistency(): Boolean {
        if (faceMetricsHistory.size < 5) return false

        // Check distance consistency
        val distances = faceMetricsHistory.map { it.faceDistance }
        val avgDistance = distances.average()
        val distanceVariance = distances.map { abs(it - avgDistance) }.average()

        // More advanced consistency check
        val consistentDistance = distanceVariance in 0.05f..0.25f

        // Add additional checks if needed
        return consistentDistance
    }

    fun reset() {
        _livenessState.value = LivenessState.Initializing
        _currentChallenge.value = null
        faceMetricsHistory.clear()
        validationCounter = 0
        smileCounter = 0
        neutralFaceCounter = 0

        // Cancel any ongoing jobs
        scope.coroutineContext.cancelChildren()
    }

    // Helper function to avoid negative values
    private fun max(a: Int, b: Int): Int = if (a > b) a else b
}

data class FaceMetrics(
    val faceDetected: Boolean,
    val headEulerAngleY: Float,
    val smileProbability: Float = 0f,
    val faceDistance: Float
)

enum class LivenessChallenge {
    TURN_LEFT, TURN_RIGHT, SMILE
}

sealed class LivenessState {
    object Initializing : LivenessState()
    data class ChallengeInProgress(val currentIndex: Int, val challenges: List<LivenessChallenge>) : LivenessState()
    object Success : LivenessState()
    data class Failed(val reason: String) : LivenessState()
}