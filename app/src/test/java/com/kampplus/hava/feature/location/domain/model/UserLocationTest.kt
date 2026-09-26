package com.kampplus.hava.feature.location.domain.model

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.testing.userLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserLocationTest {

    @Test
    fun `becomes a device-location city with the resolved place name`() {
        val city = userLocation().toCity()

        assertTrue(city.isDeviceLocation)
        assertEquals(City.DEVICE_LOCATION_ID, city.id)
        assertEquals("Çankaya", city.name)
        assertEquals("Ankara", city.region)
        assertEquals("Türkiye", city.country)
        assertEquals(39.93, city.coordinates.latitude, 0.0)
    }

    @Test
    fun `falls back to coordinates when no place name is known`() {
        val city = userLocation(placeName = null, region = null, country = null).toCity()

        assertEquals("39.93°, 32.86°", city.name)
    }
}
