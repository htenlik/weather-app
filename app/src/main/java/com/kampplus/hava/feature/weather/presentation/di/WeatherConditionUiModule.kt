package com.kampplus.hava.feature.weather.presentation.di

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.theme.WeatherPalette
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import com.kampplus.hava.feature.weather.presentation.model.WeatherConditionUi
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@MapKey
annotation class WeatherConditionKey(
    val value: WeatherCondition
)

/** Her koşulun ikonu, rengi ve etiketi tek yerde; ekranlar koşul adını değil bu görünümü bilir. */
@Module
@InstallIn(SingletonComponent::class)
object WeatherConditionUiModule {
    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.Clear)
    fun clear() = WeatherConditionUi(
        emoji = "☀️",
        labelRes = R.string.condition_clear,
        icon = Icons.Filled.WbSunny,
        tint = WeatherPalette.Sun,
        nightIcon = WeatherConditionUi.ClearNight
    )

    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.MainlyClear)
    fun mainlyClear() = WeatherConditionUi(
        emoji = "🌤️",
        labelRes = R.string.condition_mainly_clear,
        icon = Icons.Filled.WbSunny,
        tint = WeatherPalette.Sun,
        nightIcon = WeatherConditionUi.ClearNight
    )

    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.PartlyCloudy)
    fun partlyCloudy() = WeatherConditionUi(
        emoji = "⛅",
        labelRes = R.string.condition_partly_cloudy,
        icon = Icons.Filled.WbCloudy,
        tint = WeatherPalette.Cloud
    )

    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.Overcast)
    fun overcast() = WeatherConditionUi(
        emoji = "☁️",
        labelRes = R.string.condition_overcast,
        icon = Icons.Filled.Cloud,
        tint = WeatherPalette.Cloud
    )

    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.Fog)
    fun fog() = WeatherConditionUi(
        emoji = "🌫️",
        labelRes = R.string.condition_fog,
        icon = Icons.Filled.Dehaze,
        tint = WeatherPalette.Fog
    )

    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.Drizzle)
    fun drizzle() = WeatherConditionUi(
        emoji = "🌦️",
        labelRes = R.string.condition_drizzle,
        icon = Icons.Filled.Grain,
        tint = WeatherPalette.Rain
    )

    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.Rain)
    fun rain() = WeatherConditionUi(
        emoji = "🌧️",
        labelRes = R.string.condition_rain,
        icon = Icons.Filled.WaterDrop,
        tint = WeatherPalette.Rain
    )

    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.Snow)
    fun snow() = WeatherConditionUi(
        emoji = "❄️",
        labelRes = R.string.condition_snow,
        icon = Icons.Filled.AcUnit,
        tint = WeatherPalette.Snow
    )

    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.RainShowers)
    fun rainShowers() = WeatherConditionUi(
        emoji = "🌦️",
        labelRes = R.string.condition_rain_showers,
        icon = Icons.Filled.Opacity,
        tint = WeatherPalette.Rain
    )

    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.SnowShowers)
    fun snowShowers() = WeatherConditionUi(
        emoji = "🌨️",
        labelRes = R.string.condition_snow_showers,
        icon = Icons.Filled.AcUnit,
        tint = WeatherPalette.Snow
    )

    @Provides @IntoMap
    @WeatherConditionKey(WeatherCondition.Thunderstorm)
    fun thunderstorm() = WeatherConditionUi(
        emoji = "⛈️",
        labelRes = R.string.condition_thunderstorm,
        icon = Icons.Filled.Thunderstorm,
        tint = WeatherPalette.Storm
    )
}
