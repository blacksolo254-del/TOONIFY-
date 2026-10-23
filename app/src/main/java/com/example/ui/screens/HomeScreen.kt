package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AiVideo
import com.example.model.VideoCategory
import com.example.ui.components.VideoCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun HomeScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val videos by viewModel.filteredVideos.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val featuredVideo = videos.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // App Header
        HomeTopBar(
            onSearchClick = { viewModel.navigateTo(AppScreen.Search) },
            onStudioClick = { viewModel.navigateTo(AppScreen.Studio) },
            onLeaderboardClick = { viewModel.navigateTo(AppScreen.Leaderboard) },
            onQrClick = { viewModel.navigateTo(AppScreen.QrCode()) },
            onProfileClick = { viewModel.navigateTo(AppScreen.Profile) }
        )

        // Category Filter Chips
        CategoryBar(
            selectedCategory = selectedCategory,
            onSelect = { viewModel.selectCategory(it) }
        )

        // Main Feed List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
        ) {
            // Trending Spotlight Card (only in ALL category and if available)
            if (selectedCategory == VideoCategory.ALL && featuredVideo != null) {
                item {
                    FeaturedSpotlightCard(
                        video = featuredVideo,
                        onWatch = { viewModel.navigateTo(AppScreen.Watch(featuredVideo.id)) },
                        onRemix = { viewModel.loadPromptIntoStudio(featuredVideo) }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            if (videos.isEmpty()) {
                item {
                    EmptyVideosState(selectedCategory = selectedCategory)
                }
            } else {
                items(videos, key = { it.id }) { video ->
                    VideoCard(
                        video = video,
                        onVideoClick = { viewModel.navigateTo(AppScreen.Watch(it.id)) },
                        onRemixClick = { viewModel.loadPromptIntoStudio(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    onSearchClick: () -> Unit,
    onStudioClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onQrClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & Branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("app_branding_logo")
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(ToonifyPink, ToonifyViolet))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "TOONIFY",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                letterSpacing = 1.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
                color = ToonifyCyan.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyCyan.copy(alpha = 0.6f))
            ) {
                Text(
                    text = "AI",
                    color = ToonifyCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }
        }

        // Action Buttons: Weekly Leaderboard + AI Studio shortcut + Search + Avatar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = ToonifyAmber.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyAmber.copy(alpha = 0.5f)),
                modifier = Modifier
                    .clickable(onClick = onLeaderboardClick)
                    .testTag("top_leaderboard_chip")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = ToonifyAmber,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ranks",
                        color = ToonifyAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
                color = ToonifyViolet.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.5f)),
                modifier = Modifier
                    .clickable(onClick = onStudioClick)
                    .testTag("top_ai_create_chip")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = ToonifyPink,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Create",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = onQrClick,
                modifier = Modifier.size(38.dp).testTag("home_qr_code_btn")
            ) {
                Icon(
                    imageVector = Icons.Rounded.QrCode2,
                    contentDescription = "App QR Code & Scanner",
                    tint = ToonifyCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(38.dp).testTag("top_search_btn")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(ToonifyViolet, ToonifyCyan))
                    )
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Y",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun CategoryBar(
    selectedCategory: VideoCategory,
    onSelect: (VideoCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VideoCategory.entries.forEach { category ->
            val isSelected = category == selectedCategory
            Surface(
                color = if (isSelected) ToonifyViolet else DarkSurfaceElevated,
                shape = RoundedCornerShape(14.dp),
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DarkOutline.copy(alpha = 0.6f)),
                modifier = Modifier
                    .clickable { onSelect(category) }
                    .testTag("category_${category.name}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(text = category.iconEmoji, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category.displayName,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF130A2A) else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedSpotlightCard(
    video: AiVideo,
    onWatch: () -> Unit,
    onRemix: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9.5f)
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceElevated)
            .clickable(onClick = onWatch)
            .testTag("featured_spotlight")
    ) {
        Image(
            painter = painterResource(id = video.thumbnailResId),
            contentDescription = video.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient protection
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.92f)
                        )
                    )
                )
        )

        // Top tag: Spotlight AI
        Surface(
            color = ToonifyPink,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Whatshot,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "FEATURED TOON",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Bottom info and quick actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = video.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${video.creatorName} • ${video.viewsFormatted} views • ${video.aiModelUsed}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onWatch,
                    colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Watch Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onRemix,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyCyan),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = ToonifyCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remix Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EmptyVideosState(selectedCategory: VideoCategory) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.MovieFilter,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "No videos yet in ${selectedCategory.displayName}",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Be the first creator to generate an AI cartoon for this category!",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
