package com.kampplus.hava.feature.settings.presentation

import com.kampplus.hava.feature.settings.domain.model.UserSettings

/** Ayarlar ekranı durumu. [isLoaded] false iken kayıtlı değerler henüz okunmamıştır; kontroller varsayılanı gösterir. */
data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val isLoaded: Boolean = false
)
