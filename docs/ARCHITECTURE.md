# Mimari

Tek `:app` modülü, **feature-first** paketleme; her feature kendi `data / domain / presentation` katmanına sahiptir.
Bağımlılık yönü: `presentation → domain ← data`. Domain saf Kotlin'dir; `LayerDependencyTest` domain'de Android/Retrofit/Room importunu, presentation'da data importunu yasaklar.

## Paket haritası

```
com.kampplus.hava
├── HavaApplication.kt / MainActivity.kt (splash, tema, sistem çubukları) / MainViewModel.kt (ayarlar + bağlantı)
├── HavaApp.kt (Scaffold + BottomBar + OfflineBanner)
├── core/
│   ├── common/        AppResult (+ runCatchingApp), AppError, ErrorMapper (+ Default), NetworkMonitor sözleşmesi,
│   │                  dispatcher qualifier'ları, CommonModule
│   ├── network/       NetworkModule (OkHttp + HTTP cache, Json, @ForecastRetrofit / @GeocodingRetrofit), NetworkErrorMapper,
│   │                  cache/ (CacheControlInterceptor, OfflineCacheInterceptor), connectivity/ (ConnectivityNetworkMonitor)
│   ├── database/      HavaDatabase, DatabaseModule
│   ├── ui/            theme (renk rolleri, TemperaturePalette + temperatureColor, WeatherPalette, hero gradient),
│   │                  component (Loading/Error/Empty/Shimmer/ConditionIcon/FavoriteToggle/OfflineBanner/LightStatusBarIcons),
│   │                  adaptive/WindowWidthClass, UiState, UiText, AppErrorText
│   └── navigation/    Destinations (List, Favorites, Settings, Forecast(cityId, name, region, country, lat, lon)),
│                      TopLevelDestination, BottomBar, HavaNavHost
└── feature/
    ├── weather/
    │   ├── domain/    model (City [+ DEVICE_LOCATION_ID], Coordinates, WeatherCode, CurrentWeather, CityWeather, Forecast [+ fetchedAt])
    │   │              repository (WeatherRepository, CityRepository)
    │   │              usecase (GetCityWeathers, GetCurrentWeather, GetForecast, SearchCityWeathers)
    │   │              policy (WeatherConditionClassifier + WmoWeatherConditionClassifier)
    │   ├── data/      local/CityCatalog (+ TurkishCityCatalog, 20 şehir, geocoding kimlikleriyle)
    │   │              remote/ WeatherRemoteDataSource (Fake → OpenMeteo, Response<T> ile alınma zamanı), CityRemoteDataSource, api, dto
    │   │              mapper (ForecastDtoMapper — sütun → satır, GeocodingDtoMapper), repository, di/WeatherDataModule
    │   └── presentation/ list (CityList Route/Screen/ViewModel/UiState, CitySearchField, CityWeatherCard)
    │                  detail (ForecastDetail Route/Screen/ViewModel, HeroHeader, HourlyForecastRow, DailyForecastItem, ShareButton)
    │                  model (UI modelleri, WeatherUiMapper [birim, alınma zamanı], WeatherConditionUiRegistry, FavoriteMapping)
    │                  di/WeatherConditionUiModule (@IntoMap + özel @MapKey: ikon, renk, gece ikonu)
    ├── favorites/
    │   ├── domain/    FavoriteCity, FavoriteCityRepository, Observe/ObserveIds/Toggle use case'leri
    │   ├── data/      FavoriteCityLocalDataSource (InMemory → Room), dao, entity, repository, di
    │   └── presentation/ Favorites Route/Screen/ViewModel (canlı hava + önbellek), FavoritesEvent (undo)
    ├── settings/
    │   ├── domain/    UserSettings, TemperatureUnit (+ fromCelsius), ThemeMode, SettingsRepository, Observe/Update use case'leri
    │   ├── data/      DataStoreSettingsRepository (Preferences DataStore), di/SettingsDataModule
    │   └── presentation/ Settings Route/Screen/ViewModel/UiState, ThemeModeResolver
    └── location/
        ├── domain/    UserLocation (+ toCity), LocationRepository, GetLocationWeatherUseCase
        ├── data/      AndroidLocationRepository (LocationManager), geocoder/ (PlaceNameResolver, GeocoderPlaceNameResolver), di
        └── presentation/ LocationCardRoute (izin akışı), LocationCard, LocationWeatherViewModel, LocationUiState

test/          ViewModel'ler (Turbine, debounce için virtual time), use case'ler, MockWebServer veri kaynağı testleri,
               OfflineCacheInterceptor (gerçek OkHttp önbelleği), DataStore ayar deposu, NetworkErrorMapper,
               WMO sınıflandırıcı, favori senkron testi, LayerDependencyTest; testing/ altında sahte repository'ler
androidTest/   Room DAO testi
```

