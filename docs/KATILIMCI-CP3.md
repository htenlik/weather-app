# CP3 — State yönetimi ve veri tutarlılığı

**2. Gün · 10.15–11.15 · 60 dk** · Branch: `katilimci/htenlik/cp3` (başlangıç: `cp3-baslangic`)

## Uygulama adımları ve karşılıkları

| Adım | Ne yapıldı | Commit |
|---|---|---|
| 1. State modelini tanımlayın | `SelectionUiState(favoriteCityIds: Set<Long>, showOnlyFavorites: Boolean)`: seçim (favori şehirler) + görünüm tercihi (yalnızca favoriler). `favoriteCount` ve `isFavorite(id)` türetilmiş üyeler. `SelectionViewModel` durumu `StateFlow<SelectionUiState>` olarak sunar. | `feat(selection): add shared SelectionViewModel …` |
| 2. Ortak sahipliği kurun | `HavaApp` içinde, `NavHost`'un **üstünde** `hiltViewModel()` ile tek örnek alınır (Activity kapsamı) ve `HavaNavHost(selectionViewModel = …)` ile her iki hedefe parametre olarak verilir. Liste ve detay route'ları aynı örneği gözler. | `feat(list): toggle favorites …`, `feat(detail): reflect and toggle …` |
| 3. Aksiyonları işleyin | Kart kalbi → `onFavoriteClick(cityId)` → `selectionViewModel.toggleFavorite`; filtre çipi → `setShowOnlyFavorites`; detay üst çubuğu → `toggleFavorite(viewModel.cityId)`. Her aksiyon `MutableStateFlow.update` ile **yeni** state üretir. Route'larda `collectAsStateWithLifecycle()`. | aynı |
| 4. Tutarlılığı doğrulayın | Ekran geçişi: aynı ViewModel örneği (Activity kapsamı). Döndürme: ViewModel yaşar; ayrıca `SavedStateHandle` süreç ölümünde bile geri yükler (`selection survives process death …` testi). Türetim: `applySelection` yükleme durumu + seçimi birleştirir; `isFavorite` ve filtrelenmiş liste saklanmaz, hesaplanır. | aynı |

## Tamamlanma ölçütleri

- [x] **Kullanıcı seçimi ekrandaki görünümü günceller.** Kalbe dokununca kart ikonu dolar, çipteki sayaç artar; filtre açıkken liste anında daralır.
- [x] **İlgili ekranlar aynı state kaynağından beslenir.** `CityListRoute` ve `ForecastDetailRoute` parametre olarak gelen tek `SelectionViewModel` örneğini gözler; başka bir favori kaynağı yok.
- [x] **Ekranlar arası geçişte seçim korunur.** ViewModel NavHost'un üstünde olduğu için hedefler değişse de örnek aynı kalır (listede ekle → detayda dolu kalp; detayda kaldır → listede boş kalp).
- [x] **Yapılandırma değişikliğinde seçim korunur.** `ViewModel` döndürmede yaşar; `SavedStateHandle` (`favoriteCityIds: LongArray`, `showOnlyFavorites`) süreç ölümünü de kapsar — testle sabitlendi.
- [x] **Türetilen bilgiler için ayrı değiştirilebilir state tutulmaz.** `favoriteCount` hesaplanan özellik; `isFavorite` işareti ve filtrelenmiş liste `applySelection` ile her recomposition'da türetilir (`CityListDerivationTest`, 4 test).

## İsteğe bağlı uygulama

- [x] **Seçime bağlı filtre / görünüm değişikliği.** "Favoriler (n)" `FilterChip`'i `showOnlyFavorites` tercihini değiştirir; sonuç `UiState.Success(list).applySelection(selection)` ile mevcut state'ten türetilir, ayrı bir "filtrelenmiş liste" state'i yoktur. Favori yokken filtre açılırsa `UiState.Empty` ve açıklayıcı metin ("Henüz favori şehir yok…").

## Notlar (kitapçığa yazılacak metin)

> State'in sahibi tek: `SelectionViewModel`. Liste ve detay ekranı kendi ViewModel'lerinde yalnızca yükleme durumunu tutar; kullanıcı seçimi (favoriler) ve görünüm tercihi (yalnızca favoriler) ortak ViewModel'de. Örnek `NavHost`'un üstünde, Activity kapsamında alındığı için ekranlar hangi sırayla açılırsa açılsın aynı state'i görür.
>
> Kapsam kararı: seçim ekranlardan uzun yaşamalı ama uygulamadan uzun yaşamak zorunda değil; bu yüzden Activity kapsamı yeterli. Döndürmeyi ViewModel, süreç ölümünü `SavedStateHandle` karşılıyor; kalıcı depolama (Room) CP4'ün konusu.
>
> Türetilen bilgi kuralı: "favori mi", "kaç favori var", "filtrelenmiş liste" hiçbir yerde saklanmıyor; `favoriteCount` hesaplanan özellik, liste `applySelection(loadState, selection)` ile her seferinde yeniden hesaplanıyor. Böylece iki state'in birbirinden kopması (ör. sayaç 3 derken listede 2 favori) imkânsız.
>
> Aksiyonlar tek yönlü akıyor: ekran callback verir → ViewModel yeni state üretir (`update { copy(...) }`) → `StateFlow` yayar → Route `collectAsStateWithLifecycle` ile alır → ekran yeniden çizilir. Ekran state'i doğrudan değiştirmez.

## Sunumda söylenecek üç cümle

1. Ortak state'in tek sahibi var ve NavHost'un üstünde yaşıyor; liste ile detay aynı örneği gözlüyor.
2. Favori işareti, sayaç ve filtre sonucu saklanmıyor, mevcut state'ten türetiliyor; tutarsızlık mümkün değil.
3. Döndürmede ViewModel, süreç ölümünde `SavedStateHandle` seçimi koruyor; ikisi de testle gösterildi.
