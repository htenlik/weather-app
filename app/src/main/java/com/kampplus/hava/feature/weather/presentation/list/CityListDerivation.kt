package com.kampplus.hava.feature.weather.presentation.list

import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.selection.SelectionUiState

/**
 * Liste ekranının gösterdiği durum, iki kaynaktan **türetilir**: yüklenen hava verisi ve ortak seçim state'i.
 * Sonuç hiçbir yerde saklanmaz; her iki kaynaktan biri değiştiğinde yeniden hesaplanır.
 * Böylece "favori mi" işareti ve "yalnızca favoriler" filtresi için ayrı değiştirilebilir state tutulmaz.
 */
fun UiState<List<CityWeatherUiModel>>.applySelection(selection: SelectionUiState): UiState<List<CityWeatherUiModel>> {
    if (this !is UiState.Success) return this
    val marked = data.map { item -> item.copy(isFavorite = selection.isFavorite(item.cityId)) }
    val visible = if (selection.showOnlyFavorites) marked.filter { it.isFavorite } else marked
    return if (visible.isEmpty()) UiState.Empty else UiState.Success(visible)
}
