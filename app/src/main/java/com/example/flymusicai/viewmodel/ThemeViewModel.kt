package com.example.flymusicai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flymusicai.datastore.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** 🎨 Enhanced ViewModel for managing app theme and preferences */
class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application.applicationContext)

    // ========== Existing States ==========

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _aiPersonalizationEnabled = MutableStateFlow(true)
    val aiPersonalizationEnabled: StateFlow<Boolean> = _aiPersonalizationEnabled.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    // ========== New Advanced States ==========

    private val _autoPlayEnabled = MutableStateFlow(true)
    val autoPlayEnabled: StateFlow<Boolean> = _autoPlayEnabled.asStateFlow()

    private val _highQualityAudioEnabled = MutableStateFlow(false)
    val highQualityAudioEnabled: StateFlow<Boolean> = _highQualityAudioEnabled.asStateFlow()

    private val _downloadWifiOnly = MutableStateFlow(true)
    val downloadWifiOnly: StateFlow<Boolean> = _downloadWifiOnly.asStateFlow()

    private val _crossfadeEnabled = MutableStateFlow(false)
    val crossfadeEnabled: StateFlow<Boolean> = _crossfadeEnabled.asStateFlow()

    private val _crossfadeDuration = MutableStateFlow(5)
    val crossfadeDuration: StateFlow<Int> = _crossfadeDuration.asStateFlow()

    private val _audioQuality = MutableStateFlow("Normal")
    val audioQuality: StateFlow<String> = _audioQuality.asStateFlow()

    private val _equalizerPreset = MutableStateFlow("Flat")
    val equalizerPreset: StateFlow<String> = _equalizerPreset.asStateFlow()

    private val _sleepTimer = MutableStateFlow(0)
    val sleepTimer: StateFlow<Int> = _sleepTimer.asStateFlow()

    private val _lyricsEnabled = MutableStateFlow(true)
    val lyricsEnabled: StateFlow<Boolean> = _lyricsEnabled.asStateFlow()

    private val _offlineModeEnabled = MutableStateFlow(false)
    val offlineModeEnabled: StateFlow<Boolean> = _offlineModeEnabled.asStateFlow()

    // 🎛️ Equalizer States
    private val _equalizerEnabled = MutableStateFlow(false)
    val equalizerEnabled: StateFlow<Boolean> = _equalizerEnabled.asStateFlow()

    private val _bassBoost = MutableStateFlow(0)
    val bassBoost: StateFlow<Int> = _bassBoost.asStateFlow()

    private val _virtualizer = MutableStateFlow(0)
    val virtualizer: StateFlow<Int> = _virtualizer.asStateFlow()

    private val _reverb = MutableStateFlow("None")
    val reverb: StateFlow<String> = _reverb.asStateFlow()

    init {
        loadPreferences()
    }

    /** Load all preferences from DataStore */
    private fun loadPreferences() {
        // Existing preferences
        viewModelScope.launch {
            preferencesManager.darkModeFlow.collect { darkMode -> _isDarkMode.value = darkMode }
        }

        viewModelScope.launch {
            preferencesManager.aiPersonalizationFlow.collect { enabled ->
                _aiPersonalizationEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.notificationsFlow.collect { enabled ->
                _notificationsEnabled.value = enabled
            }
        }

        // New preferences
        viewModelScope.launch {
            preferencesManager.autoPlayFlow.collect { enabled -> _autoPlayEnabled.value = enabled }
        }

        viewModelScope.launch {
            preferencesManager.highQualityAudioFlow.collect { enabled ->
                _highQualityAudioEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.downloadWifiOnlyFlow.collect { enabled ->
                _downloadWifiOnly.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.crossfadeEnabledFlow.collect { enabled ->
                _crossfadeEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.crossfadeDurationFlow.collect { duration ->
                _crossfadeDuration.value = duration
            }
        }

        viewModelScope.launch {
            preferencesManager.audioQualityFlow.collect { quality -> _audioQuality.value = quality }
        }

        viewModelScope.launch {
            preferencesManager.equalizerPresetFlow.collect { preset ->
                _equalizerPreset.value = preset
            }
        }

        viewModelScope.launch {
            preferencesManager.sleepTimerFlow.collect { timer -> _sleepTimer.value = timer }
        }

        viewModelScope.launch {
            preferencesManager.lyricsEnabledFlow.collect { enabled ->
                _lyricsEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.offlineModeFlow.collect { enabled ->
                _offlineModeEnabled.value = enabled
            }
        }

        // Equalizer preferences
        viewModelScope.launch {
            preferencesManager.equalizerEnabledFlow.collect { enabled ->
                _equalizerEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.bassBoostFlow.collect { level -> _bassBoost.value = level }
        }

        viewModelScope.launch {
            preferencesManager.virtualizerFlow.collect { level -> _virtualizer.value = level }
        }

        viewModelScope.launch {
            preferencesManager.reverbFlow.collect { preset -> _reverb.value = preset }
        }
    }

    // ========== Existing Toggle Functions ==========

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_isDarkMode.value
            preferencesManager.setDarkMode(newValue)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setDarkMode(enabled) }
    }

    fun toggleAIPersonalization() {
        viewModelScope.launch {
            val newValue = !_aiPersonalizationEnabled.value
            preferencesManager.setAIPersonalization(newValue)
        }
    }

    fun toggleNotifications() {
        viewModelScope.launch {
            val newValue = !_notificationsEnabled.value
            preferencesManager.setNotifications(newValue)
        }
    }

    // ========== New Toggle Functions ==========

    fun toggleAutoPlay() {
        viewModelScope.launch {
            val newValue = !_autoPlayEnabled.value
            preferencesManager.setAutoPlay(newValue)
        }
    }

    fun toggleHighQualityAudio() {
        viewModelScope.launch {
            val newValue = !_highQualityAudioEnabled.value
            preferencesManager.setHighQualityAudio(newValue)
        }
    }

    fun toggleDownloadWifiOnly() {
        viewModelScope.launch {
            val newValue = !_downloadWifiOnly.value
            preferencesManager.setDownloadWifiOnly(newValue)
        }
    }

    fun toggleCrossfade() {
        viewModelScope.launch {
            val newValue = !_crossfadeEnabled.value
            preferencesManager.setCrossfadeEnabled(newValue)
        }
    }

    fun setCrossfadeDuration(seconds: Int) {
        viewModelScope.launch { preferencesManager.setCrossfadeDuration(seconds) }
    }

    fun setAudioQuality(quality: String) {
        viewModelScope.launch { preferencesManager.setAudioQuality(quality) }
    }

    fun setEqualizerPreset(preset: String) {
        viewModelScope.launch { preferencesManager.setEqualizerPreset(preset) }
    }

    fun setSleepTimer(minutes: Int) {
        viewModelScope.launch { preferencesManager.setSleepTimer(minutes) }
    }

    fun toggleLyrics() {
        viewModelScope.launch {
            val newValue = !_lyricsEnabled.value
            preferencesManager.setLyricsEnabled(newValue)
        }
    }

    fun toggleOfflineMode() {
        viewModelScope.launch {
            val newValue = !_offlineModeEnabled.value
            preferencesManager.setOfflineMode(newValue)
        }
    }

    // 🎛️ Equalizer Functions
    fun toggleEqualizer() {
        viewModelScope.launch {
            val newValue = !_equalizerEnabled.value
            preferencesManager.setEqualizerEnabled(newValue)
        }
    }

    fun setBassBoost(level: Int) {
        viewModelScope.launch { preferencesManager.setBassBoost(level) }
    }

    fun setVirtualizer(level: Int) {
        viewModelScope.launch { preferencesManager.setVirtualizer(level) }
    }

    fun setReverb(preset: String) {
        viewModelScope.launch { preferencesManager.setReverb(preset) }
    }
}
