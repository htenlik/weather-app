# CP4 — Asenkron veri ve arayüz durumları

**2. Gün · 13.30–14.30 · 60 dk** · Branch: `katilimci/htenlik/cp4` (başlangıç: `cp4-baslangic`)

## Uygulama adımları ve karşılıkları

| Adım | Ne yapıldı | Commit |
|---|---|---|
| 1. Veri çağrısını inceleyin | Veri kaynağı `suspend fun getCurrentWeather(...)`: gerçek ağ gibi bekler, sonucu döner ya da fırlatır. Senaryolar `DemoScenarioSwitch` ile çalışma zamanında seçilir: **NORMAL** (300 ms, dolu), **SLOW** (4 sn), **EMPTY** (boş liste), **ERROR** (`IOException` → `AppError.Network`). Debug'da liste üst çubuğundaki menüden seçilir. | `feat(data): add demo scenarios …` |
| 2. Yüklemeyi yönetin | `CityListViewModel.loadData()`: `init` ve "Tekrar dene" aynı fonksiyonu çağırır; yükleme durumu `MutableStateFlow<UiState<…>>` (kitapçıktaki `LoadState` karşılığı: `Loading / Success / Empty / Error`) üzerinden `StateFlow` olarak sunulur. Detay ekranında aynı kalıp. | `feat(list): load city weathers …`, `feat(detail): retry …` |
| 3. Sonuçları eşleştirin | `viewModelScope.launch` içinde önce `Loading`, sonra `AppResult` → `Success` / boşsa `Empty` / `Failure` → `Error(mesaj)`. İptal: `runCatchingApp` `CancellationException`'ı yeniden fırlatır, coroutine sessizce biter, `Error` yazılmaz. | aynı |
| 4. Arayüzü bağlayın | Tek `when` ile dört görünüm: `LoadingView`, `EmptyView`, `ErrorView(onRetry)`, liste. "Tekrar dene" → `loadData()`. Eşzamanlı istek koruması: aktif `Job` varken `loadData`/`refresh` yok sayılır (`if (loadJob?.isActive == true) return`). | aynı |

## Tamamlanma ölçütleri

- [x] **Yükleme sırasında arayüz yanıt vermeye devam eder.** Çağrı `suspend`, repository `flowOn(ioDispatcher)` ile arka planda; ana thread bloklanmaz. "Yavaş ağ (4 sn)" senaryosunda kaydırma, menü ve favori butonu çalışmaya devam eder.
- [x] **İçerik ve boş sonuç ayrı görünümlerle sunulur.** `UiState.Success` → liste; `UiState.Empty` → ikon + "Gösterilecek şehir yok" (`EmptyView`). "Boş sonuç" senaryosuyla cihazda gösterilir.
- [x] **Hata görünümünde açıklama ve yeniden deneme bulunur.** `ErrorView`: uyarı ikonu + `AppError.toUiText()` ile kullanıcı dilinde mesaj ("İnternet bağlantısı yok. Bağlantını kontrol edip tekrar dene.") + **Tekrar dene**.
- [x] **Yeniden deneme, yükleme ve başarı geçişini gerçekleştirir.** `retry after error emits loading and then the loaded list` (liste) ve `… then the forecast` (detay) testleri: Error → Loading → Success.
- [x] **Yeniden çizim ve hızlı tıklamalar ek istek üretmez.** İstekler yalnızca ViewModel'de (`init`/`loadData`/`refresh`) başlar, composable içinde `LaunchedEffect` ile istek yok → recomposition istek üretmez. Hızlı tıklama: `loadJob` koruması; `repeated loadData calls while a request is in flight do not start new requests` testleri repository'nin **1 kez** çağrıldığını doğrular.

## İsteğe bağlı uygulama

- [x] **Mevcut içerik üzerinde yenileme akışı.** `refresh()` `Loading`'e düşmez; `isRefreshing` bayrağını açar, yeni veri gelince listeyi değiştirir, `finally` ile bayrağı kapatır. Ekranda `PullToRefreshBox`: ilk yüklemede tam ekran `LoadingView`, yenilemede içerik ekranda kalır ve yalnızca çekme göstergesi döner. `refresh keeps the current content visible and then replaces it` testi. Boş/hata görünümleri kaydırılabilir bir kapsayıcıda olduğu için o durumlarda da aşağı çekerek yenilenebilir.

## Notlar (kitapçığa yazılacak metin)

> Veri durumlarını dörde ayırdım ve her birini ayrı bileşenle çizdim: `Loading` (ilk yükleme), `Success` (liste), `Empty` (başarılı ama boş) ve `Error` (mesaj + Tekrar dene). Boş sonuç bir hata değildir; kullanıcıya farklı bir şey söyler, o yüzden ayrı durum.
>
> Yükleme tek bir yerden yönetiliyor: `loadData()` hem `init`'te hem "Tekrar dene"de çağrılıyor, önce `Loading` yayımlıyor, sonra sonucu duruma çeviriyor. Mesajlar `AppError → UiText` eşlemesiyle üretiliyor; ViewModel `Context` bilmiyor.
>
> İptal davranışı: `runCatchingApp` `CancellationException`'ı yakalamayıp yeniden fırlatıyor; ViewModel temizlendiğinde ya da coroutine iptal edildiğinde ekran `Error`'a düşmüyor, sadece sessizce duruyor. İptal bir hata değil, bir vazgeçme.
>
> Yeniden deneme politikası: aktif bir istek varken yeni `loadData`/`refresh` çağrıları yok sayılıyor (`Job.isActive` koruması). Böylece hızlı tıklamalar ve çekme hareketleri paralel istek üretmiyor; testte repository'nin bir kez çağrıldığı doğrulandı. Yenileme ise `Loading`'e düşmüyor, içerik ekranda kalıyor, sadece `isRefreshing` bayrağı dönüyor.
>
> Senaryoları (gecikme, boş, hata) sahte veri kaynağına bir anahtarla ekledim; böylece dört durumu emülatörde ağı bozmadan, menüden seçerek gösterebiliyorum.

## Sunumda söylenecek üç cümle

1. Dört durum tek `when`'de, her biri kendi bileşeniyle; boş sonuç hata değil, ayrı bir durum.
2. `loadData()` tek giriş noktası: önce Loading, sonra sonuç; iptal Error'a çevrilmiyor; aktif istek varken yeni istek yok.
3. Yenileme içeriği bozmuyor: liste ekranda kalıyor, yalnızca çekme göstergesi dönüyor — demo menüsüyle dört durum canlı gösterilebiliyor.
