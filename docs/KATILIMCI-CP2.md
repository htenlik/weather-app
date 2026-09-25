# CP2 — Ekranlar ve navigasyon

**1. Gün · 15.15–16.15 · 60 dk** · Branch: `katilimci/htenlik/cp2` (başlangıç: `cp2-baslangic`)

## Uygulama adımları ve karşılıkları

| Adım | Ne yapıldı | Commit |
|---|---|---|
| 1. Ekranları tanımlayın | **Ana ekran** `CityListScreen`: öne çıkan şehirlerin anlık havasını listeler, tek sorumluluğu "hangi şehre tıklandı"yı bildirmek. **İkinci ekran** `CityDetailScreen`: kimliği verilen şehrin sıcaklık, koşul, hissedilen, nem, rüzgâr ve gözlem saatini gösterir. İhtiyaç duyduğu tek girdi `cityId`; veriyi `CityDetailViewModel` aynı `GetCityWeathersUseCase` üzerinden yükleyip kimliğe göre seçer. | `feat(detail): add CityDetailScreen and view model …` |
| 2. Geçişleri yapılandırın | `HavaNavHost` içinde type-safe hedefler: `ListDestination` (başlangıç) ve `CityDetailDestination(cityId: Long)`. Detay ekranına yalnızca kimlik taşınır; `SavedStateHandle["cityId"]` ile okunur. | `feat(nav): add HavaNavHost …` |
| 3. Bileşenleri ayırın | Karar zinciri: `CityWeatherCard(onClick)` → `CityListScreen(onCityClick: (Long) -> Unit)` → `CityListRoute` → `HavaNavHost` içinde `navController.navigate(CityDetailDestination(cityId))`. Kart ve ekran `NavController`'ı tanımaz; geçiş kararı callback'i sağlayan üst katmanda verilir. | `feat(nav): add HavaNavHost …` |
| 4. Geri dönüşü doğrulayın | Sistem Geri: `NavHost` geri yığınını kendisi yönetir. Üst çubuk geri ikonu: `onBack = navController::navigateUp`. Geçersiz parametre: eksik/negatif kimlik → "Geçersiz şehir kimliği…", listede olmayan kimlik → "42 kimlikli şehir listede bulunamadı…"; iki durumda da ikon + açıklama + **Listeye dön** butonu. 5 birim testi. | `feat(detail): show explanatory view …` |

## Tamamlanma ölçütleri

- [x] **En az iki ekran beklenen içerikle görüntülenir.** Liste (20 şehir) ve detay (seçilen şehrin anlık havası).
- [x] **Kullanıcı aksiyonu doğru hedefe geçişi başlatır.** Karta dokunma → `CityDetailDestination(cityId)`; her kart kendi kimliğini taşır (`onClick = { onCityClick(item.cityId) }`).
- [x] **Aktarılan parametre ikinci ekranda doğru kullanılır.** `CityDetailViewModelTest.shows the city whose id was passed through navigation`: kimlik 2 ile açılan ekran İzmir'i, 27°'yi ve 12:00 gözlem saatini gösterir.
- [x] **Sistem Geri işlemi önceki ekrana dönüş sağlar.** NavHost varsayılan davranışı; üst çubuk ikonu `navigateUp` ile aynı yolu kullanır.
- [x] **Geçersiz parametre için açıklayıcı bir görünüm sunulur.** `InvalidCityView`: neden boş ekran görüldüğü açıklanır ve listeye dönüş butonu verilir. Testler: eksik parametre, negatif kimlik, bilinmeyen kimlik.

## İsteğe bağlı uygulama

- [x] **Bir ekranı farklı boyutlarda değerlendirin.** `CityDetailScreenSizePreview`: 320dp (dar) ve 600dp (geniş) önizlemeleri; başlık satırı ve kart tam genişliğe uyum sağlar.
- [x] **Geri dönüşte kaydırma durumunu koruyun.** Liste `LazyColumn` kullanır; `rememberLazyListState()` `rememberSaveable` tabanlıdır ve NavHost geri yığını girdilerinin saved state'ini koruduğu için detaydan dönüşte kaydırma konumu kaybolmaz. Ek kod gerekmedi; cihazda listeyi kaydırıp bir şehre girip geri dönerek doğrulanır.

## Notlar (kitapçığa yazılacak metin)

> Her ekrana tek sorumluluk verdim: liste yalnızca "hangi şehir seçildi"yi bilir, detay yalnızca "kimliği verilen şehri göster"i bilir. İkisini bağlayan tek yer `HavaNavHost`; `NavController`'ı yalnızca o tanır.
>
> Ekranlar arasında nesne değil kimlik (`cityId: Long`) taşıdım. Küçük parametre geri yığınında güvenle saklanır (süreç ölümünde bile), detay ekranı veriyi kendi ViewModel'iyle yükler; böylece liste ekranı kapanmış olsa da detay kendi başına çalışır.
>
> Alt bileşenler karar vermez, olay bildirir: kart `onClick`, ekran `onCityClick(cityId)` callback'i verir; navigate çağrısı callback'i sağlayan üst katmanda. Bu sayede ekranlar önizlemede ve testte navigasyon olmadan çalışır.
>
> Geri dönüş davranışı: sistem Geri'yi NavHost, üst çubuktaki oku `navigateUp` yönetir. Geçersiz kimlik için `checkNotNull` ile çökmek yerine `SavedStateHandle`'ı null-güvenli okudum; eksik, negatif veya listede olmayan kimlik açıklayıcı bir görünüm ve "Listeye dön" butonu üretir. Bu davranış 5 birim testiyle sabitlendi.

## Sunumda söylenecek üç cümle

1. İki ekran, tek NavHost: geçiş kararı ekranların değil, callback'i sağlayan NavHost'un.
2. Detay ekranına yalnızca `cityId` gidiyor; ekran veriyi kendi yüklüyor, bu yüzden geri yığını hafif ve süreç ölümüne dayanıklı.
3. Geçersiz kimlik çökmüyor: açıklama + "Listeye dön" ile her durumda anlaşılır bir geri dönüş yolu var.
