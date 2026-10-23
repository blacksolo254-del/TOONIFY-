package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiVideo
import com.example.ui.theme.*

@Composable
fun VideoCard(
    video: AiVideo,
    onVideoClick: (AiVideo) -> Unit,
    onRemixClick: (AiVideo) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onVideoClick(video) }
            .testTag("video_card_${video.id}")
            .padding(bottom = 18.dp)
    ) {
        // Thumbnail Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
        ) {
            Image(
                painter = painterResource(id = video.thumbnailResId),
                contentDescription = video.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Top-left AI Model tag
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, ToonifyViolet.copy(alpha = 0.6f)),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = ToonifyCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = video.aiModelUsed,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Bottom-right Duration badge
            Surface(
                color = Color.Black.copy(alpha = 0.85f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            ) {
                Text(
                    text = video.durationFormatted,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Info Row: Avatar + Title & Meta + Overflow Menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Channel Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(ToonifyViolet, ToonifyCyan)
                        )
                    )
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceCard),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = video.creatorName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Channel info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = video.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = video.creatorName,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (video.isVerifiedCreator) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Verified Creator",
                            tint = ToonifyCyan,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⭐ ${video.creatorStars}",
                        color = ToonifyAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (video.weeklyRank != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = ToonifyAmber.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "#${video.weeklyRank}",
                                color = ToonifyAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = " • ${video.viewsFormatted} views",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                }
            }

            // More Options dropdown
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(DarkSurfaceCard)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = ToonifyPink,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Remix in AI Studio", color = TextPrimary)
                            }
                        },
                        onClick = {
                            showMenu = false
                            onRemixClick(video)
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = null,
                                    tint = ToonifyCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copy AI Prompt", color = TextPrimary)
                            }
                        },
                        onClick = {
                            showMenu = false
                            clipboardManager.setText(AnnotatedString(video.aiPrompt))
                            Toast.makeText(context, "AI Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = null,
                                    tint = ToonifyViolet,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share Video", color = TextPrimary)
                            }
                        },
                        onClick = {
                            showMenu = false
                            Toast.makeText(context, "Sharing link to ${video.title}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
