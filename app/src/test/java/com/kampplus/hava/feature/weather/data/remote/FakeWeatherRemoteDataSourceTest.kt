package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.core.common.demo.DemoScenario
import com.kampplus.hava.core.common.demo.DemoScenarioSwitch
import com.kampplus.hava.testing.city
import java.io.IOException
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FakeWeatherRemoteDataSourceTest {

    private val scenarioSwitch = DemoScenarioSwitch()
    private val dataSource = FakeWeatherRemoteDataSource(scenarioSwitch)
    private val cities = listOf(city(id = 1, name = "Ankara"), city(id = 2, name = "İzmir", region = "İzmir"))

    @Test
    fun `normal scenario returns weather for every city after a short delay`() = runTest {
        val result = dataSource.getCurrentWeather(cities)

        assertEquals(listOf("Ankara", "İzmir"), result.map { it.city.name })
        assertEquals(300L, currentTime)
    }

    @Test
    fun `slow scenario waits four seconds`() = runTest {
        scenarioSwitch.select(DemoScenario.SLOW)

        val result = dataSource.getCurrentWeather(cities)

        assertEquals(2, result.size)
        assertEquals(4_000L, currentTime)
    }

    @Test
    fun `empty scenario returns a successful empty list`() = runTest {
        scenarioSwitch.select(DemoScenario.EMPTY)

        assertTrue(dataSource.getCurrentWeather(cities).isEmpty())
    }

    @Test
    fun `error scenario throws an io exception`() = runTest {
        scenarioSwitch.select(DemoScenario.ERROR)

        try {
            dataSource.getCurrentWeather(cities)
            fail("IOException bekleniyordu")
        } catch (expected: IOException) {
            assertEquals("Simüle edilmiş ağ hatası", expected.message)
        }
    }
}
