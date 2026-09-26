package com.kampplus.hava.feature.weather.data.mapper

import com.kampplus.hava.feature.weather.data.remote.dto.CurrentDto
import com.kampplus.hava.feature.weather.data.remote.dto.DailyDto
import com.kampplus.hava.feature.weather.data.remote.dto.ForecastResponseDto
import com.kampplus.hava.feature.weather.data.remote.dto.HourlyDto
import com.kampplus.hava.feature.weather.domain.model.CurrentWeather
import com.kampplus.hava.feature.weather.domain.model.DailyForecast
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.HourlyForecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.serialization.SerializationException

/** `current` istenmiş ama gelmemişse yanıt bozuktur → AppError.Parse. */
fun ForecastResponseDto.requireCurrent(): CurrentDto = current ?: throw SerializationException("Forecast response has no current block")

fun ForecastResponseDto.toForecast(fetchedAt: Instant): Forecast = Forecast(
    current = requireCurrent().toDomain(),
    hourly = hourly?.toDomain().orEmpty(),
    daily = daily?.toDomain().orEmpty(),
    fetchedAt = fetchedAt
)

/** Zamanlar `timezone=auto` ile şehrin yerel saatinde, ofsetsiz ISO formatında gelir. */
fun CurrentDto.toDomain() = CurrentWeather(
    temperatureC = temperature,
    weatherCode = WeatherCode(weatherCode),
    observedAt = LocalDateTime.parse(time),
    apparentTemperatureC = apparentTemperature,
    humidityPercent = relativeHumidity,
    windSpeedKmh = windSpeed,
    isDay = isDay != 0
)

/** Sütun biçimindeki listeleri satırlara çevirir; eksik değer içeren satırlar atlanır. */
fun HourlyDto.toDomain(): List<HourlyForecast> = time.indices.mapNotNull { index ->
    val temperature = temperature.getOrNull(index) ?: return@mapNotNull null
    val code = weatherCode.getOrNull(index) ?: return@mapNotNull null
    HourlyForecast(
        time = LocalDateTime.parse(time[index]),
        temperatureC = temperature,
        weatherCode = WeatherCode(code),
        precipitationProbability = precipitationProbability.getOrNull(index),
        isDay = isDay.getOrNull(index)?.let { it != 0 } ?: true
    )
}

fun DailyDto.toDomain(): List<DailyForecast> = time.indices.mapNotNull { index ->
    val max = temperatureMax.getOrNull(index) ?: return@mapNotNull null
    val min = temperatureMin.getOrNull(index) ?: return@mapNotNull null
    val code = weatherCode.getOrNull(index) ?: return@mapNotNull null
    DailyForecast(
        date = LocalDate.parse(time[index]),
        minTemperatureC = min,
        maxTemperatureC = max,
        weatherCode = WeatherCode(code),
        precipitationProbability = precipitationProbabilityMax.getOrNull(index)
    )
}
