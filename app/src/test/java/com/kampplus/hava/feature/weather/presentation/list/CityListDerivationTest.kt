package com.kampplus.hava.feature.weather.presentation.list

import com.kampplus.hava.R
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.selection.SelectionUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class CityListDerivationTest {

    private val items = listOf(item(id = 1), item(id = 2), item(id = 3))

    @Test
    fun `favorites are marked from the shared selection`() {
        val derived = UiState.Success(items).applySelection(SelectionUiState(favoriteCityIds = setOf(2L)))

        val marked = (derived as UiState.Success).data
        assertEquals(listOf(false, true, false), marked.map { it.isFavorite })
    }

    @Test
    fun `only favorites are visible when the view preference is on`() {
        val selection = SelectionUiState(favoriteCityIds = setOf(1L, 3L), showOnlyFavorites = true)

        val derived = UiState.Success(items).applySelection(selection)

        assertEquals(listOf(1L, 3L), (derived as UiState.Success).data.map { it.cityId })
    }

    @Test
    fun `filtering without favorites yields the empty state`() {
        val selection = SelectionUiState(favoriteCityIds = emptySet(), showOnlyFavorites = true)

        assertEquals(UiState.Empty, UiState.Success(items).applySelection(selection))
    }

    @Test
    fun `loading and error pass through unchanged`() {
        val selection = SelectionUiState(favoriteCityIds = setOf(1L), showOnlyFavorites = true)
        val error = UiState.Error(UiText.Resource(R.string.error_generic))

        assertEquals(UiState.Loading, UiState.Loading.applySelection(selection))
        assertEquals(error, error.applySelection(selection))
    }

    private fun item(id: Long) = CityWeatherUiModel(
        cityId = id,
        title = "Şehir $id",
        subtitle = "Türkiye",
        temperatureText = "20°",
        temperatureC = 20.0,
        conditionEmoji = "☀️",
        conditionLabel = UiText.Dynamic("Açık")
    )
}
