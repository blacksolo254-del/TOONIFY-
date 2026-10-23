package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketplaceSampleData
import com.example.model.CreditPack
import com.example.model.SubscriptionPlan
import com.example.ui.theme.*
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun PricingAndPlansScreen(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userCredits by viewModel.userCredits.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    val creditPacks = remember { MarketplaceSampleData.getCreditPacks() }

    var isAnnualBilling by remember { mutableStateOf(true) }

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
                    text = "Credits & SaaS Subscriptions",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fuel your AI Video creations",
                    color = ToonifyCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
        ) {
            // Live Credit Balance Dashboard Card
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth().testTag("credit_dashboard_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(ToonifyViolet.copy(alpha = 0.15f), Color.Transparent),
                                    radius = 400f
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
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
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Current Credit Balance",
                                        color = TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Surface(
                                    color = ToonifyPink.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = currentPlan.title,
                                        color = ToonifyPink,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "$userCredits Credits",
                                color = TextPrimary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )

                            Text(
                                text = "≈ ${(userCredits / 25)} standard 10s AI videos remaining (25 cr/video)",
                                color = TextTertiary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Quick Credit Recharge Packs Carousel
            item {
                Text(
                    text = "Top-Up Instant Credit Packs",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    creditPacks.forEach { pack ->
                        CreditPackCard(
                            pack = pack,
                            onBuy = {
                                viewModel.purchaseCreditPack(pack)
                                Toast.makeText(context, "Added ${pack.credits + pack.bonusCredits} credits!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Tiered SaaS Subscriptions Header & Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tiered SaaS Plans",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Monthly allowance & commercial features",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Annual / Monthly Toggle Pill
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
                    ) {
                        Row(
                            modifier = Modifier.padding(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (!isAnnualBilling) ToonifyViolet else Color.Transparent,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.clickable { isAnnualBilling = false }
                            ) {
                                Text(
                                    text = "Monthly",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isAnnualBilling) Color.White else TextTertiary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                color = if (isAnnualBilling) ToonifyViolet else Color.Transparent,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.clickable { isAnnualBilling = true }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Annual",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAnnualBilling) Color.White else TextTertiary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        color = ToonifyGreen,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "-20%",
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Subscription Plan Cards
            items(SubscriptionPlan.entries) { plan ->
                val isCurrent = plan == currentPlan
                val price = if (isAnnualBilling) plan.annualPriceMonthlyUsd else plan.monthlyPriceUsd

                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isCurrent) 2.dp else 1.dp,
                        if (isCurrent) ToonifyPink else DarkOutline
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("plan_card_${plan.name}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = plan.title,
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${plan.monthlyCredits} credits / mo • ${plan.maxResolution}",
                                    color = ToonifyCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Surface(
                                color = if (isCurrent) ToonifyPink else DarkSurfaceCard,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isCurrent) "ACTIVE PLAN" else plan.badge,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Price
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = if (price == 0) "Free" else "$$price",
                                color = TextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                            if (price > 0) {
                                Text(
                                    text = if (isAnnualBilling) " /mo (billed yearly)" else " /month",
                                    color = TextTertiary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Features List
                        plan.features.forEach { feature ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = ToonifyGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = feature,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.upgradeSubscription(plan)
                                Toast.makeText(context, "Switched to ${plan.title}!", Toast.LENGTH_SHORT).show()
                            },
                            enabled = !isCurrent,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrent) DarkSurfaceCard else ToonifyViolet
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text(
                                text = if (isCurrent) "Current Plan" else "Select ${plan.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isCurrent) TextTertiary else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun CreditPackCard(
    pack: CreditPack,
    onBuy: () -> Unit
) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (pack.isBestValue) 1.5.dp else 1.dp,
            if (pack.isBestValue) ToonifyPink else DarkOutline
        ),
        modifier = Modifier
            .width(155.dp)
            .clickable(onClick = onBuy)
            .testTag("credit_pack_${pack.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (pack.isBestValue) {
                Surface(
                    color = ToonifyPink,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "POPULAR",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            Icon(
                imageVector = Icons.Rounded.Token,
                contentDescription = null,
                tint = ToonifyAmber,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${pack.credits} cr",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (pack.bonusCredits > 0) {
                Text(
                    text = "+${pack.bonusCredits} FREE bonus",
                    color = ToonifyGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Standard top-up",
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = DarkSurfaceCard,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$${pack.priceUsd}",
                    color = ToonifyCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}
