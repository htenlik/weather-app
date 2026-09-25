package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.core.common.demo.DemoScenario
import com.kampplus.hava.core.common.demo.DemoScenarioSwitch
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.CurrentWeather
import com.kampplus.hava.feature.weather.domain.model.DailyForecast
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.HourlyForecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import java.io.IOException
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.delay

/**
 * Koda gömülü sabit hava verisi. İnternet gerektirmez; aynı şehir için her zaman aynı değeri üretir.
 * Gerçek bir ağ çağrısı gibi `suspend` ile bekler; [DemoScenarioSwitch] ile gecikme, boş sonuç ve
 * hata senaryoları çalışma zamanında seçilir (CP4). Gerçek API'ye geçişte yalnızca DI binding'i değişir.
 */
class FakeWeatherRemoteDataSource @Inject constructor(
    private val scenarioSwitch: DemoScenarioSwitch
) : WeatherRemoteDataSource {

    override suspend fun getCurrentWeather(cities: List<City>): List<CityWeather> {
        // Senaryo istek başında bir kez okunur; istek sürerken menüden değişmesi o isteği etkilemez.
        val scenario = scenarioSwitch.scenario.value
        simulateNetwork(scenario)
        if (scenario == DemoScenario.EMPTY) return emptyList()
        return cities.map { city -> CityWeather(city = city, current = currentFor(city)) }
    }

    override suspend fun getForecast(city: City): Forecast {
        simulateNetwork(scenarioSwitch.scenario.value)
        val current = currentFor(city)
        val slot = slotOf(city)
        val hourly = List(HOURS) { hour ->
            val time = OBSERVED_AT.plusHours(hour.toLong())
            HourlyForecast(
                time = time,
                // Gün içi eğri, gözlem saatinde anlık sıcaklığa eşit olacak şekilde kaydırılır.
                temperatureC = current.temperatureC + DAILY_CURVE[time.hour] - DAILY_CURVE[OBSERVED_AT.hour],
                weatherCode = if (hour == 0) current.weatherCode else WeatherCode(CODES[(slot + hour) % CODES.size]),
                precipitationProbability = (hour * 7) % 60
            )
        }
        val daily = List(DAYS) { day ->
            DailyForecast(
                date = OBSERVED_AT.toLocalDate().plusDays(day.toLong()),
                minTemperatureC = current.temperatureC - 7 + day % 3,
                maxTemperatureC = current.temperatureC + 2 - day % 2,
                weatherCode = if (day == 0) current.weatherCode else WeatherCode(CODES[(slot + day * 3) % CODES.size]),
                precipitationProbability = (day * 13) % 80
            )
        }
        return Forecast(current = current, hourly = hourly, daily = daily)
    }

    /** Gecikme ve hata senaryoları. İptal edilirse [delay] CancellationException fırlatır; bu hata sayılmaz. */
    private suspend fun simulateNetwork(scenario: DemoScenario) {
        when (scenario) {
            DemoScenario.SLOW -> delay(SLOW_LATENCY_MS)

            DemoScenario.ERROR -> {
                delay(FAKE_LATENCY_MS)
                throw IOException("Simüle edilmiş ağ hatası")
            }

            DemoScenario.NORMAL, DemoScenario.EMPTY -> delay(FAKE_LATENCY_MS)
        }
    }

    /** Liste ve detay aynı şehir için aynı anlık değeri göstersin diye tek kaynaktan üretilir. */
    private fun currentFor(city: City): CurrentWeather {
        val slot = slotOf(city)
        val temperature = TEMPERATURES[slot % TEMPERATURES.size]
        return CurrentWeather(
            temperatureC = temperature,
            weatherCode = WeatherCode(CODES[slot % CODES.size]),
            observedAt = OBSERVED_AT,
            apparentTemperatureC = temperature - 1.5,
            humidityPercent = 40 + (slot * 3) % 50,
            windSpeedKmh = 5.0 + slot % 15
        )
    }

    private fun slotOf(city: City): Int = (city.id % TEMPERATURES.size).toInt().let { if (it < 0) -it else it }

    private companion object {
        const val HOURS = 24
        const val DAYS = 7
        val DAILY_CURVE = listOf(
            -5.0, -5.5, -6.0, -6.0, -5.5, -5.0, -4.0, -2.5, -1.0, 0.5, 1.5, 2.5,
            3.0, 3.5, 3.5, 3.0, 2.0, 1.0, 0.0, -1.0, -2.0, -3.0, -4.0, -4.5
        )
        const val FAKE_LATENCY_MS = 300L
        const val SLOW_LATENCY_MS = 4_000L
        val OBSERVED_AT: LocalDateTime = LocalDateTime.of(2026, 9, 24, 12, 0)
        val TEMPERATURES =
            listOf(18.4, 21.0, 26.3, 19.7, 29.1, 30.2, 22.8, 27.5, 31.4, 20.1, 28.0, 25.6, 17.9, 16.4, 19.2, 18.8, 11.3, 13.7, 23.5, 27.0)
        val CODES = listOf(2, 0, 1, 3, 0, 1, 0, 2, 0, 61, 1, 0, 3, 45, 80, 63, 2, 71, 1, 95)
    }
}
