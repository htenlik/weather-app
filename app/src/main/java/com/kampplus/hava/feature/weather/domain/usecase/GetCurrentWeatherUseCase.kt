package com.kampplus.hava.feature.weather.domain.usecase

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Verilen şehirlerin anlık havası (ör. favoriler ekranı). Boş listede istek atılmaz. */
class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(cities: List<City>): Flow<AppResult<List<CityWeather>>> = repository.getCurrentWeather(cities)
}
