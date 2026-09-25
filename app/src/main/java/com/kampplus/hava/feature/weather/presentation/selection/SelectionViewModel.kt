package com.kampplus.hava.feature.weather.presentation.selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Ekranlar arasında paylaşılan seçim durumunun tek sahibi.
 *
 * Örnek NavHost'un üstünde, Activity kapsamında alınır ([com.kampplus.hava.HavaApp]); liste ve detay ekranı
 * aynı örneği kullandığı için seçim ekran geçişinde korunur. ViewModel yapılandırma değişikliğinde
 * (döndürme) yaşadığı için seçim orada da korunur; [SavedStateHandle] ise süreç ölümünden sonra geri yükler.
 */
@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SelectionUiState(
            favoriteCityIds = savedStateHandle.get<LongArray>(KEY_FAVORITES)?.toSet().orEmpty(),
            showOnlyFavorites = savedStateHandle[KEY_ONLY_FAVORITES] ?: false
        )
    )
    val uiState: StateFlow<SelectionUiState> = _uiState.asStateFlow()

    /** Aksiyon: favoriye ekle / favoriden çıkar. Yeni state üretir; türetilen sayaç kendiliğinden güncellenir. */
    fun toggleFavorite(cityId: Long) {
        _uiState.update { state ->
            val ids = if (state.isFavorite(cityId)) state.favoriteCityIds - cityId else state.favoriteCityIds + cityId
            state.copy(favoriteCityIds = ids)
        }
        savedStateHandle[KEY_FAVORITES] = _uiState.value.favoriteCityIds.toLongArray()
    }

    /** Aksiyon: görünüm tercihi. Liste bu bayrağa göre filtrelenir; filtrelenmiş liste ayrıca saklanmaz. */
    fun setShowOnlyFavorites(enabled: Boolean) {
        _uiState.update { it.copy(showOnlyFavorites = enabled) }
        savedStateHandle[KEY_ONLY_FAVORITES] = enabled
    }

    companion object {
        const val KEY_FAVORITES = "favoriteCityIds"
        const val KEY_ONLY_FAVORITES = "showOnlyFavorites"
    }
}
