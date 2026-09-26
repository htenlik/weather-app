package com.kampplus.hava.feature.weather.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.component.ErrorView
import com.kampplus.hava.core.ui.component.FavoriteToggleButton
import com.kampplus.hava.core.ui.component.LightStatusBarIcons
import com.kampplus.hava.core.ui.component.LoadingView
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.core.ui.theme.WeatherPalette
import com.kampplus.hava.core.ui.theme.heroColors
import com.kampplus.hava.feature.weather.presentation.detail.component.DailyForecastItem
import com.kampplus.hava.feature.weather.presentation.detail.component.HeroHeader
import com.kampplus.hava.feature.weather.presentation.detail.component.HourlyForecastRow
import com.kampplus.hava.feature.weather.presentation.detail.component.ShareButton
import com.kampplus.hava.feature.weather.presentation.model.DailyUiModel
import com.kampplus.hava.feature.weather.presentation.model.ForecastUiModel
import com.kampplus.hava.feature.weather.presentation.model.HourlyUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastDetailScreen(
    uiState: UiState<ForecastUiModel>,
    onBack: () -> Unit,
    onShare: (ForecastUiModel) -> Unit,
    onFavoriteClick: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false
) {
    val forecast = (uiState as? UiState.Success)?.data
    // Üst çubuk, başlık gradient'inin ilk rengini alır; içerik yokken varsayılan renklerde kalır.
    val heroStart = forecast?.let { heroColors(tint = it.iconTint, isDay = it.isDay).first() }
    LightStatusBarIcons(enabled = heroStart != null)
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(forecast?.cityName ?: stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    if (forecast != null) {
                        if (forecast.isFavoritable) {
                            FavoriteToggleButton(isFavorite = forecast.isFavorite, onClick = onFavoriteClick, tintOnDark = true)
                        }
                        ShareButton(onClick = { onShare(forecast) })
                    }
                },
                colors = if (heroStart != null) {
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = heroStart,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                } else {
                    TopAppBarDefaults.topAppBarColors()
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = uiState,
                contentKey = { it::class },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "forecastState"
            ) { state ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (state) {
                        UiState.Loading -> LoadingView()
                        UiState.Empty -> ErrorView(message = stringResource(R.string.error_not_found))
                        is UiState.Error -> ErrorView(message = state.message.asString(), onRetry = onRetry)
                        is UiState.Success -> ForecastContent(forecast = state.data)
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastContent(forecast: ForecastUiModel, modifier: Modifier = Modifier) {
    val rangeMin = forecast.daily.minOfOrNull { it.minC } ?: 0.0
    val rangeMax = forecast.daily.maxOfOrNull { it.maxC } ?: 0.0
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeroHeader(forecast = forecast)
        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = forecast.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            forecast.updatedAtText?.let { updatedAt ->
                Text(
                    text = stringResource(R.string.updated_at, updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        SectionTitle(text = stringResource(R.string.detail_hourly))
        HourlyForecastRow(items = forecast.hourly)
        SectionTitle(text = stringResource(R.string.detail_daily))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            forecast.daily.forEachIndexed { index, day ->
                if (index > 0) HorizontalDivider()
                DailyForecastItem(day = day, rangeMinC = rangeMin, rangeMaxC = rangeMax)
            }
        }
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
                    hourly = List(8) { HourlyUiModel("1$it:00", "☀️", "2$it°", if (it % 3 == 0) "%20" else null, isNow = it == 0) },
                    daily = List(7) {
                        DailyUiModel(UiText.Dynamic("Cuma"), "⛅", "1$it°", "2${it + 2}°", "%10", minC = 10.0 + it, maxC = 22.0 + it)
                    },
                    iconTint = WeatherPalette.Sun,
                    todayMinText = "14°",
                    todayMaxText = "24°"
                )
            ),
            onBack = {},
            onShare = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {}
        )
    }
}
