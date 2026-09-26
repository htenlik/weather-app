package com.kampplus.hava.feature.settings.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.settings.domain.model.ThemeMode
import com.kampplus.hava.feature.settings.domain.model.UserSettings

/** Ayarlar ekranındaki dış bağlantılar. */
object SettingsLinks {
    const val DATA_SOURCE = "https://open-meteo.com/"
    const val SOURCE_CODE = "https://github.com/htenlik/weather-app"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onTemperatureUnitChange: (TemperatureUnit) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onOpenLink: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDynamicColorSupported: Boolean = true,
    appVersion: String = ""
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }) }
    ) { innerPadding ->
        // Geniş ekranda ayarlar kenardan kenara uzamaz; okunabilir bir sütunda ortalanır.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = CONTENT_MAX_WIDTH)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SettingsSection(titleRes = R.string.settings_section_appearance) {
                    ChoiceRow(
                        titleRes = R.string.settings_theme,
                        options = ThemeMode.entries,
                        selected = uiState.settings.themeMode,
                        optionLabelRes = ThemeMode::labelRes,
                        onSelect = onThemeModeChange
                    )
                    HorizontalDivider()
                    SwitchRow(
                        titleRes = R.string.settings_dynamic_color,
                        descriptionRes = if (isDynamicColorSupported) {
                            R.string.settings_dynamic_color_description
                        } else {
                            R.string.settings_dynamic_color_unavailable
                        },
                        checked = uiState.settings.dynamicColor && isDynamicColorSupported,
                        enabled = isDynamicColorSupported,
                        onCheckedChange = onDynamicColorChange
                    )
                }
                SettingsSection(titleRes = R.string.settings_section_units) {
                    ChoiceRow(
                        titleRes = R.string.settings_temperature_unit,
                        options = TemperatureUnit.entries,
                        selected = uiState.settings.temperatureUnit,
                        optionLabelRes = TemperatureUnit::labelRes,
                        onSelect = onTemperatureUnitChange
                    )
                }
                SettingsSection(titleRes = R.string.settings_section_about) {
                    InfoRow(icon = Icons.Filled.Info, titleRes = R.string.settings_version, description = appVersion)
                    HorizontalDivider()
                    LinkRow(
                        icon = Icons.Filled.Cloud,
                        titleRes = R.string.settings_data_source,
                        descriptionRes = R.string.settings_data_source_description,
                        onClick = { onOpenLink(SettingsLinks.DATA_SOURCE) }
                    )
                    HorizontalDivider()
                    LinkRow(
                        icon = Icons.Filled.Code,
                        titleRes = R.string.settings_source_code,
                        descriptionRes = R.string.settings_source_code_description,
                        onClick = { onOpenLink(SettingsLinks.SOURCE_CODE) }
                    )
                }
                Text(
                    text = stringResource(R.string.settings_made_with),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(@StringRes titleRes: Int, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 4.dp)
                .semantics { heading() }
        )
        Card(modifier = Modifier.fillMaxWidth()) { content() }
    }
}

/** Birkaç seçenekten birinin seçildiği satır; seçenekler segmentli düğme olarak yan yana durur. */
@Composable
private fun <T> ChoiceRow(@StringRes titleRes: Int, options: List<T>, selected: T, optionLabelRes: (T) -> Int, onSelect: (T) -> Unit) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = stringResource(titleRes), style = MaterialTheme.typography.bodyLarge)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    label = { Text(stringResource(optionLabelRes(option))) }
                )
            }
        }
    }
}

@Composable
private fun SwitchRow(
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(stringResource(titleRes)) },
        supportingContent = { Text(stringResource(descriptionRes)) },
        trailingContent = { Switch(checked = checked, onCheckedChange = null, enabled = enabled) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
    )
}

@Composable
private fun InfoRow(icon: ImageVector, @StringRes titleRes: Int, description: String) {
    ListItem(
        headlineContent = { Text(stringResource(titleRes)) },
        supportingContent = { Text(description) },
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun LinkRow(icon: ImageVector, @StringRes titleRes: Int, @StringRes descriptionRes: Int, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(stringResource(titleRes)) },
        supportingContent = { Text(stringResource(descriptionRes)) },
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@StringRes
private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.System -> R.string.settings_theme_system
    ThemeMode.Light -> R.string.settings_theme_light
    ThemeMode.Dark -> R.string.settings_theme_dark
}

@StringRes
private fun TemperatureUnit.labelRes(): Int = when (this) {
    TemperatureUnit.Celsius -> R.string.settings_unit_celsius
    TemperatureUnit.Fahrenheit -> R.string.settings_unit_fahrenheit
}

private val CONTENT_MAX_WIDTH = 640.dp

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    HavaTheme {
        SettingsScreen(
            uiState = SettingsUiState(settings = UserSettings(themeMode = ThemeMode.Dark), isLoaded = true),
            onTemperatureUnitChange = {},
            onThemeModeChange = {},
            onDynamicColorChange = {},
            onOpenLink = {},
            appVersion = "1.1.0"
        )
    }
}
