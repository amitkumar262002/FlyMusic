package com.example.flymusicai.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

/**
 * 🎤 Custom Speech Recognizer with Visual Feedback
 * Bypasses Google's default UI for a fully custom experience
 */
class CustomSpeechRecognizer(private val context: Context) {

    companion object {
        private const val TAG = "CustomSpeechRecognizer"
    }

    private var speechRecognizer: SpeechRecognizer? = null

    // State flows for UI updates
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText

    private val _soundLevel = MutableStateFlow(0f)
    val soundLevel: StateFlow<Float> = _soundLevel

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _partialResults = MutableStateFlow("")
    val partialResults: StateFlow<String> = _partialResults

    init {
        createSpeechRecognizer()
    }

    private fun createSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(recognitionListener)
            }
            Log.d(TAG, "✅ Speech recognizer created successfully")
        } else {
            Log.e(TAG, "❌ Speech recognition not available on this device")
            _error.value = "Speech recognition not available"
        }
    }

    fun startListening() {
        _isListening.value = true
        _recognizedText.value = ""
        _partialResults.value = ""
        _error.value = null

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }

        try {
            speechRecognizer?.startListening(intent)
            Log.d(TAG, "🎤 Started listening...")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start listening", e)
            _error.value = "Failed to start voice recognition"
            _isListening.value = false
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
        Log.d(TAG, "⏹️ Stopped listening")
    }

    fun cancel() {
        speechRecognizer?.cancel()
        _isListening.value = false
        _recognizedText.value = ""
        _partialResults.value = ""
        Log.d(TAG, "❌ Cancelled")
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        Log.d(TAG, "🔌 Destroyed")
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "✅ Ready for speech")
            _isListening.value = true
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "🎤 Speech started")
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Convert RMS dB to 0-1 range for visualizer
            // RMS typically ranges from 0 to 10
            val normalized = (rmsdB / 10f).coerceIn(0f, 1f)
            _soundLevel.value = normalized
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            // Not used
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "⏸️ Speech ended")
            _isListening.value = false
        }

        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            Log.e(TAG, "❌ Error: $errorMessage (code: $error)")
            _error.value = errorMessage
            _isListening.value = false
            _soundLevel.value = 0f
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val topResult = matches[0]
                Log.d(TAG, "✅ Results: $topResult")
                _recognizedText.value = topResult
            }
            _isListening.value = false
            _soundLevel.value = 0f
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val partial = matches[0]
                Log.d(TAG, "📝 Partial: $partial")
                _partialResults.value = partial
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            // Not used
        }
    }
}
