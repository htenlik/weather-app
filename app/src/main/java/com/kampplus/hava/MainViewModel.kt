package com.kampplus.hava

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.common.network.NetworkMonitor
import com.kampplus.hava.feature.settings.domain.model.UserSettings
import com.kampplus.hava.feature.settings.domain.usecase.ObserveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Uygulama kabuğunun ihtiyaçları: tema tercihleri ve bağlantı durumu; ekranlar kendi ViewModel'lerini kullanır. */
@HiltViewModel
class MainViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    /** null: ayarlar henüz diskten okunmadı. Açılış ekranı bu sürede tutulur, tema yanıp sönmez. */
    val settings: StateFlow<UserSettings?> = observeSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = null
    )

    /** Çevrimdışı şeridi için; ilk değer "çevrimiçi"dir, şerit gereksiz yere yanıp sönmez. */
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = true
    )

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
