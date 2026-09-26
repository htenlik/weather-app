package com.kampplus.hava.feature.location.data.geocoder

import com.kampplus.hava.feature.weather.domain.model.Coordinates

/** Koordinatı insan okur bir yer adına çevirir; çözümlenemezse null (kart koordinatla yetinir). */
fun interface PlaceNameResolver {
    suspend fun resolve(coordinates: Coordinates): PlaceName?
}

data class PlaceName(
    val name: String,
    val region: String?,
    val country: String?
)
