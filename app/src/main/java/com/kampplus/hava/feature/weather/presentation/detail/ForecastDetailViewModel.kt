package com.kampplus.hava.feature.weather.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.navigation.ForecastDestination
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.toUiText
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.usecase.GetForecastUseCase
import com.kampplus.hava.feature.weather.presentation.model.ForecastUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.model.toFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ForecastDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getForecast: GetForecastUseCase,
    observeFavoriteCityIds: ObserveFavoriteCityIdsUseCase,
    private val toggleFavoriteCity: ToggleFavoriteCityUseCase,
    observeSettings: ObserveSettingsUseCase,
    private val uiMapper: WeatherUiMapper
) : ViewModel() {

    // toRoute<ForecastDestination>() Android Bundle'a ihtiyaç duyar; anahtarla okumak JVM testlerini sade tutar.
    private val city = City(
        id = checkNotNull(savedStateHandle[ForecastDestination.ARG_CITY_ID]),
        name = checkNotNull(savedStateHandle[ForecastDestination.ARG_NAME]),
        region = savedStateHandle[ForecastDestination.ARG_REGION],
        country = savedStateHandle[ForecastDestination.ARG_COUNTRY],
        coordinates = Coordinates(
            latitude = checkNotNull(savedStateHandle[ForecastDestination.ARG_LATITUDE]),
            longitude = checkNotNull(savedStateHandle[ForecastDestination.ARG_LONGITUDE])
        )
    )

    /** null = henüz yükleniyor. */
    private val result = MutableStateFlow<AppResult<Forecast>?>(null)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<UiState<ForecastUiModel>> = combine(
        result,
        observeFavoriteCityIds(),
        observeSettings()
    ) { result, favoriteIds, settings ->
        when (result) {
            null -> UiState.Loading
            is AppResult.Success -> UiState.Success(
                uiMapper.toForecast(city, result.data, isFavorite = city.id in favoriteIds, unit = settings.temperatureUnit)
            )
            is AppResult.Failure -> UiState.Error(result.error.toUiText())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = UiState.Loading
    )

    init {
        load()
    }

    fun onRetry() = load()

    /** Mevcut tahmin ekranda kalır; yeni veri gelince yerine geçer. */
    fun onRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            result.value = getForecast(city)
            _isRefreshing.value = false
        }
    }

    fun onToggleFavorite() {
        if (city.isDeviceLocation) return
        viewModelScope.launch { toggleFavoriteCity(city.toFavorite()) }
    }

    private fun load() {
        viewModelScope.launch {
            result.value = null
            result.value = getForecast(city)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
