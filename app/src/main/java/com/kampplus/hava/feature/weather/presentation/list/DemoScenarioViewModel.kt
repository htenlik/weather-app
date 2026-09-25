package com.kampplus.hava.feature.weather.presentation.list

import androidx.lifecycle.ViewModel
import com.kampplus.hava.core.common.demo.DemoScenario
import com.kampplus.hava.core.common.demo.DemoScenarioSwitch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

/** Demo senaryosu menüsünü [DemoScenarioSwitch]'e bağlar; liste ViewModel'i bu eğitim aracını tanımak zorunda değildir. */
@HiltViewModel
class DemoScenarioViewModel @Inject constructor(
    private val scenarioSwitch: DemoScenarioSwitch
) : ViewModel() {

    val scenario: StateFlow<DemoScenario> = scenarioSwitch.scenario

    fun select(scenario: DemoScenario) = scenarioSwitch.select(scenario)
}
