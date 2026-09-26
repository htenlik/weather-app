package com.kampplus.hava.feature.settings.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.settings.domain.model.ThemeMode
import com.kampplus.hava.feature.settings.domain.model.UserSettings
import com.kampplus.hava.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Ayarları Preferences DataStore'da tutar. Enum değerleri ada göre yazılır; tanınmayan ya da eksik
 * bir değer (örneğin eski sürümden kalan kayıt) sessizce varsayılana düşer, uygulama açılmaya devam eder.
 */
@Singleton
class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override fun observeSettings(): Flow<UserSettings> = dataStore.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { preferences -> preferences.toUserSettings() }

    override suspend fun update(transform: (UserSettings) -> UserSettings) {
        dataStore.edit { preferences ->
            val updated = transform(preferences.toUserSettings())
            preferences[Keys.TEMPERATURE_UNIT] = updated.temperatureUnit.name
            preferences[Keys.THEME_MODE] = updated.themeMode.name
            preferences[Keys.DYNAMIC_COLOR] = updated.dynamicColor
        }
    }

    private fun Preferences.toUserSettings(): UserSettings {
        val defaults = UserSettings()
        return UserSettings(
            temperatureUnit = this[Keys.TEMPERATURE_UNIT].toEnumOrNull<TemperatureUnit>() ?: defaults.temperatureUnit,
            themeMode = this[Keys.THEME_MODE].toEnumOrNull<ThemeMode>() ?: defaults.themeMode,
            dynamicColor = this[Keys.DYNAMIC_COLOR] ?: defaults.dynamicColor
        )
    }

    private inline fun <reified T : Enum<T>> String?.toEnumOrNull(): T? = enumValues<T>().firstOrNull { it.name == this }

    private object Keys {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    }
}
