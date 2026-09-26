package com.kampplus.hava.feature.weather.presentation.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.WeatherPalette
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import javax.inject.Inject

/**
 * Bir hava koşulunun ekrandaki temsili: vektör ikon, vurgu rengi, paylaşım metni için emoji ve etiket.
 * Gece için ayrı bir ikon verilebilir ([nightIcon]); verilmezse gündüz ikonu kullanılır.
 */
data class WeatherConditionUi(
    val emoji: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector = Icons.Filled.Thermostat,
    val tint: Color = WeatherPalette.Fog,
    val nightIcon: ImageVector? = null,
    val nightTint: Color? = null
) {
    val label: UiText get() = UiText.Resource(labelRes)

    /** Gündüz/gece ayrımını uygulanmış görünüm. */
    fun at(isDay: Boolean): WeatherConditionUi = if (isDay || nightIcon == null) {
        this
    } else {
        copy(icon = nightIcon, tint = nightTint ?: WeatherPalette.Night)
    }

    companion object {
        val Unknown = WeatherConditionUi(emoji = "🌡️", labelRes = R.string.condition_unknown)

        /** Açık gökyüzü için gece ikonu; açık ve az bulutlu koşullar paylaşır. */
        val ClearNight: ImageVector = Icons.Filled.NightsStay
    }
}

/**
 * Hava koşullarının UI karşılıkları Hilt `@IntoMap` ile toplanır. Yeni bir görünüm eklemek için
 * yalnızca `WeatherConditionUiModule`'e girdi eklenir; eşlemesi olmayan koşul [WeatherConditionUi.Unknown] ile gösterilir.
 */
class WeatherConditionUiRegistry @Inject constructor(
    private val entries: Map<WeatherCondition, @JvmSuppressWildcards WeatherConditionUi>
) {
    fun resolve(condition: WeatherCondition): WeatherConditionUi = entries[condition] ?: WeatherConditionUi.Unknown
}
