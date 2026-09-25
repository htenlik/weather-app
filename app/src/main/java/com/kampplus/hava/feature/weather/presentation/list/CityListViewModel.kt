package com.kampplus.hava.feature.weather.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.R
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.policy.WeatherConditionClassifier
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherConditionUiRegistry
import com.kampplus.hava.feature.weather.presentation.model.displaySubtitle
import com.kampplus.hava.feature.weather.presentation.model.toTemperatureText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CityListViewModel @Inject constructor(
    getCityWeathers: GetCityWeathersUseCase,
    private val conditionClassifier: WeatherConditionClassifier,
    private val conditionUiRegistry: WeatherConditionUiRegistry
) : ViewModel() {

    val uiState: StateFlow<UiState<List<CityWeatherUiModel>>> = getCityWeathers()
        .map { result ->
            when (result) {
                is AppResult.Success ->
                    if (result.data.isEmpty()) UiState.Empty else UiState.Success(result.data.map { it.toUiModel() })

                is AppResult.Failure -> UiState.Error(UiText.Resource(R.string.error_generic))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = UiState.Loading
        )

    private fun CityWeather.toUiModel(): CityWeatherUiModel {
        val conditionUi = conditionUiRegistry.resolve(conditionClassifier.classify(current.weatherCode))
        return CityWeatherUiModel(
            cityId = city.id,
            title = city.name,
            subtitle = city.displaySubtitle(),
            temperatureText = current.temperatureC.toTemperatureText(),
            temperatureC = current.temperatureC,
            conditionEmoji = conditionUi.emoji,
            conditionLabel = conditionUi.label
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
