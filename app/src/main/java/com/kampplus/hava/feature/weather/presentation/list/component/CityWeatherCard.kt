package com.kampplus.hava.feature.weather.presentation.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.core.ui.component.FavoriteToggleButton
import com.kampplus.hava.core.ui.component.TemperatureBadge
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.temperatureColor

@Composable
fun CityWeatherCard(item: CityWeatherUiModel, onClick: () -> Unit, onFavoriteClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TemperatureBadge(text = item.temperatureText, containerColor = temperatureColor(item.temperatureC))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.conditionEmoji} ${item.conditionLabel.asString()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            FavoriteToggleButton(isFavorite = item.isFavorite, onClick = onFavoriteClick)
        }
    }
}

@Preview
@Composable
private fun CityWeatherCardPreview() {
    HavaTheme {
        CityWeatherCard(
            item = CityWeatherUiModel(
                cityId = 1,
                title = "Ankara",
                subtitle = "Ankara, Türkiye",
                temperatureText = "21°",
                temperatureC = 21.0,
                conditionEmoji = "☀️",
                conditionLabel = UiText.Dynamic("Açık")
            ),
            onClick = {},
            onFavoriteClick = {}
        )
    }
}
