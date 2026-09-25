package com.kampplus.hava.feature.favorites.presentation

import androidx.lifecycle.SavedStateHandle
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.navigation.ForecastDestination
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.favorites.data.local.InMemoryFavoriteCityDataSource
import com.kampplus.hava.feature.favorites.data.repository.FavoriteCityRepositoryImpl
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCitiesUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.domain.usecase.GetForecastUseCase
import com.kampplus.hava.feature.weather.presentation.detail.ForecastDetailViewModel
import com.kampplus.hava.feature.weather.presentation.list.CityListViewModel
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.city
import com.kampplus.hava.testing.cityWeather
import com.kampplus.hava.testing.forecast
import com.kampplus.hava.testing.testUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** CP3 kabul kriteri: "birinde ekleyince diğerinde de görünüyor". */
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesSyncTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val ankara = city()
    private val weatherRepository = FakeWeatherRepository(
        cityWeathersResult = { AppResult.Success(listOf(cityWeather(city = ankara))) },
        forecastResult = { AppResult.Success(forecast()) }
    )
    private val favoritesRepository = FavoriteCityRepositoryImpl(InMemoryFavoriteCityDataSource())
    private val observeIds = ObserveFavoriteCityIdsUseCase(favoritesRepository)
    private val toggle = ToggleFavoriteCityUseCase(favoritesRepository)

    // Lazy: ViewModel'ler MainDispatcherRule, Main dispatcher'ı değiştirdikten sonra oluşturulmalı.
    private val listViewModel by lazy {
        CityListViewModel(GetCityWeathersUseCase(weatherRepository), observeIds, toggle, testUiMapper())
    }
    private val detailViewModel by lazy {
        ForecastDetailViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    ForecastDestination.ARG_CITY_ID to ankara.id,
                    ForecastDestination.ARG_NAME to ankara.name,
                    ForecastDestination.ARG_REGION to ankara.region,
                    ForecastDestination.ARG_COUNTRY to ankara.country,
                    ForecastDestination.ARG_LATITUDE to ankara.coordinates.latitude,
                    ForecastDestination.ARG_LONGITUDE to ankara.coordinates.longitude
                )
            ),
            getForecast = GetForecastUseCase(weatherRepository),
            observeFavoriteCityIds = observeIds,
            toggleFavoriteCity = toggle,
            uiMapper = testUiMapper()
        )
    }
    private val favoritesViewModel by lazy { FavoritesViewModel(ObserveFavoriteCitiesUseCase(favoritesRepository), toggle) }

    @Test
    fun `adding from list is reflected on detail and favorites screens`() = runTest {
        subscribeAll()

        listViewModel.onToggleFavorite(ankara.id)
        runCurrent()

        assertTrue(listIsFavorite())
        assertTrue(detailIsFavorite())
        assertEquals(listOf(ankara.id), (favoritesViewModel.uiState.value as UiState.Success).data.map { it.id })
    }

    @Test
    fun `removing from detail is reflected on list and favorites screens`() = runTest {
        subscribeAll()
        listViewModel.onToggleFavorite(ankara.id)
        runCurrent()

        detailViewModel.onToggleFavorite()
        runCurrent()

        assertFalse(listIsFavorite())
        assertFalse(detailIsFavorite())
        assertEquals(UiState.Empty, favoritesViewModel.uiState.value)
    }

    @Test
    fun `undo restores a favorite removed from favorites screen`() = runTest {
        subscribeAll()
        detailViewModel.onToggleFavorite()
        runCurrent()

        favoritesViewModel.onRemoveFavorite(ankara.id)
        runCurrent()
        assertEquals(FavoritesEvent.ShowUndo(cityName = ankara.name), favoritesViewModel.events.first())
        assertFalse(listIsFavorite())

        favoritesViewModel.onUndoRemove()
        runCurrent()
        assertTrue(listIsFavorite())
    }

    /**
     * stateIn(WhileSubscribed) akışlarını ekran açıkmış gibi aktif tutar.
     * Not: advanceUntilIdle() yalnızca backgroundScope işi kaldığında durur; bu yüzden runCurrent() kullanılır.
     */
    private fun TestScope.subscribeAll() {
        backgroundScope.launch { listViewModel.uiState.collect {} }
        backgroundScope.launch { detailViewModel.uiState.collect {} }
        backgroundScope.launch { favoritesViewModel.uiState.collect {} }
        runCurrent()
    }

    private fun listIsFavorite() = (listViewModel.uiState.value as UiState.Success).data.single().isFavorite

    private fun detailIsFavorite() = (detailViewModel.uiState.value as UiState.Success).data.isFavorite
}
