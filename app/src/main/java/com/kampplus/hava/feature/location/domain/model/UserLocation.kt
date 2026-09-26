package com.kampplus.hava.feature.location.domain.model

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import java.util.Locale

/** Cihazın konumu ve (çözümlenebildiyse) yer adı. Yer adı yoksa koordinatlar ad olarak gösterilir. */
data class UserLocation(
    val coordinates: Coordinates,
    val placeName: String? = null,
    val region: String? = null,
    val country: String? = null
) {
    fun toCity(): City = City(
        id = City.DEVICE_LOCATION_ID,
        name = placeName ?: coordinates.toLabel(),
        region = region,
        country = country,
        coordinates = coordinates
    )

    private fun Coordinates.toLabel(): String = String.format(Locale.ROOT, "%.2f°, %.2f°", latitude, longitude)
}
