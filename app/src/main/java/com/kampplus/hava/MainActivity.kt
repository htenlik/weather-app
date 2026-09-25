package com.kampplus.hava

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.main.MainScreen
import com.kampplus.hava.feature.weather.presentation.main.sampleContent
import dagger.hilt.android.AndroidEntryPoint

/**
 * Uygulama girişi. [setContent] Compose ağacını başlatır, [HavaTheme] Material 3 temasını
 * sağlar, [MainScreen] ise ekranın kendisidir. Activity yalnızca bu üçünü birbirine bağlar.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HavaTheme {
                MainScreen(
                    title = stringResource(R.string.main_title),
                    description = stringResource(R.string.main_description),
                    cards = sampleContent
                )
            }
        }
    }
}
