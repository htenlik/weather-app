package com.kampplus.hava.feature.settings.domain.usecase

import com.kampplus.hava.feature.settings.domain.model.UserSettings
import com.kampplus.hava.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<UserSettings> = repository.observeSettings()
}
