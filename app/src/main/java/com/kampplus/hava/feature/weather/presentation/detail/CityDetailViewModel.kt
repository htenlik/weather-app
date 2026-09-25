package com.kampplus.hava.feature.weather.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.R
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.navigation.CityDetailDestination
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.policy.WeatherConditionClassifier
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.presentation.model.CityDetailUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherConditionUiRegistry
import com.kampplus.hava.feature.weather.presentation.model.displaySubtitle
import com.kampplus.hava.feature.weather.presentation.model.toTemperatureText
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Detay ekranının durumu. Şehir kimliği navigasyon argümanından ([SavedStateHandle]) okunur;
 * veri, liste ekranıyla aynı use case üzerinden gelir ve kimliğe göre seçilir.
 */
@HiltViewModel
class CityDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCityWeathers: GetCityWeathersUseCase,
    private val conditionClassifier: WeatherConditionClassifier,
    private val conditionUiRegistry: WeatherConditionUiRegistry
) : ViewModel() {

    /** Eksik ya da pozitif olmayan kimlik "geçersiz parametre" sayılır; çökmek yerine hata durumu üretilir. */
    private val cityId: Long? = savedStateHandle.get<Long>(CityDetailDestination.ARG_CITY_ID)?.takeIf { it > 0 }

    val uiState: StateFlow<UiState<CityDetailUiModel>> = flow<UiState<CityDetailUiModel>> {
        val id = cityId
        if (id == null) {
            emit(UiState.Error(UiText.Resource(R.string.detail_invalid_parameter)))
        } else {
            emitAll(getCityWeathers().map { result -> result.toDetailState(id) })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = UiState.Loading
    )

    private fun AppResult<List<CityWeather>>.toDetailState(id: Long): UiState<CityDetailUiModel> = when (this) {
        is AppResult.Success ->
            data.firstOrNull { it.city.id == id }
                ?.let { UiState.Success(it.toDetailUiModel()) }
                ?: UiState.Error(UiText.Resource(R.string.detail_city_not_found, id))

        is AppResult.Failure -> UiState.Error(UiText.Resource(R.string.error_generic))
    }

    private fun CityWeather.toDetailUiModel(): CityDetailUiModel {
        val conditionUi = conditionUiRegistry.resolve(conditionClassifier.classify(current.weatherCode))
        return CityDetailUiModel(
            cityId = city.id,
            title = city.name,
            subtitle = city.displaySubtitle(),
            temperatureText = current.temperatureC.toTemperatureText(),
            temperatureC = current.temperatureC,
            conditionEmoji = conditionUi.emoji,
            conditionLabel = conditionUi.label,
            apparentTemperatureText = current.apparentTemperatureC?.toTemperatureText(),
            humidityPercent = current.humidityPercent,
            windSpeedKmh = current.windSpeedKmh?.roundToInt(),
            observedAtText = current.observedAt.format(TIME_FORMATTER)
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
