package com.kampplus.hava.core.network.cache

import com.kampplus.hava.testing.FakeNetworkMonitor
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Gerçek OkHttp önbelleği + MockWebServer: önbellek kararları uçtan uca doğrulanır. */
class OfflineCacheInterceptorTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val server = MockWebServer()
    private val networkMonitor = FakeNetworkMonitor()
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        server.start()
        client = OkHttpClient.Builder()
            // Bağlantı kopunca OkHttp'nin kendi tekrar denemesi devreye girmesin; sahte sunucu tek yanıt verir.
            .retryOnConnectionFailure(false)
            .cache(Cache(temporaryFolder.newFolder(), CACHE_SIZE_BYTES))
            .addInterceptor(OfflineCacheInterceptor(networkMonitor))
            .addNetworkInterceptor(CacheControlInterceptor())
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `offline serves the last cached response and keeps its original receive time`() {
        server.enqueue(MockResponse().setBody("first"))
        val receivedAt = client.newCall(request()).execute().use { response ->
            assertEquals("first", response.body!!.string())
            response.receivedResponseAtMillis
        }

        networkMonitor.online.value = false
        client.newCall(request()).execute().use { response ->
            assertEquals("first", response.body!!.string())
            assertNotNull(response.cacheResponse)
            assertNull(response.networkResponse)
            assertEquals(receivedAt, response.receivedResponseAtMillis)
        }
        assertEquals(1, server.requestCount)
    }

    @Test(expected = OfflineException::class)
    fun `offline without a cached response fails as a network error`() {
        networkMonitor.online.value = false

        client.newCall(request()).execute()
    }

    @Test
    fun `network failure falls back to the cached response`() {
        server.enqueue(MockResponse().setBody("first"))
        client.newCall(request()).execute().readBody()
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST))

        client.newCall(request(CacheControl.FORCE_NETWORK)).execute().use { response ->
            assertEquals("first", response.body!!.string())
            assertNotNull(response.cacheResponse)
        }
    }

    @Test
    fun `fresh responses are reused without a second request`() {
        server.enqueue(MockResponse().setBody("first"))
        client.newCall(request()).execute().readBody()

        client.newCall(request()).execute().use { response ->
            assertEquals("first", response.body!!.string())
            assertNotNull(response.cacheResponse)
        }
        assertEquals(1, server.requestCount)
    }

    /** OkHttp bir yanıtı ancak gövdesi sonuna kadar okununca önbelleğe yazar. */
    private fun Response.readBody(): String = use { checkNotNull(body).string() }

    private fun request(cacheControl: CacheControl? = null): Request = Request.Builder()
        .url(server.url("/v1/forecast?latitude=39.9&longitude=32.8"))
        .apply { if (cacheControl != null) cacheControl(cacheControl) }
        .build()

    private companion object {
        const val CACHE_SIZE_BYTES = 1024L * 1024
    }
}
