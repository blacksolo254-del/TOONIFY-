package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TimelineClip
import com.example.model.TrackType
import com.example.ui.theme.*
import com.example.ui.viewmodel.ToonifyViewModel
import kotlinx.coroutines.delay

@Composable
fun CanvasEditorScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clips by viewModel.timelineClips.collectAsState()
    val audioSync by viewModel.audioSyncConfig.collectAsState()

    var playheadSeconds by remember { mutableFloatStateOf(4.5f) }
    var isPlayingTimeline by remember { mutableStateOf(false) }
    var selectedClip by remember { mutableStateOf<TimelineClip?>(clips.firstOrNull()) }

    BackHandler {
        viewModel.navigateBack()
    }

    LaunchedEffect(isPlayingTimeline) {
        while (isPlayingTimeline) {
            delay(100)
            playheadSeconds += 0.1f
            if (playheadSeconds >= 15f) {
                playheadSeconds = 0f
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top Bar
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
                Column {
                    Text(
                        text = "Multi-Track Video Canvas",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Non-Linear AI Animation Timeline",
                        color = ToonifyCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Button(
                onClick = {
                    val queued = viewModel.enqueueStudioJobToQueue()
                    if (queued) {
                        Toast.makeText(context, "Queued Timeline Canvas to Render Cluster!", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp).testTag("render_canvas_btn")
            ) {
                Icon(Icons.Rounded.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Render Master", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Timeline Toolbar & Transport Controls
        Surface(
            color = DarkSurfaceElevated,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause transport
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isPlayingTimeline = !isPlayingTimeline },
                        modifier = Modifier
                            .size(36.dp)
                            .background(ToonifyViolet, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlayingTimeline) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isPlayingTimeline) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = String.format("00:%04.1fs / 15.0s", playheadSeconds),
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Audio BPM & Lip-Sync Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = DarkSurfaceCard,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = ToonifyCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${audioSync.bpmTempo} BPM Beat-Locked",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Multi-Track Timeline Canvas Editor
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            item {
                Text(
                    text = "Timeline Tracks (4 Layer Composition)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Track 1: Video Keyframes
            item {
                TimelineTrackRow(
                    trackLabel = "Track 1: Video Latents",
                    trackColor = ToonifyViolet,
                    clips = clips.filter { it.trackType == TrackType.VIDEO_KEYFRAMES },
                    playheadSeconds = playheadSeconds,
                    selectedClipId = selectedClip?.clipId,
                    onSelectClip = { selectedClip = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Track 2: Audio & Foley Beats
            item {
                TimelineTrackRow(
                    trackLabel = "Track 2: Audio & Beats",
                    trackColor = ToonifyCyan,
                    clips = clips.filter { it.trackType == TrackType.AUDIO_TRACK },
                    playheadSeconds = playheadSeconds,
                    selectedClipId = selectedClip?.clipId,
                    onSelectClip = { selectedClip = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Track 3: Voiceover & Lip-Sync
            item {
                TimelineTrackRow(
                    trackLabel = "Track 3: Voiceover Lip-Sync",
                    trackColor = ToonifyAmber,
                    clips = clips.filter { it.trackType == TrackType.VOICEOVER_LIPSYNC },
                    playheadSeconds = playheadSeconds,
                    selectedClipId = selectedClip?.clipId,
                    onSelectClip = { selectedClip = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Track 4: VFX Overlays
            item {
                TimelineTrackRow(
                    trackLabel = "Track 4: VFX & Particles",
                    trackColor = ToonifyPink,
                    clips = clips.filter { it.trackType == TrackType.VFX_OVERLAY },
                    playheadSeconds = playheadSeconds,
                    selectedClipId = selectedClip?.clipId,
                    onSelectClip = { selectedClip = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Selected Clip Inspector
            if (selectedClip != null) {
                item {
                    val clip = selectedClip!!
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                        modifier = Modifier.fillMaxWidth().testTag("clip_inspector_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = clip.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Surface(
                                    color = DarkSurfaceCard,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${clip.durationSeconds}s duration",
                                        color = ToonifyCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Prompt Direction: \"${clip.promptKeyword}\"",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(context, "Split ${clip.name} at ${playheadSeconds}s", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
                                ) {
                                    Icon(Icons.Rounded.ContentCut, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Split at Playhead", fontSize = 11.sp, color = TextPrimary)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.removeTimelineClip(clip.clipId)
                                        selectedClip = null
                                        Toast.makeText(context, "Deleted clip", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyPink.copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Rounded.Delete, contentDescription = null, tint = ToonifyPink, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remove", fontSize = 11.sp, color = ToonifyPink)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineTrackRow(
    trackLabel: String,
    trackColor: Color,
    clips: List<TimelineClip>,
    playheadSeconds: Float,
    selectedClipId: String?,
    onSelectClip: (TimelineClip) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = trackLabel,
                color = trackColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DarkSurfaceElevated)
        ) {
            // Horizontal container with proportional clip blocks
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                clips.forEach { clip ->
                    val weight = (clip.durationSeconds / 15f).coerceAtLeast(0.1f)
                    val isSelected = clip.clipId == selectedClipId

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weight)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) trackColor else trackColor.copy(alpha = 0.5f)
                            )
                            .clickable { onSelectClip(clip) }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = clip.name,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(3.dp))
                }
            }

            // Playhead red needle
            val playheadFraction = (playheadSeconds / 15f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = (320 * playheadFraction).dp)
                    .background(Color.Red)
            )
        }
    }
}
