package com.kampplus.hava.feature.weather.presentation.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kampplus.hava.R
import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.navigation.CityDetailDestination
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.policy.WmoWeatherConditionClassifier
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.presentation.model.WeatherConditionUiRegistry
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.city
import com.kampplus.hava.testing.cityWeather
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CityDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWeatherRepository()

    private fun createViewModel(arguments: Map<String, Any?>) = CityDetailViewModel(
        savedStateHandle = SavedStateHandle(arguments),
        getCityWeathers = GetCityWeathersUseCase(repository),
        conditionClassifier = WmoWeatherConditionClassifier(),
        conditionUiRegistry = WeatherConditionUiRegistry(emptyMap())
    )

    @Test
    fun `shows the city whose id was passed through navigation`() = runTest {
        repository.cityWeathersResult = {
            AppResult.Success(
                listOf(
                    cityWeather(city = city(id = 1, name = "Ankara"), temperatureC = 21.4),
                    cityWeather(city = city(id = 2, name = "İzmir", region = "İzmir"), temperatureC = 26.6)
                )
            )
        }

        createViewModel(mapOf(CityDetailDestination.ARG_CITY_ID to 2L)).uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            val detail = (awaitItem() as UiState.Success).data
            assertEquals(2L, detail.cityId)
            assertEquals("İzmir", detail.title)
            assertEquals("İzmir, Türkiye", detail.subtitle)
            assertEquals("27°", detail.temperatureText)
            assertEquals("12:00", detail.observedAtText)
        }
    }

    @Test
    fun `missing parameter produces an explanatory error instead of crashing`() = runTest {
        createViewModel(emptyMap()).uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Error(UiText.Resource(R.string.detail_invalid_parameter)), awaitItem())
        }
    }

    @Test
    fun `non positive id is treated as invalid parameter`() = runTest {
        createViewModel(mapOf(CityDetailDestination.ARG_CITY_ID to -1L)).uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Error(UiText.Resource(R.string.detail_invalid_parameter)), awaitItem())
        }
    }

    @Test
    fun `unknown id produces a not found message with the id`() = runTest {
        repository.cityWeathersResult = { AppResult.Success(listOf(cityWeather(city = city(id = 1)))) }

        createViewModel(mapOf(CityDetailDestination.ARG_CITY_ID to 42L)).uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Error(UiText.Resource(R.string.detail_city_not_found, 42L)), awaitItem())
        }
    }

    @Test
    fun `repository failure is shown as error`() = runTest {
        repository.cityWeathersResult = { AppResult.Failure(AppError.Network) }

        createViewModel(mapOf(CityDetailDestination.ARG_CITY_ID to 1L)).uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertTrue(awaitItem() is UiState.Error)
        }
    }
}
