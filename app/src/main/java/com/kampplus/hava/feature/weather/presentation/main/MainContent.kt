package com.kampplus.hava.feature.weather.presentation.main

/** Ana ekrandaki bir kartın içeriği. CP1'de sabit örnek veridir; sonraki checkpoint'lerde veri katmanından gelir. */
data class ContentItem(
    val title: String,
    val description: String
)

/** Uygulama çalıştığında ekranda görünen örnek içerik. */
val sampleContent: List<ContentItem> = listOf(
    ContentItem(title = "İstanbul", description = "24° · Parçalı bulutlu · Nem %62"),
    ContentItem(title = "Ankara", description = "21° · Açık · Nem %35"),
    ContentItem(title = "İzmir", description = "28° · Güneşli · Nem %48"),
    ContentItem(title = "Antalya", description = "31° · Açık · Nem %55"),
    ContentItem(title = "Trabzon", description = "19° · Yağmurlu · Nem %84"),
    ContentItem(title = "Erzurum", description = "11° · Kar sağanağı · Nem %70"),
    ContentItem(title = "Kahramanmaraş", description = "27° · Az bulutlu · Nem %40")
)
