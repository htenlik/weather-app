# Değişiklik Günlüğü

Biçim [Keep a Changelog](https://keepachangelog.com/tr/1.1.0/), sürümleme [Semantic Versioning](https://semver.org/lang/tr/).

## [1.1.0] — 2026-09-27

### Eklenenler
- **Konumum kartı:** cihaz konumundaki hava, listenin başında. İzin akışının tüm halleri ele alınır: ilk istek,
  kalıcı ret (uygulama ayarlarına kısayol), konum servisleri kapalı (konum ayarlarına kısayol), hata ve tekrar deneme.
  Konum Play Services'e bağımlı olmadan `LocationManager` ile alınır; yer adı `Geocoder`'dan gelir.
- **Ayarlar sekmesi (DataStore):** tema (sistem / açık / koyu), Material You dinamik renk, sıcaklık birimi (°C / °F),
  hakkında bölümü (sürüm, veri kaynağı ve kaynak kodu bağlantıları). Tercihler açılışta okunana kadar splash ekranı kalır.
- **Çevrimdışı mod:** bağlantı yokken son alınan yanıtlar HTTP önbelleğinden sunulur, alt çubuğun üstünde
  "Çevrimdışısın" şeridi görünür, her ekranda "Son güncelleme HH:mm" bilgisi bulunur; bağlantı geri gelince hata
  ekranındaki veriler kendiliğinden yenilenir.
- **İngilizce dil desteği:** tüm metinler `values-en` altında; gün adları, yüzde ve hız yazımı cihaz dilini izler.
- **Geniş ekran düzenleri:** şehir ve favori listeleri tablette / yatayda iki sütun, tahmin detayı iki bölme,
  ayarlar okunabilir genişlikte bir sütun.
- **Erişilebilirlik:** kartlar ve günlük satırlar ekran okuyucuda tek parça okunur, bölüm başlıkları işaretli,
  çevrimdışı şeridi kendini duyurur, büyük yazı tipinde metinler kırpılmaz.

### Değişenler
- **Görsel yenileme:** emoji yerine renkli vektör ikonlar (gece için ay), koşula göre renklenen gradient detay
  başlığı, "Şimdi" vurgulu saatlik kartlar, haftalık aralığa göre ölçeklenen günlük sıcaklık çubukları,
  soğuk→sıcak renklenen sıcaklıklar, durum geçişlerinde ve liste değişimlerinde animasyon.
- Tema: eksik Material 3 renk rolleri tamamlandı (yüzeyler tek aileden), koyu temada okunur sıcaklık paleti,
  sistem çubuğu ikonları uygulama temasını izliyor, içerik durum çubuğunun altına kadar uzanıyor.
- Favoriler ekranı artık şehirlerin anlık havasını gösteriyor (önbellekli, aşağı çekerek yenileme).
- Sürüm 1.1.0, `versionCode` 3.

### Düzeltilenler
- Saatlik tahminde her saat kendi gündüz/gece bilgisini kullanır (Open-Meteo `is_day`); akşam saatlerinde
  artık güneş ikonu görünmez.
- Yatayda tahmin başlığının gradient'i çentik boşluğunun altına kadar uzanır; metinler çentikten kaçar.
- Konum isteği, iptal edilen bir coroutine ile çakıştığında uygulamanın kapanmasına yol açan
  `OperationCanceledException` ele alındı.

## [1.0.0] — 2026-09-24

Kamp referans çözümü: 20 şehirlik liste, Open-Meteo ile anlık hava ve 7 günlük tahmin, şehir arama, favoriler (Room),
dört UI durumu, paylaşım. Ayrıntılar: [docs/GIT-HISTORY.md](docs/GIT-HISTORY.md).

[1.1.0]: https://github.com/htenlik/weather-app/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/Kamp-Teaching/weather-app/releases/tag/v1.0.0
