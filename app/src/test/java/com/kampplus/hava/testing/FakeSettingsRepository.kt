package com.kampplus.hava.testing

import com.kampplus.hava.feature.settings.domain.model.UserSettings
import com.kampplus.hava.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/** Bellekteki ayarlar; testler [state] üzerinden başlangıç değerini verir ya da değişikliği doğrular. */
class FakeSettingsRepository(initial: UserSettings = UserSettings()) : SettingsRepository {

    val state = MutableStateFlow(initial)

    override fun observeSettings(): Flow<UserSettings> = state

    override suspend fun update(transform: (UserSettings) -> UserSettings) = state.update(transform)
}
