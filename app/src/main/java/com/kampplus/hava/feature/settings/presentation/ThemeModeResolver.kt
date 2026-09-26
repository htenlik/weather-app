package com.kampplus.hava.feature.settings.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.kampplus.hava.feature.settings.domain.model.ThemeMode

/** Tema tercihini o anki koyu/açık kararına çevirir; [ThemeMode.System] cihaz ayarını izler. */
@Composable
@ReadOnlyComposable
fun ThemeMode.shouldUseDarkTheme(): Boolean = when (this) {
    ThemeMode.System -> isSystemInDarkTheme()
    ThemeMode.Light -> false
    ThemeMode.Dark -> true
}
