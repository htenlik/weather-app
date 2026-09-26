package com.kampplus.hava.feature.favorites.presentation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.R
import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity

@Composable
fun FavoritesRoute(onCityClick: (FavoriteCity) -> Unit, modifier: Modifier = Modifier, viewModel: FavoritesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is FavoritesEvent.ShowUndo -> {
                    val result = snackbarHostState.showSnackbar(
                        message = resources.getString(R.string.favorites_removed, event.cityName),
                        actionLabel = resources.getString(R.string.action_undo),
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) viewModel.onUndoRemove()
                }
            }
        }
    }

    FavoritesScreen(
        uiState = uiState,
        onCityClick = { id -> viewModel.findFavorite(id)?.let(onCityClick) },
        onRemoveFavorite = viewModel::onRemoveFavorite,
        onRetry = viewModel::onRetry,
        onRefresh = viewModel::onRefresh,
        isRefreshing = isRefreshing,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}
