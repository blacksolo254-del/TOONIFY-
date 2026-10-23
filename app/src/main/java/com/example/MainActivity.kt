package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ToonifyBottomBar
import com.example.ui.screens.*
import com.example.ui.theme.ToonifyTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ToonifyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToonifyTheme {
                val viewModel: ToonifyViewModel = viewModel()
                ToonifyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ToonifyApp(
    viewModel: ToonifyViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Determine bottom bar visibility
    val showBottomBar = when (currentScreen) {
        is AppScreen.Home,
        is AppScreen.Shorts,
        is AppScreen.Studio,
        is AppScreen.Marketplace,
        is AppScreen.Profile -> true
        is AppScreen.Watch,
        is AppScreen.Search,
        is AppScreen.PricingAndPlans,
        is AppScreen.EnterpriseApi,
        is AppScreen.Canvas,
        is AppScreen.RenderQueue,
        is AppScreen.ProductionSettings,
        is AppScreen.Leaderboard,
        is AppScreen.QrCode -> false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                ToonifyBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp.Hairline),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                is AppScreen.Home -> HomeScreen(viewModel = viewModel)
                is AppScreen.Watch -> WatchScreen(videoId = screen.videoId, viewModel = viewModel)
                is AppScreen.Studio -> StudioScreen(viewModel = viewModel)
                is AppScreen.Shorts -> ShortsScreen(viewModel = viewModel)
                is AppScreen.Marketplace -> MarketplaceScreen(viewModel = viewModel)
                is AppScreen.PricingAndPlans -> PricingAndPlansScreen(viewModel = viewModel)
                is AppScreen.EnterpriseApi -> EnterpriseApiScreen(viewModel = viewModel)
                is AppScreen.Profile -> ProfileScreen(viewModel = viewModel)
                is AppScreen.Search -> SearchScreen(viewModel = viewModel)
                is AppScreen.Canvas -> CanvasEditorScreen(viewModel = viewModel)
                is AppScreen.RenderQueue -> RenderQueueScreen(viewModel = viewModel)
                is AppScreen.ProductionSettings -> ProductionSettingsScreen(viewModel = viewModel)
                is AppScreen.Leaderboard -> WeeklyLeaderboardScreen(viewModel = viewModel)
                is AppScreen.QrCode -> QrCodeScreen(
                    viewModel = viewModel,
                    initialTab = screen.initialTab,
                    targetUrl = screen.targetUrl
                )
            }
        }
    }
}
