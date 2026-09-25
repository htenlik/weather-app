package com.kampplus.hava.testing

import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Sonuçları testten ayarlanabilen sahte repository. */
class FakeWeatherRepository(
    var cityWeathersResult: () -> AppResult<List<CityWeather>> = { AppResult.Success(emptyList()) },
    var forecastResult: (City) -> AppResult<Forecast> = { AppResult.Failure(AppError.NotFound) }
) : WeatherRepository {

    val requestedForecasts = mutableListOf<City>()

    /** Kaç kez liste istendi; "hızlı tıklamalar ek istek üretmez" ölçütü için. */
    var cityWeathersCalls = 0
        private set

    /** null değilse istek, kapı açılana ([CompletableDeferred.complete]) kadar bekler: uçuştaki isteği simüle eder. */
    var gate: CompletableDeferred<Unit>? = null

    override fun getCityWeathers(): Flow<AppResult<List<CityWeather>>> = flow {
        cityWeathersCalls++
        gate?.await()
        emit(cityWeathersResult())
    }

    override suspend fun getForecast(city: City): AppResult<Forecast> {
        requestedForecasts += city
        gate?.await()
        return forecastResult(city)
    }
}
