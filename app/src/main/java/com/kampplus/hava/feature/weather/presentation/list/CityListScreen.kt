package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.list.component.CityWeatherCard
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.selection.SelectionUiState

/**
 * Liste ekranı stateless'tır: yüklenen listeyi ([uiState], seçim uygulanmış hâli) ve ortak seçim
 * durumunu ([selection]) parametre olarak alır; kullanıcı aksiyonlarını callback ile bildirir.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    uiState: UiState<List<CityWeatherUiModel>>,
    selection: SelectionUiState,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onShowOnlyFavoritesChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
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
            FilterChip(
                selected = selection.showOnlyFavorites,
                onClick = { onShowOnlyFavoritesChange(!selection.showOnlyFavorites) },
                label = { Text(stringResource(R.string.filter_only_favorites, selection.favoriteCount)) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Favorite, contentDescription = null) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when (uiState) {
                    UiState.Loading -> CircularProgressIndicator()

                    UiState.Empty -> Text(
                        text = stringResource(
                            if (selection.showOnlyFavorites) R.string.favorites_empty else R.string.empty_generic
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )

                    is UiState.Error -> Text(uiState.message.asString())

                    is UiState.Success -> CityList(items = uiState.data, onCityClick = onCityClick, onFavoriteClick = onFavoriteClick)
                }
            }
        }
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
        isFavorite = index % 2 == 0
    )
}

@Preview(showBackground = true)
@Composable
private fun CityListScreenPreview() {
    HavaTheme {
        CityListScreen(
            uiState = UiState.Success(previewItems),
            selection = SelectionUiState(favoriteCityIds = setOf(0L, 2L, 4L)),
            onCityClick = {},
            onFavoriteClick = {},
            onShowOnlyFavoritesChange = {}
        )
    }
}

@Preview(name = "Favori filtresi boş", showBackground = true)
@Composable
private fun CityListScreenNoFavoritesPreview() {
    HavaTheme {
        CityListScreen(
            uiState = UiState.Empty,
            selection = SelectionUiState(showOnlyFavorites = true),
            onCityClick = {},
            onFavoriteClick = {},
            onShowOnlyFavoritesChange = {}
        )
    }
}
