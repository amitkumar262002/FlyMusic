package com.example.flymusicai.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** 🔧 Enhanced Manager for app preferences using DataStore */
class PreferencesManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by
                preferencesDataStore(name = "app_preferences")

        // Existing Keys
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val AI_PERSONALIZATION_KEY = booleanPreferencesKey("ai_personalization")
        private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications")

        // 🆕 New Advanced Settings Keys
        private val AUTO_PLAY_KEY = booleanPreferencesKey("auto_play")
        private val HIGH_QUALITY_AUDIO_KEY = booleanPreferencesKey("high_quality_audio")
        private val DOWNLOAD_WIFI_ONLY_KEY = booleanPreferencesKey("download_wifi_only")
        private val CROSSFADE_ENABLED_KEY = booleanPreferencesKey("crossfade_enabled")
        private val CROSSFADE_DURATION_KEY = intPreferencesKey("crossfade_duration")
        private val AUDIO_QUALITY_KEY = stringPreferencesKey("audio_quality")
        private val EQUALIZER_PRESET_KEY = stringPreferencesKey("equalizer_preset")
        private val SLEEP_TIMER_KEY = intPreferencesKey("sleep_timer")
        private val LYRICS_ENABLED_KEY = booleanPreferencesKey("lyrics_enabled")
        private val OFFLINE_MODE_KEY = booleanPreferencesKey("offline_mode")
        private val EXPLICIT_CONTENT_KEY =
                booleanPreferencesKey(
                        "explicit_content_enabled"
                ) // Default: True (which means disabled IS false)
        private val ANNOTATIONS_KEY = booleanPreferencesKey("annotations_enabled")
        private val SHOW_ADS_KEY = booleanPreferencesKey("show_ads")
        private val VIDEO_PLAYBACK_KEY = booleanPreferencesKey("video_playback")
        private val MOBILE_NOTIFICATIONS_KEY = booleanPreferencesKey("mobile_notifications")
        private val EMAIL_NOTIFICATIONS_KEY = booleanPreferencesKey("email_notifications")
        private val MUSIC_LANGUAGES_KEY = stringPreferencesKey("music_languages")
        private val DISPLAY_LANGUAGE_KEY = stringPreferencesKey("display_language")
        private val APP_THEME_KEY = stringPreferencesKey("app_theme")

        // 🎛️ Equalizer Settings Keys
        private val EQUALIZER_ENABLED_KEY = booleanPreferencesKey("equalizer_enabled")
        private val BASS_BOOST_KEY = intPreferencesKey("bass_boost")
        private val VIRTUALIZER_KEY = intPreferencesKey("virtualizer")
        private val REVERB_KEY = stringPreferencesKey("reverb")
        private val BAND_60HZ_KEY = floatPreferencesKey("band_60hz")
        private val BAND_230HZ_KEY = floatPreferencesKey("band_230hz")
        private val BAND_910HZ_KEY = floatPreferencesKey("band_910hz")
        private val BAND_3600HZ_KEY = floatPreferencesKey("band_3600hz")
        private val BAND_14000HZ_KEY = floatPreferencesKey("band_14000hz")
        private val LOUDNESS_KEY = intPreferencesKey("loudness_boost")
        private val RECENTLY_PLAYED_KEY = stringPreferencesKey("recently_played_json")
    }

    // ========== Existing Preferences ==========

    val darkModeFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[DARK_MODE_KEY] ?: false }

    val aiPersonalizationFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[AI_PERSONALIZATION_KEY] ?: true
            }

    val notificationsFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[NOTIFICATIONS_KEY] ?: true }

    // ========== New Advanced Preferences ==========

    val autoPlayFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[AUTO_PLAY_KEY] ?: true }

    val highQualityAudioFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[HIGH_QUALITY_AUDIO_KEY] ?: false
            }

    val downloadWifiOnlyFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[DOWNLOAD_WIFI_ONLY_KEY] ?: true
            }

    val crossfadeEnabledFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[CROSSFADE_ENABLED_KEY] ?: false
            }

    val crossfadeDurationFlow: Flow<Int> =
            context.dataStore.data.map { preferences ->
                preferences[CROSSFADE_DURATION_KEY] ?: 5 // seconds
            }

    val audioQualityFlow: Flow<String> =
            context.dataStore.data.map { preferences ->
                preferences[AUDIO_QUALITY_KEY] ?: "Normal" // Normal, High, Extreme
            }

    val equalizerPresetFlow: Flow<String> =
            context.dataStore.data.map { preferences ->
                preferences[EQUALIZER_PRESET_KEY] ?: "Flat" // Flat, Pop, Rock, Jazz, Classical
            }

    val sleepTimerFlow: Flow<Int> =
            context.dataStore.data.map { preferences ->
                preferences[SLEEP_TIMER_KEY] ?: 0 // 0 = disabled, otherwise minutes
            }

    val lyricsEnabledFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[LYRICS_ENABLED_KEY] ?: true }

    val offlineModeFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[OFFLINE_MODE_KEY] ?: false }

    val explicitContentEnabledFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[EXPLICIT_CONTENT_KEY] ?: true }

    val annotationsEnabledFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[ANNOTATIONS_KEY] ?: true }

    val showAdsFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[SHOW_ADS_KEY] ?: true }

    val videoPlaybackEnabledFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[VIDEO_PLAYBACK_KEY] ?: true }

    val mobileNotificationsFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[MOBILE_NOTIFICATIONS_KEY] ?: true
            }

    val emailNotificationsFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[EMAIL_NOTIFICATIONS_KEY] ?: false
            }

    val musicLanguagesFlow: Flow<String> =
            context.dataStore.data.map { preferences ->
                preferences[MUSIC_LANGUAGES_KEY] ?: "Hindi, English"
            }

    val displayLanguageFlow: Flow<String> =
            context.dataStore.data.map { preferences ->
                preferences[DISPLAY_LANGUAGE_KEY] ?: "English"
            }

    val appThemeFlow: Flow<String> =
            context.dataStore.data.map { preferences ->
                preferences[APP_THEME_KEY] ?: "System Default"
            }

    // 🎛️ Equalizer Flows
    val equalizerEnabledFlow: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[EQUALIZER_ENABLED_KEY] ?: false
            }

    val bassBoostFlow: Flow<Int> =
            context.dataStore.data.map { preferences -> preferences[BASS_BOOST_KEY] ?: 0 }

    val virtualizerFlow: Flow<Int> =
            context.dataStore.data.map { preferences -> preferences[VIRTUALIZER_KEY] ?: 0 }

    val reverbFlow: Flow<String> =
            context.dataStore.data.map { preferences -> preferences[REVERB_KEY] ?: "None" }

    val band60HzFlow: Flow<Float> =
            context.dataStore.data.map { preferences -> preferences[BAND_60HZ_KEY] ?: 0f }
    val band230HzFlow: Flow<Float> =
            context.dataStore.data.map { preferences -> preferences[BAND_230HZ_KEY] ?: 0f }
    val band910HzFlow: Flow<Float> =
            context.dataStore.data.map { preferences -> preferences[BAND_910HZ_KEY] ?: 0f }
    val band3600HzFlow: Flow<Float> =
            context.dataStore.data.map { preferences -> preferences[BAND_3600HZ_KEY] ?: 0f }
    val band14000HzFlow: Flow<Float> =
            context.dataStore.data.map { preferences -> preferences[BAND_14000HZ_KEY] ?: 0f }

    val loudnessFlow: Flow<Int> =
            context.dataStore.data.map { preferences -> preferences[LOUDNESS_KEY] ?: 0 }

    val recentlyPlayedFlow: Flow<String> =
            context.dataStore.data.map { preferences -> preferences[RECENTLY_PLAYED_KEY] ?: "[]" }

    // ========== Setters ==========

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[DARK_MODE_KEY] = enabled }
    }

    suspend fun setAIPersonalization(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[AI_PERSONALIZATION_KEY] = enabled }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[NOTIFICATIONS_KEY] = enabled }
    }

    suspend fun setAutoPlay(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[AUTO_PLAY_KEY] = enabled }
    }

    suspend fun setHighQualityAudio(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[HIGH_QUALITY_AUDIO_KEY] = enabled }
    }

    suspend fun setDownloadWifiOnly(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[DOWNLOAD_WIFI_ONLY_KEY] = enabled }
    }

    suspend fun setCrossfadeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[CROSSFADE_ENABLED_KEY] = enabled }
    }

    suspend fun setCrossfadeDuration(seconds: Int) {
        context.dataStore.edit { preferences -> preferences[CROSSFADE_DURATION_KEY] = seconds }
    }

    suspend fun setAudioQuality(quality: String) {
        context.dataStore.edit { preferences -> preferences[AUDIO_QUALITY_KEY] = quality }
    }

    suspend fun setEqualizerPreset(preset: String) {
        context.dataStore.edit { preferences -> preferences[EQUALIZER_PRESET_KEY] = preset }
    }

    suspend fun setSleepTimer(minutes: Int) {
        context.dataStore.edit { preferences -> preferences[SLEEP_TIMER_KEY] = minutes }
    }

    suspend fun setLyricsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[LYRICS_ENABLED_KEY] = enabled }
    }

    suspend fun setOfflineMode(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[OFFLINE_MODE_KEY] = enabled }
    }

    suspend fun setExplicitContentEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[EXPLICIT_CONTENT_KEY] = enabled }
    }

    suspend fun setAnnotationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[ANNOTATIONS_KEY] = enabled }
    }

    suspend fun setShowAds(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[SHOW_ADS_KEY] = enabled }
    }

    suspend fun setVideoPlaybackEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[VIDEO_PLAYBACK_KEY] = enabled }
    }

    suspend fun setMobileNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[MOBILE_NOTIFICATIONS_KEY] = enabled }
    }

    suspend fun setEmailNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[EMAIL_NOTIFICATIONS_KEY] = enabled }
    }

    suspend fun setMusicLanguages(languages: String) {
        context.dataStore.edit { preferences -> preferences[MUSIC_LANGUAGES_KEY] = languages }
    }

    suspend fun setDisplayLanguage(language: String) {
        context.dataStore.edit { preferences -> preferences[DISPLAY_LANGUAGE_KEY] = language }
    }

    suspend fun setAppTheme(theme: String) {
        context.dataStore.edit { preferences -> preferences[APP_THEME_KEY] = theme }
    }

    // 🎛️ Equalizer Setters
    suspend fun setEqualizerEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[EQUALIZER_ENABLED_KEY] = enabled }
    }

    suspend fun setBassBoost(level: Int) {
        context.dataStore.edit { preferences -> preferences[BASS_BOOST_KEY] = level }
    }

    suspend fun setVirtualizer(level: Int) {
        context.dataStore.edit { preferences -> preferences[VIRTUALIZER_KEY] = level }
    }

    suspend fun setReverb(preset: String) {
        context.dataStore.edit { preferences -> preferences[REVERB_KEY] = preset }
    }

    suspend fun setLoudness(level: Int) {
        context.dataStore.edit { preferences -> preferences[LOUDNESS_KEY] = level }
    }

    suspend fun setRecentlyPlayed(json: String) {
        context.dataStore.edit { preferences -> preferences[RECENTLY_PLAYED_KEY] = json }
    }

    suspend fun setEqualizerBands(bands: List<Float>) {
        context.dataStore.edit { preferences ->
            if (bands.size >= 5) {
                preferences[BAND_60HZ_KEY] = bands[0]
                preferences[BAND_230HZ_KEY] = bands[1]
                preferences[BAND_910HZ_KEY] = bands[2]
                preferences[BAND_3600HZ_KEY] = bands[3]
                preferences[BAND_14000HZ_KEY] = bands[4]
            }
        }
    }
}
