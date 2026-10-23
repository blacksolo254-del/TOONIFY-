package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.VideoCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun ProfileScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val userCredits by viewModel.userCredits.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    val videos by viewModel.videos.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    BackHandler {
        viewModel.navigateBack()
    }

    val myCreations = videos.filter { it.isUserUploaded }
    val savedVideos = videos.filter { it.isSaved }

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                Text(
                    text = "Creator Studio Hub",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.QrCode(initialTab = 0)) },
                modifier = Modifier.size(36.dp).testTag("profile_qr_btn")
            ) {
                Icon(
                    imageVector = Icons.Rounded.QrCode2,
                    contentDescription = "App QR Code & Scanner",
                    tint = ToonifyCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
        ) {
            // Profile Header Card
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("profile_header_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(ToonifyPink, ToonifyViolet, ToonifyCyan))
                                )
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "T",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "ToonMaster Studio",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "@toonmaster • Member since 2026",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // SaaS Plan & Credit Badges
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = ToonifyPink.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyPink.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = currentPlan.title,
                                    color = ToonifyPink,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                color = ToonifyAmber.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyAmber.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "$userCredits AI Credits",
                                    color = ToonifyAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Hub Portals (Subscriptions, Marketplace, Enterprise API)
            item {
                Text(
                    text = "Monetization & Developer Portals",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                HubActionRow(
                    icon = Icons.Rounded.Token,
                    title = "Credits & SaaS Subscriptions",
                    subtitle = "Manage $userCredits credits, upgrade to Studio Master",
                    tint = ToonifyAmber,
                    onClick = { viewModel.navigateTo(AppScreen.PricingAndPlans) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                HubActionRow(
                    icon = Icons.Rounded.Storefront,
                    title = "Asset & LoRA Marketplace",
                    subtitle = "Install community styles or sell custom character rigs",
                    tint = ToonifyViolet,
                    onClick = { viewModel.navigateTo(AppScreen.Marketplace) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                HubActionRow(
                    icon = Icons.Rounded.Code,
                    title = "Enterprise API & Commercial Rights",
                    subtitle = "SDK keys, webhook configs, and legal clearance certs",
                    tint = ToonifyCyan,
                    onClick = { viewModel.navigateTo(AppScreen.EnterpriseApi) }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Studio Production & Engine Tools
            item {
                Text(
                    text = "Studio Production & Engine Pipeline",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                HubActionRow(
                    icon = Icons.Rounded.EmojiEvents,
                    title = "Weekly Hall of Fame & Rankings",
                    subtitle = "Compete for top creator podiums, weekly prizes, and stars",
                    tint = ToonifyAmber,
                    onClick = { viewModel.navigateTo(AppScreen.Leaderboard) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                HubActionRow(
                    icon = Icons.Rounded.MovieFilter,
                    title = "Multi-Track Canvas Timeline Editor",
                    subtitle = "128 BPM beat-snapped visual cuts, phoneme-to-viseme sync",
                    tint = ToonifyCyan,
                    onClick = { viewModel.navigateTo(AppScreen.Canvas) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                HubActionRow(
                    icon = Icons.Rounded.Schedule,
                    title = "GPU Background Render Queue",
                    subtitle = "Monitor asynchronous generation cluster & export jobs",
                    tint = ToonifyPink,
                    onClick = { viewModel.navigateTo(AppScreen.RenderQueue) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                HubActionRow(
                    icon = Icons.Rounded.Tune,
                    title = "Temporal Consistency & C2PA Provenance",
                    subtitle = "Optical flow deflicker, audio sync, S3/R2 storage, IP guardrails",
                    tint = ToonifyViolet,
                    onClick = { viewModel.navigateTo(AppScreen.ProductionSettings) }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Tabs: My Creations vs Saved Videos
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurfaceElevated,
                    contentColor = ToonifyViolet
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("My AI Creations (${myCreations.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Saved Toons (${savedVideos.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            val currentList = if (selectedTab == 0) myCreations else savedVideos

            if (currentList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Rounded.VideoCall else Icons.Rounded.BookmarkBorder,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedTab == 0) "No videos published yet" else "No saved videos",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        if (selectedTab == 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.Studio) },
                                colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink)
                            ) {
                                Text("Create with AI Studio")
                            }
                        }
                    }
                }
            } else {
                items(currentList, key = { it.id }) { video ->
                    VideoCard(
                        video = video,
                        onVideoClick = { viewModel.navigateTo(AppScreen.Watch(video.id)) },
                        onRemixClick = { viewModel.loadPromptIntoStudio(video) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HubActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = TextTertiary
            )
        }
    }
}
