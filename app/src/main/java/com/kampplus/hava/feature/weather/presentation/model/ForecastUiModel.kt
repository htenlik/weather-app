package com.kampplus.hava.feature.weather.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.WeatherPalette

data class ForecastUiModel(
    val cityId: Long,
    val cityName: String,
    val subtitle: String,
    val temperatureText: String,
    val temperatureC: Double,
    val conditionEmoji: String,
    val conditionLabel: UiText,
    val feelsLikeText: String?,
    val humidityText: String?,
    val windText: String?,
    val hourly: List<HourlyUiModel>,
    val daily: List<DailyUiModel>,
    val isFavorite: Boolean = false,
    val icon: ImageVector = Icons.Filled.Thermostat,
    val iconTint: Color = WeatherPalette.Fog,
    val isDay: Boolean = true,
    /** Bugünün en düşük / en yüksek sıcaklığı; günlük tahmin yoksa null. */
    val todayMinText: String? = null,
    val todayMaxText: String? = null
)

data class HourlyUiModel(
    val timeText: String,
    val emoji: String,
    val temperatureText: String,
    val precipitationText: String?,
    val icon: ImageVector = Icons.Filled.Thermostat,
    val iconTint: Color = WeatherPalette.Fog,
    val isNow: Boolean = false
)

data class DailyUiModel(
    val dayLabel: UiText,
    val emoji: String,
    val minText: String,
    val maxText: String,
    val precipitationText: String?,
    val icon: ImageVector = Icons.Filled.Thermostat,
    val iconTint: Color = WeatherPalette.Fog,
    /** Sıcaklık aralığı çubuğu için ham değerler (°C). */
    val minC: Double = 0.0,
    val maxC: Double = 0.0
)
