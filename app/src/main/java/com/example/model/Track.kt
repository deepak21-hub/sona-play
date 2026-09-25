package com.example.model

import androidx.compose.ui.graphics.Color

enum class AudioFormat(
    val extension: String,
    val sampleRateStr: String,
    val bitDepthStr: String,
    val defaultBitrateKbps: Int,
    val isLossless: Boolean
) {
    FLAC_96_24("FLAC", "96.0 kHz", "24-bit", 9216, true),
    FLAC_48_24("FLAC", "48.0 kHz", "24-bit", 4608, true),
    WAV_192_24("WAV", "192.0 kHz", "24-bit", 9216, true),
    ALAC_48_16("ALAC", "48.0 kHz", "16-bit", 1536, true),
    HI_RES_STREAM("HLS/AAC", "48.0 kHz", "24-bit", 320, false)
}

enum class VinylArtTheme {
    SUNSET_TOKYO,
    NEON_MIDNIGHT,
    OCEAN_BREEZE,
    ANIME_PIRATE,
    BEBOP_JAZZ,
    VINTAGE_CHINATOWN,
    MOUNT_FUJI
}

data class Track(
    val id: String,
    val title: String,
    val titleJp: String,
    val artist: String,
    val artistJp: String,
    val album: String,
    val year: Int,
    val durationMs: Long,
    val audioUrl: String,
    val format: AudioFormat = AudioFormat.FLAC_96_24,
    val sampleRate: String = "96.0 kHz",
    val bitDepth: String = "24-bit",
    val bitrateKbps: Int = 9216,
    val catalogNumber: String = "SNA-8401",
    val theme: VinylArtTheme = VinylArtTheme.SUNSET_TOKYO,
    val primaryColorHex: Long = 0xFFE63946,
    val secondaryColorHex: Long = 0xFFF4A261,
    val bpm: Int = 118,
    val key: String = "F# Minor",
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lyrics: List<LyricLine> = emptyList(),
    val isCustomStream: Boolean = false,
    val albumArtUri: android.net.Uri? = null,
    val albumArtUrl: String? = null,
    val videoUrl: String? = null
)

data class LyricLine(
    val timestampMs: Long,
    val textRomaji: String,
    val textJp: String,
    val textEn: String
)

data class AudioEffectSettings(
    val vinylCrackleEnabled: Boolean = true,
    val vinylCrackleVolume: Float = 0.25f,
    val playbackSpeedRpm: Float = 1.0f, // 1.0f = 33 RPM standard, 1.35f = 45 RPM
    val is45RpmMode: Boolean = false,
    val bassBoostDb: Float = 4.0f,
    val midFreqDb: Float = 1.5f,
    val trebleFreqDb: Float = 3.0f,
    val analogWarmth: Boolean = true,
    val cassetteMode: Boolean = false
)
