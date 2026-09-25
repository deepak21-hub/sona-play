package com.example.ui.panes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Track
import com.example.ui.components.TrackCard
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepNavyMuted
import com.example.ui.theme.DeepNavyText
import com.example.ui.theme.SunsetGold
import com.example.ui.theme.VintageCream
import com.example.ui.theme.VintageCreamLight
import com.example.ui.theme.VintageCreamMuted
import com.example.ui.theme.WaveTeal
import com.example.ui.theme.WaveTealDark

/**
 * Center Dashboard (Library & Discovery):
 * - Main backdrop: Deep Navy (#151C2B)
 * - Floating cards: Vintage Cream (#F0EAD6) with Deep Navy (#151C2B) text
 * - Selected category chips: Crimson Red (#CE3232)
 * - Unselected category chips: Wave Teal (#4A7C8C)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowsePane(
    tracks: List<Track>,
    allTracks: List<Track>,
    currentTrack: Track,
    isPlaying: Boolean,
    searchQuery: String,
    selectedCategory: String,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onTrackSelect: (Track) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "All (すべて)",
        "City Pop (シティポップ)",
        "Anime OST (アニメOST)",
        "Hi-Res Lossless (ハイレゾ)",
        "Favorites (お気に入り)"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search & Category Chips Header (Aligned to Left Edge)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search Input Field in Vintage Cream surface with Deep Navy text
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("search_text_field"),
                    placeholder = {
                        Text(
                            text = "Search City Pop, Anime OST, Artist... (検索)",
                            color = DeepNavyMuted.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = CrimsonRed,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = DeepNavyMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = DeepNavyText,
                        unfocusedTextColor = DeepNavyText,
                        focusedContainerColor = VintageCream,
                        unfocusedContainerColor = VintageCream
                    ),
                    singleLine = true
                )

                // Category Chips Row: Selected = Crimson Red (#CE3232), Unselected = Wave Teal (#4A7C8C)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CrimsonRed else WaveTeal)
                                .clickable { onCategorySelected(category) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("cat_chip_$category"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                color = VintageCream,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // "Recently Spun Vinyl" Section: Floating Vintage Cream Cards with Deep Navy Text
        if (searchQuery.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RECENTLY SPUN",
                                color = VintageCream,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "最近再生したレコード",
                                color = SunsetGold,
                                fontSize = 9.sp
                            )
                        }

                        Text(
                            text = "JAPANESE GROOVE",
                            color = VintageCreamMuted,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(allTracks.take(4)) { track ->
                            val isThisTrack = currentTrack.id == track.id
                            Surface(
                                modifier = Modifier
                                    .width(135.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onTrackSelect(track) },
                                color = if (isThisTrack) VintageCreamLight else VintageCream,
                                shape = RoundedCornerShape(16.dp),
                                shadowElevation = if (isThisTrack) 4.dp else 2.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Mini pitch black vinyl with Crimson Red center label
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        CrimsonRed,
                                                        Color(0xFF0C0D11)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "45",
                                            color = VintageCream,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = track.title,
                                        color = if (isThisTrack) CrimsonRed else DeepNavyText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = track.artist,
                                        color = DeepNavyMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Tracks Library with Editorial Poster Japanese Tag
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CITY POP & RETRO SELECTIONS",
                        color = VintageCream,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${tracks.size}曲)",
                        color = SunsetGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "暮らしの音楽",
                    color = VintageCreamMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Full Tracks Library: Floating Vintage Cream Cards with Deep Navy Text
        items(tracks, key = { it.id }) { track ->
            TrackCard(
                track = track,
                isSelected = currentTrack.id == track.id,
                isPlaying = isPlaying,
                onTrackClick = { onTrackSelect(track) },
                onToggleFavorite = { onToggleFavorite(track.id) }
            )
        }
    }
}
