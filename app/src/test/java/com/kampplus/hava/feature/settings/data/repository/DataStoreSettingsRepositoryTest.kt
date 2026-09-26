package com.kampplus.hava.feature.settings.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.settings.domain.model.ThemeMode
import com.kampplus.hava.feature.settings.domain.model.UserSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Gerçek DataStore ile, geçici bir dosya üzerinde. */
@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreSettingsRepositoryTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private val dataStore = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { temporaryFolder.newFile("settings.preferences_pb") }
    )

    private val repository = DataStoreSettingsRepository(dataStore)

    @Test
    fun `returns defaults when nothing is stored`() = testScope.runTest {
        assertEquals(UserSettings(), repository.observeSettings().first())
    }

    @Test
    fun `persists updates and emits them`() = testScope.runTest {
        repository.update { it.copy(temperatureUnit = TemperatureUnit.Fahrenheit, themeMode = ThemeMode.Dark) }
        repository.update { it.copy(dynamicColor = true) }

        val stored = repository.observeSettings().first()
        assertEquals(UserSettings(temperatureUnit = TemperatureUnit.Fahrenheit, themeMode = ThemeMode.Dark, dynamicColor = true), stored)
    }

    @Test
    fun `unknown stored values fall back to defaults`() = testScope.runTest {
        dataStore.edit { preferences -> preferences[stringPreferencesKey("theme_mode")] = "Sepia" }

        assertEquals(ThemeMode.System, repository.observeSettings().first().themeMode)
    }
}
