package com.kampplus.hava.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Marka renkleri: turkuaz ana renk, kehribar ikincil renk, mavi üçüncül renk.
internal val Teal10 = Color(0xFF002020)
internal val Teal20 = Color(0xFF003737)
internal val Teal30 = Color(0xFF004F50)
internal val Teal40 = Color(0xFF006A6A)
internal val Teal80 = Color(0xFF4CDADA)
internal val Teal90 = Color(0xFF6FF7F6)
internal val Amber10 = Color(0xFF2C1600)
internal val Amber20 = Color(0xFF4A2800)
internal val Amber30 = Color(0xFF6A3C00)
internal val Amber40 = Color(0xFF8B5000)
internal val Amber80 = Color(0xFFFFB86E)
internal val Amber90 = Color(0xFFFFDCBE)
internal val Blue10 = Color(0xFF041C35)
internal val Blue20 = Color(0xFF1C314B)
internal val Blue30 = Color(0xFF334863)
internal val Blue40 = Color(0xFF4B607C)
internal val Blue80 = Color(0xFFB3C8E8)
internal val Blue90 = Color(0xFFD3E4FF)
internal val Red10 = Color(0xFF410002)
internal val Red20 = Color(0xFF690005)
internal val Red30 = Color(0xFF93000A)
internal val Red40 = Color(0xFFBA1A1A)
internal val Red80 = Color(0xFFFFB4AB)
internal val Red90 = Color(0xFFFFDAD6)

// Turkuaza çalan nötr tonlar: yüzeyler, kartlar ve çubuklar aynı aileden gelir.
internal val Neutral4 = Color(0xFF090F0F)
internal val Neutral6 = Color(0xFF0F1414)
internal val Neutral10 = Color(0xFF171D1C)
internal val Neutral12 = Color(0xFF1B2120)
internal val Neutral17 = Color(0xFF252B2B)
internal val Neutral20 = Color(0xFF2B3231)
internal val Neutral22 = Color(0xFF303636)
internal val Neutral24 = Color(0xFF343A3A)
internal val Neutral87 = Color(0xFFDBDDDD)
internal val Neutral90 = Color(0xFFDEE4E3)
internal val Neutral92 = Color(0xFFE3E5E5)
internal val Neutral94 = Color(0xFFE9EBEB)
internal val Neutral95 = Color(0xFFEEF1F0)
internal val Neutral96 = Color(0xFFF4F7F6)
internal val Neutral99 = Color(0xFFFAFDFC)
internal val NeutralVariant30 = Color(0xFF3F4948)
internal val NeutralVariant50 = Color(0xFF6F7979)
internal val NeutralVariant60 = Color(0xFF889392)
internal val NeutralVariant80 = Color(0xFFBEC9C8)
internal val NeutralVariant90 = Color(0xFFDAE5E4)

/**
 * Sıcaklık vurgusu renkleri (soğuk → sıcak). Koyu temada aynı tonların daha açık sürümleri kullanılır;
 * hangi setin geçerli olduğuna [HavaTheme] karar verir, ekranlar [temperatureColor] üzerinden okur.
 */
@Immutable
data class TemperaturePalette(
    val freezing: Color,
    val cold: Color,
    val mild: Color,
    val warm: Color,
    val hot: Color
) {
    /** Verilen sıcaklığa (°C) karşılık gelen vurgu rengi. */
    fun forCelsius(celsius: Double): Color = when {
        celsius < FREEZING_MAX -> freezing
        celsius < COLD_MAX -> cold
        celsius < MILD_MAX -> mild
        celsius < WARM_MAX -> warm
        else -> hot
    }

    companion object {
        private const val FREEZING_MAX = 0.0
        private const val COLD_MAX = 12.0
        private const val MILD_MAX = 22.0
        private const val WARM_MAX = 30.0

        val Light = TemperaturePalette(
            freezing = Color(0xFF1565C0),
            cold = Color(0xFF0288D1),
            mild = Color(0xFF2E7D32),
            warm = Color(0xFFEF6C00),
            hot = Color(0xFFC62828)
        )

        val Dark = TemperaturePalette(
            freezing = Color(0xFF64B5F6),
            cold = Color(0xFF4FC3F7),
            mild = Color(0xFF81C784),
            warm = Color(0xFFFFB74D),
            hot = Color(0xFFE57373)
        )
    }
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
