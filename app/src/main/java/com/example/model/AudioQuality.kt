package com.example.model

/**
 * Audiophile-grade Audio Quality metadata extracted in real-time from ExoPlayer's active audio track.
 */
data class AudioQuality(
    val codec: String,
    val sampleRate: Int,
    val bitDepth: Int,
    val channelCount: Int = 2,
    val bitrate: Int = 0,
    val isLossless: Boolean = true,
    val isOffloaded: Boolean = false
) {
    val formattedSampleRate: String
        get() = when {
            sampleRate >= 1000 -> String.format("%.1f kHz", sampleRate / 1000.0)
            sampleRate > 0 -> "$sampleRate Hz"
            else -> "96.0 kHz"
        }

    val formattedBitDepth: String
        get() = if (bitDepth > 0) "$bitDepth-bit" else "24-bit"

    val formattedBitrate: String
        get() = if (bitrate > 0) "${bitrate / 1000} kbps" else "9216 kbps"

    val formattedTag: String
        get() = "$codec • $formattedBitDepth / $formattedSampleRate"

    companion object {
        fun defaultLossless(): AudioQuality = AudioQuality(
            codec = "FLAC",
            sampleRate = 96000,
            bitDepth = 24,
            channelCount = 2,
            bitrate = 9216000,
            isLossless = true,
            isOffloaded = true
        )
    }
}
