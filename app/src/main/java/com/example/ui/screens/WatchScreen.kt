package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiVideo
import com.example.model.VideoComment
import com.example.ui.components.SendPrizeDialog
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun WatchScreen(
    videoId: String,
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val videos by viewModel.videos.collectAsState()
    val video = videos.find { it.id == videoId } ?: videos.firstOrNull()

    var showPromptDetails by remember { mutableStateOf(false) }
    var commentInput by remember { mutableStateOf("") }
    var showSendPrizeDialog by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateBack()
    }

    if (video == null) {
        Box(
            modifier = modifier.fillMaxSize().background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Video not found", color = TextPrimary)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Sticky Header: Back button + Live Video Player
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            VideoPlayerView(
                video = video,
                autoPlay = true
            )

            // Back button overlaid on top-left
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .padding(8.dp)
                    .size(38.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .align(Alignment.TopStart)
                    .testTag("watch_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Scrollable Video Details & Comments
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
        ) {
            // Title
            item {
                Text(
                    text = video.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${video.viewsFormatted} views • ${video.uploadedAgo}",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = DarkSurfaceCard,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = video.category.displayName,
                            color = ToonifyCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Channel & Subscribe Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(ToonifyViolet, ToonifyCyan))
                                )
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = video.creatorName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = video.creatorName,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (video.isVerifiedCreator) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = ToonifyCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${video.creatorStars} Stars ⭐",
                                    color = ToonifyAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (video.weeklyRank != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = ToonifyAmber.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "🏆 #${video.weeklyRank}",
                                            color = ToonifyAmber,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Star Creator Button (giving Stars instead of subscribe)
                    Button(
                        onClick = { viewModel.toggleStar(video.creatorHandle) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (video.isStarred) DarkSurfaceCard else ToonifyAmber
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("star_creator_button")
                    ) {
                        Text(
                            text = if (video.isStarred) "⭐ Starred" else "⭐ Give Star",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (video.isStarred) TextSecondary else Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Interactive Action Buttons Row (Like, Dislike, Send Prize, Remix, Share, Save)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Like / Dislike pill group
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                modifier = Modifier
                                    .clickable { viewModel.toggleLike(video.id) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("watch_like_button"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (video.isLiked) Icons.Rounded.ThumbUp else Icons.Rounded.ThumbUpOffAlt,
                                    contentDescription = "Like",
                                    tint = if (video.isLiked) ToonifyPink else TextPrimary,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = video.likesFormatted,
                                    color = if (video.isLiked) ToonifyPink else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(DarkOutline)
                            )

                            IconButton(
                                onClick = { viewModel.toggleDislike(video.id) },
                                modifier = Modifier.size(34.dp).testTag("watch_dislike_button")
                            ) {
                                Icon(
                                    imageVector = if (video.isDisliked) Icons.Rounded.ThumbDown else Icons.Rounded.ThumbDownOffAlt,
                                    contentDescription = "Dislike",
                                    tint = if (video.isDisliked) ToonifyPink else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Send Prize / Gift Button
                    Surface(
                        color = ToonifyPink.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyPink.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .clickable { showSendPrizeDialog = true }
                            .testTag("watch_send_prize_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CardGiftcard,
                                contentDescription = null,
                                tint = ToonifyPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Send Prize",
                                color = ToonifyPink,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Remix Button
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable { viewModel.loadPromptIntoStudio(video) }
                            .testTag("watch_remix_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = ToonifyCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Remix AI",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Share Button
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Watch '${video.title}' generated with Toonify AI Video Studio! Prompt: ${video.aiPrompt}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share AI Toon"))
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = "Share",
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Share",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Video QR Code Button
                    Surface(
                        color = ToonifyCyan.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyCyan.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clickable {
                                val videoUrl = "https://toonify.ai/watch/${video.id}"
                                viewModel.navigateTo(AppScreen.QrCode(targetUrl = videoUrl, initialTab = 0))
                            }
                            .testTag("watch_qr_code_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.QrCode2,
                                contentDescription = null,
                                tint = ToonifyCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "QR Code",
                                color = ToonifyCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Save Button
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { viewModel.toggleSave(video.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (video.isSaved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (video.isSaved) ToonifyViolet else TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // AI Prompt & Parameters Inspector Card
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPromptDetails = !showPromptDetails },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Psychology,
                                    contentDescription = null,
                                    tint = ToonifyCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI Generation Prompt & Metadata",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Icon(
                                imageVector = if (showPromptDetails) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }

                        // Preview text
                        Text(
                            text = video.aiPrompt,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            maxLines = if (showPromptDetails) Int.MAX_VALUE else 2,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        AnimatedVisibility(visible = showPromptDetails) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                HorizontalDivider(color = DarkOutline, modifier = Modifier.padding(vertical = 8.dp))

                                MetaItem(label = "AI Engine / Model", value = video.aiModelUsed)
                                MetaItem(label = "Camera Motion", value = video.cameraMotion)
                                MetaItem(label = "Seed Number", value = video.seed.toString())
                                MetaItem(label = "Guidance Scale", value = video.guidanceScale.toString())
                                MetaItem(label = "Negative Prompt", value = video.aiNegativePrompt)

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(video.aiPrompt))
                                            Toast.makeText(context, "AI Prompt copied!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ToonifyCyan),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyCyan),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.ContentCopy,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Copy Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.loadPromptIntoStudio(video) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Remix in Studio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Comments Section Header & Input
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Comments (${video.commentsCount})",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Post a Comment Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        placeholder = { Text("Add an AI feedback comment...", color = TextTertiary, fontSize = 13.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_input_field")
                    )

                    IconButton(
                        onClick = {
                            if (commentInput.isNotBlank()) {
                                viewModel.addComment(video.id, commentInput)
                                commentInput = ""
                                Toast.makeText(context, "Comment posted!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("submit_comment_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = "Post Comment",
                            tint = if (commentInput.isNotBlank()) ToonifyViolet else TextTertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Comments List
            items(video.comments, key = { it.id }) { comment ->
                CommentItem(comment = comment)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (showSendPrizeDialog) {
            SendPrizeDialog(
                video = video,
                viewModel = viewModel,
                onDismiss = { showSendPrizeDialog = false }
            )
        }
    }
}

@Composable
private fun MetaItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(text = label, color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = TextPrimary, fontSize = 12.sp)
    }
}

@Composable
private fun CommentItem(comment: VideoComment) {
    val isPrizeGift = comment.authorBadge.startsWith("🎁")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isPrizeGift) ToonifyAmber.copy(alpha = 0.12f) else DarkSurface)
            .then(
                if (isPrizeGift) {
                    Modifier.border(
                        1.dp,
                        Brush.horizontalGradient(listOf(ToonifyAmber, ToonifyPink)),
                        RoundedCornerShape(12.dp)
                    )
                } else Modifier
            )
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(DarkSurfaceCard),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.authorName.take(1).uppercase(),
                color = ToonifyCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                if (comment.authorBadge.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = ToonifyViolet.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = comment.authorBadge,
                            color = ToonifyViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = comment.timeAgo,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = comment.text,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.ThumbUp,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${comment.likesCount}",
                    color = TextTertiary,
                    fontSize = 11.sp
                )

                if (comment.isHeartedByCreator) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = "Loved by creator",
                        tint = ToonifyPink,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Creator loved",
                        color = ToonifyPink,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
