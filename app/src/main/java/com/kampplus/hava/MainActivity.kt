package com.kampplus.hava

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.settings.domain.model.UserSettings
import com.kampplus.hava.feature.settings.presentation.shouldUseDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var settings: UserSettings? by mutableStateOf(null)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings = it }
            }
        }
        // Tema tercihi okunana kadar açılış ekranı kalır; böylece varsayılan temadan tercihe geçiş görünmez.
        splashScreen.setKeepOnScreenCondition { settings == null }
        enableEdgeToEdge()

        setContent {
            val current = settings
            if (current != null) {
                val darkTheme = current.themeMode.shouldUseDarkTheme()
                SystemBarsFollowTheme(darkTheme)
                val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
                HavaTheme(darkTheme = darkTheme, dynamicColor = current.dynamicColor) {
                    HavaApp(isOnline = isOnline)
                }
            }
        }
    }

    /** Sistem çubuğu ikonları cihaz temasına değil uygulama temasına göre açık/koyu olur. */
    @Composable
    private fun SystemBarsFollowTheme(darkTheme: Boolean) {
        DisposableEffect(darkTheme) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme }
            )
            onDispose {}
        }
    }
}
