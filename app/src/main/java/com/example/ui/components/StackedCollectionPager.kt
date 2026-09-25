package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Track
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedDark
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepNavyMuted
import com.example.ui.theme.DeepNavyText
import com.example.ui.theme.SunsetGold
import com.example.ui.theme.VintageCream
import com.example.ui.theme.VintageCreamDarker
import com.example.ui.theme.VintageCreamLight
import com.example.ui.theme.WaveTeal
import com.example.ui.theme.WaveTealDark
import com.example.util.CrateSoundPoolManager
import kotlin.math.absoluteValue
import kotlin.math.sign

enum class CrateViewMode {
    STACK_CRATE_FLIP, // 3D Vertical Record Crate Flip
    SPINE_PERSPECTIVE_SHELF // 3D Horizontal Jewel Case Spine Shelf (Matching User Reference)
}

/**
 * 3D Stacked Collection View
 *
 * Implements physical record crate flipping and 3D skewed jewel-case spine perspective
 * with zero-latency SoundPool audio feedback and tactile haptics.
 */
@Composable
fun StackedCollectionPager(
    tracks: List<Track>,
    currentTrack: Track,
    isPlaying: Boolean,
    onTrackSelect: (Track) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tracks.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "コレクションが空です (No saved records)",
                color = VintageCreamLight,
                fontSize = 13.sp
            )
        }
        return
    }

    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val density = LocalDensity.current

    // Initialize low-latency SoundPool for crate flip SFX
    val soundPoolManager = remember { CrateSoundPoolManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            soundPoolManager.release()
        }
    }

    var viewMode by remember { mutableStateOf(CrateViewMode.SPINE_PERSPECTIVE_SHELF) }
    val initialPage = remember(tracks, currentTrack) {
        val idx = tracks.indexOfFirst { it.id == currentTrack.id }
        if (idx != -1) idx else 0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F1522))
            .padding(8.dp)
    ) {
        // 1. Top Header Bar: Collection Title + 3D View Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF161F2E))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SunsetGold)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "3D CRATE",
                        color = DeepNavyText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RECORD CRATE COLLECTION (${tracks.size})",
                    color = VintageCream,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // View Mode Switcher
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0B101A))
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spine Shelf Mode
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (viewMode == CrateViewMode.SPINE_PERSPECTIVE_SHELF) CrimsonRed else Color.Transparent)
                        .clickable { viewMode = CrateViewMode.SPINE_PERSPECTIVE_SHELF }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("crate_mode_shelf_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ViewCarousel,
                            contentDescription = "Spine Shelf",
                            tint = VintageCream,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "3D Spines",
                            color = VintageCream,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Vertical Crate Flip Mode
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (viewMode == CrateViewMode.STACK_CRATE_FLIP) WaveTeal else Color.Transparent)
                        .clickable { viewMode = CrateViewMode.STACK_CRATE_FLIP }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("crate_mode_stack_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Vertical Flip",
                            tint = VintageCream,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Crate Flip",
                            color = VintageCream,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Interactive 3D Cascading Stage
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0C1019),
                            Color(0xFF070A10)
                        )
                    )
                )
                .border(1.dp, Color(0xFF1B2436), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            when (viewMode) {
                CrateViewMode.SPINE_PERSPECTIVE_SHELF -> {
                    SpinePerspectiveShelfPager(
                        tracks = tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        initialPage = initialPage,
                        onTrackSelect = onTrackSelect,
                        onToggleFavorite = onToggleFavorite,
                        onPageChanged = {
                            soundPoolManager.playFlipSound(volume = 0.7f)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    )
                }
                CrateViewMode.STACK_CRATE_FLIP -> {
                    VerticalCrateFlipPager(
                        tracks = tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        initialPage = initialPage,
                        onTrackSelect = onTrackSelect,
                        onToggleFavorite = onToggleFavorite,
                        onPageChanged = {
                            soundPoolManager.playFlipSound(volume = 0.8f)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    )
                }
            }
        }
    }
}

