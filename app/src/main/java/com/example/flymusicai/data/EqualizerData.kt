package com.example.flymusicai.data

/** 🎛️ Equalizer Preset Data Class */
data class EqualizerPreset(
        val name: String,
        val bands: List<Float> // 5 bands: 60Hz, 230Hz, 910Hz, 3600Hz, 14000Hz
)

/** 🎵 Predefined Equalizer Presets */
object EqualizerPresets {
    val CUSTOM = EqualizerPreset("Custom", listOf(0f, 0f, 0f, 0f, 0f))
    val NORMAL = EqualizerPreset("Normal", listOf(0f, 0f, 0f, 0f, 0f))
    val CLASSICAL = EqualizerPreset("Classical", listOf(5f, 3f, -2f, 4f, 4f))
    val DANCE = EqualizerPreset("Dance", listOf(6f, 0f, 2f, 4f, 1f))
    val FLAT = EqualizerPreset("Flat", listOf(0f, 0f, 0f, 0f, 0f))
    val FOLK = EqualizerPreset("Folk", listOf(3f, 0f, 0f, 2f, -1f))
    val HEAVY_METAL = EqualizerPreset("Heavy Metal", listOf(4f, 1f, 5f, 3f, 0f))
    val HIP_HOP = EqualizerPreset("Hip Hop", listOf(5f, 3f, 0f, 1f, 3f))
    val JAZZ = EqualizerPreset("Jazz", listOf(4f, 2f, -2f, 2f, 5f))
    val POP = EqualizerPreset("Pop", listOf(1f, 3f, 5f, 3f, 1f))
    val ROCK = EqualizerPreset("Rock", listOf(5f, 3f, -1f, 3f, 5f))
    val ELECTRONIC = EqualizerPreset("Electronic", listOf(4f, 3f, 1f, 0f, 4f))
    val LATIN = EqualizerPreset("Latin", listOf(4f, 2f, 0f, 0f, 4f))
    val PIANO = EqualizerPreset("Piano", listOf(3f, 1f, 0f, 3f, 4f))
    val R_AND_B = EqualizerPreset("R&B", listOf(3f, 5f, 1f, 1f, 3f))
    val VOCAL = EqualizerPreset("Vocal", listOf(-2f, -1f, 4f, 3f, 2f))

    fun getAllPresets(): List<EqualizerPreset> =
            listOf(
                    CUSTOM,
                    NORMAL,
                    CLASSICAL,
                    DANCE,
                    FLAT,
                    FOLK,
                    HEAVY_METAL,
                    HIP_HOP,
                    JAZZ,
                    POP,
                    ROCK,
                    ELECTRONIC,
                    LATIN,
                    PIANO,
                    R_AND_B,
                    VOCAL
            )

    fun getPresetByName(name: String): EqualizerPreset {
        return getAllPresets().find { it.name == name } ?: NORMAL
    }
}

/** 🎚️ Equalizer Settings */
data class EqualizerSettings(
        val enabled: Boolean = false,
        val preset: String = "Normal",
        val customBands: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
        val bassBoost: Int = 0, // 0-100
        val virtualizer: Int = 0, // 0-100
        val reverb: String = "None" // None, Small Room, Medium Room, Large Room, Hall
)
