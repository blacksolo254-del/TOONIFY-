package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ToonifyViewModel
import com.example.util.QrCodeCard

@Composable
fun QrCodeScreen(
    viewModel: ToonifyViewModel,
    initialTab: Int = 0,
    targetUrl: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var useDevUrl by remember { mutableStateOf(false) }

    // Scanner state
    var manualInputText by remember { mutableStateOf("") }
    var scannedResult by remember { mutableStateOf<String?>(null) }
    var scannedActionText by remember { mutableStateOf<String?>(null) }

    BackHandler {
        viewModel.navigateBack()
    }

    val appUrl = if (useDevUrl) viewModel.appDevUrl else (targetUrl ?: viewModel.appSharedUrl)
    val creatorChannelUrl = "https://toonify.ai/${viewModel.creatorChannelHandle}"

    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

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
                    modifier = Modifier.size(36.dp).testTag("qr_back_btn")
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
                        text = "App QR Code & Scanner",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scan with your phone to open Toonify",
                        color = ToonifyCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quick Share Icon Button
            IconButton(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Watch and create AI cartoon videos on Toonify! 🚀 $appUrl")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Toonify App"))
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = "Share",
                    tint = ToonifyPink
                )
            }
        }

        // Tab Selector: App QR Code vs Creator Channel QR vs QR Scanner
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceElevated,
            contentColor = ToonifyPink,
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.PhoneAndroid, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("App QR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Creator QR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.QrCodeScanner, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("QR Scanner", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: App QR Code
                    item {
                        // Scan Instructions Banner
                        Surface(
                            color = ToonifyCyan.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyCyan.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(ToonifyCyan.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CameraAlt,
                                        contentDescription = null,
                                        tint = ToonifyCyan,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Scan with Any Phone Camera",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Open your iPhone or Android camera, point at the QR code, and tap the link banner to open the full app instantly!",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // URL Environment Switcher (Shared vs Dev)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = !useDevUrl,
                                onClick = { useDevUrl = false },
                                label = { Text("Shared App URL (Public)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ToonifyPink.copy(alpha = 0.25f),
                                    selectedLabelColor = ToonifyPink
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = useDevUrl,
                                onClick = { useDevUrl = true },
                                label = { Text("Dev Preview URL", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ToonifyViolet.copy(alpha = 0.25f),
                                    selectedLabelColor = ToonifyViolet
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // QR Code Card
                        QrCodeCard(
                            content = appUrl,
                            title = "Toonify Mobile App",
                            subtitle = "Instant live streaming web application\nNo store install required",
                            onShare = { link ->
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Watch and create AI cartoon videos on Toonify! 🚀 $link")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Toonify App"))
                            },
                            onOpenInBrowser = { link ->
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open browser: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Key App Features Bullet List
                        Surface(
                            color = DarkSurfaceCard,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "What You Get On Mobile:",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                QrFeatureRow(icon = Icons.Rounded.PlayCircle, text = "Full HD/4K 60fps AI cartoon video player & reels")
                                QrFeatureRow(icon = Icons.Rounded.AutoAwesome, text = "AI Creator Studio with director prompts & render queue")
                                QrFeatureRow(icon = Icons.Rounded.Star, text = "Weekly creator rankings, stars, and real prizes")
                                QrFeatureRow(icon = Icons.Rounded.CloudUpload, text = "Direct video uploading & instant community publishing")
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: Creator Channel QR Code
                    item {
                        Surface(
                            color = ToonifyAmber.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyAmber.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(ToonifyAmber.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.EmojiEvents,
                                        contentDescription = null,
                                        tint = ToonifyAmber,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = viewModel.creatorChannelName,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Rounded.Verified,
                                            contentDescription = null,
                                            tint = ToonifyCyan,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Text(
                                        text = "Rank #${viewModel.creatorWeeklyRank} Global Creator • ${viewModel.creatorTotalStars} Total Stars",
                                        color = ToonifyAmber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        QrCodeCard(
                            content = creatorChannelUrl,
                            title = "Creator Channel: ${viewModel.creatorChannelHandle}",
                            subtitle = "Scan to follow @toonmaster, watch AI creations, and send Stars!",
                            onShare = { link ->
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out my AI cartoon creator channel on Toonify! ⭐ $link")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Creator Channel"))
                            },
                            onOpenInBrowser = {
                                viewModel.navigateTo(AppScreen.Studio)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Card
                        Surface(
                            color = DarkSurfaceCard,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("⭐ ${viewModel.creatorTotalStars}", color = ToonifyAmber, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    Text("Total Stars", color = TextSecondary, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🏆 #${viewModel.creatorWeeklyRank}", color = ToonifyPink, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    Text("Weekly Rank", color = TextSecondary, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${viewModel.creatorWeeklyPoints}", color = ToonifyCyan, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    Text("Weekly Points", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: QR Scanner & Decoder
                    item {
                        // Scanner Viewport Card with Laser Animation
                        Surface(
                            color = Color(0xFF141720),
                            shape = RoundedCornerShape(22.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .testTag("qr_scanner_viewport")
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                // Corner Reticle Bounds
                                Box(
                                    modifier = Modifier
                                        .size(180.dp)
                                        .border(2.dp, Brush.sweepGradient(listOf(ToonifyPink, ToonifyCyan, ToonifyViolet, ToonifyPink)), RoundedCornerShape(16.dp))
                                ) {
                                    // Animated Scanning Laser Bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .align(Alignment.TopCenter)
                                            .offset(y = (laserOffsetY * 175).dp)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Color.Transparent, ToonifyPink, ToonifyCyan, Color.Transparent)
                                                )
                                            )
                                    )
                                }

                                Text(
                                    text = "Point camera at any Toonify QR Code\nor tap a quick test below",
                                    color = Color(0xFF8E95A5),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Simulation Quick Test Buttons
                        Text(
                            text = "Test QR Detection:",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    scannedResult = appUrl
                                    scannedActionText = viewModel.handleScannedQrContent(appUrl)
                                    Toast.makeText(context, "✅ Detected Toonify App URL!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("test_scan_app_btn")
                            ) {
                                Text("App Link", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val videoLink = "https://toonify.ai/watch/v_user_1"
                                    scannedResult = videoLink
                                    scannedActionText = viewModel.handleScannedQrContent(videoLink)
                                    Toast.makeText(context, "✅ Detected Video: Mecha Ronin!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("test_scan_video_btn")
                            ) {
                                Text("Video #1", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val creatorLink = "https://toonify.ai/@toonmaster"
                                    scannedResult = creatorLink
                                    scannedActionText = viewModel.handleScannedQrContent(creatorLink)
                                    Toast.makeText(context, "✅ Detected Creator Channel!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("test_scan_creator_btn")
                            ) {
                                Text("Creator", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Manual QR Code Decoder input
                        OutlinedTextField(
                            value = manualInputText,
                            onValueChange = { manualInputText = it },
                            label = { Text("Or Paste / Type QR Code Text") },
                            placeholder = { Text("e.g. https://toonify.ai/watch/v1") },
                            singleLine = true,
                            trailingIcon = {
                                if (manualInputText.isNotBlank()) {
                                    IconButton(onClick = {
                                        scannedResult = manualInputText
                                        scannedActionText = viewModel.handleScannedQrContent(manualInputText)
                                    }) {
                                        Icon(Icons.Rounded.Search, contentDescription = "Decode", tint = ToonifyCyan)
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ToonifyPink,
                                unfocusedBorderColor = Color(0xFF2C3040),
                                focusedLabelColor = ToonifyPink,
                                cursorColor = ToonifyPink
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("qr_manual_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Scanned Output Card
                        AnimatedVisibility(visible = scannedResult != null) {
                            Surface(
                                color = DarkSurfaceElevated,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyGreen.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().testTag("qr_scan_result_card")
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = ToonifyGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Scan Successful!",
                                            color = ToonifyGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = scannedResult ?: "",
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        maxLines = 2
                                    )

                                    if (scannedActionText != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = scannedActionText!!,
                                            color = ToonifyCyan,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            val target = scannedResult ?: ""
                                            viewModel.handleScannedQrContent(target)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ToonifyGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Open Detected Content", fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
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
private fun QrFeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ToonifyCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}
