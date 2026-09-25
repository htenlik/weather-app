package com.kampplus.hava.feature.weather.presentation.selection

/**
 * Kullanıcı etkileşimiyle değişen ortak ekran durumu: hangi şehirlerin seçildiği (favori)
 * ve bir görünüm tercihi (yalnızca favorileri göster). Tüm ekranlar bu tek kaynaktan beslenir.
 */
data class SelectionUiState(
    val favoriteCityIds: Set<Long> = emptySet(),
    val showOnlyFavorites: Boolean = false
) {
    /** Türetilmiş bilgi: ayrı bir state olarak tutulmaz, mevcut state'ten hesaplanır. */
    val favoriteCount: Int get() = favoriteCityIds.size

    fun isFavorite(cityId: Long): Boolean = cityId in favoriteCityIds
}
