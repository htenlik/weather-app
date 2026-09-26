package com.kampplus.hava.testing

import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.location.domain.model.UserLocation
import com.kampplus.hava.feature.location.domain.repository.LocationRepository
import com.kampplus.hava.feature.weather.domain.model.Coordinates

class FakeLocationRepository(
    var result: () -> AppResult<UserLocation> = { AppResult.Failure(AppError.LocationUnavailable) }
) : LocationRepository {

    var requestCount = 0
        private set

    override suspend fun getCurrentLocation(): AppResult<UserLocation> {
        requestCount++
        return result()
    }
}

fun userLocation(
    coordinates: Coordinates = Coordinates(39.93, 32.86),
    placeName: String? = "Çankaya",
    region: String? = "Ankara",
    country: String? = "Türkiye"
) = UserLocation(coordinates = coordinates, placeName = placeName, region = region, country = country)
