package com.kampplus.hava.core.network.cache

import com.kampplus.hava.core.common.network.NetworkMonitor
import java.io.IOException
import java.net.HttpURLConnection
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Çevrimdışıyken ya da ağ isteği başarısız olunca önbellekteki son yanıtı (kaç saatlik olursa olsun) döner.
 * Önbellekte de yoksa [IOException] fırlatır; bu, hata sözlüğünde "internet yok" olarak görünür.
 * Uygulama interceptor'ı olarak takılır: önbellek kararı ağ katmanına inmeden verilir.
 */
class OfflineCacheInterceptor(private val networkMonitor: NetworkMonitor) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!networkMonitor.isCurrentlyOnline()) return chain.proceedFromCache(request) ?: throw OfflineException()
        return try {
            chain.proceed(request)
        } catch (e: IOException) {
            chain.proceedFromCache(request) ?: throw e
        }
    }

    /** OkHttp, yalnızca önbellek istenip de kayıt yoksa 504 üretir; bu yanıt sonuç sayılmaz. */
    private fun Interceptor.Chain.proceedFromCache(request: Request): Response? {
        val cached = proceed(request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build())
        if (cached.code == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
            cached.close()
            return null
        }
        return cached
    }
}

/** Cihaz çevrimdışı ve istenen veri önbellekte yok. */
class OfflineException : IOException("Çevrimdışı; yanıt önbellekte yok")
