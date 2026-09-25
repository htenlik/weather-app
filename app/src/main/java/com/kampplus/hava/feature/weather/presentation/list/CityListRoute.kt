package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.BuildConfig
import com.kampplus.hava.feature.weather.domain.model.City

/**
 * ViewModel'i ekrana bağlayan katman. Ekranın kendisi ([CityListScreen]) stateless'tır.
 * "Tekrar dene" ve çekerek yenileme aynı ViewModel'e gider; hiçbir istek composable içinden başlatılmaz,
 * bu yüzden yeniden çizim (recomposition) ek istek üretmez.
 */
@Composable
fun CityListRoute(
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityListViewModel = hiltViewModel(),
    demoViewModel: DemoScenarioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val scenario by demoViewModel.scenario.collectAsStateWithLifecycle()
    CityListScreen(
        uiState = uiState,
        isRefreshing = isRefreshing,
        demoScenario = scenario.takeIf { BuildConfig.DEBUG },
        onCityClick = { cityId -> viewModel.findCity(cityId)?.let(onCityClick) },
        onFavoriteClick = viewModel::onToggleFavorite,
        onRetry = viewModel::loadData,
        onRefresh = viewModel::refresh,
        onScenarioSelected = { selected ->
            demoViewModel.select(selected)
            viewModel.reload()
        },
        modifier = modifier
    )
}
