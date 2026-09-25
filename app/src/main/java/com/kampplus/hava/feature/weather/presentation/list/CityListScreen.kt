package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.common.demo.DemoScenario
import com.kampplus.hava.core.ui.component.EmptyView
import com.kampplus.hava.core.ui.component.ErrorView
import com.kampplus.hava.core.ui.component.LoadingView
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.list.component.CityWeatherCard
import com.kampplus.hava.feature.weather.presentation.list.component.DemoScenarioMenu
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel

/**
 * Dört arayüz durumu tek `when` ile çizilir: yükleniyor / içerik / boş / hata.
 * Yenileme sırasında içerik ekranda kalır, yalnızca çekme göstergesi döner ([isRefreshing]).
 * [demoScenario] null ise senaryo menüsü gösterilmez (release build).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    uiState: UiState<List<CityWeatherUiModel>>,
    isRefreshing: Boolean,
    demoScenario: DemoScenario?,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onScenarioSelected: (DemoScenario) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.list_title)) },
                actions = {
                    demoScenario?.let { DemoScenarioMenu(selected = it, onSelect = onScenarioSelected) }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                UiState.Loading -> StateContainer { LoadingView() }

                UiState.Empty -> StateContainer {
                    EmptyView(
                        icon = Icons.Filled.Search,
                        title = stringResource(R.string.list_empty_title),
                        message = stringResource(R.string.list_empty_message)
                    )
                }

                is UiState.Error -> StateContainer { ErrorView(message = uiState.message.asString(), onRetry = onRetry) }

                is UiState.Success -> CityList(items = uiState.data, onCityClick = onCityClick, onFavoriteClick = onFavoriteClick)
            }
        }
    }
}

/**
 * Liste dışı durumları ortalar ve kaydırılabilir yapar; kaydırılabilir olmayan içerikte çekerek
 * yenileme hareketi çalışmaz, boş/hata durumunda da aşağı çekerek yenilemek mümkün olsun.
 */
@Composable
private fun StateContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        content()
    }
}

@Composable
private fun CityList(
    items: List<CityWeatherUiModel>,
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
                onFavoriteClick = { onFavoriteClick(item.cityId) }
            )
        }
    }
}

private val previewItems = List(5) { index ->
    CityWeatherUiModel(
        cityId = index.toLong(),
        title = "İstanbul",
        subtitle = "İstanbul, Türkiye",
        temperatureText = "2$index°",
        temperatureC = 20.0 + index,
        conditionEmoji = "⛅",
        conditionLabel = UiText.Dynamic("Parçalı bulutlu"),
        isFavorite = index == 0
    )
}

@Composable
private fun PreviewScreen(uiState: UiState<List<CityWeatherUiModel>>, isRefreshing: Boolean = false) {
    HavaTheme {
        CityListScreen(
            uiState = uiState,
            isRefreshing = isRefreshing,
            demoScenario = DemoScenario.NORMAL,
            onCityClick = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {},
            onScenarioSelected = {}
        )
    }
}

@Preview(name = "İçerik", showBackground = true)
@Composable
private fun CityListScreenContentPreview() = PreviewScreen(uiState = UiState.Success(previewItems))

@Preview(name = "Yükleniyor", showBackground = true)
@Composable
private fun CityListScreenLoadingPreview() = PreviewScreen(uiState = UiState.Loading)

@Preview(name = "Boş", showBackground = true)
@Composable
private fun CityListScreenEmptyPreview() = PreviewScreen(uiState = UiState.Empty)

@Preview(name = "Hata", showBackground = true)
@Composable
private fun CityListScreenErrorPreview() =
    PreviewScreen(uiState = UiState.Error(UiText.Dynamic("İnternet bağlantısı yok. Bağlantını kontrol edip tekrar dene.")))
