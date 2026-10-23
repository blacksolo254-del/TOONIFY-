package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import com.example.model.AiVideo
import com.example.model.WeeklyCreatorLeaderboardEntry
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun WeeklyLeaderboardScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val videos by viewModel.videos.collectAsState()
    val weeklyCreators by viewModel.weeklyCreators.collectAsState()

    var leaderboardCategory by remember { mutableIntStateOf(0) } // 0 = Best Videos, 1 = Top Starred Creators

    BackHandler {
        viewModel.navigateBack()
    }

    val rankedVideos = remember(videos) {
        videos.sortedByDescending { it.weeklyPoints }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "Weekly Hall of Fame",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Weekly Ranked Best AI Toons & Creators",
                    color = ToonifyAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Leaderboard Tabs
        TabRow(
            selectedTabIndex = leaderboardCategory,
            containerColor = DarkSurfaceElevated,
            contentColor = ToonifyAmber
        ) {
            Tab(
                selected = leaderboardCategory == 0,
                onClick = { leaderboardCategory = 0 },
                text = { Text("🏆 Ranked AI Videos", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = leaderboardCategory == 1,
                onClick = { leaderboardCategory = 1 },
                text = { Text("⭐ Top Starred Creators", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
        ) {
            // Weekly Reset Countdown Card
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("weekly_countdown_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ToonifyAmber.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⏳", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Week 42 Official Rankings",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Scores update dynamically from Stars, Likes & Gifts",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            color = DarkSurfaceCard,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "3d 14h left",
                                color = ToonifyCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Podiums for Top 3
            if (leaderboardCategory == 0 && rankedVideos.size >= 3) {
                item {
                    Text(
                        text = "Podium: Top 3 Best Toons of the Week",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 2nd Place Silver (Left)
                        PodiumColumn(
                            rank = 2,
                            badgeEmoji = "🥈",
                            badgeColor = Color(0xFFC0C0C0),
                            video = rankedVideos[1],
                            onWatch = { viewModel.navigateTo(AppScreen.Watch(rankedVideos[1].id)) },
                            modifier = Modifier.weight(1f).height(175.dp)
                        )

                        // 1st Place Gold Crown (Center - Elevated!)
                        PodiumColumn(
                            rank = 1,
                            badgeEmoji = "👑",
                            badgeColor = ToonifyAmber,
                            video = rankedVideos[0],
                            onWatch = { viewModel.navigateTo(AppScreen.Watch(rankedVideos[0].id)) },
                            modifier = Modifier.weight(1.1f).height(205.dp)
                        )

                        // 3rd Place Bronze (Right)
                        PodiumColumn(
                            rank = 3,
                            badgeEmoji = "🥉",
                            badgeColor = Color(0xFFCD7F32),
                            video = rankedVideos[2],
                            onWatch = { viewModel.navigateTo(AppScreen.Watch(rankedVideos[2].id)) },
                            modifier = Modifier.weight(1f).height(160.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            // Full Leaderboard Rankings List
            if (leaderboardCategory == 0) {
                item {
                    Text(
                        text = "All Ranked AI Videos (${rankedVideos.size})",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(rankedVideos) { video ->
                    RankedVideoRow(
                        video = video,
                        onWatch = { viewModel.navigateTo(AppScreen.Watch(video.id)) },
                        onStarCreator = { viewModel.toggleStar(video.creatorHandle) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            } else {
                item {
                    Text(
                        text = "Most Starred Creators of the Week",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(weeklyCreators) { creator ->
                    RankedCreatorRow(
                        creator = creator,
                        onStarToggle = { viewModel.toggleStar(creator.creatorHandle) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun PodiumColumn(
    rank: Int,
    badgeEmoji: String,
    badgeColor: Color,
    video: AiVideo,
    onWatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, badgeColor),
        modifier = modifier.clickable(onClick = onWatch)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(badgeEmoji, fontSize = 24.sp)
                Text(
                    text = "#$rank",
                    color = badgeColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "${video.weeklyPoints} pts",
                    color = ToonifyCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Image(
                painter = painterResource(id = video.thumbnailResId),
                contentDescription = video.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = video.title,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RankedVideoRow(
    video: AiVideo,
    onWatch: () -> Unit,
    onStarCreator: () -> Unit
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkOutline),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onWatch)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number Badge
            val rankColor = when (video.weeklyRank) {
                1 -> ToonifyAmber
                2 -> Color(0xFFC0C0C0)
                3 -> Color(0xFFCD7F32)
                else -> TextSecondary
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(rankColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${video.weeklyRank ?: '-'}",
                    color = rankColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Image(
                painter = painterResource(id = video.thumbnailResId),
                contentDescription = video.title,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = "${video.creatorName} (${video.creatorStars})",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text("⭐ ${video.weeklyPoints} pts", color = ToonifyAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("• 👍 ${video.likesFormatted}", color = TextTertiary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("• 💬 ${video.commentsCount}", color = TextTertiary, fontSize = 10.sp)
                }
            }

            if (video.weeklyPrizeBadge != null) {
                Surface(
                    color = ToonifyPink.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = video.weeklyPrizeBadge.take(12),
                        color = ToonifyPink,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RankedCreatorRow(
    creator: WeeklyCreatorLeaderboardEntry,
    onStarToggle: () -> Unit
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(ToonifyViolet.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${creator.rank}",
                    color = ToonifyViolet,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = creator.creatorName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (creator.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Verified",
                            tint = ToonifyCyan,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Text(
                    text = "${creator.starsCount} Stars • +${creator.starsGainedThisWeek} gained this week",
                    color = ToonifyAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Weekly Prize Pool: +${creator.prizeCreditsReward} Credits",
                    color = ToonifyGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onStarToggle,
                colors = ButtonDefaults.buttonColors(containerColor = ToonifyAmber),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Icon(Icons.Rounded.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Star", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
