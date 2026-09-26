package com.kampplus.hava.feature.weather.presentation.detail.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kampplus.hava.core.ui.component.ConditionIcon
import com.kampplus.hava.feature.weather.presentation.model.DailyUiModel
import com.kampplus.hava.feature.weather.presentation.model.temperatureColor

/**
 * Günlük tahmin satırı. Sıcaklık aralığı, haftanın en düşük ([rangeMinC]) ve en yüksek ([rangeMaxC])
 * değerine göre ölçeklenen bir çubukla gösterilir; günler birbiriyle bir bakışta karşılaştırılır.
 */
@Composable
fun DailyForecastItem(day: DailyUiModel, rangeMinC: Double, rangeMaxC: Double, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = day.dayLabel.asString(), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.width(92.dp))
        ConditionIcon(icon = day.icon, tint = day.iconTint, size = 32.dp)
        Text(
            text = day.precipitationText.orEmpty(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .width(44.dp)
                .padding(start = 6.dp)
        )
        Text(
            text = day.minText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End
        )
        TemperatureRangeBar(
            minC = day.minC,
            maxC = day.maxC,
            rangeMinC = rangeMinC,
            rangeMaxC = rangeMaxC,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )
        Text(text = day.maxText, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
    }
}

/** Yatay bir iz üzerinde min–max aralığını, sıcaklık renklerinden oluşan bir gradient ile çizer. */
@Composable
private fun TemperatureRangeBar(minC: Double, maxC: Double, rangeMinC: Double, rangeMaxC: Double, modifier: Modifier = Modifier) {
    val track = MaterialTheme.colorScheme.surfaceVariant
    val gradient = listOf(temperatureColor(minC), temperatureColor(maxC))
    val span = (rangeMaxC - rangeMinC).takeIf { it > 0 } ?: 1.0
    val start = ((minC - rangeMinC) / span).toFloat().coerceIn(0f, 1f)
    val end = ((maxC - rangeMinC) / span).toFloat().coerceIn(start, 1f)
    Canvas(modifier = modifier.height(8.dp)) {
        val radius = CornerRadius(size.height / 2)
        drawRoundRect(color = track, cornerRadius = radius)
        val left = size.width * start
        val width = (size.width * (end - start)).coerceAtLeast(size.height)
        drawRoundRect(
            brush = Brush.horizontalGradient(colors = gradient, startX = left, endX = left + width),
            topLeft = Offset(left, 0f),
            size = Size(width, size.height),
            cornerRadius = radius
        )
    }
}
