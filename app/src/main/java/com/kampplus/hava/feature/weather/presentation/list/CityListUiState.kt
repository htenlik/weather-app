package com.kampplus.hava.feature.weather.presentation.list

import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel

data class CityListUiState(
    val query: String = "",
    val content: UiState<List<CityWeatherUiModel>> = UiState.Loading,
    val isRefreshing: Boolean = false,
    /** Listedeki verinin alınma saati; yalnızca başarılı yüklemede dolu. */
    val updatedAtText: String? = null
) {
    /** Arama kutusu dolu mu? (Boş durum metni buna göre değişir.) */
    val isSearching: Boolean get() = query.trim().length >= CityListViewModel.MIN_QUERY_LENGTH
}
