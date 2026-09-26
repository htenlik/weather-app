package com.kampplus.hava.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Hava koşulu ikonu: renkli, yumuşak arka planlı daire içinde. Renk ve ikon çağırandan gelir. */
@Composable
fun ConditionIcon(icon: ImageVector, tint: Color, modifier: Modifier = Modifier, size: Dp = 48.dp, contentDescription: String? = null) {
    Box(
        modifier = modifier
            .size(size)
            .background(color = tint.copy(alpha = 0.16f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size * 0.58f)
        )
    }
}
