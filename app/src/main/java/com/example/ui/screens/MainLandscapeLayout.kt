package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.AudioFormat
import com.example.model.NavigationTab
import com.example.model.Track
import com.example.ui.components.HalftoneBackground
import com.example.ui.components.NavigationRailSona
import com.example.ui.components.StackedCollectionPager
import com.example.ui.components.VinylTurntable
import com.example.ui.panes.BrowsePane
import com.example.ui.panes.CassetteLyricsPane
import com.example.ui.panes.ChartsPane
import com.example.ui.panes.EqualizerPane
import com.example.ui.panes.SpotifyRemotePane
import com.example.viewmodel.PlayerViewModel

/**
 * Tablet / Landscape Master Layout:
 * Cohesive 3-Column Architecture:
 * - Left Navigation Sidebar (0.15f): Minimalist, border-free design with volume control
 * - Center Dashboard (0.45f): Left-aligned Search & category chips, 3D Stacked Record Crate, full-height songs library, Spotify Remote
 * - Right Interactive Player & Vinyl Hub (0.40f): Bleeding vinyl, top metadata, consolidated transport controls & timeline
 */
@Composable
fun MainLandscapeLayout(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val allTracks by viewModel.tracks.collectAsState()
    val filteredTracks by viewModel.filteredTracks.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val audioEffects by viewModel.audioEffects.collectAsState()
    val isShuffle by viewModel.isShuffle.collectAsState()
    val isRepeat by viewModel.isRepeat.collectAsState()
    val masterVolume by viewModel.masterVolume.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val spotifyState by viewModel.spotifyState.collectAsState()
    val spotifyConnectionStatus by viewModel.spotifyConnectionStatus.collectAsState()
    val spotifyErrorMessage by viewModel.spotifyErrorMessage.collectAsState()

    val isPlayingLocal by viewModel.audioEngine.isPlaying.collectAsState()
    val currentPositionLocal by viewModel.audioEngine.currentPosition.collectAsState()
    val durationLocal by viewModel.audioEngine.duration.collectAsState()
    val activeBitrate by viewModel.audioEngine.activeBitrate.collectAsState()
    val activeSampleRate by viewModel.audioEngine.activeSampleRate.collectAsState()

    // Determine whether to display Spotify Remote Track or Local Audio Engine Track
    val isSpotifyActive = activeTab == NavigationTab.SPOTIFY && spotifyState != null
    val displayTrack: Track = if (isSpotifyActive && spotifyState != null) {
        Track(
            id = "spotify_remote_active",
            title = spotifyState!!.title,
            titleJp = "スポティファイ再生中",
            artist = spotifyState!!.artist,
            artistJp = "Spotify Remote",
            album = spotifyState!!.album,
            year = 2026,
            durationMs = spotifyState!!.durationMs,
            audioUrl = "",
            format = AudioFormat.HI_RES_STREAM,
            bitrateKbps = 320,
            sampleRate = "44.1 kHz"
        )
    } else {
        currentTrack
    }

    val isPlaying = if (isSpotifyActive) (spotifyState?.isPlaying == true) else isPlayingLocal
    val currentPosition = if (isSpotifyActive) (spotifyState?.positionMs ?: 0L) else currentPositionLocal
    val duration = if (isSpotifyActive) (spotifyState?.durationMs ?: 1L) else durationLocal

    Box(modifier = modifier.fillMaxSize()) {
        // Retro Halftone & Japanese subtle backdrop
        HalftoneBackground(isDark = true)

        // 3-Column Balanced Layout (0.15f, 0.45f, 0.40f)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            // 1. Left Navigation Sidebar (Weight: 0.15f)
            NavigationRailSona(
                activeTab = activeTab,
                onTabSelected = { viewModel.setTab(it) },
                masterVolume = masterVolume,
                onVolumeChanged = { viewModel.setMasterVolume(it) },
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxHeight()
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 2. Center Dashboard (Weight: 0.45f)
            Box(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
            ) {
                when (activeTab) {
                    NavigationTab.BROWSE, NavigationTab.HIRES -> {
                        BrowsePane(
                            tracks = filteredTracks,
                            allTracks = allTracks,
                            currentTrack = currentTrack,
                            isPlaying = isPlayingLocal,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            onSearchChanged = { viewModel.setSearchQuery(it) },
                            onCategorySelected = { viewModel.setSelectedCategory(it) },
                            onTrackSelect = { viewModel.selectTrack(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) }
                        )
                    }
                    NavigationTab.COLLECTION -> {
                        // 3D Stacked Record Crate & Spine Perspective Collection View
                        StackedCollectionPager(
                            tracks = allTracks,
                            currentTrack = currentTrack,
                            isPlaying = isPlayingLocal,
                            onTrackSelect = { viewModel.selectTrack(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) }
                        )
                    }
                    NavigationTab.CHARTS -> {
                        ChartsPane(
                            tracks = allTracks,
                            currentTrack = currentTrack,
                            onTrackSelect = { viewModel.selectTrack(it) }
                        )
                    }
                    NavigationTab.SPOTIFY -> {
                        SpotifyRemotePane(
                            spotifyManager = viewModel.spotifyRemoteManager,
                            spotifyState = spotifyState,
                            connectionStatus = spotifyConnectionStatus,
                            errorMessage = spotifyErrorMessage
                        )
                    }
                    NavigationTab.CASSETTE -> {
                        CassetteLyricsPane(
                            track = currentTrack,
                            isPlaying = isPlayingLocal,
                            currentPositionMs = currentPositionLocal,
                            onSeekTo = { viewModel.seekTo(it) }
                        )
                    }
                    NavigationTab.EQUALIZER -> {
                        EqualizerPane(
                            effects = audioEffects,
                            onUpdateEffects = { viewModel.updateAudioEffects(it) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 3. Right Interactive Player & Vinyl Hub (Weight: 0.40f)
            VinylTurntable(
                track = displayTrack,
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                isShuffle = isShuffle,
                isRepeat = isRepeat,
                effects = audioEffects,
                activeBitrate = if (isSpotifyActive) 320 else activeBitrate,
                activeSampleRate = if (isSpotifyActive) "44.1 kHz" else activeSampleRate,
                exoPlayer = if (isSpotifyActive) null else viewModel.audioEngine.player,
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onNext = { viewModel.nextTrack() },
                onPrevious = { viewModel.previousTrack() },
                onSeekTo = { viewModel.seekTo(it) },
                onSeekRelative = { viewModel.seekRelative(it) },
                onToggleShuffle = { viewModel.toggleShuffle() },
                onToggleRepeat = { viewModel.toggleRepeat() },
                onToggle45Rpm = { viewModel.toggle45RpmMode() },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                modifier = Modifier
                    .weight(0.40f)
                    .fillMaxHeight()
            )
        }
    }
}
