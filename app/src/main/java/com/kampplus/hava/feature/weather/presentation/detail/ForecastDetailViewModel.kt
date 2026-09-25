package com.kampplus.hava.feature.weather.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.R
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.navigation.ForecastDestination
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.usecase.GetForecastUseCase
import com.kampplus.hava.feature.weather.presentation.model.ForecastUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ForecastDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getForecast: GetForecastUseCase,
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

    /** Ortak seçim state'iyle eşleşmek için; ekranın hangi şehri gösterdiği yüklemeden bağımsız bilinir. */
    val cityId: Long get() = city.id

    private val _uiState = MutableStateFlow<UiState<ForecastUiModel>>(UiState.Loading)
    val uiState: StateFlow<UiState<ForecastUiModel>> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = when (val result = getForecast(city)) {
                is AppResult.Success -> UiState.Success(uiMapper.toForecast(city, result.data))
                is AppResult.Failure -> UiState.Error(UiText.Resource(R.string.error_generic))
            }
        }
    }
}
