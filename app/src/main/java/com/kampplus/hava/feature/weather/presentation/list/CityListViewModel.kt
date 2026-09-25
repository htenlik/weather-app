package com.kampplus.hava.feature.weather.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.toUiText
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.model.toFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Liste ekranının yükleme durumu (kitapçık CP4): `init` ve "Tekrar dene" aynı [loadData] fonksiyonunu çağırır,
 * her çağrı önce Loading yayımlar, sonuç dört durumdan birine çevrilir. Yüklenirken gelen ikinci istek yok sayılır.
 */
@HiltViewModel
class CityListViewModel @Inject constructor(
    private val getCityWeathers: GetCityWeathersUseCase,
    observeFavoriteCityIds: ObserveFavoriteCityIdsUseCase,
    private val toggleFavoriteCity: ToggleFavoriteCityUseCase,
    private val uiMapper: WeatherUiMapper
) : ViewModel() {

    /** Detaya giderken ve favori eklerken şehrin tamamına ihtiyaç var; son yüklenen liste burada tutulur. */
    private var loadedCities: Map<Long, City> = emptyMap()

    /** Yükleme durumunun tek sahibi. Domain modeli tutar; favori işareti aşağıda türetilir. */
    private val loadState = MutableStateFlow<UiState<List<CityWeather>>>(UiState.Loading)

    /** Yenilemede mevcut içerik ekranda kalır; yalnızca bu bayrak açılır. */
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    /** Aktif istek. Bitmeden gelen yeni istek yok sayılır: hızlı tıklamalar ek istek üretmez. */
    private var loadJob: Job? = null

    val uiState: StateFlow<UiState<List<CityWeatherUiModel>>> = combine(loadState, observeFavoriteCityIds()) { load, favoriteIds ->
        when (load) {
            UiState.Loading -> UiState.Loading
            UiState.Empty -> UiState.Empty
            is UiState.Error -> load
            is UiState.Success -> UiState.Success(load.data.map { uiMapper.toListItem(it, isFavorite = it.city.id in favoriteIds) })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = UiState.Loading
    )

    init {
        loadData()
    }

    /**
     * İlk yükleme ve "Tekrar dene". Önce Loading yayımlanır, sonra sonuç uygun duruma çevrilir.
     * İptal (ör. ViewModel temizlendi) CancellationException olarak yayılır; Error'a çevrilmez.
     */
    fun loadData() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            loadState.value = UiState.Loading
            loadState.value = getCityWeathers().first().toLoadState()
        }
    }

    /** Senaryo değişti: sürmekte olan istek iptal edilir (hata değil, vazgeçme) ve baştan yüklenir. */
    fun reload() {
        loadJob?.cancel()
        loadData()
    }

    /** Mevcut içerik üzerinde yenileme: Loading'e düşülmez, yeni veri gelince yerine geçer. */
    fun refresh() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            _isRefreshing.value = true
            try {
                loadState.value = getCityWeathers().first().toLoadState()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun findCity(cityId: Long): City? = loadedCities[cityId]

    fun onToggleFavorite(cityId: Long) {
        val city = loadedCities[cityId] ?: return
        viewModelScope.launch { toggleFavoriteCity(city.toFavorite()) }
    }

    private fun AppResult<List<CityWeather>>.toLoadState(): UiState<List<CityWeather>> = when (this) {
        is AppResult.Success -> {
            loadedCities = data.associate { it.city.id to it.city }
            if (data.isEmpty()) UiState.Empty else UiState.Success(data)
        }

        is AppResult.Failure -> UiState.Error(error.toUiText())
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
