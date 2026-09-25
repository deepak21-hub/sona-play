package com.example.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.ui.theme.VintageNavyBorder
import com.example.ui.theme.VintageNavyDark

/**
 * VideoSurfacePlayer Composable:
 * Wraps an ExoPlayer instance using AndroidView and PlayerView with lifecycle-aware
 * surface attachment and detachment.
 *
 * The Handoff Logic:
 * - On ON_RESUME: Attaches ExoPlayer to the PlayerView (playerView.player = exoPlayer)
 * - On ON_PAUSE / ON_STOP: Detaches the player (playerView.player = null) to release
 *   the video surface and save GPU/memory without pausing audio playback.
 *   The background MediaSessionService keeps the lossless audio stream playing seamlessly.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoSurfacePlayer(
    exoPlayer: ExoPlayer?,
    modifier: Modifier = Modifier,
    useController: Boolean = false,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val playerView = remember {
        PlayerView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.useController = useController
            this.resizeMode = resizeMode
            setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
            setBackgroundColor(android.graphics.Color.BLACK)
        }
    }

    // Lifecycle observation for surface attachment/detachment handoff
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Attach ExoPlayer to PlayerView to display video surface
                    playerView.player = exoPlayer
                }
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    // Detach ExoPlayer without calling pause()
                    // Allows audio to continue playing uninterrupted in background
                    playerView.player = null
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        // Initial attachment if currently in resumed state
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            playerView.player = exoPlayer
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            playerView.player = null
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(VintageNavyDark)
            .border(1.dp, VintageNavyBorder, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { playerView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