/**
 * 3D Spine Perspective Shelf (Matching User Image Reference):
 * Skewed 3D Jewel Cases with physical spine thickness, spine typography, and fluid horizontal perspective
 */
@Composable
private fun SpinePerspectiveShelfPager(
    tracks: List<Track>,
    currentTrack: Track,
    isPlaying: Boolean,
    initialPage: Int,
    onTrackSelect: (Track) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onPageChanged: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (tracks.size - 1).coerceAtLeast(0)),
        pageCount = { tracks.size }
    )

    // Trigger sound and haptics on page change
    var lastSettledPage by remember { mutableIntStateOf(pagerState.currentPage) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { newPage ->
            if (newPage != lastSettledPage) {
                lastSettledPage = newPage
                onPageChanged()
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("spine_perspective_shelf_container"),
        contentAlignment = Alignment.Center
    ) {
        val containerWidth = maxWidth
        val containerHeight = maxHeight

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = containerWidth * 0.32f),
            pageSpacing = 16.dp
        ) { page ->
            val track = tracks[page]
            val isCurrent = track.id == currentTrack.id
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
            val absOffset = pageOffset.absoluteValue.coerceIn(0f, 3f)

            // 3D Matrix & Perspective Calculations
            val rotationY = (pageOffset * -38f).coerceIn(-65f, 65f)
            val scale = (1f - absOffset * 0.15f).coerceIn(0.68f, 1.05f)
            val translationX = pageOffset * -24f
            val alpha = (1f - absOffset * 0.28f).coerceIn(0.4f, 1f)
            val zIndexVal = 100f - absOffset * 10f

            Box(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .width(containerWidth * 0.42f)
                    .zIndex(zIndexVal)
                    .graphicsLayer {
                        this.rotationY = rotationY
                        this.scaleX = scale
                        this.scaleY = scale
                        this.translationX = translationX
                        this.alpha = alpha
                        this.cameraDistance = 24f * density
                    }
                    .clickable {
                        onTrackSelect(track)
                    }
                    .testTag("spine_card_$page"),
                contentAlignment = Alignment.Center
            ) {
                // Physical 3D Jewel Case Container with Front Cover & Realistic Spine Thickness
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = if (absOffset < 0.3f) 20.dp else 8.dp,
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    // Left 3D Spine (Physical Thickness with Vertical Typography)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(22.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(track.primaryColorHex).copy(alpha = 0.95f),
                                        Color(track.secondaryColorHex).copy(alpha = 0.85f),
                                        Color(0xFF10131B)
                                    )
                                )
                            )
                            .border(0.5.dp, VintageCream.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Vertical Rotated Spine Text (Title + Artist)
                        Text(
                            text = "${track.title} • ${track.artist}",
                            color = VintageCream,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .graphicsLayer {
                                    rotationZ = -90f
                                }
                                .width(containerHeight * 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Front Album Art Face
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color(0xFF141824))
                            .border(1.dp, Color(0xFF242C3F))
                    ) {
                        // Album Cover Art (or rich generative vintage graphic)
                        if (track.albumArtUri != null || track.albumArtUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(track.albumArtUri ?: track.albumArtUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = track.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Authentic Retro City Pop Sleeve Art
                            GenerativeRetroCoverArt(track = track)
                        }

                        // Glass Overlay & Specular Sheen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.12f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.6f)
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(300f, 500f)
                                    )
                                )
                        )

                        // Top & Bottom Metadata Badges
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CrimsonRed.copy(alpha = 0.9f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = track.catalogNumber,
                                        color = VintageCream,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                IconButton(
                                    onClick = { onToggleFavorite(track.id) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (track.isFavorite) CrimsonRed else VintageCream.copy(alpha = 0.8f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }

                            // Active Now Playing / Play Button Indicator
                            if (isCurrent && isPlaying) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(WaveTeal.copy(alpha = 0.9f))
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Equalizer,
                                        contentDescription = "Playing",
                                        tint = VintageCream,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "SPINNING",
                                        color = VintageCream,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3D Vertical Record Crate Flip:
 * Tilted isometric record sleeves stacked tightly with overlapping translations and rotationX animations.
 */
@Composable
private fun VerticalCrateFlipPager(
    tracks: List<Track>,
    currentTrack: Track,
    isPlaying: Boolean,
    initialPage: Int,
    onTrackSelect: (Track) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onPageChanged: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (tracks.size - 1).coerceAtLeast(0)),
        pageCount = { tracks.size }
    )

    // Trigger sound and haptics on page change
    var lastSettledPage by remember { mutableIntStateOf(pagerState.currentPage) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { newPage ->
            if (newPage != lastSettledPage) {
                lastSettledPage = newPage
                onPageChanged()
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vertical_crate_flip_container"),
        contentAlignment = Alignment.Center
    ) {
        val containerHeight = maxHeight
        val containerWidth = maxWidth

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = containerHeight * 0.22f),
            pageSpacing = (-50).dp // Overlap sleeves tightly like a physical crate
        ) { page ->
            val track = tracks[page]
            val isCurrent = track.id == currentTrack.id
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
            val absOffset = pageOffset.absoluteValue.coerceIn(0f, 3f)

            // Physical Crate Math:
            // Top items tilt back (rotationX ~ 52 deg) and slide down, active item rotates forward and pops up
            val rotationX = (52f - (1f - absOffset.coerceIn(0f, 1f)) * 32f).coerceIn(18f, 58f)
            val scale = (1f - absOffset * 0.12f).coerceIn(0.72f, 1.0f)
            val translationY = if (pageOffset > 0) pageOffset * 35f else pageOffset * 20f
            val alpha = (1f - absOffset * 0.22f).coerceIn(0.5f, 1f)
            val zIndexVal = if (pageOffset >= 0) 100f - pageOffset * 10f else 100f + pageOffset * 2f

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .height(containerHeight * 0.48f)
                    .zIndex(zIndexVal)
                    .graphicsLayer {
                        this.rotationX = rotationX
                        this.scaleX = scale
                        this.scaleY = scale
                        this.translationY = translationY
                        this.alpha = alpha
                        this.cameraDistance = 18f * density
                    }
                    .clickable {
                        onTrackSelect(track)
                    }
                    .testTag("crate_flip_item_$page"),
                contentAlignment = Alignment.Center
            ) {
                // Physical Vinyl Record Jacket with Bottom Spine Thickness Lip
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = if (absOffset < 0.3f) 16.dp else 6.dp,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF141824))
                        .border(1.2.dp, VintageCream.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                ) {
                    // Main Album Face
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        GenerativeRetroCoverArt(track = track)

                        // Gradient Sheen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.08f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.55f)
                                        )
                                    )
                                )
                        )

                        // Top Catalog Tag & Favorite
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SunsetGold)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = track.catalogNumber,
                                    color = DeepNavyText,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            IconButton(
                                onClick = { onToggleFavorite(track.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Fav",
                                    tint = if (track.isFavorite) CrimsonRed else VintageCream,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Front Facing Bottom Spine / Edge Lip (Physical Thickness Illusion)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(track.primaryColorHex),
                                        Color(0xFF0F141F)
                                    )
                                )
                            )
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                color = VintageCream,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${track.artist} (${track.artistJp})",
                                color = VintageCreamLight,
                                fontSize = 7.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (isCurrent && isPlaying) {
                            Icon(
                                imageVector = Icons.Default.Equalizer,
                                contentDescription = "Playing",
                                tint = SunsetGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Authentic 1980s Japanese City Pop Graphic Art Generator for Covers
 */
@Composable
private fun GenerativeRetroCoverArt(
    track: Track,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(track.primaryColorHex),
                        Color(track.secondaryColorHex),
                        Color(0xFF101420)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SONA STEREO",
                color = VintageCream.copy(alpha = 0.9f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = track.titleJp,
                    color = VintageCream,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = track.title,
                    color = SunsetGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    color = VintageCreamLight,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "${track.year} • ${track.album}",
                color = VintageCream.copy(alpha = 0.75f),
                fontSize = 7.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
