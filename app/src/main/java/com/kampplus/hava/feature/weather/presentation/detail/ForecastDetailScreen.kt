package com.kampplus.hava.feature.weather.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.kampplus.hava.core.ui.component.ErrorView
import com.kampplus.hava.core.ui.component.FavoriteToggleButton
import com.kampplus.hava.core.ui.component.LoadingView
import com.kampplus.hava.core.ui.component.TemperatureBadge
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.detail.component.DailyForecastItem
import com.kampplus.hava.feature.weather.presentation.detail.component.HourlyForecastRow
import com.kampplus.hava.feature.weather.presentation.detail.component.ShareButton
import com.kampplus.hava.feature.weather.presentation.model.DailyUiModel
import com.kampplus.hava.feature.weather.presentation.model.ForecastUiModel
import com.kampplus.hava.feature.weather.presentation.model.HourlyUiModel
import com.kampplus.hava.feature.weather.presentation.model.temperatureColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastDetailScreen(
    uiState: UiState<ForecastUiModel>,
    onBack: () -> Unit,
    onShare: (ForecastUiModel) -> Unit,
    onFavoriteClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text((uiState as? UiState.Success)?.data?.cityName ?: stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    if (uiState is UiState.Success) {
                        FavoriteToggleButton(isFavorite = uiState.data.isFavorite, onClick = onFavoriteClick)
                        ShareButton(onClick = { onShare(uiState.data) })
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                UiState.Loading -> LoadingView()
                UiState.Empty -> ErrorView(message = stringResource(R.string.error_not_found))
                is UiState.Error -> ErrorView(message = uiState.message.asString(), onRetry = onRetry)
                is UiState.Success -> ForecastContent(forecast = uiState.data)
            }
        }
    }
}

@Composable
private fun ForecastContent(forecast: ForecastUiModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CurrentWeatherHeader(forecast = forecast, modifier = Modifier.padding(horizontal = 16.dp))
        SectionTitle(text = stringResource(R.string.detail_hourly))
        HourlyForecastRow(items = forecast.hourly)
        SectionTitle(text = stringResource(R.string.detail_daily))
        Card(modifier = Modifier.padding(horizontal = 16.dp)) {
            forecast.daily.forEachIndexed { index, day ->
                if (index > 0) HorizontalDivider()
                DailyForecastItem(day = day)
            }
        }
    }
}

@Composable
private fun CurrentWeatherHeader(forecast: ForecastUiModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TemperatureBadge(text = forecast.temperatureText, containerColor = temperatureColor(forecast.temperatureC), size = 88.dp)
            Column {
                Text(
                    text = forecast.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${forecast.conditionEmoji} ${forecast.conditionLabel.asString()}",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                forecast.feelsLikeText?.let { Metric(label = stringResource(R.string.detail_feels_like), value = it) }
                forecast.humidityText?.let { Metric(label = stringResource(R.string.detail_humidity), value = it) }
                forecast.windText?.let { Metric(label = stringResource(R.string.detail_wind), value = it) }
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp))
}

@Preview(showBackground = true)
@Composable
private fun ForecastDetailScreenPreview() {
    HavaTheme {
        ForecastDetailScreen(
            uiState = UiState.Success(
                ForecastUiModel(
                    cityId = 1,
                    cityName = "Ankara",
                    subtitle = "Ankara, Türkiye",
                    temperatureText = "21°",
                    temperatureC = 21.0,
                    conditionEmoji = "☀️",
                    conditionLabel = UiText.Dynamic("Açık"),
                    feelsLikeText = "20°",
                    humidityText = "%45",
                    windText = "12 km/sa",
                    hourly = List(8) { HourlyUiModel("1$it:00", "☀️", "2$it°", null) },
                    daily = List(7) { DailyUiModel(UiText.Dynamic("Cuma"), "⛅", "14°", "24°", "%10") }
                )
            ),
            onBack = {},
            onShare = {},
            onFavoriteClick = {},
            onRetry = {}
        )
    }
}
