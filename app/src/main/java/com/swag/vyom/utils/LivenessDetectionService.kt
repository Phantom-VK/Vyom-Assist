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
    private val VALIDATION_THRESHOLD = 2 // More forgiving validation frames
    private val BUFFER_FRAMES = 2 // Allow buffer frames before resetting counter
    private var bufferCounter = 0

    private val challenges = listOf(LivenessChallenge.TURN_LEFT, LivenessChallenge.TURN_RIGHT)

    fun startLivenessDetection() {
        _livenessState.value = LivenessState.Initializing
        faceMetricsHistory.clear()
        validationCounter = 0
        bufferCounter = 0

        scope.launch {
            delay(1000)
            _livenessState.value = LivenessState.ChallengeInProgress(0, challenges)
            _currentChallenge.value = challenges.first()
        }
    }

    fun processFaceMetrics(metrics: FaceMetrics) {
        if (!metrics.faceDetected) {
            validationCounter = 0
            bufferCounter = 0
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
            }

            if (completed) {
                validationCounter++
                bufferCounter = 0 // Reset buffer when action is done
                if (validationCounter >= VALIDATION_THRESHOLD) {
                    moveToNextChallenge(currentState)
                }
            } else {
                bufferCounter++
                if (bufferCounter > BUFFER_FRAMES) {
                    validationCounter = 0 // Reset only after buffer frames
                    bufferCounter = 0
                }
            }
        }
    }

    private fun moveToNextChallenge(currentState: LivenessState.ChallengeInProgress) {
        validationCounter = 0
        bufferCounter = 0

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

        val distances = faceMetricsHistory.map { it.faceDistance }
        val avgDistance = distances.average()
        val distanceVariance = distances.map { abs(it - avgDistance) }.average()

        return distanceVariance in 0.03f..0.3f // Slightly relaxed threshold
    }

    fun reset() {
        _livenessState.value = LivenessState.Initializing
        _currentChallenge.value = null
        faceMetricsHistory.clear()
        validationCounter = 0
        bufferCounter = 0
        scope.coroutineContext.cancelChildren()
    }
}

data class FaceMetrics(
    val faceDetected: Boolean,
    val headEulerAngleY: Float,
    val faceDistance: Float
)

enum class LivenessChallenge {
    TURN_LEFT, TURN_RIGHT
}

sealed class LivenessState {
    object Initializing : LivenessState()
    data class ChallengeInProgress(val currentIndex: Int, val challenges: List<LivenessChallenge>) : LivenessState()
    object Success : LivenessState()
    data class Failed(val reason: String) : LivenessState()
}
