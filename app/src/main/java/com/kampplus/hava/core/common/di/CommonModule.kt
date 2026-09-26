package com.kampplus.hava.core.common.di

import com.kampplus.hava.core.common.dispatcher.DefaultDispatcher
import com.kampplus.hava.core.common.dispatcher.IoDispatcher
import com.kampplus.hava.core.common.error.ErrorMapper
import com.kampplus.hava.core.common.network.NetworkMonitor
import com.kampplus.hava.core.network.connectivity.ConnectivityNetworkMonitor
import com.kampplus.hava.core.network.error.NetworkErrorMapper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {
    @Binds
    abstract fun bindErrorMapper(impl: NetworkErrorMapper): ErrorMapper

    @Binds
    abstract fun bindNetworkMonitor(impl: ConnectivityNetworkMonitor): NetworkMonitor

    companion object {
        @Provides
        @IoDispatcher
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        @DefaultDispatcher
        fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

        @Provides
        fun provideClock(): Clock = Clock.systemDefaultZone()
    }
}
