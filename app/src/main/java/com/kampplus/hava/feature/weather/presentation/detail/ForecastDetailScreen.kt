package com.kampplus.hava.feature.weather.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.adaptive.WindowWidthClass
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

/** Dar ekranda tek sütun; geniş ekranda (tablet, yatay telefon) başlık solda, tahminler sağda. */
@Composable
private fun ForecastContent(forecast: ForecastUiModel, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (WindowWidthClass.fromWidth(maxWidth).isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HeroHeader(forecast = forecast)
                    SourceInfo(forecast = forecast)
                }
                Column(
                    modifier = Modifier
                        .weight(WIDE_FORECAST_PANE_WEIGHT)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ForecastSections(forecast = forecast)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeroHeader(forecast = forecast)
                SourceInfo(forecast = forecast)
                ForecastSections(forecast = forecast)
            }
        }
    }
}

@Composable
private fun SourceInfo(forecast: ForecastUiModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = forecast.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        forecast.updatedAtText?.let { updatedAt ->
            Text(
                text = stringResource(R.string.updated_at, updatedAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Saatlik satır ve 7 günlük kart; her iki düzende de aynı. Sütun düzeni çağırana bırakılır. */
@Composable
private fun ForecastSections(forecast: ForecastUiModel) {
    val rangeMin = forecast.daily.minOfOrNull { it.minC } ?: 0.0
    val rangeMax = forecast.daily.maxOfOrNull { it.maxC } ?: 0.0
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

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .semantics { heading() }
    )
}

private const val WIDE_FORECAST_PANE_WEIGHT = 1.3f

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
                    humidityText = UiText.Resource(R.string.percent_value, 45),
                    windText = UiText.Resource(R.string.wind_speed_value, 12),
                    hourly = List(8) { hour ->
                        HourlyUiModel(
                            timeText = "1$hour:00",
                            emoji = "☀️",
                            temperatureText = "2$hour°",
                            precipitationText = if (hour % 3 == 0) UiText.Resource(R.string.percent_value, 20) else null,
                            isNow = hour == 0
                        )
                    },
                    daily = List(7) {
                        DailyUiModel(
                            UiText.Dynamic("Cuma"),
                            "⛅",
                            "1$it°",
                            "2${it + 2}°",
                            UiText.Resource(R.string.percent_value, 10),
                            minC = 10.0 + it,
                            maxC = 22.0 + it
                        )
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
