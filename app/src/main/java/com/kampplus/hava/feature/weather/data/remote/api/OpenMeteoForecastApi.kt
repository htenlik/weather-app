package com.kampplus.hava.feature.weather.data.remote.api

import com.kampplus.hava.feature.weather.data.remote.dto.ForecastResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * https://api.open-meteo.com/v1/ — API key gerektirmez.
 * Yanıtlar [Response] olarak alınır: gövdenin yanında alınma zamanı da okunur (önbellekten gelen yanıtta ilk alınma anı).
 */
interface OpenMeteoForecastApi {

    /** Tek konum → tek nesne. */
    @GET("forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("current") current: String,
        @Query("hourly") hourly: String? = null,
        @Query("daily") daily: String? = null,
        @Query("forecast_days") forecastDays: Int? = null,
        @Query("timezone") timezone: String = "auto"
    ): Response<ForecastResponseDto>

    /** Virgülle ayrılmış birden çok konum → konum sırasıyla dizi. */
    @GET("forecast")
    suspend fun getForecasts(
        @Query("latitude") latitudes: String,
        @Query("longitude") longitudes: String,
        @Query("current") current: String,
        @Query("timezone") timezone: String = "auto"
    ): Response<List<ForecastResponseDto>>
}
