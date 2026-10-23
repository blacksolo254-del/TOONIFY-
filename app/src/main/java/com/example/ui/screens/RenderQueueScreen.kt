package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RenderJob
import com.example.model.RenderJobStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun RenderQueueScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val queue by viewModel.renderQueue.collectAsState()

    BackHandler {
        viewModel.navigateBack()
    }

    val activeCount = queue.count { it.status == RenderJobStatus.PROCESSING || it.status == RenderJobStatus.QUEUED }

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
                Column {
                    Text(
                        text = "Asynchronous Render Queue",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$activeCount active background jobs running",
                        color = ToonifyCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Surface(
                color = ToonifyViolet.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "Cluster: H100 GPU",
                    color = ToonifyViolet,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
        ) {
            if (queue.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.HourglassEmpty,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Render Queue is Empty",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Create new videos in the Studio and queue them for background rendering without waiting.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                items(queue, key = { it.jobId }) { job ->
                    RenderJobCard(
                        job = job,
                        onCancel = {
                            viewModel.cancelQueueJob(job.jobId)
                            Toast.makeText(context, "Cancelled ${job.projectTitle}", Toast.LENGTH_SHORT).show()
                        },
                        onBoostPriority = {
                            viewModel.boostQueueJob(job.jobId)
                            Toast.makeText(context, "Boosted to High-Priority GPU Slice!", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun RenderJobCard(
    job: RenderJob,
    onCancel: () -> Unit,
    onBoostPriority: () -> Unit
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (job.status == RenderJobStatus.PROCESSING) 1.5.dp else 1.dp,
            if (job.status == RenderJobStatus.PROCESSING) ToonifyPink else DarkOutline
        ),
        modifier = Modifier.fillMaxWidth().testTag("render_job_${job.jobId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = job.previewThumbnailResId),
                    contentDescription = job.projectTitle,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = job.projectTitle,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        // Status Badge
                        val (statusColor, statusText) = when (job.status) {
                            RenderJobStatus.PROCESSING -> ToonifyPink to "PROCESSING"
                            RenderJobStatus.QUEUED -> ToonifyAmber to "QUEUED"
                            RenderJobStatus.COMPLETED -> ToonifyGreen to "COMPLETED"
                            RenderJobStatus.FAILED -> Color.Red to "FAILED"
                            RenderJobStatus.PAUSED -> TextTertiary to "PAUSED"
                        }

                        Surface(
                            color = statusColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = statusText,
                                color = statusColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "${job.style.label} • ${job.durationSeconds}s clip • ${job.costCredits} cr",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )

                    Text(
                        text = job.currentStepLabel,
                        color = if (job.status == RenderJobStatus.PROCESSING) ToonifyCyan else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            if (job.status == RenderJobStatus.PROCESSING) {
                LinearProgressIndicator(
                    progress = { job.progress },
                    color = ToonifyPink,
                    trackColor = DarkSurfaceCard,
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(job.progress * 100).toInt()}% • ~${job.estimatedSecondsRemaining}s remaining",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (!job.isPriorityQueue) {
                            OutlinedButton(
                                onClick = onBoostPriority,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                                modifier = Modifier.height(28.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyCyan)
                            ) {
                                Icon(Icons.Rounded.Bolt, contentDescription = null, tint = ToonifyCyan, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Boost", fontSize = 10.sp, color = ToonifyCyan, fontWeight = FontWeight.Bold)
                            }
                        }

                        TextButton(
                            onClick = onCancel,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Cancel", color = TextTertiary, fontSize = 11.sp)
                        }
                    }
                }
            } else if (job.status == RenderJobStatus.QUEUED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Queued ${job.queuedTimestamp}",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = onBoostPriority,
                            colors = ButtonDefaults.buttonColors(containerColor = ToonifyViolet),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Rounded.FastForward, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Fast Track", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        TextButton(
                            onClick = onCancel,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Cancel", color = TextTertiary, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ready to download or distribute",
                        color = ToonifyGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Surface(
                        color = DarkSurfaceCard,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Stored in Cloudflare R2",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
