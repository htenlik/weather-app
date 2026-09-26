package com.kampplus.hava.feature.settings.domain.model

/** Kullanıcının kalıcı tercihleri. Varsayılanlar ilk açılışta ve okunamayan kayıtlarda geçerlidir. */
data class UserSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.Celsius,
    val themeMode: ThemeMode = ThemeMode.System,
    val dynamicColor: Boolean = false
)
