package com.kampplus.hava.core.common.demo

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Asenkron veri senaryoları (kitapçık CP4 / adım 1): gecikme, dolu sonuç, boş sonuç ve hata.
 * Sahte veri kaynağı bu seçime göre davranır; böylece dört arayüz durumu cihazda istenildiği an gösterilebilir.
 */
enum class DemoScenario {
    /** 300 ms gecikme, dolu sonuç. */
    NORMAL,

    /** 4 saniye gecikme: yükleme görünümü ve "yüklenirken arayüz yanıt verir" ölçütü için. */
    SLOW,

    /** Başarılı ama boş sonuç. */
    EMPTY,

    /** IOException: ağ hatası olarak eşlenir, "Tekrar dene" akışı için. */
    ERROR
}

/** Seçili senaryonun tek sahibi. Uygulama kapsamında tektir; menüden seçilir, veri kaynağı okur. */
@Singleton
class DemoScenarioSwitch @Inject constructor() {
    private val _scenario = MutableStateFlow(DemoScenario.NORMAL)
    val scenario: StateFlow<DemoScenario> = _scenario.asStateFlow()

    fun select(scenario: DemoScenario) {
        _scenario.value = scenario
    }
}
