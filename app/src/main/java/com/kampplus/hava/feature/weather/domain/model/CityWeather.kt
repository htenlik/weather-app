package com.kampplus.hava.feature.weather.domain.model

import java.time.Instant

/** [fetchedAt]: verinin kaynaktan alındığı an; önbellekten sunulan veride eski kalır ve ekranda "son güncelleme" olur. */
data class CityWeather(
    val city: City,
    val current: CurrentWeather,
    val fetchedAt: Instant
)
