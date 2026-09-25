package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.AudioEffectSettings
import com.example.model.Track
import com.example.ui.theme.CityPopCyan
import com.example.ui.theme.CityPopTeal
import com.example.ui.theme.CrimsonDeep
import com.example.ui.theme.RisingSunRed
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.TextCreamLight
import com.example.ui.theme.TextCreamMuted
import com.example.ui.theme.VintageBrass
import com.example.ui.theme.VintageNavyBorder
import com.example.ui.theme.VintageNavyCard
import com.example.ui.theme.VintageNavyDark

@Composable
fun RetroControls(
    track: Track,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    isShuffle: Boolean,
    isRepeat: Boolean,
    effects: AudioEffectSettings,
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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, VintageNavyBorder, RoundedCornerShape(20.dp)),
        color = VintageNavyDark.copy(alpha = 0.96f),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Track Info Row & Heart / 45 RPM Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = track.title,
                                color = TextCreamLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${track.titleJp})",
                                color = SunsetOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "${track.artist} (${track.artistJp}) • ${track.album} (${track.year})",
                            color = TextCreamMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Controls for Favorite & 45 RPM Mode
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 45 RPM Switch Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (effects.is45RpmMode) SunsetOrange else VintageNavyCard)
                            .border(1.dp, if (effects.is45RpmMode) Color(0xFFFFD166) else VintageNavyBorder, RoundedCornerShape(8.dp))
                            .clickable { onToggle45Rpm() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("toggle_45rpm_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "RPM Mode",
                                tint = if (effects.is45RpmMode) VintageNavyDark else TextCreamLight,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (effects.is45RpmMode) "45 RPM (Speed+)" else "33 RPM (Norm)",
                                color = if (effects.is45RpmMode) VintageNavyDark else TextCreamLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Heart / Favorite
                    IconButton(
                        onClick = { onToggleFavorite(track.id) },
                        modifier = Modifier.size(32.dp).testTag("favorite_button")
                    ) {
                        Icon(
                            imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (track.isFavorite) RisingSunRed else TextCreamMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Timeline Scrubber & Digital LED Times
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTime(currentPosition),
                    color = CityPopCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                val effectiveDuration = if (duration > 0L) duration else track.durationMs
                val progress = if (effectiveDuration > 0L) {
                    (currentPosition.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Slider(
                    value = progress,
                    onValueChange = { frac ->
                        onSeekTo((frac * effectiveDuration).toLong())
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .height(20.dp)
                        .testTag("scrubber_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = RisingSunRed,
                        activeTrackColor = RisingSunRed,
                        inactiveTrackColor = VintageNavyCard
                    )
                )

                Text(
                    text = formatTime(effectiveDuration),
                    color = TextCreamMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }

            // Transport Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle Button
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier.size(36.dp).testTag("shuffle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) CityPopCyan else TextCreamMuted.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Rewind 10s
                IconButton(
                    onClick = { onSeekRelative(-10000L) },
                    modifier = Modifier.size(36.dp).testTag("rewind_10s_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Rewind 10s",
                        tint = TextCreamLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Previous
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.size(38.dp).testTag("prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = TextCreamLight,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Big Center Play / Pause Push Button with glowing ring
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    RisingSunRed,
                                    CrimsonDeep
                                )
                            )
                        )
                        .border(2.dp, Color(0xFFFFD166), CircleShape)
                        .shadow(8.dp, CircleShape)
                        .clickable { onTogglePlayPause() }
                        .testTag("play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Next
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(38.dp).testTag("next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = TextCreamLight,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Forward 10s
                IconButton(
                    onClick = { onSeekRelative(10000L) },
                    modifier = Modifier.size(36.dp).testTag("forward_10s_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "Forward 10s",
                        tint = TextCreamLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Repeat Button
                IconButton(
                    onClick = onToggleRepeat,
                    modifier = Modifier.size(36.dp).testTag("repeat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (isRepeat) CityPopCyan else TextCreamMuted.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
