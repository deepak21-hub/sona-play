package com.example.spotify

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Immutable State Model representing the live Spotify playback stream
 */
data class SpotifyTrackState(
    val title: String,
    val artist: String,
    val album: String,
    val isPlaying: Boolean,
    val positionMs: Long,
    val durationMs: Long,
    val imageUri: String? = null,
    val albumArtBitmap: Bitmap? = null
) {
    /**
     * Converts raw Android Bitmap to Compose ImageBitmap for high-performance Canvas & Composable rendering
     */
    val composeImageBitmap: ImageBitmap?
        get() = albumArtBitmap?.asImageBitmap()
}

enum class SpotifyConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    NOT_INSTALLED,
    AUTH_ERROR,
    UNSUPPORTED
}
