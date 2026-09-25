package com.kampplus.hava.feature.weather.presentation.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** ViewModel'i detay ekranına bağlayan katman. Ekranın kendisi ([CityDetailScreen]) stateless'tır. */
@Composable
fun CityDetailRoute(onBack: () -> Unit, modifier: Modifier = Modifier, viewModel: CityDetailViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CityDetailScreen(uiState = uiState, onBack = onBack, modifier = modifier)
}
