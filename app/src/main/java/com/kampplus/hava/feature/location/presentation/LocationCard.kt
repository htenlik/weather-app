package com.kampplus.hava.feature.location.presentation

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.component.ConditionIcon
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.core.ui.theme.temperatureColor
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel

/** Şehir listesinin üstündeki "Konumum" kartı; her durumu tek bir kart alanında, geçişli gösterir. */
@Composable
fun LocationCard(
    state: LocationUiState,
    onUseLocation: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onRetry: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state,
        contentKey = { it::class },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "locationCard",
        modifier = modifier
    ) { current ->
        when (current) {
            LocationUiState.PermissionRequired -> MessageCard(
                icon = Icons.Filled.MyLocation,
                title = stringResource(R.string.location_permission_title),
                message = stringResource(R.string.location_permission_message),
                actionLabelRes = R.string.action_use_location,
                onAction = onUseLocation
            )

            LocationUiState.PermissionDenied -> MessageCard(
                icon = Icons.Filled.LocationOff,
                title = stringResource(R.string.location_denied_title),
                message = stringResource(R.string.location_denied_message),
                actionLabelRes = R.string.action_open_app_settings,
                onAction = onOpenAppSettings
            )

            LocationUiState.Locating -> LocatingCard()

            is LocationUiState.Ready -> LocationWeatherCard(item = current.weather, onClick = onClick)

            is LocationUiState.Error -> MessageCard(
                icon = Icons.Filled.LocationOff,
                title = stringResource(R.string.location_label),
                message = current.message.asString(),
                actionLabelRes = if (current.isLocationDisabled) R.string.action_open_location_settings else R.string.action_retry,
                onAction = if (current.isLocationDisabled) onOpenLocationSettings else onRetry
            )
        }
    }
}

@Composable
private fun LocationWeatherCard(item: CityWeatherUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConditionIcon(icon = item.icon, tint = item.iconTint, contentDescription = item.conditionLabel.asString())
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = stringResource(R.string.location_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(text = item.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (item.subtitle.isNotBlank()) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(text = item.conditionLabel.asString(), style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            }
            Text(
                text = item.temperatureText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = temperatureColor(item.temperatureC),
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

@Composable
private fun LocatingCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            Text(text = stringResource(R.string.location_locating), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/** İzin isteği, kalıcı ret ve hata durumları için ortak kart: ikon, başlık, açıklama ve tek bir eylem. */
@Composable
private fun MessageCard(
    icon: ImageVector,
    title: String,
    message: String,
    @StringRes actionLabelRes: Int,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(28.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Button(onClick = onAction, modifier = Modifier.align(Alignment.End)) {
                    Text(stringResource(actionLabelRes))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationCardPreview() {
    HavaTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LocationCard(
                state = LocationUiState.PermissionRequired,
                onUseLocation = {},
                onOpenAppSettings = {},
                onOpenLocationSettings = {},
                onRetry = {},
                onClick = {}
            )
            LocationCard(
                state = LocationUiState.Ready(
                    CityWeatherUiModel(
                        cityId = -1,
                        title = "Çankaya",
                        subtitle = "Ankara, Türkiye",
                        temperatureText = "24°",
                        temperatureC = 24.0,
                        conditionEmoji = "☀️",
                        conditionLabel = UiText.Dynamic("Açık")
                    )
                ),
                onUseLocation = {},
                onOpenAppSettings = {},
                onOpenLocationSettings = {},
                onRetry = {},
                onClick = {}
            )
        }
    }
}
