package com.kampplus.hava.core.ui.adaptive

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Material 3 genişlik sınıfları; ekranlar tek/çift sütun kararını buradan verir. */
enum class WindowWidthClass {
    Compact,
    Medium,
    Expanded;

    /** Yan yana iki bölme (ör. detayda başlık + tahmin) için yeterli genişlik. */
    val isWide: Boolean get() = this != Compact

    companion object {
        private val MEDIUM_MIN = 600.dp
        private val EXPANDED_MIN = 840.dp

        fun fromWidth(width: Dp): WindowWidthClass = when {
            width < MEDIUM_MIN -> Compact
            width < EXPANDED_MIN -> Medium
            else -> Expanded
        }
    }
}
