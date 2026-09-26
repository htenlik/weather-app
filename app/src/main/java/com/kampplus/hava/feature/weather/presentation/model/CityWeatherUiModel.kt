package com.kampplus.hava.feature.weather.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.WeatherPalette

/** Liste satırında gösterilmeye hazır, biçimlendirilmiş şehir havası. */
data class CityWeatherUiModel(
    val cityId: Long,
    val title: String,
    val subtitle: String,
    val temperatureText: String,
    val temperatureC: Double,
    val conditionEmoji: String,
    val conditionLabel: UiText,
    val isFavorite: Boolean = false,
    val icon: ImageVector = Icons.Filled.Thermostat,
    val iconTint: Color = WeatherPalette.Fog
)
