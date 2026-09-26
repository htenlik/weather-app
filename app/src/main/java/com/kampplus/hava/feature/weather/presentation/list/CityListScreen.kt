package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.component.EmptyView
import com.kampplus.hava.core.ui.component.ErrorView
import com.kampplus.hava.core.ui.component.ShimmerList
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.list.component.CitySearchField
import com.kampplus.hava.feature.weather.presentation.list.component.CityWeatherCard
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    uiState: CityListUiState,
    onQueryChange: (String) -> Unit,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    locationCard: @Composable () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.list_title)) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CitySearchField(
                query = uiState.query,
                onQueryChange = onQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                ListContent(
                    uiState = uiState,
                    onCityClick = onCityClick,
                    onFavoriteClick = onFavoriteClick,
                    onRetry = onRetry,
                    // Arama sırasında konum kartı çekilir; sonuçlar için yer açılır.
                    header = if (uiState.isSearching) null else locationCard
                )
            }
        }
    }
}

/**
 * Liste durumları. [header] (konum kartı) listeyle birlikte kayar; yükleme/hata durumlarında
 * içeriğin üstünde durur. Böylece yatay ekranda da liste için yer kalır.
 */
@Composable
private fun ListContent(
    uiState: CityListUiState,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null
) {
    // Durumlar arası geçiş yumuşak: shimmer → liste, liste → boş sonuç vb. birbirinin içine erir.
    AnimatedContent(
        targetState = uiState.content,
        contentKey = { it::class },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "listState",
        modifier = modifier.fillMaxSize()
    ) { content ->
        if (content is UiState.Success) {
            CityList(
                items = content.data,
                updatedAtText = uiState.updatedAtText,
                onCityClick = onCityClick,
                onFavoriteClick = onFavoriteClick,
                header = header
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                if (header != null) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) { header() }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    when (content) {
                        UiState.Loading -> ShimmerList()
                        UiState.Empty -> EmptyView(
                            icon = Icons.Filled.Search,
                            title = stringResource(R.string.list_empty_title),
                            message = if (uiState.isSearching) {
                                stringResource(R.string.search_empty_message, uiState.query.trim())
                            } else {
                                stringResource(R.string.list_empty_message)
                            }
                        )

                        is UiState.Error -> ErrorView(message = content.message.asString(), onRetry = onRetry)

                        is UiState.Success -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun CityList(
    items: List<CityWeatherUiModel>,
    updatedAtText: String?,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null
) {
    // Geniş ekranda (tablet, yatay) kartlar iki sütuna dizilir; telefonda tek sütun.
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = CARD_MIN_WIDTH),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (header != null) {
            item(key = "header", span = { GridItemSpan(maxLineSpan) }) {
                Box(modifier = Modifier.animateItem()) { header() }
            }
        }
        items(items = items, key = { it.cityId }) { item ->
            CityWeatherCard(
                item = item,
                onClick = { onCityClick(item.cityId) },
                onFavoriteClick = { onFavoriteClick(item.cityId) },
                modifier = Modifier.animateItem()
            )
        }
        if (updatedAtText != null) {
            item(key = "updatedAt", span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.updated_at, updatedAtText),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }
        }
    }
}

private val CARD_MIN_WIDTH = 320.dp

@Preview(showBackground = true)
@Composable
private fun CityListScreenPreview() {
    HavaTheme {
        CityListScreen(
            uiState = CityListUiState(
                content = UiState.Success(
                    List(5) { index ->
                        CityWeatherUiModel(
                            cityId = index.toLong(),
                            title = "İstanbul",
                            subtitle = "İstanbul, Türkiye",
                            temperatureText = "2$index°",
                            temperatureC = 20.0 + index,
                            conditionEmoji = "⛅",
                            conditionLabel = UiText.Dynamic("Parçalı bulutlu")
                        )
                    }
                )
            ),
            onQueryChange = {},
            onCityClick = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {}
        )
    }
}
