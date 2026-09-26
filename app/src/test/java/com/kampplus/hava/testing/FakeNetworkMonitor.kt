package com.kampplus.hava.testing

import com.kampplus.hava.core.common.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNetworkMonitor(initiallyOnline: Boolean = true) : NetworkMonitor {

    val online = MutableStateFlow(initiallyOnline)

    override val isOnline: Flow<Boolean> = online

    override fun isCurrentlyOnline(): Boolean = online.value
}
