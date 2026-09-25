package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationTab(
    val titleEn: String,
    val titleJp: String,
    val icon: ImageVector,
    val badge: String? = null
) {
    BROWSE("Browse", "ディスカバリー", Icons.Default.Explore),
    COLLECTION("Collection", "コレクション", Icons.Default.LibraryMusic),
    CHARTS("Charts", "ランキング", Icons.Default.Whatshot, "80s"),
    HIRES("Hi-Res", "ハイレゾ", Icons.Default.HighQuality, "96k"),
    SPOTIFY("Spotify Remote", "スポティファイ", Icons.Default.Podcasts, "SDK"),
    CASSETTE("Cassette & Lyrics", "カセット歌詞", Icons.Default.Radio),
    EQUALIZER("Analog EQ", "イコライザー", Icons.Default.GraphicEq)
}
