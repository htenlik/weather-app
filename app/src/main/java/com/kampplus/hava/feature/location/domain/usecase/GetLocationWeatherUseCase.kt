package com.kampplus.hava.feature.location.domain.usecase

import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.location.domain.repository.LocationRepository
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/** Konumu bulur, o nokta için anlık havayı getirir. Konum hatası olduğu gibi iletilir; ağ istenmez. */
class GetLocationWeatherUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(): AppResult<CityWeather> {
        val city = when (val location = locationRepository.getCurrentLocation()) {
            is AppResult.Failure -> return location
            is AppResult.Success -> location.data.toCity()
        }
        return when (val weather = weatherRepository.getCurrentWeather(listOf(city)).first()) {
            is AppResult.Failure -> weather
            is AppResult.Success -> weather.data.firstOrNull()?.let { AppResult.Success(it) } ?: AppResult.Failure(AppError.NotFound)
        }
    }
}
