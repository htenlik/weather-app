package com.kampplus.hava

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kampplus.hava.core.navigation.HavaNavHost

/** Uygulamanın Compose kökü: tema altında navigasyon grafiğini kurar. */
@Composable
fun HavaApp(modifier: Modifier = Modifier) {
    HavaNavHost(modifier = modifier)
}
