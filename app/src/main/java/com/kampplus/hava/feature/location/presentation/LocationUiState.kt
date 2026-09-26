package com.kampplus.hava.feature.location.presentation

import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel

/** Konum kartının durumları. İzin durumu platformdan gelir, gerisi ViewModel'den. */
sealed interface LocationUiState {
    /** İzin henüz verilmedi; kullanıcıya sorulabilir. */
    data object PermissionRequired : LocationUiState

    /** Kullanıcı izni kalıcı olarak reddetti; ancak uygulama ayarlarından açılabilir. */
    data object PermissionDenied : LocationUiState

    data object Locating : LocationUiState

    data class Ready(
        val weather: CityWeatherUiModel
    ) : LocationUiState

    data class Error(
        val message: UiText,
        /** Konum servisleri kapalıysa "tekrar dene" yerine konum ayarlarına yönlendirilir. */
        val isLocationDisabled: Boolean
    ) : LocationUiState
}
