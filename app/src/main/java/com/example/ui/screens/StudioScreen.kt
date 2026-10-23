package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.model.AiVideo
import com.example.model.ArtStyle
import com.example.model.CameraMotion
import com.example.model.StoryboardScene
import com.example.model.VideoCategory
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StudioUiState
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun StudioScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val studioState by viewModel.studioState.collectAsState()
    var studioTab by remember { mutableIntStateOf(0) }

    BackHandler {
        viewModel.navigateBack()
    }

    val inspirationChips = listOf(
        "Neon samurai duel on bullet train in rain",
        "Baby dragon learning to fly in cozy canyon",
        "Chef raccoon balancing giant towering burger",
        "Fluffy cat piloting wooden airship over sakura",
        "Retro robot tending glowing cyber garden"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Studio Top Bar
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
                        text = "AI Creator Studio",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Prompt-to-Video Engine",
                        color = ToonifyCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quick QR Code button for Creator Channel & App
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.QrCode(initialTab = 1)) },
                    modifier = Modifier.size(36.dp).testTag("studio_qr_btn")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.QrCode2,
                        contentDescription = "Studio QR Code & Scanner",
                        tint = ToonifyCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                val userCredits by viewModel.userCredits.collectAsState()
                Surface(
                    color = DarkSurfaceCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.PricingAndPlans) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Token,
                            contentDescription = null,
                            tint = ToonifyAmber,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$userCredits cr",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                if (studioState.directorPlan != null || studioState.renderedVideo != null) {
                    TextButton(
                        onClick = { viewModel.resetStudio() },
                        colors = ButtonDefaults.textButtonColors(contentColor = TextTertiary)
                    ) {
                        Text("Reset", fontSize = 12.sp)
                    }
                }
            }
        }

        // Studio Mode Tabs: 0 = Creator Hub (My Videos & Rank), 1 = AI Studio Gen, 2 = Post Any Video
        TabRow(
            selectedTabIndex = studioTab,
            containerColor = DarkSurfaceElevated,
            contentColor = ToonifyPink,
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = studioTab == 0,
                onClick = { studioTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Dashboard, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Creator Hub", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            )
            Tab(
                selected = studioTab == 1,
                onClick = { studioTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Studio Gen", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            )
            Tab(
                selected = studioTab == 2,
                onClick = { studioTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post Video", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            )
        }

        when (studioTab) {
            0 -> {
                CreatorStudioHubView(
                    viewModel = viewModel,
                    onOpenAiGen = { studioTab = 1 },
                    onOpenCustomPost = { studioTab = 2 }
                )
            }
            1 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
                ) {
            // If video has been rendered, show prominent preview & Publish Card!
            if (studioState.renderedVideo != null) {
                item {
                    val rendered = studioState.renderedVideo!!
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyPink.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth().testTag("rendered_video_preview_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = ToonifyGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AI Video Successfully Generated!",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Surface(
                                    color = ToonifyPink.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "60 FPS 4K",
                                        color = ToonifyPink,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Playable Player View
                            VideoPlayerView(
                                video = rendered,
                                autoPlay = true,
                                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = rendered.title,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = rendered.description,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 2,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Publish to Community Feed Button
                            Button(
                                onClick = {
                                    viewModel.publishRenderedVideo()
                                    Toast.makeText(context, "Published to Toonify Community!", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ToonifyPink
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("publish_to_feed_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CloudUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Publish to Toonify Feed",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Rendering In Progress Indicator
            if (studioState.isRendering) {
                item {
                    RenderingProgressCard(studioState = studioState)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Step 1: Input Idea
            item {
                Text(
                    text = "1. Describe Your Video Idea",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Inspiration Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    inspirationChips.forEach { chip ->
                        Surface(
                            color = DarkSurfaceCard,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkOutline),
                            modifier = Modifier.clickable { viewModel.setStudioIdea(chip) }
                        ) {
                            Text(
                                text = "✨ $chip",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Input Text Field
                OutlinedTextField(
                    value = studioState.ideaInput,
                    onValueChange = { viewModel.setStudioIdea(it) },
                    placeholder = {
                        Text(
                            "e.g. A tiny baby robot trying to water a glowing flower on a lunar station...",
                            color = TextTertiary,
                            fontSize = 14.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = ToonifyViolet,
                        unfocusedBorderColor = DarkOutline,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("studio_idea_input")
                )

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Step 2: Choose Art Direction / Style
            item {
                Text(
                    text = "2. Select Animation Art Style",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArtStyle.entries.forEach { style ->
                        val isSelected = style == studioState.selectedStyle
                        Surface(
                            color = if (isSelected) ToonifyViolet.copy(alpha = 0.25f) else DarkSurfaceElevated,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 0.5.dp,
                                if (isSelected) ToonifyViolet else DarkOutline
                            ),
                            modifier = Modifier
                                .width(150.dp)
                                .clickable { viewModel.setStudioStyle(style) }
                                .testTag("style_${style.name}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = style.label,
                                    color = if (isSelected) ToonifyViolet else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = style.description,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 3,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Step 3: Camera Dynamics & Duration
            item {
                Text(
                    text = "3. Camera Movement & Duration",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CameraMotion.entries.forEach { camera ->
                        val isSelected = camera == studioState.selectedCamera
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setStudioCamera(camera) },
                            label = { Text(camera.label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ToonifyCyan.copy(alpha = 0.25f),
                                selectedLabelColor = ToonifyCyan,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = ToonifyCyan,
                                borderColor = DarkOutline
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Clip Duration: ${studioState.durationSeconds}s",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(5, 10, 15, 30).forEach { sec ->
                            Surface(
                                color = if (studioState.durationSeconds == sec) ToonifyViolet else DarkSurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { viewModel.setStudioDuration(sec) }
                            ) {
                                Text(
                                    text = "${sec}s",
                                    color = if (studioState.durationSeconds == sec) Color.White else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Action: AI Director Plan Generation
            item {
                Button(
                    onClick = { viewModel.generateAiDirectorPlan() },
                    enabled = !studioState.isDirectorGenerating && !studioState.isRendering,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ToonifyVioletDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("ai_director_plan_btn")
                ) {
                    if (studioState.isDirectorGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Director is writing storyboard...", fontSize = 13.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = ToonifyCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (studioState.directorPlan != null) "✦ Re-Craft AI Storyboard & Prompt" else "✦ AI Director: Craft Scene & Script",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Error message banner if insufficient credits
            if (studioState.errorMessage != null) {
                item {
                    Surface(
                        color = Color(0xFF38101E),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyPink),
                        modifier = Modifier.fillMaxWidth().testTag("studio_error_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Warning, contentDescription = null, tint = ToonifyPink)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(studioState.errorMessage!!, color = TextPrimary, fontSize = 12.sp, lineHeight = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.PricingAndPlans) },
                                colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Top Up", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Display AI Director's Generated Plan & Storyboard
            if (studioState.directorPlan != null) {
                val plan = studioState.directorPlan!!

                item {
                    DirectorPlanCard(
                        plan = plan,
                        onGenerateVideo = { viewModel.startVideoRendering() },
                        isRendering = studioState.isRendering
                    )
                }
            }
        }
    }
    2 -> {
        CustomVideoPostView(viewModel = viewModel)
    }
}
}
}

@Composable
private fun DirectorPlanCard(
    plan: com.example.model.AiCreatorPrompt,
    onGenerateVideo: () -> Unit,
    isRendering: Boolean
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyCyan.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().testTag("director_plan_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.MovieCreation,
                        contentDescription = null,
                        tint = ToonifyCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Director Storyboard",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = ToonifyViolet.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = plan.recommendedModel,
                        color = ToonifyViolet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Suggested Title: \"${plan.suggestedTitle}\"",
                color = ToonifyPink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Engineered Diffusion Prompt:",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = plan.fullPrompt,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceCard)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Multi-shot Storyboard
            Text(
                text = "Shot-by-Shot Timeline:",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            plan.scenes.forEach { scene ->
                StoryboardSceneItem(scene = scene)
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Audio notes
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    tint = ToonifyCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI Sound Design: ${plan.soundDesign}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            val cost = plan.durationSeconds.let { sec ->
                when (sec) {
                    5 -> 15
                    10 -> 25
                    15 -> 35
                    30 -> 60
                    else -> 25
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Token,
                        contentDescription = null,
                        tint = ToonifyAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cost: $cost credits",
                        color = ToonifyAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "High Priority GPU Diffusion",
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Step 2 Button: Generate & Render Video
            Button(
                onClick = onGenerateVideo,
                enabled = !isRendering,
                colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("render_ai_video_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🎬 Render AI Video ($cost Credits)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StoryboardSceneItem(scene: StoryboardScene) {
    Surface(
        color = DarkSurfaceCard,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(ToonifyCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${scene.sceneNumber}",
                    color = ToonifyCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${scene.title} (${scene.shotType}) • ${scene.durationSeconds}s",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = scene.actionDescription,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun RenderingProgressCard(studioState: StudioUiState) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyPink),
        modifier = Modifier.fillMaxWidth().testTag("rendering_progress_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                progress = { studioState.renderProgress },
                color = ToonifyPink,
                trackColor = DarkSurfaceCard,
                modifier = Modifier.size(52.dp),
                strokeWidth = 5.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Rendering AI Video Clip (${(studioState.renderProgress * 100).toInt()}%)",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = studioState.renderPhaseLabel,
                color = ToonifyCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CustomVideoPostView(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var customTitle by remember { mutableStateOf("Neon Samurai: Rain Duel In Sector 7") }
    var customDescription by remember { mutableStateOf("Custom rendered animated scene with hand-crafted dynamic camera and cel-shaded lighting.") }
    var selectedCategory by remember { mutableStateOf(VideoCategory.ANIME) }
    var selectedStyle by remember { mutableStateOf(ArtStyle.CYBERPUNK_ANIME) }
    var durationSeconds by remember { mutableIntStateOf(15) }
    var customTagsInput by remember { mutableStateOf("#IndieAnimation, #StudioPost, #Cartoon") }
    var selectedOrigin by remember { mutableStateOf("Device Storage / Gallery Video") }
    var importedFileName by remember { mutableStateOf("samurai_rain_duel_1080p.mp4") }
    var selectedAudioTrack by remember { mutableStateOf("Cyber-Bass Synth Odyssey (128 BPM)") }

    val originOptions = listOf(
        "Device Storage / Gallery Video",
        "External Blender / Maya Render",
        "Screen Capture / Camera Clip",
        "Freeform Handcrafted Animation"
    )

    val audioTracks = listOf(
        "Cyber-Bass Synth Odyssey (128 BPM)",
        "Lo-Fi Anime Chillhop (90 BPM)",
        "Whimsical Orchestral Fantasy (110 BPM)",
        "8-Bit Retro Chiptune Run (140 BPM)"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
    ) {
        // Universal Creation & Post Banner
        item {
            Surface(
                color = ToonifyCyan.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyCyan.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("custom_post_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ToonifyCyan.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudUpload,
                            contentDescription = null,
                            tint = ToonifyCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Universal Video Studio Publishing",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Create, import, or upload ANY video or animation to your channel! You are not restricted to Toonify AI prompts — publish your own custom renders, device media, and external animations directly to the community feed.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section 1: Video File & Media Source
        item {
            Text(
                text = "1. Video Source & Media File",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Origin Type Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                originOptions.forEach { origin ->
                    val isSelected = origin == selectedOrigin
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedOrigin = origin },
                        label = { Text(origin, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ToonifyViolet,
                            selectedLabelColor = Color.White,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Simulated File Picker Box
            Surface(
                color = DarkSurfaceElevated,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (importedFileName.isNotBlank()) ToonifyGreen.copy(alpha = 0.6f) else DarkOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        importedFileName = when ((1..4).random()) {
                            1 -> "cyber_samurai_fight_scene_final.mp4"
                            2 -> "baby_dragon_flight_test_2k.mp4"
                            3 -> "claymation_baking_clay_take3.mp4"
                            else -> "surreal_cat_adventure_master.mp4"
                        }
                        Toast.makeText(context, "Loaded: $importedFileName", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("file_picker_surface")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Rounded.VideoFile,
                            contentDescription = null,
                            tint = if (importedFileName.isNotBlank()) ToonifyGreen else TextTertiary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = importedFileName.ifBlank { "Tap to browse / pick video from device" },
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (importedFileName.isNotBlank()) "24.6 MB • 1080p 60FPS • Ready to post" else "Supports MP4, WebM, MOV, Animated GIF",
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        color = ToonifyViolet.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Browse",
                            color = ToonifyViolet,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Section 2: Audio Track & Sync
        item {
            Text(
                text = "2. Soundtrack & Audio Design",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                audioTracks.forEach { track ->
                    val isSelected = track == selectedAudioTrack
                    Surface(
                        color = if (isSelected) ToonifyCyan.copy(alpha = 0.18f) else DarkSurfaceElevated,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.dp else 0.5.dp,
                            if (isSelected) ToonifyCyan else DarkOutline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedAudioTrack = track }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Rounded.GraphicEq else Icons.Rounded.MusicNote,
                                contentDescription = null,
                                tint = if (isSelected) ToonifyCyan else TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = track,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Section 3: Metadata Form
        item {
            Text(
                text = "3. Video Title & Description",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customTitle,
                onValueChange = { customTitle = it },
                label = { Text("Video Title", fontSize = 12.sp) },
                placeholder = { Text("e.g. My Custom Animation / Scene Edit", fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedBorderColor = ToonifyViolet,
                    unfocusedBorderColor = DarkOutline,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("custom_title_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = customDescription,
                onValueChange = { customDescription = it },
                label = { Text("Video Description", fontSize = 12.sp) },
                placeholder = { Text("Tell your audience what this video is about...", fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedBorderColor = ToonifyViolet,
                    unfocusedBorderColor = DarkOutline,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(90.dp).testTag("custom_desc_input")
            )

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Section 4: Category & Thumbnail Visual Theme
        item {
            Text(
                text = "4. Category & Thumbnail Theme",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VideoCategory.entries.filterNot { it == VideoCategory.ALL }.forEach { cat ->
                    val isCatSelected = cat == selectedCategory
                    FilterChip(
                        selected = isCatSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text("${cat.iconEmoji} ${cat.displayName}", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ToonifyPink,
                            selectedLabelColor = Color.White,
                            containerColor = DarkSurfaceCard,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ArtStyle.entries.forEach { style ->
                    val isStyleSelected = style == selectedStyle
                    Surface(
                        color = if (isStyleSelected) ToonifyViolet.copy(alpha = 0.25f) else DarkSurfaceElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isStyleSelected) 1.5.dp else 0.5.dp,
                            if (isStyleSelected) ToonifyViolet else DarkOutline
                        ),
                        modifier = Modifier
                            .clickable { selectedStyle = style }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = style.label,
                            color = if (isStyleSelected) ToonifyViolet else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Section 5: Tags & Duration
        item {
            Text(
                text = "5. Tags & Duration",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customTagsInput,
                onValueChange = { customTagsInput = it },
                label = { Text("Hashtags / Keywords", fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedBorderColor = ToonifyViolet,
                    unfocusedBorderColor = DarkOutline,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("custom_tags_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Clip Duration: ${durationSeconds}s", color = TextSecondary, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(5, 10, 15, 30, 60).forEach { sec ->
                        val isSelected = sec == durationSeconds
                        Surface(
                            color = if (isSelected) ToonifyCyan else DarkSurfaceCard,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { durationSeconds = sec }
                        ) {
                            Text(
                                text = "${sec}s",
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section 6: Big Post Action Button
        item {
            Button(
                onClick = {
                    val rawTags = customTagsInput.split(",", " ")
                        .map { it.trim().removePrefix("#") }
                        .filter { it.isNotBlank() }

                    viewModel.createAndPostCustomVideo(
                        title = customTitle,
                        description = customDescription,
                        category = selectedCategory,
                        style = selectedStyle,
                        tags = rawTags,
                        durationSeconds = durationSeconds,
                        customPromptOrSource = "Uploaded via Studio: $selectedOrigin ($importedFileName) with $selectedAudioTrack",
                        isCustomUpload = true
                    )
                    Toast.makeText(context, "🎉 Video posted to Toonify Community!", Toast.LENGTH_LONG).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("custom_video_post_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Post Video to Channel & Feed",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Studio Screen UI Component displaying:
 * 1. Creator's uploaded AI videos
 * 2. Total 'Stars' earned
 * 3. Current weekly ranking position
 * 4. Quick QR codes for Channel and App scanning
 */
@Composable
private fun CreatorStudioHubView(
    viewModel: ToonifyViewModel,
    onOpenAiGen: () -> Unit,
    onOpenCustomPost: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uploadedVideos by viewModel.creatorUploadedVideos.collectAsState()
    var filterType by remember { mutableIntStateOf(0) } // 0: All, 1: AI Prompt Gen, 2: Custom Uploads

    val filteredList = remember(uploadedVideos, filterType) {
        when (filterType) {
            1 -> uploadedVideos.filter { !it.isUserUploaded || it.aiModelUsed.contains("Neural") || it.aiModelUsed.contains("Diffusion") }
            2 -> uploadedVideos.filter { it.isUserUploaded && (it.aiModelUsed.contains("Upload") || it.aiModelUsed.contains("Universal")) }
            else -> uploadedVideos
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
    ) {
        // Section 1: Creator Channel Profile Header Card
        item {
            Surface(
                color = DarkSurfaceElevated,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("creator_studio_header_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(ToonifyPink, ToonifyViolet, ToonifyCyan))
                                    )
                                    .padding(2.5.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF161822)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "TM",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = viewModel.creatorChannelName,
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Rounded.Verified,
                                        contentDescription = "Verified Creator",
                                        tint = ToonifyCyan,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Text(
                                    text = "${viewModel.creatorChannelHandle} • Pro AI Director",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Status Badge
                        Surface(
                            color = ToonifyGreen.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyGreen.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(ToonifyGreen)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "CREATOR ACTIVE",
                                    color = ToonifyGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // QR Code Shortcuts Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.QrCode(initialTab = 1)) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ToonifyAmber),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("creator_qr_btn")
                        ) {
                            Icon(Icons.Rounded.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Channel QR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.QrCode(initialTab = 0)) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ToonifyCyan),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("app_mobile_qr_btn")
                        ) {
                            Icon(Icons.Rounded.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("App Mobile QR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Section 2: Total 'Stars' Earned Card (PROMINENT REQUIREMENT)
        item {
            Surface(
                color = DarkSurfaceElevated,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyAmber.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("creator_stars_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(ToonifyAmber.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = ToonifyAmber,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Total 'Stars' Earned",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${viewModel.creatorTotalStars} ⭐",
                                        color = TextPrimary,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = Color(0xFF00E676).copy(alpha = 0.18f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${viewModel.creatorWeeklyStarsDelta} this week",
                                            color = Color(0xFF00E676),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            color = ToonifyAmber.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyAmber.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "Tier III Master",
                                color = ToonifyAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = Color(0xFF2C3040), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.CardGiftcard,
                                contentDescription = null,
                                tint = ToonifyPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "54 Community Gifts Received (💎 Super Nova, 🚀 Rocket)",
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Section 3: Current Weekly Ranking Position Card (PROMINENT REQUIREMENT)
        item {
            Surface(
                color = DarkSurfaceElevated,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyPink.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_ranking_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(ToonifyPink, ToonifyViolet))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Current Weekly Ranking Position",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${viewModel.creatorWeeklyRank} in Global Creators",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = ToonifyCyan.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "▲ +${viewModel.creatorRankDelta} spots",
                                            color = ToonifyCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.Leaderboard) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = "View Leaderboard",
                                tint = ToonifyPink
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress to Podium (#3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${viewModel.creatorWeeklyPoints} pts",
                            color = ToonifyPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "2,400 pts to Podium #3 (${viewModel.creatorNextRankPoints} pts)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { 0.91f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ToonifyPink,
                        trackColor = Color(0xFF2C3040)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.Leaderboard) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2838)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = ToonifyAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "View Global Leaderboard (Top 100 Creators)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Section 4: Analytics Summary (Total Views, Video Count, Positive Engagement)
        item {
            Surface(
                color = DarkSurfaceCard,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("892K", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text("Total Views", color = TextSecondary, fontSize = 11.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFF2C3040)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${uploadedVideos.size}", color = ToonifyCyan, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text("Published Videos", color = TextSecondary, fontSize = 11.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFF2C3040)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("94.2%", color = Color(0xFF00E676), fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text("Positive Ratio", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Section 5: Creator's Uploaded AI Videos Component Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Creator's Uploaded AI Videos",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = ToonifyPink.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${uploadedVideos.size}",
                            color = ToonifyPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                Button(
                    onClick = onOpenAiGen,
                    colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp).testTag("new_ai_video_quick_btn")
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New AI Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterType == 0,
                    onClick = { filterType = 0 },
                    label = { Text("All Uploads (${uploadedVideos.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ToonifyPink,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = filterType == 1,
                    onClick = { filterType = 1 },
                    label = { Text("AI Neural Gen", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ToonifyCyan,
                        selectedLabelColor = Color.Black
                    )
                )
                FilterChip(
                    selected = filterType == 2,
                    onClick = { filterType = 2 },
                    label = { Text("Custom Studio Posts", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ToonifyViolet,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section 6: Uploaded AI Videos List
        if (filteredList.isEmpty()) {
            item {
                Surface(
                    color = DarkSurfaceCard,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Movie,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Uploaded Videos in this filter",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Use the AI Studio Gen tab to synthesize a new video prompt or upload from your device.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onOpenAiGen,
                                colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Generate AI Video", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onOpenCustomPost,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Post Custom Video", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { video ->
                CreatorUploadedVideoCard(
                    video = video,
                    onWatch = { viewModel.navigateTo(AppScreen.Watch(video.id)) },
                    onRemix = { viewModel.loadPromptIntoStudio(video) },
                    onViewQr = {
                        val videoUrl = "https://toonify.ai/watch/${video.id}"
                        viewModel.navigateTo(AppScreen.QrCode(targetUrl = videoUrl, initialTab = 0))
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Card displaying an individual creator's uploaded AI video with:
 * - Thumbnail and duration
 * - Title and category
 * - AI model used
 * - Stars received (⭐)
 * - Views count (👁️)
 * - Quick actions: Watch, Remix, QR Code
 */
@Composable
private fun CreatorUploadedVideoCard(
    video: AiVideo,
    onWatch: () -> Unit,
    onRemix: () -> Unit,
    onViewQr: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282B38)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("uploaded_video_item_${video.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Video Thumbnail with duration overlay
                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 75.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onWatch)
                ) {
                    Image(
                        painter = painterResource(id = video.thumbnailResId),
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Play icon overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Duration Badge
                    Surface(
                        color = Color.Black.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = video.durationFormatted,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        lineHeight = 17.sp,
                        modifier = Modifier.clickable(onClick = onWatch)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = ToonifyCyan.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = video.category.displayName,
                                color = ToonifyCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = video.aiModelUsed.take(18) + if (video.aiModelUsed.length > 18) "..." else "",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Stats Row: Stars earned on this video + Views
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = ToonifyAmber,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = video.creatorStars.ifBlank { "840 ⭐" },
                                color = ToonifyAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Visibility,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${video.viewsFormatted} views",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = video.uploadedAgo,
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(color = Color(0xFF222530), thickness = 1.dp)

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row: Watch, Remix, QR Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onViewQr,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.QrCode2,
                        contentDescription = "Video QR Code",
                        tint = ToonifyCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("QR Code", fontSize = 10.sp, color = ToonifyCyan, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                OutlinedButton(
                    onClick = onRemix,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = "Remix AI Prompt",
                        tint = ToonifyViolet,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remix Prompt", fontSize = 10.sp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = onWatch,
                    colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Watch", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

