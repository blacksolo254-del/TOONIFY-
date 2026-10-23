package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiVideo
import com.example.ui.theme.*
import com.example.ui.viewmodel.ToonifyViewModel
import kotlinx.coroutines.delay

@Composable
fun ShortsScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videos by viewModel.videos.collectAsState()
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentVideo = videos.getOrNull(currentIndex) ?: videos.firstOrNull()

    var showPromptSheet by remember { mutableStateOf(false) }
    var showHeartAnimation by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateBack()
    }

    if (currentVideo == null) {
        Box(modifier = modifier.fillMaxSize().background(DarkBackground))
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(currentVideo.id) {
                detectTapGestures(
                    onDoubleTap = {
                        viewModel.toggleLike(currentVideo.id)
                        showHeartAnimation = true
                    }
                )
            }
            .testTag("shorts_fullscreen_container")
    ) {
        // Video Fullbleed Artwork
        Image(
            painter = painterResource(id = currentVideo.thumbnailResId),
            contentDescription = currentVideo.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Vignette Gradient Overlays
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Top Controls: Back + "ToonReels" + Next Reel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.PlayCircle,
                    contentDescription = null,
                    tint = ToonifyPink,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ToonReels",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Next reel button
            IconButton(
                onClick = {
                    currentIndex = (currentIndex + 1) % videos.size
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Next Reel",
                    tint = Color.White
                )
            }
        }

        // Floating Action Buttons on Right (Like, Comments, Remix, Prompt, Share)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like
            ShortsActionButton(
                icon = if (currentVideo.isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                label = currentVideo.likesFormatted,
                tint = if (currentVideo.isLiked) ToonifyPink else Color.White,
                onClick = { viewModel.toggleLike(currentVideo.id) }
            )

            // Comments
            ShortsActionButton(
                icon = Icons.Rounded.ChatBubbleOutline,
                label = "${currentVideo.commentsCount}",
                tint = Color.White,
                onClick = {
                    Toast.makeText(context, "${currentVideo.commentsCount} comments on this ToonReel", Toast.LENGTH_SHORT).show()
                }
            )

            // Remix with AI
            ShortsActionButton(
                icon = Icons.Rounded.AutoAwesome,
                label = "Remix",
                tint = ToonifyCyan,
                onClick = { viewModel.loadPromptIntoStudio(currentVideo) }
            )

            // Prompt Inspector
            ShortsActionButton(
                icon = Icons.Rounded.Psychology,
                label = "Prompt",
                tint = ToonifyViolet,
                onClick = { showPromptSheet = true }
            )

            // Share
            ShortsActionButton(
                icon = Icons.Rounded.Share,
                label = "Share",
                tint = Color.White,
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Watch '${currentVideo.title}' on Toonify AI Reels!")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share ToonReel"))
                }
            )
        }

        // Bottom Left Video & Creator Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.82f)
                .padding(start = 14.dp, bottom = 90.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ToonifyViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentVideo.creatorName.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = currentVideo.creatorHandle,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.toggleSubscribe(currentVideo.creatorHandle) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentVideo.isSubscribed) DarkSurfaceCard else ToonifyPink
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = if (currentVideo.isSubscribed) "Subscribed" else "Subscribe",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentVideo.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            // AI Audio badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    tint = ToonifyCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Original AI Audio • ${currentVideo.aiModelUsed}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
            }
        }

        // Heart Burst Animation on Double Tap
        if (showHeartAnimation) {
            LaunchedEffect(Unit) {
                delay(700)
                showHeartAnimation = false
            }

            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = ToonifyPink,
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.Center)
            )
        }
    }

    // Prompt Inspector Dialog
    if (showPromptSheet) {
        AlertDialog(
            onDismissRequest = { showPromptSheet = false },
            title = {
                Text("AI Prompt & Generation Seed", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(currentVideo.aiPrompt, color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Model: ${currentVideo.aiModelUsed}", color = ToonifyCyan, fontSize = 11.sp)
                    Text("Camera: ${currentVideo.cameraMotion}", color = ToonifyViolet, fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.loadPromptIntoStudio(currentVideo)
                        showPromptSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink)
                ) {
                    Text("Remix in Studio", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPromptSheet = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
private fun ShortsActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color.Black.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
