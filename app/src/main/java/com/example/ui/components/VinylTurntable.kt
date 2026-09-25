package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.example.model.AudioEffectSettings
import com.example.model.Track
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedDark
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SunsetGold
import com.example.ui.theme.VintageCream
import com.example.ui.theme.VintageCreamLight
import com.example.ui.theme.VintageCreamMuted
import com.example.ui.theme.WaveTeal
import com.example.ui.theme.WaveTealDark

/**
 * Right Interactive Player & Vinyl Hub:
 * - Constant, rock-solid vinyl dimensions that never change size when switching songs
 * - Fixed metadata row height preventing layout shifting
 * - Bleeding asymmetric vinyl anchored to right edge
 * - High-contrast Retro City Pop palette
 */
@Composable
fun VinylTurntable(
    track: Track,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    isShuffle: Boolean,
    isRepeat: Boolean,
    effects: AudioEffectSettings,
    activeBitrate: Int = 9216,
    activeSampleRate: String = "96.0 kHz",
    exoPlayer: ExoPlayer? = null,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSeekRelative: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggle45Rpm: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isVideoMode by remember { mutableStateOf(false) }
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableFloatStateOf(0f) }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp)),
        color = DeepNavy,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Bar: Fixed Height (30.dp) Crimson Red HI-RES Badge, Audio Specs, and Mode Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F1522))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Hi-Res Audio Specs & Editorial Japanese Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CrimsonRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "HI-RES",
                            color = VintageCream,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${track.format.extension} $activeSampleRate",
                        color = VintageCream,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ステレオ STEREO",
                        color = VintageCreamMuted,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Vinyl / Video Mode Switcher
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF151C2B))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isVideoMode) CrimsonRed else Color.Transparent)
                            .clickable { isVideoMode = false }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("mode_vinyl_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Album,
                                contentDescription = "Vinyl",
                                tint = VintageCream,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Vinyl",
                                color = VintageCream,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isVideoMode) WaveTeal else Color.Transparent)
                            .clickable { isVideoMode = true }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("mode_video_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video",
                                tint = VintageCream,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Video",
                                color = VintageCream,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Metadata: Fixed Height (40.dp) so changing songs never causes vertical shifts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = track.title,
                            color = VintageCream,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "(${track.titleJp})",
                            color = SunsetGold,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${track.artist} (${track.artistJp}) • ${track.album}",
                            color = VintageCreamMuted,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "•",
                            color = VintageCreamMuted.copy(alpha = 0.5f),
                            fontSize = 8.sp
                        )
                        Text(
                            text = "都会の夜",
                            color = CrimsonRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = { onToggleFavorite(track.id) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (track.isFavorite) CrimsonRed else VintageCreamMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // 3. Bleeding Pitch Black Vinyl (Constant Size & Stable Dimensions)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F1522)),
                contentAlignment = Alignment.CenterEnd
            ) {
                AnimatedContent(
                    targetState = isVideoMode,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    label = "deck_mode_transition"
                ) { targetVideoMode ->
                    if (targetVideoMode) {
                        VideoSurfacePlayer(
                            exoPlayer = exoPlayer,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .testTag("video_surface_player_view")
                        )
                    } else {
                        // Constant size bleeding vinyl container
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            // Constant calculated size derived purely from container height
                            val vinylDiameter = maxHeight * 1.25f

                            SpinningVinyl(
                                albumArtUri = track.albumArtUri ?: track.albumArtUrl?.let { Uri.parse(it) },
                                isPlaying = isPlaying,
                                rpmSpeedMultiplier = if (effects.is45RpmMode) 1.35f else 1.0f,
                                fallbackTitle = track.title,
                                fallbackArtist = track.artist,
                                modifier = Modifier
                                    .size(vinylDiameter)
                                    .offset(x = vinylDiameter * 0.28f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 4. Consolidated Playback Hub (Fixed Height 88.dp: Timeline Scrubber & Tactile Buttons)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F1522))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Timeline Scrubber Row
                val totalDuration = duration.coerceAtLeast(1L)
                val currentPos = if (isDraggingSlider) {
                    (dragPosition * totalDuration).toLong()
                } else {
                    currentPosition.coerceIn(0L, totalDuration)
                }

                val curMin = (currentPos / 1000) / 60
                val curSec = (currentPos / 1000) % 60
                val durMin = (totalDuration / 1000) / 60
                val durSec = (totalDuration / 1000) % 60

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format("%02d:%02d", curMin, curSec),
                        color = VintageCream,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Slider(
                        value = if (isDraggingSlider) dragPosition else (currentPos.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f),
                        onValueChange = {
                            isDraggingSlider = true
                            dragPosition = it
                        },
                        onValueChangeFinished = {
                            isDraggingSlider = false
                            onSeekTo((dragPosition * totalDuration).toLong())
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(18.dp)
                            .padding(horizontal = 6.dp)
                            .testTag("playback_timeline_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = CrimsonRed,
                            activeTrackColor = CrimsonRed,
                            inactiveTrackColor = WaveTealDark
                        )
                    )

                    Text(
                        text = String.format("%02d:%02d", durMin, durSec),
                        color = VintageCreamMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // 33⅓ vs 45 RPM Pitch Badge in Sunset Gold
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (effects.is45RpmMode) CrimsonRed else Color(0xFF1B2436))
                            .clickable { onToggle45Rpm() }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("rpm_speed_toggle")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Speed",
                                tint = if (effects.is45RpmMode) VintageCream else SunsetGold,
                                modifier = Modifier.size(9.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (effects.is45RpmMode) "45 RPM" else "33⅓ RPM",
                                color = VintageCream,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Tactile Transport Buttons with Stark Crimson Red Play/Pause Center
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle
                    IconButton(
                        onClick = onToggleShuffle,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffle) CrimsonRed else VintageCreamMuted.copy(alpha = 0.7f),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Seek -10s
                    IconButton(
                        onClick = { onSeekRelative(-10000L) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Back 10s",
                            tint = VintageCreamMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Previous Track
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = VintageCream,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Master Big Play/Pause Button in Stark Crimson Red (#CE3232)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(CrimsonRed, CrimsonRedDark)
                                )
                            )
                            .clickable { onTogglePlayPause() }
                            .testTag("play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = VintageCream,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Next Track
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = VintageCream,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Seek +10s
                    IconButton(
                        onClick = { onSeekRelative(10000L) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = VintageCreamMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Repeat
                    IconButton(
                        onClick = onToggleRepeat,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Repeat",
                            tint = if (isRepeat) SunsetGold else VintageCreamMuted.copy(alpha = 0.7f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
