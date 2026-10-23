package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssetCategory
import com.example.model.MarketplaceAsset
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun MarketplaceScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val assets by viewModel.marketplaceAssets.collectAsState()
    val userCredits by viewModel.userCredits.collectAsState()

    var selectedCategory by remember { mutableStateOf(AssetCategory.ALL) }
    var selectedAssetForDetails by remember { mutableStateOf<MarketplaceAsset?>(null) }

    BackHandler {
        viewModel.navigateBack()
    }

    val filteredAssets = if (selectedCategory == AssetCategory.ALL) {
        assets
    } else {
        assets.filter { it.category == selectedCategory }
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
                        text = "Asset Marketplace",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Style LoRAs & Character Checkpoints",
                        color = ToonifyCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // User Credit Balance Pill (Click to open Pricing/Plans)
            Surface(
                color = DarkSurfaceElevated,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.6f)),
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
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$userCredits cr",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssetCategory.entries.forEach { category ->
                val isSelected = category == selectedCategory
                Surface(
                    color = if (isSelected) ToonifyViolet else DarkSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { selectedCategory = category }
                ) {
                    Text(
                        text = category.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
        ) {
            // Creator Monetization Banner
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyPink.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(ToonifyPink, ToonifyViolet))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MonetizationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Earn 85% Creator Royalty",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Train and publish your own style LoRAs or character checkpoints to earn passive credits & cash.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Marketplace Assets List
            items(filteredAssets, key = { it.id }) { asset ->
                MarketplaceAssetCard(
                    asset = asset,
                    onInspect = { selectedAssetForDetails = asset },
                    onInstallOrBuy = {
                        val success = viewModel.purchaseOrInstallAsset(asset.id)
                        if (success) {
                            Toast.makeText(context, "${asset.title} installed to AI Studio!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Insufficient credits! Need ${asset.priceCredits} cr.", Toast.LENGTH_SHORT).show()
                            viewModel.navigateTo(AppScreen.PricingAndPlans)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Asset Detail / Trigger Inspection Dialog
    if (selectedAssetForDetails != null) {
        val asset = selectedAssetForDetails!!
        AlertDialog(
            onDismissRequest = { selectedAssetForDetails = null },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setStudioIdea("in ${asset.promptTrigger}, cinematic master quality")
                        viewModel.navigateTo(AppScreen.Studio)
                        selectedAssetForDetails = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink)
                ) {
                    Text("Equip in AI Studio", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAssetForDetails = null }) {
                    Text("Close", color = TextSecondary)
                }
            },
            title = {
                Text(asset.title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(asset.description, color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Prompt Activation Trigger:", color = ToonifyCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        color = DarkSurfaceCard,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
                    ) {
                        Text(
                            text = asset.promptTrigger,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Sample Video: ${asset.sampleVideoTitle}", color = TextTertiary, fontSize = 11.sp)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
private fun MarketplaceAssetCard(
    asset: MarketplaceAsset,
    onInspect: () -> Unit,
    onInstallOrBuy: () -> Unit
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkOutline),
        modifier = Modifier.fillMaxWidth().testTag("market_asset_${asset.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail preview
            Image(
                painter = painterResource(id = asset.thumbnailResId),
                contentDescription = asset.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onInspect),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = ToonifyViolet.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = asset.category.displayName,
                            color = ToonifyViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = ToonifyAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${asset.rating}",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = asset.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = "by ${asset.creatorName} • ${asset.downloadsCount} DLs",
                    color = TextTertiary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Price in credits
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Token,
                            contentDescription = null,
                            tint = ToonifyAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (asset.isInstalled) "Owned" else "${asset.priceCredits} cr",
                            color = if (asset.isInstalled) ToonifyGreen else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = onInspect,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
                        ) {
                            Text("Inspect", fontSize = 11.sp, color = TextSecondary)
                        }

                        Button(
                            onClick = onInstallOrBuy,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (asset.isInstalled) ToonifyCyan.copy(alpha = 0.2f) else ToonifyPink
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(
                                text = if (asset.isInstalled) "Installed" else "Equip",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (asset.isInstalled) ToonifyCyan else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
