package com.kampplus.hava.feature.settings.domain.repository

import com.kampplus.hava.feature.settings.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    /** Her değişiklikte güncel ayarları yayınlar; ilk değer kayıtlı ayarlar ya da varsayılanlardır. */
    fun observeSettings(): Flow<UserSettings>

    /** Ayarları atomik olarak günceller: [transform] mevcut ayarları alır, yenisini döner. */
    suspend fun update(transform: (UserSettings) -> UserSettings)
}
