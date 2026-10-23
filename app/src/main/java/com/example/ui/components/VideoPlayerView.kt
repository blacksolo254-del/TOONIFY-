package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiVideo
import com.example.ui.theme.ToonifyCyan
import com.example.ui.theme.ToonifyPink
import com.example.ui.theme.ToonifyViolet
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerView(
    video: AiVideo,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    aspectRatio: Float = 16f / 9f
) {
    var isPlaying by remember { mutableStateOf(autoPlay) }
    var currentProgressSeconds by remember { mutableFloatStateOf(0f) }
    var controlsVisible by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isMuted by remember { mutableStateOf(false) }

    // Auto-advance progress when playing
    LaunchedEffect(isPlaying, playbackSpeed, video.id) {
        while (isPlaying) {
            delay(100)
            currentProgressSeconds += (0.1f * playbackSpeed)
            if (currentProgressSeconds >= video.durationSeconds) {
                currentProgressSeconds = 0f // loop
            }
        }
    }

    // Auto-hide controls after 3 seconds of user inactivity
    LaunchedEffect(controlsVisible, isPlaying) {
        if (controlsVisible && isPlaying) {
            delay(3500)
            controlsVisible = false
        }
    }

    // Simulated camera drift animation for dynamic video sensation
    val infiniteTransition = rememberInfiniteTransition(label = "camera_drift")
    val driftScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPlaying) 1.06f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                controlsVisible = !controlsVisible
            }
            .testTag("video_player_container"),
        contentAlignment = Alignment.Center
    ) {
        // Video Visual / Canvas
        Image(
            painter = painterResource(id = video.thumbnailResId),
            contentDescription = video.title,
            modifier = Modifier
                .fillMaxSize()
                .scale(driftScale),
            contentScale = ContentScale.Crop
        )

        // Subtle motion scanline gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        // Overlay Controls
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Top Bar in player: Model badge + Quality + Speed chip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // AI Model Badge
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = ToonifyCyan,
                                modifier = Modifier.size(14.dp)
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

                    // Speed & Mute
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = CircleShape,
                            modifier = Modifier
                                .clickable {
                                    playbackSpeed = when (playbackSpeed) {
                                        1.0f -> 1.5f
                                        1.5f -> 2.0f
                                        else -> 1.0f
                                    }
                                }
                                .padding(2.dp)
                        ) {
                            Text(
                                text = "${playbackSpeed}x",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                                contentDescription = "Volume",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Center Play / Pause button
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(58.dp)
                        .align(Alignment.Center)
                        .background(
                            Brush.linearGradient(listOf(ToonifyViolet, ToonifyPink)),
                            CircleShape
                        )
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Bottom scrubber and timecodes
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    // Scrub slider
                    val progressFraction = (currentProgressSeconds / video.durationSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)

                    Slider(
                        value = progressFraction,
                        onValueChange = { frac ->
                            currentProgressSeconds = frac * video.durationSeconds
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = ToonifyPink,
                            activeTrackColor = ToonifyPink,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val currentMins = (currentProgressSeconds.toInt() / 60)
                        val currentSecs = (currentProgressSeconds.toInt() % 60)
                        val formattedCurrent = "$currentMins:${currentSecs.toString().padStart(2, '0')}"

                        Text(
                            text = "$formattedCurrent / ${video.durationFormatted}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // 4K resolution pill
                        Surface(
                            color = ToonifyViolet.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = video.resolution,
                                color = ToonifyCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Persistent subtle playing indicator at bottom edge when controls hidden
        if (!controlsVisible && isPlaying) {
            val progressFraction = (currentProgressSeconds / video.durationSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.BottomCenter),
                color = ToonifyPink,
                trackColor = Color.Transparent
            )
        }
    }
}
