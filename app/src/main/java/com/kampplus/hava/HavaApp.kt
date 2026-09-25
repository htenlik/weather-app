package com.kampplus.hava

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kampplus.hava.core.navigation.HavaNavHost
import com.kampplus.hava.feature.weather.presentation.selection.SelectionViewModel

/**
 * Uygulamanın Compose kökü. Ortak sahiplik burada kurulur: [SelectionViewModel] NavHost'un **üstünde**,
 * Activity kapsamında alınır ve grafiğe verilir; böylece liste ve detay ekranı aynı örneği paylaşır.
 * Ekranların kendi ViewModel'leri ise (yükleme durumu) hedef kapsamında kalmaya devam eder.
 */
@Composable
fun HavaApp(modifier: Modifier = Modifier) {
    val selectionViewModel: SelectionViewModel = hiltViewModel()
    HavaNavHost(selectionViewModel = selectionViewModel, modifier = modifier)
}
