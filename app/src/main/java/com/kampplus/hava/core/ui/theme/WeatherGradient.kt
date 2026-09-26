package com.kampplus.hava.core.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/** Rengi siyaha doğru [fraction] kadar koyulaştırır (0 = aynı, 1 = siyah). */
fun Color.darken(fraction: Float): Color = lerp(this, Color.Black, fraction.coerceIn(0f, 1f))

/**
 * Detay başlığının arka plan renkleri: koşulun vurgu renginden türeyen, üzerine beyaz metnin
 * her koşulda okunabildiği koyu bir geçiş. Gece için lacivert tonlar.
 */
fun heroColors(tint: Color, isDay: Boolean): List<Color> = if (isDay) {
    listOf(tint.darken(DAY_TOP), tint.darken(DAY_BOTTOM))
} else {
    listOf(WeatherPalette.Night.darken(NIGHT_TOP), WeatherPalette.Night.darken(NIGHT_BOTTOM))
}

private const val DAY_TOP = 0.5f
private const val DAY_BOTTOM = 0.18f
private const val NIGHT_TOP = 0.65f
private const val NIGHT_BOTTOM = 0.35f

fun heroBrush(tint: Color, isDay: Boolean): Brush = Brush.verticalGradient(heroColors(tint, isDay))