## Veri akışı

`Retrofit/Room → DataSource → RepositoryImpl (Flow<AppResult<…>>) → UseCase → ViewModel (StateFlow<UiState>) → Route (collectAsStateWithLifecycle) → Screen (stateless)`

## Open/Closed genişleme noktaları

| Senaryo | Eklenir | Değişir | Dokunulmaz |
|---|---|---|---|
| Sabit veri → gerçek API (CP3 → CP4) | `OpenMeteoWeatherRemoteDataSource`, DTO'lar, mapper | `WeatherDataModule` (1 `@Binds`) | Domain, ViewModel, ekranlar |
| Favoriler bellek → disk (CP3 → CP4) | `RoomFavoriteCityDataSource`, entity, DAO | `FavoritesDataModule` (1 `@Binds`) | Use case'ler, ViewModel'ler |
| Hata eşleme genel → ağ (CP4) | `NetworkErrorMapper` | `CommonModule` (1 `@Binds`) | Repository'ler |
| Yeni hava durumu görünümü (ör. dolu) | `WeatherConditionUiModule`'e `@IntoMap` girdisi | — | Mapper, ekranlar |
| Farklı şehir listesi (ör. Avrupa başkentleri) | Yeni `CityCatalog` implementasyonu | `WeatherDataModule` | Tüm üst katmanlar |
| Yeni hava değişkeni (ör. UV indeksi) | DTO alanı + domain alanı (varsayılanlı) | Mapper, istek parametre listesi | ViewModel sözleşmeleri |
| Yeni kullanıcı tercihi (ör. rüzgâr birimi) | `UserSettings` alanı + DataStore anahtarı + Ayarlar satırı | `WeatherUiMapper` (birim metni) | Repository sözleşmesi, ViewModel'ler |
| Konum sağlayıcısını değiştirme (ör. Fused Location) | Yeni `LocationRepository` implementasyonu | `LocationDataModule` (1 `@Binds`) | Use case, kart, izin akışı |
| Yeni dil | `values-<dil>/strings.xml` | — | Kod (metinler `UiText` / kaynak kimliği ile taşınır) |

## 1.1.0 ile gelen kalıplar

- **Ayarların akışı:** `SettingsRepository.observeSettings()` tek doğruluk kaynağıdır. Ekran ViewModel'leri kendi
  akışlarını `observeSettings()` ile `combine` eder; birim değişince mapper'a `TemperatureUnit` geçer, renk eşiği için
  Celsius değeri modelde korunur. `MainViewModel` aynı akışı tema için okur; `MainActivity` tercih gelene kadar splash'ı tutar.
- **Konum kartı ve izin:** izin platforma ait olduğundan `LocationCardRoute` ister ve sonucu ViewModel'e bildirir
  (`onPermissionChanged` / `onPermissionResult`). ViewModel yalnızca izin varsa yükler; "bir daha sorma" durumu
  `shouldShowRequestPermissionRationale` ile ayırt edilir. Cihaz konumu `City.DEVICE_LOCATION_ID` ile temsil edilir ve
  favorilere eklenemez.
- **Çevrimdışı katmanı:** iki OkHttp interceptor'ı. Ağ tarafındaki `CacheControlInterceptor` yanıtlara tazelik verir;
  uygulama tarafındaki `OfflineCacheInterceptor` cihaz çevrimdışıysa ya da istek başarısız olursa önbellekteki son
  yanıtı (`FORCE_CACHE`) döner, yoksa `IOException` fırlatır → `AppError.Network`. Yanıtın `receivedResponseAtMillis`
  değeri önbellekten gelse bile ilk alınma anıdır; `CityWeather.fetchedAt` / `Forecast.fetchedAt` olarak domain'e taşınır.
- **Bağlantı geri gelince:** `NetworkMonitor.onReconnect()` akışı; hata durumundaki ViewModel'ler yeniden yükler.
- **Uyarlanabilir düzen:** listeler `LazyVerticalGrid(GridCells.Adaptive)`, detay `BoxWithConstraints` +
  `WindowWidthClass` ile iki bölme. Ek kütüphane gerekmez.
