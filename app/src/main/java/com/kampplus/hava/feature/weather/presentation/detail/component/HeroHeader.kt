package com.kampplus.hava.feature.weather.presentation.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.theme.heroBrush
import com.kampplus.hava.feature.weather.presentation.model.ForecastUiModel

/**
 * Detay ekranının başlığı: koşula göre renklenen gradient üzerinde büyük sıcaklık, koşul,
 * günün en düşük/en yüksek değeri ve hissedilen/nem/rüzgâr rozetleri.
 */
@Composable
fun HeroHeader(forecast: ForecastUiModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(heroBrush(tint = forecast.iconTint, isDay = forecast.isDay))
            // Gradient çentiğin altına kadar uzanır, metinler çentikten kaçar.
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = forecast.temperatureText,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = forecast.conditionLabel.asString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.92f)
                )
                if (forecast.todayMaxText != null && forecast.todayMinText != null) {
                    Text(
                        text = stringResource(R.string.detail_today_range, forecast.todayMaxText, forecast.todayMinText),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Icon(
                imageVector = forecast.icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.95f),
                modifier = Modifier.size(104.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            forecast.feelsLikeText?.let {
                MetricChip(icon = Icons.Filled.Thermostat, label = stringResource(R.string.detail_feels_like), value = it)
            }
            forecast.humidityText?.let {
                MetricChip(icon = Icons.Filled.WaterDrop, label = stringResource(R.string.detail_humidity), value = it.asString())
            }
            forecast.windText?.let {
                MetricChip(icon = Icons.Filled.Air, label = stringResource(R.string.detail_wind), value = it.asString())
            }
        }
    }
}

@Composable
private fun MetricChip(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(color = Color.White.copy(alpha = 0.18f), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Text(text = "$label $value", style = MaterialTheme.typography.labelLarge, color = Color.White)
    }
}
