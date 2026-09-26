package com.kampplus.hava.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kampplus.hava.feature.favorites.presentation.FavoritesRoute
import com.kampplus.hava.feature.settings.presentation.SettingsRoute
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.presentation.detail.ForecastDetailRoute
import com.kampplus.hava.feature.weather.presentation.list.CityListRoute
import com.kampplus.hava.feature.weather.presentation.model.toCity

@Composable
fun HavaNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val openForecast: (City) -> Unit = { city -> navController.navigate(city.toDestination()) }
    NavHost(
        navController = navController,
        startDestination = ListDestination,
        modifier = modifier
    ) {
        composable<ListDestination> {
            CityListRoute(onCityClick = openForecast)
        }
        composable<FavoritesDestination> {
            FavoritesRoute(onCityClick = { favorite -> openForecast(favorite.toCity()) })
        }
        composable<SettingsDestination> {
            SettingsRoute()
        }
        composable<ForecastDestination> {
            ForecastDetailRoute(onBack = navController::navigateUp)
        }
    }
}

private fun City.toDestination() = ForecastDestination(
    cityId = id,
    name = name,
    region = region,
    country = country,
    latitude = coordinates.latitude,
    longitude = coordinates.longitude
)
