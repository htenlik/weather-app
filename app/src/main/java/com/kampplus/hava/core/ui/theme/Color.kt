package com.kampplus.hava.core.ui.theme

import androidx.compose.ui.graphics.Color

// Marka renkleri
internal val Teal10 = Color(0xFF002022)
internal val Teal20 = Color(0xFF00373A)
internal val Teal30 = Color(0xFF004F53)
internal val Teal40 = Color(0xFF00696E)
internal val Teal80 = Color(0xFF4CD9E0)
internal val Teal90 = Color(0xFF6FF6FD)
internal val Amber40 = Color(0xFF8B5000)
internal val Amber80 = Color(0xFFFFB86E)
internal val Amber90 = Color(0xFFFFDCBE)
internal val Neutral10 = Color(0xFF191C1C)
internal val Neutral90 = Color(0xFFE0E3E3)
internal val Neutral95 = Color(0xFFEFF1F1)
internal val Neutral99 = Color(0xFFFAFDFC)
internal val NeutralVariant30 = Color(0xFF3F4949)
internal val NeutralVariant90 = Color(0xFFDAE4E5)
internal val Red40 = Color(0xFFBA1A1A)
internal val Red80 = Color(0xFFFFB4AB)

/** Sıcaklık vurgusu renkleri (soğuk → sıcak). */
object TemperaturePalette {
    val Freezing = Color(0xFF1565C0)
    val Cold = Color(0xFF0288D1)
    val Mild = Color(0xFF2E7D32)
    val Warm = Color(0xFFEF6C00)
    val Hot = Color(0xFFC62828)
}

/** Hava koşulu vurgu renkleri; ikon tonu ve detay başlığındaki gradient buradan türetilir. */
object WeatherPalette {
    val Sun = Color(0xFFF59E0B)
    val Night = Color(0xFF3F51B5)
    val Cloud = Color(0xFF78909C)
    val Fog = Color(0xFF9E9E9E)
    val Rain = Color(0xFF1E88E5)
    val Snow = Color(0xFF4FC3F7)
    val Storm = Color(0xFF5E35B1)
}
