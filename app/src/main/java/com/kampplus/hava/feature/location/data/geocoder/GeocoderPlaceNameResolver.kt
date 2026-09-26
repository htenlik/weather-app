package com.kampplus.hava.feature.location.data.geocoder

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.kampplus.hava.core.common.dispatcher.IoDispatcher
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Android [Geocoder] ile ters geocoding. Servis yoksa, yanıt gelmezse ya da zaman aşımı olursa null döner;
 * yer adı olmadan da hava gösterilebilir, bu yüzden hiçbir hata yukarı taşınmaz.
 */
class GeocoderPlaceNameResolver @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PlaceNameResolver {

    override suspend fun resolve(coordinates: Coordinates): PlaceName? {
        if (!Geocoder.isPresent()) return null
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = try {
            withTimeoutOrNull(TIMEOUT_MS) { geocoder.firstAddress(coordinates) }
        } catch (_: IOException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
        return address?.toPlaceName()
    }

    private suspend fun Geocoder.firstAddress(coordinates: Coordinates): Address? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                getFromLocation(
                    coordinates.latitude,
                    coordinates.longitude,
                    MAX_RESULTS,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) = continuation.resume(addresses.firstOrNull())

                        override fun onError(errorMessage: String?) = continuation.resume(null)
                    }
                )
            }
        } else {
            withContext(ioDispatcher) {
                @Suppress("DEPRECATION")
                getFromLocation(coordinates.latitude, coordinates.longitude, MAX_RESULTS)?.firstOrNull()
            }
        }

    private fun Address.toPlaceName(): PlaceName? {
        val name = locality ?: subAdminArea ?: adminArea ?: return null
        return PlaceName(name = name, region = adminArea?.takeIf { it != name }, country = countryName)
    }

    private companion object {
        const val MAX_RESULTS = 1
        const val TIMEOUT_MS = 5_000L
    }
}
