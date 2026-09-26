package com.kampplus.hava.feature.favorites.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.component.EmptyView
import com.kampplus.hava.core.ui.component.ErrorView
import com.kampplus.hava.core.ui.component.LoadingView
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.weather.presentation.list.component.CityWeatherCard
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel

/** Favori şehirler, anlık havalarıyla. Liste ekranındaki kartın aynısı kullanılır; kalp burada "çıkar" anlamına gelir. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    uiState: UiState<List<CityWeatherUiModel>>,
    onCityClick: (Long) -> Unit,
    onRemoveFavorite: (Long) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text(stringResource(R.string.favorites_title)) }) }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState,
                contentKey = { it::class },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "favoritesState"
            ) { state ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (state) {
                        UiState.Loading -> LoadingView()
                        UiState.Empty -> EmptyView(
                            icon = Icons.Filled.FavoriteBorder,
                            title = stringResource(R.string.favorites_empty_title),
                            message = stringResource(R.string.favorites_empty_message)
                        )

                        is UiState.Error -> ErrorView(message = state.message.asString(), onRetry = onRetry)

                        is UiState.Success -> LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = CARD_MIN_WIDTH),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items = state.data, key = { it.cityId }) { item ->
                                CityWeatherCard(
                                    item = item,
                                    onClick = { onCityClick(item.cityId) },
                                    onFavoriteClick = { onRemoveFavorite(item.cityId) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private val CARD_MIN_WIDTH = 320.dp
