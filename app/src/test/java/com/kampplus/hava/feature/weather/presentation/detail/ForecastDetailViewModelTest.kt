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
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.settings.domain.model.UserSettings
import com.kampplus.hava.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kampplus.hava.feature.weather.domain.usecase.GetForecastUseCase
import com.kampplus.hava.testing.FakeSettingsRepository
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.forecast
import com.kampplus.hava.testing.testUiMapper
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ForecastDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWeatherRepository()
    private val favoritesRepository = FavoriteCityRepositoryImpl(InMemoryFavoriteCityDataSource())
    private val settingsRepository = FakeSettingsRepository()

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
        observeSettings = ObserveSettingsUseCase(settingsRepository),
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
    fun `formats every temperature in the selected unit`() = runTest {
        repository.forecastResult = { AppResult.Success(forecast()) }
        settingsRepository.state.value = UserSettings(temperatureUnit = TemperatureUnit.Fahrenheit)

        createViewModel().uiState.test {
            awaitItem()
            val model = (awaitItem() as UiState.Success).data
            assertEquals("71°", model.temperatureText)
            assertEquals("68°", model.feelsLikeText)
            assertEquals("54°", model.daily.first().minText)
            assertEquals("75°", model.daily.first().maxText)
            assertEquals(21.4, model.temperatureC, 0.0)
        }
    }

    @Test
    fun `switches unit live when the setting changes`() = runTest {
        repository.forecastResult = { AppResult.Success(forecast()) }

        createViewModel().uiState.test {
            awaitItem()
            assertEquals("21°", (awaitItem() as UiState.Success).data.temperatureText)

            settingsRepository.state.value = UserSettings(temperatureUnit = TemperatureUnit.Fahrenheit)

            assertEquals("71°", (awaitItem() as UiState.Success).data.temperatureText)
        }
    }

    @Test
    fun `shows error when forecast fails`() = runTest {
        repository.forecastResult = { AppResult.Failure(AppError.Network) }

        createViewModel().uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertTrue(awaitItem() is UiState.Error)
        }
    }
}
