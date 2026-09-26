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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.core.ui.component.ConditionIcon
import com.kampplus.hava.core.ui.component.FavoriteToggleButton
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.core.ui.theme.temperatureColor
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel

@Composable
fun CityWeatherCard(item: CityWeatherUiModel, onClick: () -> Unit, onFavoriteClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConditionIcon(icon = item.icon, tint = item.iconTint, contentDescription = item.conditionLabel.asString())
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.conditionLabel.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = item.temperatureText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = temperatureColor(item.temperatureC)
            )
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
                conditionLabel = UiText.Dynamic("Açık"),
                isFavorite = true
            ),
            onClick = {},
            onFavoriteClick = {}
        )
    }
}
