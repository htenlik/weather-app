package com.kampplus.hava.testing

import com.kampplus.hava.feature.weather.domain.policy.WmoWeatherConditionClassifier
import com.kampplus.hava.feature.weather.presentation.model.WeatherConditionUiRegistry
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

/** Sabit saat: 24 Eylül 2026 12:30, Türkiye. */
val TEST_CLOCK: Clock = Clock.fixed(Instant.parse("2026-09-24T09:30:00Z"), ZoneId.of("Europe/Istanbul"))

fun testUiMapper() = WeatherUiMapper(
    conditionClassifier = WmoWeatherConditionClassifier(),
    conditionUiRegistry = WeatherConditionUiRegistry(emptyMap()),
    clock = TEST_CLOCK
)
