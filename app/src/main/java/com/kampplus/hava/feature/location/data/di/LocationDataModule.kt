package com.kampplus.hava.feature.location.data.di

import com.kampplus.hava.feature.location.data.geocoder.GeocoderPlaceNameResolver
import com.kampplus.hava.feature.location.data.geocoder.PlaceNameResolver
import com.kampplus.hava.feature.location.data.repository.AndroidLocationRepository
import com.kampplus.hava.feature.location.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationDataModule {
    @Binds
    abstract fun bindLocationRepository(impl: AndroidLocationRepository): LocationRepository

    @Binds
    abstract fun bindPlaceNameResolver(impl: GeocoderPlaceNameResolver): PlaceNameResolver
}
