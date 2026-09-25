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
import androidx.compose.material3.CircularProgressIndicator
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
import com.kampplus.hava.core.ui.component.TemperatureBadge
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.model.CityDetailUiModel
import com.kampplus.hava.feature.weather.presentation.model.temperatureColor

/**
 * İkinci ekran: seçilen şehrin anlık hava ayrıntıları. Stateless'tır; durumu ve geri isteğini
 * parametre/callback ile alır, navigasyonu tanımaz.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDetailScreen(uiState: UiState<CityDetailUiModel>, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text((uiState as? UiState.Success)?.data?.title ?: stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
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
                UiState.Loading -> CircularProgressIndicator()
                UiState.Empty -> Text(stringResource(R.string.detail_invalid_parameter), modifier = Modifier.padding(32.dp))
                is UiState.Error -> Text(uiState.message.asString(), modifier = Modifier.padding(32.dp))
                is UiState.Success -> CityDetailContent(detail = uiState.data)
            }
        }
    }
}

@Composable
private fun CityDetailContent(detail: CityDetailUiModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TemperatureBadge(text = detail.temperatureText, containerColor = temperatureColor(detail.temperatureC), size = 88.dp)
            Column {
                Text(
                    text = "${detail.conditionEmoji} ${detail.conditionLabel.asString()}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = detail.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            detail.apparentTemperatureText?.let { DetailRow(label = stringResource(R.string.detail_feels_like), value = it) }
            detail.humidityPercent?.let {
                HorizontalDivider()
                DetailRow(label = stringResource(R.string.detail_humidity), value = stringResource(R.string.detail_percent_value, it))
            }
            detail.windSpeedKmh?.let {
                HorizontalDivider()
                DetailRow(label = stringResource(R.string.detail_wind), value = stringResource(R.string.detail_wind_value, it))
            }
            HorizontalDivider()
            DetailRow(label = stringResource(R.string.detail_observed_at), value = detail.observedAtText)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

private val previewDetail = CityDetailUiModel(
    cityId = 1,
    title = "İzmir",
    subtitle = "İzmir, Türkiye",
    temperatureText = "27°",
    temperatureC = 26.6,
    conditionEmoji = "☀️",
    conditionLabel = UiText.Dynamic("Açık"),
    apparentTemperatureText = "25°",
    humidityPercent = 48,
    windSpeedKmh = 12,
    observedAtText = "12:00"
)

@Preview(showBackground = true)
@Composable
private fun CityDetailScreenPreview() {
    HavaTheme {
        CityDetailScreen(uiState = UiState.Success(previewDetail), onBack = {})
    }
}
