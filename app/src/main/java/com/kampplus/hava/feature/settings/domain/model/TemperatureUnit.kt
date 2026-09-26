package com.kampplus.hava.feature.settings.domain.model

/** Sıcaklık gösterim birimi. API her zaman Celsius verir; dönüşüm gösterim anında yapılır. */
enum class TemperatureUnit {
    Celsius,
    Fahrenheit;

    /** Celsius cinsinden değeri bu birime çevirir. */
    fun fromCelsius(celsius: Double): Double = when (this) {
        Celsius -> celsius
        Fahrenheit -> celsius * FAHRENHEIT_SCALE + FAHRENHEIT_OFFSET
    }

    private companion object {
        const val FAHRENHEIT_SCALE = 9.0 / 5.0
        const val FAHRENHEIT_OFFSET = 32.0
    }
}
