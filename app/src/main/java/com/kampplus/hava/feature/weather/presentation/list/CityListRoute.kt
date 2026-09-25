package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** ViewModel'i ekrana bağlayan katman. Ekranın kendisi ([CityListScreen]) stateless'tır. */
@Composable
fun CityListRoute(onCityClick: (Long) -> Unit, modifier: Modifier = Modifier, viewModel: CityListViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CityListScreen(uiState = uiState, onCityClick = onCityClick, modifier = modifier)
}
