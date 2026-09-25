package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.core.ui.text.UiText

/** Detay ekranında gösterilmeye hazır, biçimlendirilmiş şehir havası. Değeri olmayan alanlar ekranda çizilmez. */
data class CityDetailUiModel(
    val cityId: Long,
    val title: String,
    val subtitle: String,
    val temperatureText: String,
    val temperatureC: Double,
    val conditionEmoji: String,
    val conditionLabel: UiText,
    val apparentTemperatureText: String?,
    val humidityPercent: Int?,
    val windSpeedKmh: Int?,
    val observedAtText: String
)
