package com.kampplus.hava.feature.location.presentation

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.feature.weather.domain.model.City

/**
 * Konum kartını ViewModel'e ve platform izin akışına bağlar. İzin diyaloğu, ayar ekranları ve
 * "bir daha sorma" tespiti burada kalır; ViewModel yalnızca sonuçları öğrenir.
 */
@Composable
fun LocationCardRoute(onCityClick: (City) -> Unit, modifier: Modifier = Modifier, viewModel: LocationWeatherViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        val granted = grants.values.any { it }
        // Reddedildi ve gerekçe gösterilemiyorsa kullanıcı "bir daha sorma" demiştir.
        val canAskAgain = context.findActivity()?.let { activity ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        } ?: true
        viewModel.onPermissionResult(granted = granted, canAskAgain = canAskAgain)
    }
    LifecycleResumeEffect(Unit) {
        viewModel.onPermissionChanged(granted = context.hasLocationPermission())
        onPauseOrDispose {}
    }
    LocationCard(
        state = uiState,
        onUseLocation = { permissionLauncher.launch(LOCATION_PERMISSIONS) },
        onOpenAppSettings = { context.openSettings(appDetailsIntent(context)) },
        onOpenLocationSettings = { context.openSettings(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) },
        onRetry = viewModel::onRetry,
        onClick = { viewModel.city?.let(onCityClick) },
        modifier = modifier
    )
}

private val LOCATION_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

private fun Context.hasLocationPermission(): Boolean = LOCATION_PERMISSIONS.any { permission ->
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

private fun appDetailsIntent(context: Context) =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))

private fun Context.findActivity(): Activity? =
    generateSequence(this) { (it as? ContextWrapper)?.baseContext }.firstOrNull { it is Activity } as? Activity

private fun Context.openSettings(intent: Intent) {
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // Ayar ekranı olmayan bir cihazda yapılacak bir şey yok; kart aynı durumda kalır.
    }
}
