package com.example.ui.panes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioEffectSettings
import com.example.ui.theme.CityPopCyan
import com.example.ui.theme.CityPopTeal
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
fun EqualizerPane(
    effects: AudioEffectSettings,
    onUpdateEffects: ((AudioEffectSettings) -> AudioEffectSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Equalizer Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, VintageNavyBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = VintageNavyCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(RisingSunRed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ANALOG MASTER EQ",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "イコライザー & 真空管音響",
                                color = SunsetOrange,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vintage Tone Shaper & Vinyl Warmth Circuit",
                            color = TextCreamLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "EQ",
                        tint = CityPopCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // 3-Band Frequency Equalizer Sliders
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, VintageNavyBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = VintageNavyCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "FREQUENCY RESPONSE CURVES",
                        color = TextCreamLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Bass Slider (Low Freq 60Hz - 250Hz)
                    EqBandSlider(
                        label = "LOW BASS (60 Hz)",
                        labelJp = "低音",
                        value = effects.bassBoostDb,
                        minVal = -6f,
                        maxVal = 12f,
                        onValueChange = { newDb ->
                            onUpdateEffects { it.copy(bassBoostDb = newDb) }
                        }
                    )

                    // Mid Slider (1 kHz)
                    EqBandSlider(
                        label = "MID VOCAL (1 kHz)",
                        labelJp = "中音・ボーカル",
                        value = effects.midFreqDb,
                        minVal = -6f,
                        maxVal = 12f,
                        onValueChange = { newDb ->
                            onUpdateEffects { it.copy(midFreqDb = newDb) }
                        }
                    )

                    // Treble Slider (8 kHz - 16 kHz)
                    EqBandSlider(
                        label = "HIGH TREBLE (8 kHz)",
                        labelJp = "高音・シンセ",
                        value = effects.trebleFreqDb,
                        minVal = -6f,
                        maxVal = 12f,
                        onValueChange = { newDb ->
                            onUpdateEffects { it.copy(trebleFreqDb = newDb) }
                        }
                    )
                }
            }
        }

        // Vintage Effects & Analog Warmth Switches
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, VintageNavyBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = VintageNavyCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "VINTAGE ANALOG EFFECTS (アナログ効果)",
                        color = TextCreamLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Vinyl Needle Crackle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Vinyl Needle Crackle (レコード針音)",
                                color = TextCreamLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Simulates authentic 1980s LP surface noise & pops",
                                color = TextCreamMuted,
                                fontSize = 9.sp
                            )
                        }
                        Switch(
                            checked = effects.vinylCrackleEnabled,
                            onCheckedChange = { isEnabled ->
                                onUpdateEffects { it.copy(vinylCrackleEnabled = isEnabled) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = RisingSunRed,
                                uncheckedTrackColor = VintageNavyDark
                            )
                        )
                    }

                    // Analog Tube Warmth
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Vacuum Tube Warmth (真空管ウォームサチュレーション)",
                                color = TextCreamLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Harmonic tape distortion and cozy analog fullness",
                                color = TextCreamMuted,
                                fontSize = 9.sp
                            )
                        }
                        Switch(
                            checked = effects.analogWarmth,
                            onCheckedChange = { isWarm ->
                                onUpdateEffects { it.copy(analogWarmth = isWarm) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SunsetOrange,
                                uncheckedTrackColor = VintageNavyDark
                            )
                        )
                    }

                    // 45 RPM Speed Shift
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "45 RPM Single Speed Mode (45回転シングル)",
                                color = TextCreamLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Spins at 1.35x speed with pitch boost (Future Funk style)",
                                color = TextCreamMuted,
                                fontSize = 9.sp
                            )
                        }
                        Switch(
                            checked = effects.is45RpmMode,
                            onCheckedChange = { is45 ->
                                onUpdateEffects { it.copy(is45RpmMode = is45) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CityPopCyan,
                                uncheckedTrackColor = VintageNavyDark
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EqBandSlider(
    label: String,
    labelJp: String,
    value: Float,
    minVal: Float,
    maxVal: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    color = TextCreamLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "($labelJp)",
                    color = SunsetOrange,
                    fontSize = 9.sp
                )
            }
            Text(
                text = String.format("%+.1f dB", value),
                color = CityPopCyan,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = minVal..maxVal,
            modifier = Modifier.height(26.dp),
            colors = SliderDefaults.colors(
                thumbColor = RisingSunRed,
                activeTrackColor = CityPopCyan,
                inactiveTrackColor = VintageNavyDark
            )
        )
    }
}
