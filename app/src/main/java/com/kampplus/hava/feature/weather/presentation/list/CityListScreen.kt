package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
            // Arama sırasında konum kartı çekilir; sonuçlar için yer açılır.
            AnimatedVisibility(visible = !uiState.isSearching) {
                Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)) { locationCard() }
            }
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                ListContent(
                    uiState = uiState,
                    onCityClick = onCityClick,
                    onFavoriteClick = onFavoriteClick,
                    onRetry = onRetry
                )
            }
        }
    }
}

@Composable
private fun ListContent(
    uiState: CityListUiState,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Durumlar arası geçiş yumuşak: shimmer → liste, liste → boş sonuç vb. birbirinin içine erir.
    AnimatedContent(
        targetState = uiState.content,
        contentKey = { it::class },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "listState",
        modifier = modifier.fillMaxSize()
    ) { content ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

                is UiState.Success -> CityList(
                    items = content.data,
                    updatedAtText = uiState.updatedAtText,
                    onCityClick = onCityClick,
                    onFavoriteClick = onFavoriteClick
                )
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
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = items, key = { it.cityId }) { item ->
            CityWeatherCard(
                item = item,
                onClick = { onCityClick(item.cityId) },
                onFavoriteClick = { onFavoriteClick(item.cityId) },
                modifier = Modifier.animateItem()
            )
        }
        if (updatedAtText != null) {
            item(key = "updatedAt") {
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
