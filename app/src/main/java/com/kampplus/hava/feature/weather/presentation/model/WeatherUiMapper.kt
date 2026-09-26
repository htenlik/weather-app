package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.R
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.policy.WeatherConditionClassifier
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Domain modellerini ekranların ihtiyaç duyduğu biçimlendirilmiş modellere çevirir.
 * Sıcaklıklar Celsius gelir; [TemperatureUnit] yalnızca metinleri etkiler, renk eşiği için ham değer korunur.
 */
class WeatherUiMapper @Inject constructor(
    private val conditionClassifier: WeatherConditionClassifier,
    private val conditionUiRegistry: WeatherConditionUiRegistry,
    private val clock: Clock
) {
    fun toListItem(
        cityWeather: CityWeather,
        isFavorite: Boolean = false,
        unit: TemperatureUnit = TemperatureUnit.Celsius
    ): CityWeatherUiModel = with(cityWeather) {
        val conditionUi = conditionUi(current.weatherCode, current.isDay)
        CityWeatherUiModel(
            cityId = city.id,
            title = city.name,
            subtitle = subtitle(city),
            temperatureText = degrees(current.temperatureC, unit),
            temperatureC = current.temperatureC,
            conditionEmoji = conditionUi.emoji,
            conditionLabel = conditionUi.label,
            isFavorite = isFavorite,
            icon = conditionUi.icon,
            iconTint = conditionUi.tint
        )
    }

    fun toForecast(
        city: City,
        forecast: Forecast,
        isFavorite: Boolean = false,
        unit: TemperatureUnit = TemperatureUnit.Celsius
    ): ForecastUiModel = with(forecast) {
        val conditionUi = conditionUi(current.weatherCode, current.isDay)
        val currentHour = current.observedAt.truncatedTo(ChronoUnit.HOURS)
        val today = daily.firstOrNull()
        ForecastUiModel(
            cityId = city.id,
            cityName = city.name,
            subtitle = subtitle(city),
            temperatureText = degrees(current.temperatureC, unit),
            temperatureC = current.temperatureC,
            conditionEmoji = conditionUi.emoji,
            conditionLabel = conditionUi.label,
            feelsLikeText = current.apparentTemperatureC?.let { degrees(it, unit) },
            humidityText = current.humidityPercent?.let { "%$it" },
            windText = current.windSpeedKmh?.let { "${it.roundToInt()} km/sa" },
            hourly = hourly.filter { !it.time.isBefore(currentHour) }.take(HOURLY_COUNT).mapIndexed { index, hour ->
                // Gece/gündüz saat bazında bilinmediği için anlık değere göre karar verilir.
                val hourUi = conditionUi(hour.weatherCode, current.isDay)
                HourlyUiModel(
                    timeText = hour.time.format(HOUR_FORMAT),
                    emoji = hourUi.emoji,
                    temperatureText = degrees(hour.temperatureC, unit),
                    precipitationText = percent(hour.precipitationProbability),
                    icon = hourUi.icon,
                    iconTint = hourUi.tint,
                    isNow = index == 0
                )
            },
            daily = daily.mapIndexed { index, day ->
                val dayUi = conditionUi(day.weatherCode, isDay = true)
                DailyUiModel(
                    dayLabel = if (index == 0) {
                        UiText.Resource(R.string.today)
                    } else {
                        UiText.Dynamic(day.date.format(DAY_FORMAT).replaceFirstChar(Char::titlecase))
                    },
                    emoji = dayUi.emoji,
                    minText = degrees(day.minTemperatureC, unit),
                    maxText = degrees(day.maxTemperatureC, unit),
                    precipitationText = percent(day.precipitationProbability),
                    icon = dayUi.icon,
                    iconTint = dayUi.tint,
                    minC = day.minTemperatureC,
                    maxC = day.maxTemperatureC
                )
            },
            isFavorite = isFavorite,
            isFavoritable = !city.isDeviceLocation,
            icon = conditionUi.icon,
            iconTint = conditionUi.tint,
            isDay = current.isDay,
            todayMinText = today?.let { degrees(it.minTemperatureC, unit) },
            todayMaxText = today?.let { degrees(it.maxTemperatureC, unit) },
            updatedAtText = updatedAtText(fetchedAt)
        )
    }

    /** Verinin alınma anı, cihaz saatiyle: bugünse yalnızca saat, değilse gün ve ay da gösterilir. */
    fun updatedAtText(fetchedAt: Instant): String {
        val local = fetchedAt.atZone(clock.zone)
        val pattern = if (local.toLocalDate() == LocalDate.now(clock)) UPDATED_TIME_FORMAT else UPDATED_DATE_TIME_FORMAT
        return local.format(pattern)
    }

    private fun conditionUi(code: WeatherCode, isDay: Boolean): WeatherConditionUi =
        conditionUiRegistry.resolve(conditionClassifier.classify(code)).at(isDay)

    private fun subtitle(city: City) = listOfNotNull(city.region, city.country).distinct().joinToString(", ")

    private fun degrees(celsius: Double, unit: TemperatureUnit) = "${unit.fromCelsius(celsius).roundToInt()}°"

    private fun percent(value: Int?) = value?.takeIf { it > 0 }?.let { "%$it" }

    private companion object {
        const val HOURLY_COUNT = 24
        val HOUR_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val DAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("tr"))
        val UPDATED_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val UPDATED_DATE_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM HH:mm", Locale.forLanguageTag("tr"))
    }
}
