package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.SunsetGold
import com.example.ui.theme.VintageCream
import com.example.ui.theme.WaveTeal
import com.example.ui.theme.WaveTealDark

@Composable
fun HalftoneBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Deep Navy (#151C2B) retro gradient backdrop with subtle Wave Teal undertone
        val bgGradient = if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    DeepNavy,
                    Color(0xFF111724),
                    DeepNavyDark
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFBF7EE),
                    VintageCream,
                    Color(0xFFE5DECA)
                )
            )
        }
        drawRect(brush = bgGradient)

        // Subtle vintage halftone dot grid
        val dotColor = if (isDark) VintageCream.copy(alpha = 0.05f) else Color(0x148B5A2B)
        val step = 32f
        var x = 0f
        while (x < width) {
            var y = 0f
            while (y < height) {
                val radius = if ((x.toInt() / 32 + y.toInt() / 32) % 2 == 0) 1.5f else 0.8f
                drawCircle(
                    color = dotColor,
                    radius = radius,
                    center = Offset(x, y)
                )
                y += step
            }
            x += step
        }

        // Japanese Sun Ambient Arc at top right
        if (isDark) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CrimsonRed.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.88f, height * 0.15f),
                    radius = width * 0.28f
                ),
                radius = width * 0.28f,
                center = Offset(width * 0.88f, height * 0.15f)
            )

            // Wave Teal subtle glow at bottom left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WaveTeal.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.15f, height * 0.85f),
                    radius = width * 0.25f
                ),
                radius = width * 0.25f,
                center = Offset(width * 0.15f, height * 0.85f)
            )
        }
    }
}
