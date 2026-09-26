package com.kampplus.hava.core.navigation

import kotlinx.serialization.Serializable

/** Type-safe navigasyon hedefleri. Argümanlar derleme zamanında denetlenir. */
@Serializable
data object ListDestination

@Serializable
data object FavoritesDestination

@Serializable
data object SettingsDestination

/**
 * Tahmin ekranı. Şehrin koordinatları argüman olarak taşınır; böylece detay ekranı
 * ek bir "şehir getir" isteğine ihtiyaç duymaz.
 */
@Serializable
data class ForecastDestination(
    val cityId: Long,
    val name: String,
    val region: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        // SavedStateHandle anahtarları; property adlarıyla aynı olmalıdır.
        const val ARG_CITY_ID = "cityId"
        const val ARG_NAME = "name"
        const val ARG_REGION = "region"
        const val ARG_COUNTRY = "country"
        const val ARG_LATITUDE = "latitude"
        const val ARG_LONGITUDE = "longitude"
    }
}
