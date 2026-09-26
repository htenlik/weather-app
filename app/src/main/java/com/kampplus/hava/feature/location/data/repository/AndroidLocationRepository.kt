package com.kampplus.hava.feature.location.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.OperationCanceledException
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.kampplus.hava.core.common.dispatcher.IoDispatcher
import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.location.data.geocoder.PlaceNameResolver
import com.kampplus.hava.feature.location.domain.model.UserLocation
import com.kampplus.hava.feature.location.domain.repository.LocationRepository
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Konumu Play Services'e bağımlı olmadan, platformun [LocationManager]'ı ile alır.
 * Önce taze bir konum istenir; [FIX_TIMEOUT_MS] içinde gelmezse son bilinen konuma düşülür.
 * İzin kontrolü çağıranın sorumluluğundadır (bkz. [LocationRepository]); izinsiz çağrı [SecurityException] ile
 * değil, [AppError.LocationUnavailable] ile sonuçlanır.
 */
@SuppressLint("MissingPermission")
class AndroidLocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val placeNameResolver: PlaceNameResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocationRepository {

    override suspend fun getCurrentLocation(): AppResult<UserLocation> = withContext(ioDispatcher) {
        val manager = ContextCompat.getSystemService(context, LocationManager::class.java)
            ?: return@withContext AppResult.Failure(AppError.LocationUnavailable)
        if (!LocationManagerCompat.isLocationEnabled(manager)) return@withContext AppResult.Failure(AppError.LocationDisabled)

        val location = try {
            withTimeoutOrNull(FIX_TIMEOUT_MS) { manager.awaitCurrentLocation() } ?: manager.lastKnownLocation()
        } catch (_: SecurityException) {
            null
        } ?: return@withContext AppResult.Failure(AppError.LocationUnavailable)

        val coordinates = Coordinates(latitude = location.latitude, longitude = location.longitude)
        val place = placeNameResolver.resolve(coordinates)
        AppResult.Success(
            UserLocation(coordinates = coordinates, placeName = place?.name, region = place?.region, country = place?.country)
        )
    }

    private suspend fun LocationManager.awaitCurrentLocation(): Location? {
        val provider = preferredProvider() ?: return null
        return suspendCancellableCoroutine { continuation ->
            val signal = CancellationSignal()
            continuation.invokeOnCancellation { signal.cancel() }
            try {
                LocationManagerCompat.getCurrentLocation(this, provider, signal, ContextCompat.getMainExecutor(context)) { location ->
                    if (continuation.isActive) continuation.resume(location)
                }
            } catch (_: OperationCanceledException) {
                // Coroutine, çağrı başlamadan iptal edilmişse sinyal de iptaldir; platform bunu exception ile bildirir.
                if (continuation.isActive) continuation.resume(null)
            }
        }
    }

    /** Kaba izinle de çalışan sağlayıcılar önce gelir; en yeni son bilinen konum yedek olarak kullanılır. */
    private fun LocationManager.preferredProvider(): String? = candidateProviders().firstOrNull { provider ->
        provider in allProviders && isProviderEnabled(provider)
    }

    private fun LocationManager.lastKnownLocation(): Location? = candidateProviders()
        .filter { it in allProviders }
        .mapNotNull { provider -> getLastKnownLocation(provider) }
        .maxByOrNull { it.time }

    private fun candidateProviders(): List<String> = listOfNotNull(
        FUSED_PROVIDER.takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S },
        LocationManager.NETWORK_PROVIDER,
        LocationManager.GPS_PROVIDER,
        LocationManager.PASSIVE_PROVIDER
    )

    private companion object {
        const val FIX_TIMEOUT_MS = 8_000L

        /** LocationManager.FUSED_PROVIDER; sabit API 31'de eklendiği için ada göre kullanılır. */
        const val FUSED_PROVIDER = "fused"
    }
}
