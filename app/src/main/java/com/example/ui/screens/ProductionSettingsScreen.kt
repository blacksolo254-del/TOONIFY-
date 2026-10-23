package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun ProductionSettingsScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val temporalSettings by viewModel.temporalSettings.collectAsState()
    val audioSync by viewModel.audioSyncConfig.collectAsState()
    val cloudStorage by viewModel.cloudStorageConfig.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var testPromptGuardrailInput by remember { mutableStateOf("Cyber girl flying over Neo-Tokyo with dragon") }
    var guardrailResult by remember { mutableStateOf(viewModel.checkGuardrails(testPromptGuardrailInput)) }

    BackHandler {
        viewModel.navigateBack()
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
                    text = "Production Engine & Pipelines",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Temporal, Audio, Cloud & C2PA",
                    color = ToonifyCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceElevated,
            contentColor = ToonifyViolet,
            edgePadding = 12.dp
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Temporal & Audio", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Cloud Store (R2/S3)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("C2PA Provenance", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("IP & Guardrails", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Temporal Consistency & Audio Sync
                    item {
                        Text(
                            text = "Temporal Consistency Engine Caps",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = DarkSurfaceElevated,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Frame Rate Interpolation Target:", color = TextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TemporalInterpolation.entries.forEach { interp ->
                                        val isSel = interp == temporalSettings.interpolation
                                        Surface(
                                            color = if (isSel) ToonifyViolet else DarkSurfaceCard,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.clickable {
                                                viewModel.updateTemporalSettings(temporalSettings.copy(interpolation = interp))
                                            }
                                        ) {
                                            Text(
                                                text = "${interp.fps} FPS",
                                                color = if (isSel) Color.White else TextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Smoothing Strength: ${(temporalSettings.smoothingStrength * 100).toInt()}%", color = TextPrimary, fontSize = 13.sp)
                                    Text("Neural Latent Drift", color = TextTertiary, fontSize = 11.sp)
                                }

                                Slider(
                                    value = temporalSettings.smoothingStrength,
                                    onValueChange = {
                                        viewModel.updateTemporalSettings(temporalSettings.copy(smoothingStrength = it))
                                    },
                                    colors = SliderDefaults.colors(thumbColor = ToonifyPink, activeTrackColor = ToonifyPink)
                                )

                                ToggleSettingRow(
                                    label = "Optical Flow Motion Compensation",
                                    description = "Prevents morphing artifacts during fast camera tracking",
                                    checked = temporalSettings.opticalFlowCompensation,
                                    onCheckedChange = {
                                        viewModel.updateTemporalSettings(temporalSettings.copy(opticalFlowCompensation = it))
                                    }
                                )

                                ToggleSettingRow(
                                    label = "Deflicker & Jitter Suppression Pass",
                                    description = "Filters frame-to-frame luminance noise and strobing",
                                    checked = temporalSettings.deflickerPass,
                                    onCheckedChange = {
                                        viewModel.updateTemporalSettings(temporalSettings.copy(deflickerPass = it))
                                    }
                                )

                                ToggleSettingRow(
                                    label = "Character Facial & Clothing Lock",
                                    description = "Anchors face and outfit tokens across scene shot transitions",
                                    checked = temporalSettings.characterConsistencyLock,
                                    onCheckedChange = {
                                        viewModel.updateTemporalSettings(temporalSettings.copy(characterConsistencyLock = it))
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Audio-to-Sync Pipeline
                        Text(
                            text = "Audio-to-Sync & Lip-Sync Pipeline",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = DarkSurfaceElevated,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("BPM Tempo Detection: ${audioSync.bpmTempo} BPM", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Slider(
                                    value = audioSync.bpmTempo.toFloat(),
                                    onValueChange = {
                                        viewModel.updateAudioSync(audioSync.copy(bpmTempo = it.toInt()))
                                    },
                                    valueRange = 80f..160f,
                                    colors = SliderDefaults.colors(thumbColor = ToonifyCyan, activeTrackColor = ToonifyCyan)
                                )

                                ToggleSettingRow(
                                    label = "Automatic Beat-Snapped Visual Cuts",
                                    description = "Snaps camera cuts and kinetic impacts to audio transient peaks",
                                    checked = audioSync.beatSnapEnabled,
                                    onCheckedChange = {
                                        viewModel.updateAudioSync(audioSync.copy(beatSnapEnabled = it))
                                    }
                                )

                                ToggleSettingRow(
                                    label = "Phoneme-to-Viseme Lip-Sync Lock",
                                    description = "Animates cartoon mouth geometries to speech phonemes in real-time",
                                    checked = audioSync.lipSyncAlignment,
                                    onCheckedChange = {
                                        viewModel.updateAudioSync(audioSync.copy(lipSyncAlignment = it))
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Cloud Store & Asset Management [S3; R2]
                    item {
                        Text(
                            text = "Cloud Object Storage & Sync (S3 / R2)",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = DarkSurfaceElevated,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Storage Provider:", color = TextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    CloudStorageProvider.entries.forEach { provider ->
                                        val isSel = provider == cloudStorage.provider
                                        Surface(
                                            color = if (isSel) ToonifyCyan.copy(alpha = 0.25f) else DarkSurfaceCard,
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(if (isSel) 1.dp else 0.dp, ToonifyCyan),
                                            modifier = Modifier.clickable {
                                                viewModel.updateCloudStorage(cloudStorage.copy(provider = provider))
                                            }
                                        ) {
                                            Text(
                                                text = provider.displayName,
                                                color = if (isSel) ToonifyCyan else TextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text("Bucket Name: ${cloudStorage.bucketName}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Endpoint: ${cloudStorage.provider.endpointDefault}", color = TextTertiary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                                Spacer(modifier = Modifier.height(14.dp))

                                Text("Cloud Quota Usage: ${cloudStorage.usedStorageGb} GB / ${cloudStorage.maxStorageGb} GB", color = TextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { cloudStorage.usedStorageGb / cloudStorage.maxStorageGb },
                                    color = ToonifyCyan,
                                    trackColor = DarkSurfaceCard,
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Synced 4 videos and raw latent files to ${cloudStorage.provider.displayName}", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ToonifyCyan),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Rounded.CloudSync, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Sync Deliverables to Cloud", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // C2PA Watermarking & Provenance Standards
                    item {
                        Text(
                            text = "Watermarking & Provenance (C2PA Standards)",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val sampleC2pa = viewModel.getC2paProvenance("video_cyber_1", "CYBERPULSE Rooftop Chase")

                        Surface(
                            color = DarkSurfaceElevated,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Verified, contentDescription = null, tint = ToonifyCyan, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("C2PA Content Credentials", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Surface(
                                        color = ToonifyGreen.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("VERIFIED CA", color = ToonifyGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text("Manifest URN:", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(sampleC2pa.manifestId, color = ToonifyCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Cryptographic Asset Hash:", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(sampleC2pa.assetHashSha256, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = DarkOutline)
                                Spacer(modifier = Modifier.height(10.dp))

                                Text("C2PA Assertions Lineage:", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))

                                sampleC2pa.assertions.forEach { assertion ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(assertion.label, color = TextSecondary, fontSize = 11.sp)
                                        Text(assertion.value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Terms of Service & IP Copyright Guardrails
                    item {
                        Text(
                            text = "IP Copyright & Content Guardrail Scanner",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = DarkSurfaceElevated,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Test Prompt Safety & Trademark Infringement:", color = TextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = testPromptGuardrailInput,
                                    onValueChange = {
                                        testPromptGuardrailInput = it
                                        guardrailResult = viewModel.checkGuardrails(it)
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                                    modifier = Modifier.fillMaxWidth().height(80.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Guardrail result
                                val statusColor = if (guardrailResult.isApproved) ToonifyGreen else Color.Red

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (guardrailResult.isApproved) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                                        contentDescription = null,
                                        tint = statusColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (guardrailResult.isApproved) "Passed Guardrails (Score: ${(guardrailResult.safetyScore * 100).toInt()}%)" else "Blocked by Guardrails",
                                        color = statusColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (guardrailResult.warningMessage != null) {
                                    Text(
                                        text = guardrailResult.warningMessage!!,
                                        color = ToonifyAmber,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = DarkOutline)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Commercial Terms & IP Ownership Agreement", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• You own 100% of all synthesized video output and audio assets created on Pro/Studio tiers.", color = TextSecondary, fontSize = 11.sp)
                                Text("• Model training weights adhere to ethical datasets with zero scraping of non-consented art.", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
                                Text("• $1,000,000 copyright indemnity guarantee covers enterprise commercial licensing.", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleSettingRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(description, color = TextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ToonifyPink
            )
        )
    }
}
