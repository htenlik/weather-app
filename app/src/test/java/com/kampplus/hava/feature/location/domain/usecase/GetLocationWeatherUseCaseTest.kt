package com.kampplus.hava.feature.location.domain.usecase

import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.testing.FakeLocationRepository
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.cityWeather
import com.kampplus.hava.testing.userLocation
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetLocationWeatherUseCaseTest {

    private val locationRepository = FakeLocationRepository()
    private val weatherRepository = FakeWeatherRepository()
    private val useCase = GetLocationWeatherUseCase(locationRepository, weatherRepository)

    @Test
    fun `fetches weather for the located place`() = runTest {
        locationRepository.result = { AppResult.Success(userLocation()) }
        weatherRepository.currentWeatherResult = { cities -> AppResult.Success(cities.map { cityWeather(city = it, temperatureC = 18.0) }) }

        val result = useCase() as AppResult.Success

        assertTrue(result.data.city.isDeviceLocation)
        assertEquals("Çankaya", result.data.city.name)
        assertEquals(18.0, result.data.current.temperatureC, 0.0)
    }

    @Test
    fun `location failure is returned without asking for weather`() = runTest {
        locationRepository.result = { AppResult.Failure(AppError.LocationDisabled) }
        var weatherRequested = false
        weatherRepository.currentWeatherResult = { _ ->
            weatherRequested = true
            AppResult.Success(emptyList())
        }

        assertEquals(AppResult.Failure(AppError.LocationDisabled), useCase())
        assertFalse(weatherRequested)
    }

    @Test
    fun `weather failure is passed through`() = runTest {
        locationRepository.result = { AppResult.Success(userLocation()) }
        weatherRepository.currentWeatherResult = { _ -> AppResult.Failure(AppError.Network) }

        assertEquals(AppResult.Failure(AppError.Network), useCase())
    }
}
