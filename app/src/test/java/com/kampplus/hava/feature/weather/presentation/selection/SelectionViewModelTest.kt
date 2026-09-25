package com.kampplus.hava.feature.weather.presentation.selection

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectionViewModelTest {

    @Test
    fun `toggling a city adds it to favorites and toggling again removes it`() {
        val viewModel = SelectionViewModel(SavedStateHandle())

        viewModel.toggleFavorite(1)
        assertEquals(setOf(1L), viewModel.uiState.value.favoriteCityIds)
        assertTrue(viewModel.uiState.value.isFavorite(1))

        viewModel.toggleFavorite(1)
        assertTrue(viewModel.uiState.value.favoriteCityIds.isEmpty())
        assertFalse(viewModel.uiState.value.isFavorite(1))
    }

    @Test
    fun `favorite count is derived from the selection`() {
        val viewModel = SelectionViewModel(SavedStateHandle())

        viewModel.toggleFavorite(1)
        viewModel.toggleFavorite(2)
        viewModel.toggleFavorite(3)
        viewModel.toggleFavorite(2)

        assertEquals(2, viewModel.uiState.value.favoriteCount)
        assertEquals(setOf(1L, 3L), viewModel.uiState.value.favoriteCityIds)
    }

    @Test
    fun `view preference is part of the same state`() {
        val viewModel = SelectionViewModel(SavedStateHandle())
        assertFalse(viewModel.uiState.value.showOnlyFavorites)

        viewModel.setShowOnlyFavorites(true)

        assertTrue(viewModel.uiState.value.showOnlyFavorites)
    }

    @Test
    fun `selection survives process death through the saved state handle`() {
        val handle = SavedStateHandle()
        val before = SelectionViewModel(handle)
        before.toggleFavorite(7)
        before.toggleFavorite(9)
        before.setShowOnlyFavorites(true)

        // Süreç öldü, aynı SavedStateHandle ile yeni bir ViewModel kuruldu.
        val after = SelectionViewModel(handle)

        assertEquals(
            SelectionUiState(favoriteCityIds = setOf(7L, 9L), showOnlyFavorites = true),
            after.uiState.value
        )
    }
}
