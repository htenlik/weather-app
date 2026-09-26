package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.feature.location.presentation.LocationCardRoute
import com.kampplus.hava.feature.location.presentation.LocationWeatherViewModel
import com.kampplus.hava.feature.weather.domain.model.City

/**
 * ViewModel'i ekrana bağlayan katman. Ekranın kendisi ([CityListScreen]) stateless'tır.
 * Konum kartı kendi ViewModel'iyle gelir; aşağı çekince ikisi birlikte yenilenir.
 */
@Composable
fun CityListRoute(
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityListViewModel = hiltViewModel(),
    locationViewModel: LocationWeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CityListScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onCityClick = { cityId -> viewModel.findCity(cityId)?.let(onCityClick) },
        onFavoriteClick = viewModel::onToggleFavorite,
        onRetry = viewModel::onRetry,
        onRefresh = {
            viewModel.onRefresh()
            locationViewModel.onRefresh()
        },
        modifier = modifier,
        locationCard = { LocationCardRoute(onCityClick = onCityClick, viewModel = locationViewModel) }
    )
}
