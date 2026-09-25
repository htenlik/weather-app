package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.list.component.CityWeatherCard
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(uiState: UiState<List<CityWeatherUiModel>>, onCityClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.list_title)) }) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                UiState.Loading -> CircularProgressIndicator()
                UiState.Empty -> Text(stringResource(R.string.empty_generic))
                is UiState.Error -> Text(uiState.message.asString())
                is UiState.Success -> CityList(items = uiState.data, onCityClick = onCityClick)
            }
        }
    }
}

@Composable
private fun CityList(items: List<CityWeatherUiModel>, onCityClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = items, key = { it.cityId }) { item ->
            CityWeatherCard(item = item, onClick = { onCityClick(item.cityId) })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CityListScreenPreview() {
    HavaTheme {
        CityListScreen(
            uiState = UiState.Success(
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
            ),
            onCityClick = {}
        )
    }
}
