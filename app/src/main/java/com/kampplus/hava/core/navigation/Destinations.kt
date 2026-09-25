package com.kampplus.hava.core.navigation

import kotlinx.serialization.Serializable

/** Type-safe navigasyon hedefleri; argümanlar derleme zamanında denetlenir. */
@Serializable
data object ListDestination

/**
 * Detay ekranı. Geçiş için yalnızca şehrin kimliği taşınır; ekran veriyi kendi ViewModel'iyle yükler.
 * Kimlik listede yoksa ya da geçersizse ekran açıklayıcı bir görünüm gösterir.
 */
@Serializable
data class CityDetailDestination(
    val cityId: Long
) {
    companion object {
        /** SavedStateHandle anahtarı; property adıyla aynı olmalıdır. */
        const val ARG_CITY_ID = "cityId"
    }
}
