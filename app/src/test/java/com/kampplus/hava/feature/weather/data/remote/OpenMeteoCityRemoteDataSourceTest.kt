package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.core.network.di.NetworkModule
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoGeocodingApi
import java.util.Locale
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class OpenMeteoCityRemoteDataSourceTest {

    private val server = MockWebServer()
    private lateinit var dataSource: OpenMeteoCityRemoteDataSource
    private val defaultLocale: Locale = Locale.getDefault()

    @Before
    fun setUp() {
        // Sonuç dili cihaz dilinden gelir; test Türkçe cihaz varsayar.
        Locale.setDefault(Locale.forLanguageTag("tr"))
        server.start()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/v1/"))
            .addConverterFactory(NetworkModule.provideConverterFactory(NetworkModule.provideJson()))
            .build()
            .create(OpenMeteoGeocodingApi::class.java)
        dataSource = OpenMeteoCityRemoteDataSource(api)
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
        server.shutdown()
    }

    @Test
    fun `maps geocoding results to cities with turkish names`() = runTest {
        server.enqueue(MockResponse().setBody(checkNotNull(javaClass.classLoader?.getResource("geocoding_berlin.json")).readText()))

        val cities = dataSource.search("Berlin")

        assertEquals(listOf(2950159L, 4348460L), cities.map { it.id })
        assertEquals("Almanya", cities.first().country)
        assertEquals("Maryland", cities.last().region)
        assertEquals(52.52437, cities.first().coordinates.latitude, 0.0)
        val url = server.takeRequest().requestUrl!!
        assertEquals("Berlin", url.queryParameter("name"))
        assertEquals("tr", url.queryParameter("language"))
    }

    @Test
    fun `missing results field means no match`() = runTest {
        server.enqueue(MockResponse().setBody("""{"generationtime_ms":0.04}"""))

        assertTrue(dataSource.search("xqzw").isEmpty())
    }
}
