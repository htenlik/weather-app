package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.data.mapper.toDomain
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoGeocodingApi
import com.kampplus.hava.feature.weather.domain.model.City
import java.util.Locale
import javax.inject.Inject

class OpenMeteoCityRemoteDataSource @Inject constructor(
    private val api: OpenMeteoGeocodingApi
) : CityRemoteDataSource {
    /** Sonuç adları cihaz dilinde istenir (Open-Meteo dil kodu); dil desteklenmiyorsa servis İngilizce döner. */
    override suspend fun search(query: String): List<City> =
        api.search(name = query, language = Locale.getDefault().language.ifBlank { DEFAULT_LANGUAGE }).results.map { it.toDomain() }

    private companion object {
        const val DEFAULT_LANGUAGE = "en"
    }
}
