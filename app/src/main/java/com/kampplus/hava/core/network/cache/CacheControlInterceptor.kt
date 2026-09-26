package com.kampplus.hava.core.network.cache

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Open-Meteo yanıtlarında Cache-Control yoktur; sabit bir tazelik süresi verilir. Böylece listeden detaya
 * gidip dönmek ağa çıkmaz, çevrimdışı kalınca da son yanıt önbellekten sunulabilir (bkz. [OfflineCacheInterceptor]).
 * Ağ interceptor'ı olarak takılır: yalnızca gerçekten ağdan gelen yanıtları etkiler.
 */
class CacheControlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(chain.request())
        .newBuilder()
        .header("Cache-Control", "public, max-age=$MAX_AGE_SECONDS")
        .removeHeader("Pragma")
        .build()

    private companion object {
        const val MAX_AGE_SECONDS = 120
    }
}
