package com.kampplus.hava

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kampplus.hava.core.navigation.BottomBar
import com.kampplus.hava.core.navigation.HavaNavHost
import com.kampplus.hava.core.navigation.TopLevelDestination
import com.kampplus.hava.core.navigation.isOn
import com.kampplus.hava.core.ui.component.OfflineBanner

/** Uygulama kabuğu: alt sekmeler, çevrimdışı şeridi ve NavHost. [isOnline] false iken şerit görünür. */
@Composable
fun HavaApp(modifier: Modifier = Modifier, isOnline: Boolean = true) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isTopLevel = TopLevelDestination.entries.any { currentDestination.isOn(it) }

    Scaffold(
        modifier = modifier,
        // Durum çubuğu insets'i ekranların kendi üst çubuklarına bırakılır; böylece renkli başlıklar kenara kadar uzanır.
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Column {
                AnimatedVisibility(visible = !isOnline) {
                    // Alt çubuk yokken (detay ekranı) şerit gezinme çubuğunun üstünde kalmalı.
                    OfflineBanner(modifier = if (isTopLevel) Modifier else Modifier.navigationBarsPadding())
                }
                if (isTopLevel) {
                    BottomBar(
                        currentDestination = currentDestination,
                        onNavigate = { destination ->
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        HavaNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        )
    }
}
