package com.kampplus.hava.core.network.di

import android.content.Context
import com.kampplus.hava.BuildConfig
import com.kampplus.hava.core.common.network.NetworkMonitor
import com.kampplus.hava.core.network.cache.CacheControlInterceptor
import com.kampplus.hava.core.network.cache.OfflineCacheInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TIMEOUT_SECONDS = 20L
    private const val CACHE_SIZE_BYTES = 10L * 1024 * 1024

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideConverterFactory(json: Json): Converter.Factory = json.asConverterFactory("application/json".toMediaType())

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context, networkMonitor: NetworkMonitor): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        // Kamp salonunda 20 cihaz aynı API'ye gider; HTTP cache gereksiz istekleri azaltır.
        // Aynı cache çevrimdışı modun da temelidir: son yanıt saklanır, bağlantı yokken oradan sunulur.
        .cache(Cache(File(context.cacheDir, "http"), CACHE_SIZE_BYTES))
        .addInterceptor(OfflineCacheInterceptor(networkMonitor))
        .addNetworkInterceptor(CacheControlInterceptor())
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            }
        }
        .build()

    @Provides
    @Singleton
    @ForecastRetrofit
    fun provideForecastRetrofit(client: OkHttpClient, converterFactory: Converter.Factory): Retrofit =
        retrofit(BuildConfig.FORECAST_BASE_URL, client, converterFactory)

    @Provides
    @Singleton
    @GeocodingRetrofit
    fun provideGeocodingRetrofit(client: OkHttpClient, converterFactory: Converter.Factory): Retrofit =
        retrofit(BuildConfig.GEOCODING_BASE_URL, client, converterFactory)

    private fun retrofit(baseUrl: String, client: OkHttpClient, converterFactory: Converter.Factory): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(converterFactory)
        .build()
}
