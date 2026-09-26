package com.kampplus.hava.feature.settings.presentation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.BuildConfig
import com.kampplus.hava.R

@Composable
fun SettingsRoute(modifier: Modifier = Modifier, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    SettingsScreen(
        uiState = uiState,
        onTemperatureUnitChange = viewModel::onTemperatureUnitChange,
        onThemeModeChange = viewModel::onThemeModeChange,
        onDynamicColorChange = viewModel::onDynamicColorChange,
        onOpenLink = { url -> context.openLink(url) },
        isDynamicColorSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
        appVersion = BuildConfig.VERSION_NAME,
        modifier = modifier
    )
}

/** Bağlantıyı tarayıcıda açar; tarayıcı yoksa kullanıcıya söyler. Platform bağımlılığı Route katmanında kalır. */
private fun Context.openLink(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, R.string.error_no_browser, Toast.LENGTH_SHORT).show()
    }
}
