package com.kampplus.hava.feature.settings.domain.usecase

import com.kampplus.hava.feature.settings.domain.model.UserSettings
import com.kampplus.hava.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject

/** Tek alanı değiştirmek için de kullanılır: `updateSettings { it.copy(themeMode = ThemeMode.Dark) }`. */
class UpdateSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(transform: (UserSettings) -> UserSettings) = repository.update(transform)
}
