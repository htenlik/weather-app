package com.kampplus.hava

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.feature.settings.domain.model.UserSettings
import com.kampplus.hava.feature.settings.domain.usecase.ObserveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Uygulama kabuğunun ihtiyacı olan tek şey tema tercihleri; ekranlar kendi ViewModel'lerini kullanır. */
@HiltViewModel
class MainViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase
) : ViewModel() {

    /** null: ayarlar henüz diskten okunmadı. Açılış ekranı bu sürede tutulur, tema yanıp sönmez. */
    val settings: StateFlow<UserSettings?> = observeSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = null
    )

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
