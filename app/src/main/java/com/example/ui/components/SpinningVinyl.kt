package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedDark
import com.example.ui.theme.SunsetGold
import com.example.ui.theme.VintageCream
import com.example.ui.theme.VintageCreamLight
import com.example.ui.theme.VinylBlack
import com.example.ui.theme.VinylGroove
import com.example.ui.theme.VinylHighlight
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin

/**
 * 1980s Retro City Pop Vinyl Record:
 * - Constant, invariant disc geometry and outer/inner proportions
 * - Pitch black disc (#0C0D11) with fine concentric grey groove rings
 * - Stark Crimson Red (#CE3232) center label with consistent City Pop editorial typography
 * - Center spindle hole with Vintage Cream border
 */
@Composable
fun SpinningVinyl(
    albumArtUri: Uri?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    rpmSpeedMultiplier: Float = 1.0f,
    fallbackTitle: String = "Sona City Pop",
    fallbackArtist: String = "Stereo 1984"
) {
    val context = LocalContext.current

    // Rotation physics: persists current angle across play/pause cycles
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying, rpmSpeedMultiplier) {
        if (isPlaying) {
            var lastFrameTime = withFrameNanos { it }
            while (isActive) {
                withFrameNanos { frameTime ->
                    val deltaSeconds = (frameTime - lastFrameTime) / 1_000_000_000f
                    lastFrameTime = frameTime
                    val degreesPerSecond = 200f * rpmSpeedMultiplier
                    rotationAngle = (rotationAngle + degreesPerSecond * deltaSeconds) % 360f
                }
            }
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(16.dp, CircleShape)
            .clip(CircleShape)
            .background(VinylBlack)
            .border(
                width = 1.5.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        SunsetGold.copy(alpha = 0.6f),
                        CrimsonRed.copy(alpha = 0.4f),
                        SunsetGold.copy(alpha = 0.6f)
                    )
                ),
                shape = CircleShape
            )
            .graphicsLayer {
                rotationZ = rotationAngle
            }
            .testTag("spinning_vinyl_component"),
        contentAlignment = Alignment.Center
    ) {
        // Pitch black disc with fine concentric groove rings and radial light reflection
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.width / 2f

            // Concentric groove rings
            val grooveCount = 28
            for (i in 5 until grooveCount) {
                val r = maxRadius * (i.toFloat() / grooveCount.toFloat())
                val alpha = if (i % 4 == 0) 0.35f else 0.12f
                drawCircle(
                    color = VinylGroove.copy(alpha = alpha),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1.2f)
                )
            }

            // Radial sheen lines
            val sheenColor = VinylHighlight.copy(alpha = 0.18f)
            for (angle in listOf(45f, 225f)) {
                val rad = Math.toRadians(angle.toDouble())
                val endX = center.x + (maxRadius * cos(rad)).toFloat()
                val endY = center.y + (maxRadius * sin(rad)).toFloat()
                drawLine(
                    color = sheenColor,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 32f,
                    cap = StrokeCap.Round
                )
            }

            // Outer lead-in and run-out borders
            drawCircle(
                color = Color(0xFF141720),
                radius = maxRadius * 0.46f,
                center = center,
                style = Stroke(width = 3.5f)
            )
        }

        // Stark Crimson Red (#CE3232) Center Label (Constant 44% Disc Diameter)
        Box(
            modifier = Modifier
                .fillMaxSize(0.44f)
                .clip(CircleShape)
                .border(2.dp, VintageCream.copy(alpha = 0.9f), CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CrimsonRed,
                            CrimsonRedDark
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Authentic City Pop Poster Center Label Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CITY POP",
                    color = VintageCream,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "SIDE A",
                            color = VintageCreamLight,
                            fontSize = 6.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "33⅓ RPM",
                            color = VintageCream.copy(alpha = 0.85f),
                            fontSize = 5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "ステレオ",
                            color = VintageCreamLight,
                            fontSize = 6.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "STEREO",
                            color = VintageCream.copy(alpha = 0.85f),
                            fontSize = 5.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "シティ・ポップ",
                        color = VintageCream,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "1980–1989",
                        color = VintageCream.copy(alpha = 0.75f),
                        fontSize = 5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Center Spindle Hole with Vintage Cream Rim
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0C0D11))
                    .border(1.5.dp, VintageCream, CircleShape)
            )
        }
    }
}
