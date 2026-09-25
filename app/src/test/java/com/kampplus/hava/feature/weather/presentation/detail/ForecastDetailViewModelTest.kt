package com.kampplus.hava.feature.weather.presentation.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kampplus.hava.R
import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.navigation.ForecastDestination
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.favorites.data.local.InMemoryFavoriteCityDataSource
import com.kampplus.hava.feature.favorites.data.repository.FavoriteCityRepositoryImpl
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.weather.domain.usecase.GetForecastUseCase
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.forecast
import com.kampplus.hava.testing.testUiMapper
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForecastDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWeatherRepository()
    private val favoritesRepository = FavoriteCityRepositoryImpl(InMemoryFavoriteCityDataSource())

    private fun createViewModel() = ForecastDetailViewModel(
        savedStateHandle = SavedStateHandle(
            mapOf(
                ForecastDestination.ARG_CITY_ID to 311046L,
                ForecastDestination.ARG_NAME to "İzmir",
                ForecastDestination.ARG_REGION to "İzmir",
                ForecastDestination.ARG_COUNTRY to "Türkiye",
                ForecastDestination.ARG_LATITUDE to 38.4127,
                ForecastDestination.ARG_LONGITUDE to 27.1384
            )
        ),
        getForecast = GetForecastUseCase(repository),
        observeFavoriteCityIds = ObserveFavoriteCityIdsUseCase(favoritesRepository),
        toggleFavoriteCity = ToggleFavoriteCityUseCase(favoritesRepository),
        uiMapper = testUiMapper()
    )

    @Test
    fun `requests forecast for the city passed through navigation`() = runTest {
        repository.forecastResult = { AppResult.Success(forecast()) }

        createViewModel().uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            val model = (awaitItem() as UiState.Success).data
            assertEquals("İzmir", model.cityName)
            assertEquals("21°", model.temperatureText)
            assertEquals("12 km/sa", model.windText)
        }
        val requested = repository.requestedForecasts.single()
        assertEquals(38.4127, requested.coordinates.latitude, 0.0)
        assertEquals(311046L, requested.id)
    }

    @Test
    fun `hourly starts from current hour and daily starts with today`() = runTest {
        repository.forecastResult = { AppResult.Success(forecast()) }

        createViewModel().uiState.test {
            awaitItem()
            val model = (awaitItem() as UiState.Success).data
            assertEquals(24, model.hourly.size)
            assertEquals("12:00", model.hourly.first().timeText)
            assertEquals(UiText.Resource(R.string.today), model.daily.first().dayLabel)
            assertEquals("%30", model.daily.first().precipitationText)
        }
    }

    @Test
    fun `shows a user facing message when forecast fails`() = runTest {
        repository.forecastResult = { AppResult.Failure(AppError.Network) }

        createViewModel().uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Error(UiText.Resource(R.string.error_network)), awaitItem())
        }
    }

    @Test
    fun `retry after error emits loading and then the forecast`() = runTest {
        repository.forecastResult = { AppResult.Failure(AppError.Network) }
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertTrue(awaitItem() is UiState.Error)

            repository.forecastResult = { AppResult.Success(forecast()) }
            repository.gate = CompletableDeferred()
            viewModel.loadData()
            assertEquals(UiState.Loading, awaitItem())

            repository.gate?.complete(Unit)
            assertTrue(awaitItem() is UiState.Success)
        }
        assertEquals(2, repository.requestedForecasts.size)
    }

    @Test
    fun `repeated loadData calls while a request is in flight do not start new requests`() = runTest {
        repository.forecastResult = { AppResult.Success(forecast()) }
        repository.gate = CompletableDeferred()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            viewModel.loadData()
            viewModel.loadData()
            runCurrent()
            assertEquals(1, repository.requestedForecasts.size)

            repository.gate?.complete(Unit)
            assertTrue(awaitItem() is UiState.Success)
            assertEquals(1, repository.requestedForecasts.size)
        }
    }
}
