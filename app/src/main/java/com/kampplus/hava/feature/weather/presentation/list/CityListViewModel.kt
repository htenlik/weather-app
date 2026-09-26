package com.kampplus.hava.feature.weather.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.common.network.NetworkMonitor
import com.kampplus.hava.core.common.network.onReconnect
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.toUiText
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.domain.usecase.SearchCityWeathersUseCase
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.model.toFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CityListViewModel @Inject constructor(
    private val getCityWeathers: GetCityWeathersUseCase,
    private val searchCityWeathers: SearchCityWeathersUseCase,
    observeFavoriteCityIds: ObserveFavoriteCityIdsUseCase,
    private val toggleFavoriteCity: ToggleFavoriteCityUseCase,
    observeSettings: ObserveSettingsUseCase,
    networkMonitor: NetworkMonitor,
    private val uiMapper: WeatherUiMapper
) : ViewModel() {

    /** Detaya giderken ve favori eklerken şehrin tamamına ihtiyaç var; son yüklenen liste burada tutulur. */
    private var loadedCities: Map<Long, City> = emptyMap()

    private val query = MutableStateFlow("")

    /** Her artışta liste yeniden yüklenir (tekrar dene / yenile). */
    private val reloadTrigger = MutableStateFlow(0)

    /** Yenilemede mevcut liste ekranda kalır, Loading'e düşülmez. */
    private val isRefreshing = MutableStateFlow(false)

    /**
     * Yazarken her tuşa istek atılmaz: arama [SEARCH_DEBOUNCE_MS] kadar bekletilir.
     * Kısa sorguda öne çıkan şehirler gösterilir. flatMapLatest, yeni sorgu gelince eski isteği iptal eder.
     */
    private val results: Flow<AppResult<List<CityWeather>>?> = combine(
        query
            .map { it.trim().takeIf { text -> text.length >= MIN_QUERY_LENGTH }.orEmpty() }
            .debounce { if (it.isEmpty()) 0L else SEARCH_DEBOUNCE_MS }
            .distinctUntilChanged(),
        reloadTrigger
    ) { searchText, _ -> searchText }
        .flatMapLatest { searchText ->
            val source = if (searchText.isEmpty()) getCityWeathers() else searchCityWeathers(searchText)
            source
                .onEach { result ->
                    if (result is AppResult.Success) loadedCities = result.data.associate { it.city.id to it.city }
                    isRefreshing.value = false
                }
                .map<AppResult<List<CityWeather>>, AppResult<List<CityWeather>>?> { it }
                .onStart { if (!isRefreshing.value) emit(null) }
        }

    val uiState: StateFlow<CityListUiState> = combine(
        query,
        results,
        observeFavoriteCityIds(),
        isRefreshing,
        observeSettings()
    ) { query, result, favoriteIds, refreshing, settings ->
        CityListUiState(
            query = query,
            content = when (result) {
                null -> UiState.Loading
                is AppResult.Success ->
                    if (result.data.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(
                            result.data.map { weather ->
                                uiMapper.toListItem(weather, isFavorite = weather.city.id in favoriteIds, unit = settings.temperatureUnit)
                            }
                        )
                    }
                is AppResult.Failure -> UiState.Error(result.error.toUiText())
            },
            isRefreshing = refreshing,
            updatedAtText = (result as? AppResult.Success)?.data?.maxOfOrNull { it.fetchedAt }?.let(uiMapper::updatedAtText)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = CityListUiState()
    )

    init {
        // Hata ekranında bekleyen kullanıcı için bağlantı geri gelince kendiliğinden tekrar denenir.
        viewModelScope.launch {
            networkMonitor.onReconnect().collect { if (uiState.value.content is UiState.Error) onRetry() }
        }
    }

    fun findCity(cityId: Long): City? = loadedCities[cityId]

    fun onQueryChange(text: String) {
        query.value = text
    }

    fun onRetry() {
        reloadTrigger.update { it + 1 }
    }

    fun onRefresh() {
        isRefreshing.value = true
        reloadTrigger.update { it + 1 }
    }

    fun onToggleFavorite(cityId: Long) {
        val city = loadedCities[cityId] ?: return
        viewModelScope.launch { toggleFavoriteCity(city.toFavorite()) }
    }

    companion object {
        const val MIN_QUERY_LENGTH = 2
        const val SEARCH_DEBOUNCE_MS = 400L
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
