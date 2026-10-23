package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ApiKeyInfo
import com.example.model.CommercialLicense
import com.example.ui.theme.*
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun EnterpriseApiScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val apiKeys by viewModel.apiKeys.collectAsState()
    val licenses by viewModel.licenses.collectAsState()
    val videos by viewModel.videos.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateKeyDialog by remember { mutableStateOf(false) }
    var newKeyName by remember { mutableStateOf("") }

    var showIssueLicenseDialog by remember { mutableStateOf(false) }
    var licenseOrganizationName by remember { mutableStateOf("") }
    var selectedVideoForLicenseId by remember { mutableStateOf(videos.firstOrNull()?.id ?: "") }

    BackHandler {
        viewModel.navigateBack()
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
                    text = "Enterprise & API Licensing",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Developer Keys, SDKs & Clearance",
                    color = ToonifyCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Segmented Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceElevated,
            contentColor = ToonifyViolet
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("API Keys & SDK", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Commercial Rights", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Compute SLA", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
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
                    // API Keys Section
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Production API Credentials",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = { showCreateKeyDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp).testTag("create_api_key_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Key", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(apiKeys, key = { it.keyId }) { keyInfo ->
                        ApiKeyCard(
                            keyInfo = keyInfo,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(keyInfo.fullKey))
                                Toast.makeText(context, "API Key copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            onRevoke = {
                                viewModel.revokeApiKey(keyInfo.keyId)
                                Toast.makeText(context, "Key revoked.", Toast.LENGTH_SHORT).show()
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Developer Integration Snippet (Python SDK)",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val codeSnippet = """
import toonify

client = toonify.Client(api_key="toon_live_...")
video = client.generate_video(
    prompt="Anime cyber girl rooftop chase",
    style="STUDIO_GHIBLI",
    duration=15,
    camera="DRONE_FLYTHROUGH",
    resolution="4K_60FPS"
)
print("Rendering URL:", video.stream_url)
                        """.trimIndent()

                        Surface(
                            color = Color.Black,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("python", color = ToonifyCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(codeSnippet))
                                            Toast.makeText(context, "SDK Code copied!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.ContentCopy,
                                            contentDescription = "Copy Code",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = codeSnippet,
                                    color = Color(0xFF80CBC4),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Commercial Rights & Licensing Section
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Certified Commercial Licenses",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Proof of copyright clearance & royalty-free rights",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Button(
                                onClick = { showIssueLicenseDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ToonifyViolet),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp).testTag("issue_license_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.VerifiedUser,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Issue Cert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    items(licenses, key = { it.certificateId }) { cert ->
                        CommercialLicenseCard(
                            cert = cert,
                            onDownload = {
                                Toast.makeText(context, "Downloaded Certificate ${cert.certificateId}", Toast.LENGTH_LONG).show()
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                2 -> {
                    // Enterprise Compute SLA Section
                    item {
                        EnterpriseSlaDashboard()
                    }
                }
            }
        }
    }

    // Modal to create API key
    if (showCreateKeyDialog) {
        AlertDialog(
            onDismissRequest = { showCreateKeyDialog = false },
            title = { Text("Generate Production API Key", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter application or service name for this credential:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newKeyName,
                        onValueChange = { newKeyName = it },
                        placeholder = { Text("e.g. Video Editing Webhook Service", color = TextTertiary, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ToonifyPink
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("api_key_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val key = viewModel.createApiKey(newKeyName)
                        newKeyName = ""
                        showCreateKeyDialog = false
                        Toast.makeText(context, "Created ${key.name}!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink)
                ) {
                    Text("Generate Key", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateKeyDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Modal to issue commercial license certificate
    if (showIssueLicenseDialog) {
        AlertDialog(
            onDismissRequest = { showIssueLicenseDialog = false },
            title = { Text("Issue Commercial Clearance Cert", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Organization / Legal Entity Name:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = licenseOrganizationName,
                        onValueChange = { licenseOrganizationName = it },
                        placeholder = { Text("e.g. Acme Media Studios Inc.", color = TextTertiary, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ToonifyViolet
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select Generated Video Asset:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    videos.take(4).forEach { vid ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedVideoForLicenseId = vid.id }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedVideoForLicenseId == vid.id,
                                onClick = { selectedVideoForLicenseId = vid.id },
                                colors = RadioButtonDefaults.colors(selectedColor = ToonifyCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(vid.title, color = TextPrimary, fontSize = 12.sp, maxLines = 1)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetVideo = videos.find { it.id == selectedVideoForLicenseId } ?: videos.first()
                        viewModel.issueLicense(targetVideo.title, targetVideo.id, licenseOrganizationName)
                        licenseOrganizationName = ""
                        showIssueLicenseDialog = false
                        Toast.makeText(context, "Certificate issued!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ToonifyViolet)
                ) {
                    Text("Issue Certificate", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showIssueLicenseDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
private fun ApiKeyCard(
    keyInfo: ApiKeyInfo,
    onCopy: () -> Unit,
    onRevoke: () -> Unit
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
        modifier = Modifier.fillMaxWidth().testTag("api_key_card_${keyInfo.keyId}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = keyInfo.name,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = ToonifyGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "ACTIVE",
                        color = ToonifyGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = DarkSurfaceCard,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = keyInfo.maskedKey,
                        color = ToonifyCyan,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy Key",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Created ${keyInfo.createdDate} • ${keyInfo.monthlyRequests} API calls",
                    color = TextTertiary,
                    fontSize = 11.sp
                )

                Text(
                    text = "Revoke",
                    color = ToonifyPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onRevoke)
                )
            }
        }
    }
}

@Composable
private fun CommercialLicenseCard(
    cert: CommercialLicense,
    onDownload: () -> Unit
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
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
                    Icon(
                        imageVector = Icons.Rounded.WorkspacePremium,
                        contentDescription = null,
                        tint = ToonifyAmber,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = cert.certificateId,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDownload, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = "Download PDF",
                        tint = ToonifyCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = cert.videoTitle,
                color = ToonifyPink,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Licensee: ${cert.licenseeName} • Issued: ${cert.issuedDate}",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = "Rights: ${cert.licenseType}",
                color = ToonifyGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 2.dp)
            )

            Surface(
                color = DarkSurfaceCard,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
            ) {
                Text(
                    text = "Verification Hash: ${cert.hashProof}",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}

@Composable
private fun EnterpriseSlaDashboard() {
    Column {
        Surface(
            color = DarkSurfaceElevated,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyCyan.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Enterprise GPU Infrastructure", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBox(title = "GPU Cluster", value = "H100 SXM5", subtitle = "Dedicated Slice")
                    MetricBox(title = "Uptime SLA", value = "99.98%", subtitle = "Past 90 Days")
                    MetricBox(title = "Avg Latency", value = "1.84s", subtitle = "1080p Latent")
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = DarkOutline)
                Spacer(modifier = Modifier.height(14.dp))

                Text("Enterprise Security & Compliance", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("• Zero-data model retention (prompts & video latents never used for training)", color = TextSecondary, fontSize = 12.sp)
                Text("• SOC2 Type II & GDPR compliant cloud data isolation", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                Text("• SAML 2.0 / Okta SSO team directory provisioning", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                Text("• Commercial copyright indemnity guarantee ($1,000,000 protection)", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun MetricBox(title: String, value: String, subtitle: String) {
    Column {
        Text(title, color = TextTertiary, fontSize = 10.sp)
        Text(value, color = ToonifyCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = TextSecondary, fontSize = 9.sp)
    }
}
