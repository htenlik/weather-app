package com.kampplus.hava.feature.weather.presentation.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.component.ConditionIcon
import com.kampplus.hava.feature.weather.presentation.model.HourlyUiModel

/** Saatlik tahmin: yatay kaydırılan kartlar; ilk kart "Şimdi" olarak vurgulanır. */
@Composable
fun HourlyForecastRow(items: List<HourlyUiModel>, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = items, key = { it.timeText }) { hour ->
            HourCard(hour = hour)
        }
    }
}

@Composable
private fun HourCard(hour: HourlyUiModel, modifier: Modifier = Modifier) {
    // "Şimdi" kartı ana renkle dolu; ikon ve yağış rengi de bu zeminde okunacak tonlara geçer.
    val colors = if (hour.isNow) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
    } else {
        CardDefaults.cardColors()
    }
    val iconTint = if (hour.isNow) MaterialTheme.colorScheme.onPrimary else hour.iconTint
    val precipitationColor = if (hour.isNow) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
    Card(
        modifier = modifier
            .width(80.dp)
            .semantics(mergeDescendants = true) {},
        colors = colors
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (hour.isNow) stringResource(R.string.detail_now) else hour.timeText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (hour.isNow) FontWeight.Bold else FontWeight.Normal
            )
            ConditionIcon(icon = hour.icon, tint = iconTint, size = 36.dp)
            Text(text = hour.temperatureText, style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                if (hour.precipitationText != null) {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = precipitationColor,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Text(
                    text = hour.precipitationText?.asString() ?: " ",
                    style = MaterialTheme.typography.labelSmall,
                    color = precipitationColor
                )
            }
        }
    }
}
