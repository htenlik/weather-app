package com.kampplus.hava.feature.weather.presentation.list

import app.cash.turbine.test
import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.favorites.data.local.InMemoryFavoriteCityDataSource
import com.kampplus.hava.feature.favorites.data.repository.FavoriteCityRepositoryImpl
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.settings.domain.model.UserSettings
import com.kampplus.hava.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.domain.usecase.SearchCityWeathersUseCase
import com.kampplus.hava.testing.FakeCityRepository
import com.kampplus.hava.testing.FakeNetworkMonitor
import com.kampplus.hava.testing.FakeSettingsRepository
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.city
import com.kampplus.hava.testing.cityWeather
import com.kampplus.hava.testing.testUiMapper
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CityListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWeatherRepository()
    private val cityRepository = FakeCityRepository()
    private val favoritesRepository = FavoriteCityRepositoryImpl(InMemoryFavoriteCityDataSource())
    private val settingsRepository = FakeSettingsRepository()
    private val networkMonitor = FakeNetworkMonitor()

    private fun createViewModel() = CityListViewModel(
        getCityWeathers = GetCityWeathersUseCase(repository),
        searchCityWeathers = SearchCityWeathersUseCase(cityRepository, repository),
        observeFavoriteCityIds = ObserveFavoriteCityIdsUseCase(favoritesRepository),
        toggleFavoriteCity = ToggleFavoriteCityUseCase(favoritesRepository),
        observeSettings = ObserveSettingsUseCase(settingsRepository),
        networkMonitor = networkMonitor,
        uiMapper = testUiMapper()
    )

    @Test
    fun `emits loading then formatted city weathers`() = runTest {
        repository.cityWeathersResult = {
            AppResult.Success(listOf(cityWeather(city = city(name = "İzmir", region = "İzmir"), temperatureC = 26.6)))
        }

        createViewModel().uiState.map { it.content }.test {
            assertEquals(UiState.Loading, awaitItem())
            val item = (awaitItem() as UiState.Success).data.single()
            assertEquals("İzmir", item.title)
            assertEquals("İzmir, Türkiye", item.subtitle)
            assertEquals("27°", item.temperatureText)
        }
    }

    @Test
    fun `emits empty when there is no city`() = runTest {
        repository.cityWeathersResult = { AppResult.Success(emptyList()) }

        createViewModel().uiState.map { it.content }.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun `emits error when repository fails`() = runTest {
        repository.cityWeathersResult = { AppResult.Failure(AppError.Network) }

        createViewModel().uiState.map { it.content }.test {
            assertEquals(UiState.Loading, awaitItem())
            assertTrue(awaitItem() is UiState.Error)
        }
    }

    @Test
    fun `search waits for debounce and shows matching cities`() = runTest {
        cityRepository.searchResult =
            { AppResult.Success(listOf(city(id = 2950159, name = "Berlin", region = "Berlin", country = "Almanya"))) }
        repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather())) }
        val viewModel = createViewModel().alsoSubscribe(this)

        viewModel.onQueryChange("Berl")
        advanceTimeBy(CityListViewModel.SEARCH_DEBOUNCE_MS - 1)
        runCurrent()
        assertTrue("debounce süresi dolmadan arama yapılmamalı", cityRepository.queries.isEmpty())

        advanceTimeBy(2)
        runCurrent()
        assertEquals(listOf("Berl"), cityRepository.queries)
        val state = viewModel.uiState.value
        assertTrue(state.isSearching)
        assertEquals(listOf("Berlin"), (state.content as UiState.Success).data.map { it.title })
    }

    @Test
    fun `typing quickly only searches the last query`() = runTest {
        cityRepository.searchResult = { AppResult.Success(listOf(city())) }
        val viewModel = createViewModel().alsoSubscribe(this)

        listOf("Iz", "Izm", "Izmi", "Izmir").forEach { text ->
            viewModel.onQueryChange(text)
            advanceTimeBy(100)
        }
        advanceTimeBy(CityListViewModel.SEARCH_DEBOUNCE_MS)
        runCurrent()

        assertEquals(listOf("Izmir"), cityRepository.queries)
    }

    @Test
    fun `search without results shows empty state`() = runTest {
        cityRepository.searchResult = { AppResult.Success(emptyList()) }
        val viewModel = createViewModel().alsoSubscribe(this)

        viewModel.onQueryChange("xqzw")
        advanceTimeBy(CityListViewModel.SEARCH_DEBOUNCE_MS + 1)
        runCurrent()

        assertEquals(UiState.Empty, viewModel.uiState.value.content)
    }

    @Test
    fun `clearing query returns to featured cities`() = runTest {
        repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather(city = city(name = "İstanbul")))) }
        cityRepository.searchResult = { AppResult.Success(listOf(city(name = "Berlin"))) }
        val viewModel = createViewModel().alsoSubscribe(this)
        viewModel.onQueryChange("Berlin")
        advanceTimeBy(CityListViewModel.SEARCH_DEBOUNCE_MS + 1)
        runCurrent()

        viewModel.onQueryChange("")
        runCurrent()

        assertEquals(listOf("İstanbul"), (viewModel.uiState.value.content as UiState.Success).data.map { it.title })
    }

    @Test
    fun `retry after error loads the list again`() = runTest {
        var shouldFail = true
        repository.cityWeathersResult =
            { if (shouldFail) AppResult.Failure(AppError.Network) else AppResult.Success(listOf(cityWeather())) }
        val viewModel = createViewModel().alsoSubscribe(this)
        assertTrue(viewModel.uiState.value.content is UiState.Error)

        shouldFail = false
        viewModel.onRetry()
        runCurrent()

        assertTrue(viewModel.uiState.value.content is UiState.Success)
    }

    @Test
    fun `refresh replaces data and clears refreshing flag`() = runTest {
        repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather(temperatureC = 10.0))) }
        val viewModel = createViewModel().alsoSubscribe(this)

        repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather(temperatureC = 25.0))) }
        viewModel.onRefresh()
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isRefreshing)
        assertEquals("25°", (state.content as UiState.Success).data.single().temperatureText)
    }

    @Test
    fun `retries automatically when the connection comes back`() = runTest {
        var shouldFail = true
        repository.cityWeathersResult =
            { if (shouldFail) AppResult.Failure(AppError.Network) else AppResult.Success(listOf(cityWeather())) }
        val viewModel = createViewModel().alsoSubscribe(this)
        assertTrue(viewModel.uiState.value.content is UiState.Error)

        shouldFail = false
        networkMonitor.online.value = false
        runCurrent()
        assertTrue("bağlantı kopunca yeniden denenmez", viewModel.uiState.value.content is UiState.Error)

        networkMonitor.online.value = true
        runCurrent()

        assertTrue(viewModel.uiState.value.content is UiState.Success)
        assertEquals("12:05", viewModel.uiState.value.updatedAtText)
    }

    @Test
    fun `shows temperatures in the selected unit`() = runTest {
        repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather(temperatureC = 26.6))) }
        val viewModel = createViewModel().alsoSubscribe(this)
        assertEquals("27°", (viewModel.uiState.value.content as UiState.Success).data.single().temperatureText)

        settingsRepository.state.value = UserSettings(temperatureUnit = TemperatureUnit.Fahrenheit)
        runCurrent()

        assertEquals("80°", (viewModel.uiState.value.content as UiState.Success).data.single().temperatureText)
    }

    /** stateIn(WhileSubscribed) akışını ekran açıkmış gibi aktif tutar. */
    private fun CityListViewModel.alsoSubscribe(scope: TestScope): CityListViewModel {
        scope.backgroundScope.launch { uiState.collect {} }
        scope.runCurrent()
        return this
    }
}
