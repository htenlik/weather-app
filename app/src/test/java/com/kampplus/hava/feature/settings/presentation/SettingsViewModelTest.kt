package com.kampplus.hava.feature.settings.presentation

import app.cash.turbine.test
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.settings.domain.model.ThemeMode
import com.kampplus.hava.feature.settings.domain.model.UserSettings
import com.kampplus.hava.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kampplus.hava.feature.settings.domain.usecase.UpdateSettingsUseCase
import com.kampplus.hava.testing.FakeSettingsRepository
import com.kampplus.hava.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSettingsRepository(UserSettings(themeMode = ThemeMode.Dark))

    private fun createViewModel() = SettingsViewModel(
        observeSettings = ObserveSettingsUseCase(repository),
        updateSettings = UpdateSettingsUseCase(repository)
    )

    @Test
    fun `starts with defaults until stored settings arrive`() = runTest {
        createViewModel().uiState.test {
            val initial = awaitItem()
            assertFalse(initial.isLoaded)
            assertEquals(UserSettings(), initial.settings)

            val loaded = awaitItem()
            assertTrue(loaded.isLoaded)
            assertEquals(ThemeMode.Dark, loaded.settings.themeMode)
        }
    }

    @Test
    fun `changing one setting keeps the others`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(2)

            viewModel.onTemperatureUnitChange(TemperatureUnit.Fahrenheit)
            assertEquals(UserSettings(temperatureUnit = TemperatureUnit.Fahrenheit, themeMode = ThemeMode.Dark), awaitItem().settings)

            viewModel.onDynamicColorChange(true)
            val settings = awaitItem().settings
            assertTrue(settings.dynamicColor)
            assertEquals(TemperatureUnit.Fahrenheit, settings.temperatureUnit)
            assertEquals(ThemeMode.Dark, settings.themeMode)

            viewModel.onThemeModeChange(ThemeMode.Light)
            assertEquals(ThemeMode.Light, awaitItem().settings.themeMode)
        }
    }
}
