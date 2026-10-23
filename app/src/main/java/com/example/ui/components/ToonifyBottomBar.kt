package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun ToonifyBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurface,
        tonalElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            val isHome = currentScreen is AppScreen.Home
            BottomNavItem(
                icon = Icons.Rounded.Home,
                label = "Home",
                isSelected = isHome,
                onClick = { onNavigate(AppScreen.Home) },
                testTag = "nav_home"
            )

            // Shorts
            val isShorts = currentScreen is AppScreen.Shorts
            BottomNavItem(
                icon = Icons.Rounded.PlayCircle,
                label = "ToonReels",
                isSelected = isShorts,
                onClick = { onNavigate(AppScreen.Shorts) },
                testTag = "nav_shorts"
            )

            // Center AI Studio Action Button
            val isStudio = currentScreen is AppScreen.Studio
            Box(
                modifier = Modifier
                    .offset(y = (-4).dp)
                    .size(54.dp)
                    .shadow(12.dp, CircleShape, spotColor = ToonifyPink)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(ToonifyPink, ToonifyViolet, ToonifyCyan)
                        )
                    )
                    .clickable { onNavigate(AppScreen.Studio) }
                    .testTag("nav_ai_studio"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = "Create with AI",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Marketplace
            val isMarket = currentScreen is AppScreen.Marketplace
            BottomNavItem(
                icon = Icons.Rounded.Storefront,
                label = "Market",
                isSelected = isMarket,
                onClick = { onNavigate(AppScreen.Marketplace) },
                testTag = "nav_market"
            )

            // Profile / Studio Hub
            val isProfile = currentScreen is AppScreen.Profile || currentScreen is AppScreen.PricingAndPlans || currentScreen is AppScreen.EnterpriseApi
            BottomNavItem(
                icon = Icons.Rounded.Person,
                label = "Hub",
                isSelected = isProfile,
                onClick = { onNavigate(AppScreen.Profile) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ToonifyViolet else TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) ToonifyViolet else TextSecondary
        )
    }
}
