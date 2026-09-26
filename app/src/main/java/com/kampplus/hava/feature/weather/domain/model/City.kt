package com.kampplus.hava.feature.weather.domain.model

/**
 * Bir konum. [id] geocoding servisinin kimliğidir; favoriler bu kimlikle eşleşir.
 * Cihazın kendi konumu [DEVICE_LOCATION_ID] ile temsil edilir ve favorilere eklenmez.
 */
data class City(
    val id: Long,
    val name: String,
    val region: String?,
    val country: String?,
    val coordinates: Coordinates
) {
    val isDeviceLocation: Boolean get() = id == DEVICE_LOCATION_ID

    companion object {
        const val DEVICE_LOCATION_ID = -1L
    }
}
