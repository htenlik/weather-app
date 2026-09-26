package com.kampplus.hava.feature.settings.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TemperatureUnitTest {

    @Test
    fun `celsius passes the value through`() {
        assertEquals(21.4, TemperatureUnit.Celsius.fromCelsius(21.4), 0.0)
    }

    @Test
    fun `fahrenheit converts freezing and boiling points`() {
        assertEquals(32.0, TemperatureUnit.Fahrenheit.fromCelsius(0.0), 0.0)
        assertEquals(212.0, TemperatureUnit.Fahrenheit.fromCelsius(100.0), 0.0)
        assertEquals(-40.0, TemperatureUnit.Fahrenheit.fromCelsius(-40.0), 0.0)
    }
}
