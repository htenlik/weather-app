package com.kampplus.hava.testing

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.model.CurrentWeather
import com.kampplus.hava.feature.weather.domain.model.DailyForecast
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.HourlyForecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import java.time.Instant
import java.time.LocalDateTime

fun city(
    id: Long = 323786,
    name: String = "Ankara",
    region: String? = "Ankara",
    country: String? = "Türkiye",
    coordinates: Coordinates = Coordinates(39.92, 32.85)
) = City(id = id, name = name, region = region, country = country, coordinates = coordinates)

/** 24 Eylül 2026 12:05 (Türkiye) — sabit "alınma" anı; [testUiMapper] saati de buna göre kurulur. */
val FETCHED_AT: Instant = Instant.parse("2026-09-24T09:05:00Z")

fun cityWeather(city: City = city(), temperatureC: Double = 21.4, code: Int = 0, fetchedAt: Instant = FETCHED_AT) = CityWeather(
    city = city,
    current = CurrentWeather(
        temperatureC = temperatureC,
        weatherCode = WeatherCode(code),
        observedAt = LocalDateTime.of(2026, 9, 24, 12, 0)
    ),
    fetchedAt = fetchedAt
)

/** 12:30'da gözlenmiş, 00:00'dan başlayan 48 saatlik ve 7 günlük tahmin. */
fun forecast(observedAt: LocalDateTime = LocalDateTime.of(2026, 9, 24, 12, 30), fetchedAt: Instant = FETCHED_AT) = Forecast(
    current = CurrentWeather(
        temperatureC = 21.4,
        weatherCode = WeatherCode(0),
        observedAt = observedAt,
        apparentTemperatureC = 20.2,
        humidityPercent = 45,
        windSpeedKmh = 11.6
    ),
    hourly = List(48) { hour ->
        HourlyForecast(
            time = observedAt.toLocalDate().atStartOfDay().plusHours(hour.toLong()),
            temperatureC = 15.0 + hour % 10,
            weatherCode = WeatherCode(1),
            precipitationProbability = 0
        )
    },
    daily = List(7) { day ->
        DailyForecast(
            date = observedAt.toLocalDate().plusDays(day.toLong()),
            minTemperatureC = 12.0,
            maxTemperatureC = 24.0,
            weatherCode = WeatherCode(61),
            precipitationProbability = 30
        )
    },
    fetchedAt = fetchedAt
)
