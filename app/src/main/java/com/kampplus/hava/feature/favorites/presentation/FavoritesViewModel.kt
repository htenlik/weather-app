package com.kampplus.hava.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.toUiText
import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCitiesUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.usecase.GetCurrentWeatherUseCase
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.model.toCity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Favori şehirler ve anlık havaları. Şehir listesi yerel kaynaktan, hava verisi ağdan gelir; ikisi
 * bir önbellek üzerinden birleştirilir. Favori eklenip çıkarıldığında liste yeniden yüklenmez:
 * eldeki hava verisi korunur, yalnızca eksik şehirler ağdan istenir. Böylece ekran hiç "yükleniyor"a düşmez.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavoriteCities: ObserveFavoriteCitiesUseCase,
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    private val toggleFavoriteCity: ToggleFavoriteCityUseCase,
    observeSettings: ObserveSettingsUseCase,
    private val uiMapper: WeatherUiMapper
) : ViewModel() {

    private var favoritesById: Map<Long, FavoriteCity> = emptyMap()
    private var lastRemoved: FavoriteCity? = null
    private var fetchJob: Job? = null

    private val _events = Channel<FavoritesEvent>(Channel.BUFFERED)

    /** Tek seferlik UI olayları (snackbar). State'e konmaz; ekran dönünce tekrar gösterilmemeli. */
    val events: Flow<FavoritesEvent> = _events.receiveAsFlow()

    /** Şehir kimliği → son bilinen hava. */
    private val weatherById = MutableStateFlow<Map<Long, CityWeather>>(emptyMap())
    private val fetchError = MutableStateFlow<AppError?>(null)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val favorites = observeFavoriteCities().onEach { list ->
        favoritesById = list.associateBy { it.id }
        val missing = list.filter { it.id !in weatherById.value }
        if (missing.isNotEmpty()) fetch(list)
    }

    val uiState: StateFlow<UiState<List<CityWeatherUiModel>>> = combine(
        favorites,
        weatherById,
        fetchError,
        observeSettings()
    ) { list, weather, error, settings ->
        val items = list.mapNotNull { favorite ->
            weather[favorite.id]?.let { uiMapper.toListItem(it, isFavorite = true, unit = settings.temperatureUnit) }
        }
        when {
            list.isEmpty() -> UiState.Empty
            items.isNotEmpty() -> UiState.Success(items)
            error != null -> UiState.Error(error.toUiText())
            else -> UiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = UiState.Loading
    )

    fun findFavorite(id: Long): FavoriteCity? = favoritesById[id]

    fun onRetry() {
        fetch(favoritesById.values.toList())
    }

    fun onRefresh() {
        _isRefreshing.value = true
        fetch(favoritesById.values.toList())
    }

    fun onRemoveFavorite(id: Long) {
        val favorite = favoritesById[id] ?: return
        viewModelScope.launch {
            toggleFavoriteCity(favorite)
            lastRemoved = favorite
            _events.send(FavoritesEvent.ShowUndo(cityName = favorite.name))
        }
    }

    fun onUndoRemove() {
        val favorite = lastRemoved ?: return
        lastRemoved = null
        viewModelScope.launch { toggleFavoriteCity(favorite) }
    }

    /** Verilen favorilerin tamamı için tek istek; önceki istek sürüyorsa iptal edilir. */
    private fun fetch(list: List<FavoriteCity>) {
        fetchJob?.cancel()
        if (list.isEmpty()) {
            _isRefreshing.value = false
            return
        }
        fetchJob = viewModelScope.launch {
            fetchError.value = null
            try {
                when (val result = getCurrentWeather(list.map { it.toCity() }).first()) {
                    is AppResult.Success -> weatherById.update { it + result.data.associateBy { weather -> weather.city.id } }
                    is AppResult.Failure -> fetchError.value = result.error
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
