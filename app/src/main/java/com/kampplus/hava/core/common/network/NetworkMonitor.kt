package com.kampplus.hava.core.common.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/** Cihazın internete erişip erişemediğini bildirir. Platforma bağlı uygulaması core/network altındadır. */
interface NetworkMonitor {
    /** Her bağlantı değişiminde yayınlar; ilk değer mevcut durumdur. */
    val isOnline: Flow<Boolean>

    /** Anlık, senkron kontrol; ağ katmanı istek anında karar vermek için kullanır. */
    fun isCurrentlyOnline(): Boolean
}

/** Bağlantının geri geldiği anlar; başlangıç durumu sayılmaz. Hata ekranında bekleyen veriyi yenilemek için. */
fun NetworkMonitor.onReconnect(): Flow<Unit> = isOnline.distinctUntilChanged().drop(1).filter { it }.map { }
