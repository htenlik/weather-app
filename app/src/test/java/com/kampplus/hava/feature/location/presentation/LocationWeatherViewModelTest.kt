package com.kampplus.hava.feature.location.presentation

import app.cash.turbine.test
import com.kampplus.hava.R
import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.location.domain.usecase.GetLocationWeatherUseCase
import com.kampplus.hava.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kampplus.hava.testing.FakeLocationRepository
import com.kampplus.hava.testing.FakeNetworkMonitor
import com.kampplus.hava.testing.FakeSettingsRepository
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.cityWeather
import com.kampplus.hava.testing.testUiMapper
import com.kampplus.hava.testing.userLocation
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LocationWeatherViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val locationRepository = FakeLocationRepository(result = { AppResult.Success(userLocation()) })
    private val weatherRepository = FakeWeatherRepository().apply {
        currentWeatherResult = { cities -> AppResult.Success(cities.map { cityWeather(city = it, temperatureC = 17.6) }) }
    }

    private fun createViewModel() = LocationWeatherViewModel(
        getLocationWeather = GetLocationWeatherUseCase(locationRepository, weatherRepository),
        observeSettings = ObserveSettingsUseCase(FakeSettingsRepository()),
        networkMonitor = FakeNetworkMonitor(),
        uiMapper = testUiMapper()
    )

    @Test
    fun `asks for permission until it is granted, then shows the located weather`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(LocationUiState.Locating, awaitItem())

            viewModel.onPermissionChanged(granted = false)
            assertEquals(LocationUiState.PermissionRequired, awaitItem())

            viewModel.onPermissionResult(granted = true, canAskAgain = true)
            assertEquals(LocationUiState.Locating, awaitItem())
            val ready = awaitItem() as LocationUiState.Ready
            assertEquals("Çankaya", ready.weather.title)
            assertEquals("Ankara, Türkiye", ready.weather.subtitle)
            assertEquals("18°", ready.weather.temperatureText)
            assertTrue(viewModel.city?.isDeviceLocation == true)
        }
    }

    @Test
    fun `permanent denial points to app settings`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onPermissionResult(granted = false, canAskAgain = false)
            assertEquals(LocationUiState.PermissionDenied, awaitItem())

            // Ekran tekrar öne geldiğinde izin hâlâ yoksa kalıcı ret durumu korunur.
            viewModel.onPermissionChanged(granted = false)
            expectNoEvents()
        }
    }

    @Test
    fun `disabled location services produce an error that opens location settings`() = runTest {
        locationRepository.result = { AppResult.Failure(AppError.LocationDisabled) }
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onPermissionChanged(granted = true)
            val error = awaitItem() as LocationUiState.Error
            assertTrue(error.isLocationDisabled)
            assertEquals(UiText.Resource(R.string.error_location_disabled), error.message)
        }
    }

    @Test
    fun `retry after a failure loads again`() = runTest {
        locationRepository.result = { AppResult.Failure(AppError.LocationUnavailable) }
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onPermissionChanged(granted = true)
            assertTrue(awaitItem() is LocationUiState.Error)

            locationRepository.result = { AppResult.Success(userLocation()) }
            viewModel.onRetry()
            assertTrue(awaitItem() is LocationUiState.Ready)
            assertEquals(2, locationRepository.requestCount)
        }
    }

    @Test
    fun `resuming after a failure retries automatically`() = runTest {
        locationRepository.result = { AppResult.Failure(AppError.LocationDisabled) }
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onPermissionChanged(granted = true)
            assertTrue(awaitItem() is LocationUiState.Error)

            locationRepository.result = { AppResult.Success(userLocation()) }
            viewModel.onPermissionChanged(granted = true)
            assertTrue(awaitItem() is LocationUiState.Ready)
        }
    }

    @Test
    fun `resuming with permission already granted does not reload`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onPermissionChanged(granted = true)
            assertTrue(awaitItem() is LocationUiState.Ready)

            viewModel.onPermissionChanged(granted = true)
            expectNoEvents()
            assertEquals(1, locationRepository.requestCount)
        }
    }
}
