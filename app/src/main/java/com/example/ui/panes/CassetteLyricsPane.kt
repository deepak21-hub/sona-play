package com.example.ui.panes

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Track
import com.example.ui.theme.CassetteBeige
import com.example.ui.theme.CassettePlastic
import com.example.ui.theme.CityPopCyan
import com.example.ui.theme.CityPopTeal
import com.example.ui.theme.RisingSunRed
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.TextCreamLight
import com.example.ui.theme.TextCreamMuted
import com.example.ui.theme.VintageBrass
import com.example.ui.theme.VintageNavyBorder
import com.example.ui.theme.VintageNavyCard
import com.example.ui.theme.VintageNavyDark

@Composable
fun CassetteLyricsPane(
    track: Track,
    isPlaying: Boolean,
    currentPositionMs: Long,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spool_spin")
    val spoolAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spool_spin_angle"
    )

    val listState = rememberLazyListState()

    // Find current active lyric line
    val currentLineIndex = track.lyrics.indexOfLast { currentPositionMs >= it.timestampMs }

    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex >= 0) {
            listState.animateScrollToItem((currentLineIndex - 1).coerceAtLeast(0))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Realistic 1980s Vintage Cassette Tape Graphic
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, VintageNavyBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CassettePlastic)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Cassette Label (Beige paper card with handwritten title)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CassetteBeige)
                        .border(1.dp, Color(0xFFC7BBA2), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header Tape Stamp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SIDE A • TYPE II [CrO2] HIGH BIAS",
                            color = Color(0xFF5A4A38),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "SONA C-90",
                            color = RisingSunRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Dual Rotating Spools & Transparent Center Window
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E1E24))
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Spool
                        CassetteSpool(
                            angle = if (isPlaying) spoolAngle else 0f,
                            tapeThickness = 14f
                        )

                        // Center Tape Counter & Window Grid
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "COUNTER: ${currentPositionMs / 1000}",
                                color = CityPopCyan,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            // Tape bridge
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(4.dp)
                                    .background(Color(0xFF5A2A18))
                            )
                        }

                        // Right Spool
                        CassetteSpool(
                            angle = if (isPlaying) spoolAngle else 0f,
                            tapeThickness = 8f
                        )
                    }

                    // Track Label text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${track.title} - ${track.artist}",
                            color = Color(0xFF2B2018),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "NORMAL POSITION",
                            color = Color(0xFF6B5848),
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Synced Lyrics Stream
        Text(
            text = "SYNCHRONIZED LYRICS (歌詞)",
            color = SunsetOrange,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        if (track.lyrics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(VintageNavyCard)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No synced lyrics available for this broadcast.\nEnjoy the pure lossless analog groove.",
                    color = TextCreamMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(VintageNavyCard)
                    .border(1.dp, VintageNavyBorder, RoundedCornerShape(14.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(track.lyrics) { index, line ->
                    val isActive = index == currentLineIndex
                    val lineBg = if (isActive) RisingSunRed.copy(alpha = 0.18f) else Color.Transparent

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(lineBg)
                            .border(
                                width = if (isActive) 1.dp else 0.dp,
                                color = if (isActive) RisingSunRed else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSeekTo(line.timestampMs) }
                            .padding(8.dp)
                            .testTag("lyric_line_$index")
                    ) {
                        // Japanese Kanji Text
                        Text(
                            text = line.textJp,
                            color = if (isActive) Color(0xFFFFD166) else TextCreamLight,
                            fontSize = if (isActive) 14.sp else 12.sp,
                            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium
                        )

                        // Romaji Text
                        Text(
                            text = line.textRomaji,
                            color = if (isActive) Color.White else TextCreamMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                        )

                        // English Translation
                        Text(
                            text = "“${line.textEn}”",
                            color = if (isActive) CityPopCyan else TextCreamMuted.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CassetteSpool(
    angle: Float,
    tapeThickness: Float
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .rotate(angle),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f

            // Outer Tape Ring
            drawCircle(
                color = Color(0xFF4A2518),
                radius = radius,
                center = center
            )

            // Inner White Hub
            drawCircle(
                color = Color(0xFFEFEFEF),
                radius = radius - tapeThickness,
                center = center
            )

            // 6 Gear Teeth
            for (i in 0 until 6) {
                val rad = Math.toRadians((i * 60).toDouble())
                val pX = center.x + (radius * 0.35f * Math.cos(rad)).toFloat()
                val pY = center.y + (radius * 0.35f * Math.sin(rad)).toFloat()
                drawCircle(
                    color = Color(0xFF222228),
                    radius = 2.5f,
                    center = Offset(pX, pY)
                )
            }

            // Center Spindle Hole
            drawCircle(
                color = Color(0xFF16161A),
                radius = 5f,
                center = center
            )
        }
    }
}
