package com.kampplus.hava.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kampplus.hava.feature.weather.presentation.detail.CityDetailRoute
import com.kampplus.hava.feature.weather.presentation.list.CityListRoute

/**
 * Navigasyon grafiği. Geçiş kararı yalnızca burada verilir: ekranlar "şu şehre tıklandı",
 * "geri istendi" gibi olayları callback ile bildirir, NavController'ı tanımaz.
 * Sistem Geri tuşu NavHost tarafından otomatik ele alınır; üst çubuktaki geri ikonu aynı yolu kullanır.
 */
@Composable
fun HavaNavHost(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = ListDestination,
        modifier = modifier
    ) {
        composable<ListDestination> {
            CityListRoute(onCityClick = { cityId -> navController.navigate(CityDetailDestination(cityId)) })
        }
        composable<CityDetailDestination> {
            CityDetailRoute(onBack = navController::navigateUp)
        }
    }
}
