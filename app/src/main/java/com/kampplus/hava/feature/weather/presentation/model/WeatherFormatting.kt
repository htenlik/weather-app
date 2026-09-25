package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.feature.weather.domain.model.City
import kotlin.math.roundToInt

/** "Ankara, Türkiye" — bölge ile ülke aynıysa tekrar etmez. Liste ve detay ekranı aynı biçimi kullanır. */
fun City.displaySubtitle(): String = listOfNotNull(region, country).distinct().joinToString(", ")

/** 21.4 → "21°" */
fun Double.toTemperatureText(): String = "${roundToInt()}°"
