package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavigationTab
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedDark
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SunsetGold
import com.example.ui.theme.VintageCream
import com.example.ui.theme.VintageCreamMuted
import com.example.ui.theme.WaveTeal
import com.example.ui.theme.WaveTealDark

/**
 * Left Navigation Sidebar:
 * - Background: Deep Navy (#151C2B)
 * - Text & Icons: Vintage Cream (#F0EAD6)
 * - Selected Tab: Crimson Red (#CE3232) pill-shaped background
 * - Volume Control: Wave Teal (#4A7C8C) & Vintage Cream
 */
@Composable
fun NavigationRailSona(
    activeTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    masterVolume: Float,
    onVolumeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        color = DeepNavy,
        shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Sona Brand Badge & 1984 Logo (Crimson Red & Vintage Cream)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(CrimsonRed, CrimsonRedDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ソナ",
                        color = VintageCream,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "SONA",
                    color = VintageCream,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "音響 1984",
                    color = SunsetGold.copy(alpha = 0.9f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Scrollable Nav Items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = activeTab == tab
                    val tabBgColor by animateColorAsState(
                        targetValue = if (isSelected) CrimsonRed else Color.Transparent,
                        label = "tab_bg"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) VintageCream else VintageCreamMuted,
                        label = "tab_fg"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tabBgColor)
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("nav_tab_${tab.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            BadgedBox(
                                badge = {
                                    if (tab == NavigationTab.HIRES && !isSelected) {
                                        Badge(
                                            containerColor = WaveTeal,
                                            contentColor = VintageCream
                                        ) {
                                            Text(
                                                text = "24b",
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.titleEn,
                                    tint = contentColor,
                                    modifier = Modifier.size(19.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = tab.titleEn,
                                color = contentColor,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )

                            Text(
                                text = tab.titleJp,
                                color = contentColor.copy(alpha = 0.75f),
                                fontSize = 7.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Bottom Master Volume Scrubber (Wave Teal & Vintage Cream)
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F1522))
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (masterVolume > 0.5f) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        contentDescription = "Volume",
                        tint = WaveTeal,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${(masterVolume * 100).toInt()}%",
                        color = VintageCream,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = masterVolume,
                    onVolumeChanged,
                    valueRange = 0f..1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .testTag("volume_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = CrimsonRed,
                        activeTrackColor = CrimsonRed,
                        inactiveTrackColor = WaveTealDark
                    )
                )

                Text(
                    text = "音量 VOL",
                    color = VintageCreamMuted.copy(alpha = 0.7f),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
