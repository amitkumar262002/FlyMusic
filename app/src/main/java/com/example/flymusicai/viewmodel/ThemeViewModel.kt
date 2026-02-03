package com.example.flymusicai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flymusicai.datastore.PreferencesManager
import kotlinx.coroutines.delay
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

    // 🌗 Advanced Theme State - Forced to Dark
    private val _effectiveDarkMode = MutableStateFlow(true)
    val effectiveDarkMode: StateFlow<Boolean> = _effectiveDarkMode.asStateFlow()

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

    private val _explicitContentEnabled = MutableStateFlow(true)
    val explicitContentEnabled: StateFlow<Boolean> = _explicitContentEnabled.asStateFlow()

    private val _annotationsEnabled = MutableStateFlow(true)
    val annotationsEnabled: StateFlow<Boolean> = _annotationsEnabled.asStateFlow()

    private val _showAds = MutableStateFlow(true)
    val showAds: StateFlow<Boolean> = _showAds.asStateFlow()

    private val _videoPlaybackEnabled = MutableStateFlow(true)
    val videoPlaybackEnabled: StateFlow<Boolean> = _videoPlaybackEnabled.asStateFlow()

    private val _mobileNotificationsEnabled = MutableStateFlow(true)
    val mobileNotificationsEnabled: StateFlow<Boolean> = _mobileNotificationsEnabled.asStateFlow()

    private val _emailNotificationsEnabled = MutableStateFlow(false)
    val emailNotificationsEnabled: StateFlow<Boolean> = _emailNotificationsEnabled.asStateFlow()

    private val _musicLanguages = MutableStateFlow("Hindi, English")
    val musicLanguages: StateFlow<String> = _musicLanguages.asStateFlow()

    private val _displayLanguage = MutableStateFlow("English")
    val displayLanguage: StateFlow<String> = _displayLanguage.asStateFlow()

    private val _appTheme = MutableStateFlow("System Default")
    val appTheme: StateFlow<String> = _appTheme.asStateFlow()

    // 🚀 Extra Advanced Features
    private val _dataSaverEnabled = MutableStateFlow(false)
    val dataSaverEnabled: StateFlow<Boolean> = _dataSaverEnabled.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _sleepTimerRunning = MutableStateFlow(false)
    val sleepTimerRunning: StateFlow<Boolean> = _sleepTimerRunning.asStateFlow()

    private val _sleepTimerRemaining = MutableStateFlow(0)
    val sleepTimerRemaining: StateFlow<Int> = _sleepTimerRemaining.asStateFlow()

    // 🎛️ Equalizer States
    private val _equalizerEnabled = MutableStateFlow(false)
    val equalizerEnabled: StateFlow<Boolean> = _equalizerEnabled.asStateFlow()

    private val _bassBoost = MutableStateFlow(0)
    val bassBoost: StateFlow<Int> = _bassBoost.asStateFlow()

    private val _virtualizer = MutableStateFlow(0)
    val virtualizer: StateFlow<Int> = _virtualizer.asStateFlow()

    private val _loudness = MutableStateFlow(0)
    val loudness: StateFlow<Int> = _loudness.asStateFlow()

    private val _recentlyPlayedJson = MutableStateFlow("[]")
    val recentlyPlayedJson: StateFlow<String> = _recentlyPlayedJson.asStateFlow()

    private val _reverb = MutableStateFlow("None")
    val reverb: StateFlow<String> = _reverb.asStateFlow()

    private val _eqBands = MutableStateFlow(listOf(0f, 0f, 0f, 0f, 0f))
    val eqBands: StateFlow<List<Float>> = _eqBands.asStateFlow()

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

        viewModelScope.launch {
            preferencesManager.explicitContentEnabledFlow.collect { enabled ->
                _explicitContentEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.annotationsEnabledFlow.collect { enabled ->
                _annotationsEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.showAdsFlow.collect { enabled -> _showAds.value = enabled }
        }

        viewModelScope.launch {
            preferencesManager.videoPlaybackEnabledFlow.collect { enabled ->
                _videoPlaybackEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.mobileNotificationsFlow.collect { enabled ->
                _mobileNotificationsEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.emailNotificationsFlow.collect { enabled ->
                _emailNotificationsEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesManager.musicLanguagesFlow.collect { languages ->
                _musicLanguages.value = languages
            }
        }

        viewModelScope.launch {
            preferencesManager.displayLanguageFlow.collect { language ->
                _displayLanguage.value = language
            }
        }

        viewModelScope.launch {
            preferencesManager.appThemeFlow.collect { theme ->
                _appTheme.value = theme
                updateEffectiveDarkMode()
            }
        }
        
        // Sync system black mode changes
        viewModelScope.launch {
            preferencesManager.darkModeFlow.collect { _ ->
                updateEffectiveDarkMode()
            }
        }

        loadEqualizerPreferences()
    }

    /** Update actual dark mode state based on selection and system */
    private fun updateEffectiveDarkMode() {
        _effectiveDarkMode.value = true
    }

    /** Sync system dark mode status from UI */
    fun syncSystemDarkMode(isSystemDark: Boolean) {
        _isDarkMode.value = isSystemDark
        updateEffectiveDarkMode()
    }

    private fun loadEqualizerPreferences() {
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
            preferencesManager.loudnessFlow.collect { level -> _loudness.value = level }
        }

        viewModelScope.launch {
            preferencesManager.recentlyPlayedFlow.collect { json -> _recentlyPlayedJson.value = json }
        }

        viewModelScope.launch {
            preferencesManager.reverbFlow.collect { preset -> _reverb.value = preset }
        }

        viewModelScope.launch {
            kotlinx.coroutines.flow
                    .combine(
                            preferencesManager.band60HzFlow,
                            preferencesManager.band230HzFlow,
                            preferencesManager.band910HzFlow,
                            preferencesManager.band3600HzFlow,
                            preferencesManager.band14000HzFlow
                    ) { b1, b2, b3, b4, b5 -> listOf(b1, b2, b3, b4, b5) }
                    .collect { bands -> _eqBands.value = bands }
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
        viewModelScope.launch {
            preferencesManager.setSleepTimer(minutes)
            _sleepTimerRemaining.value = minutes
            if (minutes > 0) startSleepTimer() else stopSleepTimer()
        }
    }

    private var sleepTimerJob: kotlinx.coroutines.Job? = null

    private fun startSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimerRunning.value = true
        sleepTimerJob =
                viewModelScope.launch {
                    while (_sleepTimerRemaining.value > 0) {
                        delay(60000) // Wait 1 minute
                        _sleepTimerRemaining.value -= 1
                    }
                    _sleepTimer.value = 0 // Reset timer setting
                    _sleepTimerRunning.value = false
                }
    }

    private fun stopSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimerRunning.value = false
        _sleepTimerRemaining.value = 0
    }

    fun toggleDataSaver() {
        _dataSaverEnabled.value = !_dataSaverEnabled.value
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
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

    fun toggleExplicitContent() {
        viewModelScope.launch {
            val newValue = !_explicitContentEnabled.value
            preferencesManager.setExplicitContentEnabled(newValue)
        }
    }

    fun toggleAnnotations() {
        viewModelScope.launch {
            val newValue = !_annotationsEnabled.value
            preferencesManager.setAnnotationsEnabled(newValue)
        }
    }

    fun toggleShowAds() {
        viewModelScope.launch {
            val newValue = !_showAds.value
            preferencesManager.setShowAds(newValue)
        }
    }

    fun toggleVideoPlayback() {
        viewModelScope.launch {
            val newValue = !_videoPlaybackEnabled.value
            preferencesManager.setVideoPlaybackEnabled(newValue)
        }
    }

    fun toggleMobileNotifications() {
        viewModelScope.launch {
            val newValue = !_mobileNotificationsEnabled.value
            preferencesManager.setMobileNotifications(newValue)
        }
    }

    fun toggleEmailNotifications() {
        viewModelScope.launch {
            val newValue = !_emailNotificationsEnabled.value
            preferencesManager.setEmailNotifications(newValue)
        }
    }

    fun setMusicLanguages(languages: String) {
        viewModelScope.launch { preferencesManager.setMusicLanguages(languages) }
    }

    fun setDisplayLanguage(language: String) {
        viewModelScope.launch { preferencesManager.setDisplayLanguage(language) }
    }

    fun setAppTheme(theme: String) {
        viewModelScope.launch { preferencesManager.setAppTheme(theme) }
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

    fun setLoudness(level: Int) {
        viewModelScope.launch { preferencesManager.setLoudness(level) }
    }

    fun setRecentlyPlayed(json: String) {
        viewModelScope.launch {
            _recentlyPlayedJson.value = json
            preferencesManager.setRecentlyPlayed(json)
        }
    }

    fun setReverb(preset: String) {
        viewModelScope.launch { preferencesManager.setReverb(preset) }
    }

    fun updateEqualizerBand(index: Int, value: Float) {
        viewModelScope.launch {
            val currentBands = _eqBands.value.toMutableList()
            if (index in currentBands.indices) {
                currentBands[index] = value.coerceIn(-15f, 15f)
                _eqBands.value = currentBands
                preferencesManager.setEqualizerBands(currentBands)
            }
        }
    }

    fun setEqualizerBands(bands: List<Float>) {
        viewModelScope.launch {
            _eqBands.value = bands
            preferencesManager.setEqualizerBands(bands)
        }
    }
}
