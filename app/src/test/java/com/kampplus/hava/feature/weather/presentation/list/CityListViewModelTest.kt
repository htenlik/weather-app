package com.kampplus.hava.feature.weather.presentation.list

import app.cash.turbine.test
import com.kampplus.hava.R
import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.favorites.data.local.InMemoryFavoriteCityDataSource
import com.kampplus.hava.feature.favorites.data.repository.FavoriteCityRepositoryImpl
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.city
import com.kampplus.hava.testing.cityWeather
import com.kampplus.hava.testing.testUiMapper
import kotlinx.coroutines.CompletableDeferred
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
    private val favoritesRepository = FavoriteCityRepositoryImpl(InMemoryFavoriteCityDataSource())

    private fun createViewModel() = CityListViewModel(
        getCityWeathers = GetCityWeathersUseCase(repository),
        observeFavoriteCityIds = ObserveFavoriteCityIdsUseCase(favoritesRepository),
        toggleFavoriteCity = ToggleFavoriteCityUseCase(favoritesRepository),
        uiMapper = testUiMapper()
    )

    @Test
    fun `emits loading then formatted city weathers`() = runTest {
        repository.cityWeathersResult = {
            AppResult.Success(listOf(cityWeather(city = city(name = "İzmir", region = "İzmir"), temperatureC = 26.6)))
        }

        createViewModel().uiState.test {
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

        createViewModel().uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun `emits a user facing error message when repository fails`() = runTest {
        repository.cityWeathersResult = { AppResult.Failure(AppError.Network) }

        createViewModel().uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Error(UiText.Resource(R.string.error_network)), awaitItem())
        }
    }

    @Test
    fun `retry after error emits loading and then the loaded list`() = runTest {
        repository.cityWeathersResult = { AppResult.Failure(AppError.Network) }
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertTrue(awaitItem() is UiState.Error)

            repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather())) }
            repository.gate = CompletableDeferred()
            viewModel.loadData()
            assertEquals(UiState.Loading, awaitItem())

            repository.gate?.complete(Unit)
            assertTrue(awaitItem() is UiState.Success)
        }
    }

    @Test
    fun `repeated loadData calls while a request is in flight do not start new requests`() = runTest {
        repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather())) }
        repository.gate = CompletableDeferred()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            viewModel.loadData()
            viewModel.loadData()
            viewModel.refresh()
            runCurrent()
            assertEquals(1, repository.cityWeathersCalls)

            repository.gate?.complete(Unit)
            assertTrue(awaitItem() is UiState.Success)
            assertEquals(1, repository.cityWeathersCalls)
        }
    }

    @Test
    fun `reload cancels the in flight request and starts a new one`() = runTest {
        repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather())) }
        repository.gate = CompletableDeferred()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            runCurrent()
            assertEquals(1, repository.cityWeathersCalls)

            viewModel.reload()
            runCurrent()
            assertEquals(2, repository.cityWeathersCalls)

            repository.gate?.complete(Unit)
            assertTrue(awaitItem() is UiState.Success)
        }
    }

    @Test
    fun `refresh keeps the current content visible and then replaces it`() = runTest {
        repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather(temperatureC = 20.0))) }
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals("20°", (awaitItem() as UiState.Success).data.single().temperatureText)

            repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather(temperatureC = 25.0))) }
            repository.gate = CompletableDeferred()
            viewModel.refresh()
            runCurrent()
            assertTrue(viewModel.isRefreshing.value)
            assertEquals("20°", (viewModel.uiState.value as UiState.Success).data.single().temperatureText)

            repository.gate?.complete(Unit)
            assertEquals("25°", (awaitItem() as UiState.Success).data.single().temperatureText)
            assertFalse(viewModel.isRefreshing.value)
        }
    }
}
