package com.kampplus.hava.feature.settings.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.kampplus.hava.core.common.dispatcher.IoDispatcher
import com.kampplus.hava.feature.settings.data.repository.DataStoreSettingsRepository
import com.kampplus.hava.feature.settings.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsDataModule {
    @Binds
    abstract fun bindSettingsRepository(impl: DataStoreSettingsRepository): SettingsRepository

    companion object {
        private const val STORE_NAME = "settings"

        /** Tek DataStore örneği: aynı dosyaya ikinci bir örnek açmak DataStore tarafından yasaktır. */
        @Provides
        @Singleton
        fun provideSettingsDataStore(
            @ApplicationContext context: Context,
            @IoDispatcher ioDispatcher: CoroutineDispatcher
        ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(ioDispatcher + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(STORE_NAME) }
        )
    }
}
