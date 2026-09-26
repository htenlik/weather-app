package com.kampplus.hava.feature.location.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.network.NetworkMonitor
import com.kampplus.hava.core.common.network.onReconnect
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.text.toUiText
import com.kampplus.hava.feature.location.domain.usecase.GetLocationWeatherUseCase
import com.kampplus.hava.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Konum kartı. İzin akışı platforma ait olduğu için ViewModel izni istemez; Route'tan gelen
 * [onPermissionChanged] / [onPermissionResult] çağrılarıyla haberdar olur ve izin verilince yükler.
 */
@HiltViewModel
class LocationWeatherViewModel @Inject constructor(
    private val getLocationWeather: GetLocationWeatherUseCase,
    observeSettings: ObserveSettingsUseCase,
    networkMonitor: NetworkMonitor,
    private val uiMapper: WeatherUiMapper
) : ViewModel() {

    private enum class Permission { Unknown, Granted, Requestable, Blocked }

    private val permission = MutableStateFlow(Permission.Unknown)

    /** null = yükleniyor. */
    private val result = MutableStateFlow<AppResult<CityWeather>?>(null)
    private var loadJob: Job? = null

    val uiState: StateFlow<LocationUiState> = combine(permission, result, observeSettings()) { permission, result, settings ->
        when (permission) {
            Permission.Requestable -> LocationUiState.PermissionRequired
            Permission.Blocked -> LocationUiState.PermissionDenied
            Permission.Unknown, Permission.Granted -> when (result) {
                null -> LocationUiState.Locating
                is AppResult.Success -> LocationUiState.Ready(uiMapper.toListItem(result.data, unit = settings.temperatureUnit))
                is AppResult.Failure -> LocationUiState.Error(
                    message = result.error.toUiText(),
                    isLocationDisabled = result.error == AppError.LocationDisabled
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = LocationUiState.Locating
    )

    init {
        viewModelScope.launch {
            networkMonitor.onReconnect().collect { if (uiState.value is LocationUiState.Error) load() }
        }
    }

    /** Detaya giderken kullanılan şehir; yalnızca konum ve hava alınmışsa vardır. */
    val city: City? get() = (result.value as? AppResult.Success)?.data?.city

    /** Ekran her öne geldiğinde çağrılır: kullanıcı izni ayarlardan değiştirmiş olabilir. */
    fun onPermissionChanged(granted: Boolean) {
        if (granted) {
            permission.value = Permission.Granted
            loadIfIdle()
        } else if (permission.value != Permission.Blocked) {
            permission.value = Permission.Requestable
        }
    }

    /** Sistem izin diyaloğunun sonucu. [canAskAgain] false ise kullanıcı "bir daha sorma" demiştir. */
    fun onPermissionResult(granted: Boolean, canAskAgain: Boolean) {
        permission.value = when {
            granted -> Permission.Granted
            canAskAgain -> Permission.Requestable
            else -> Permission.Blocked
        }
        if (granted) loadIfIdle()
    }

    fun onRetry() = load()

    /** Yenilemede eski kart ekranda kalır; yeni veri gelince yerine geçer. */
    fun onRefresh() {
        if (permission.value == Permission.Granted) load(keepCurrent = true)
    }

    /**
     * İzin sonucu ve ekranın öne gelmesi art arda gelebilir; süren yükleme varsa ikinci kez başlatılmaz.
     * Eldeki sonuç hata ise (ör. konum servisleri kapalıyken) geri dönüşte kendiliğinden yeniden denenir.
     */
    private fun loadIfIdle() {
        if (loadJob?.isActive != true && result.value !is AppResult.Success) load()
    }

    private fun load(keepCurrent: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (!keepCurrent) result.value = null
            result.value = getLocationWeather()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
