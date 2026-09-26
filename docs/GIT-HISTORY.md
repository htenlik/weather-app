# Git Stratejisi ve Commit Tarihçesi

Git Flow: `main` ← `release/*` ← `develop` ← `feature/*`. Feature branch'leri `develop`'a `--no-ff` merge edilir (merge commit'i PR başlığı formatındadır). Checkpoint'ler `develop`'tan açılan salt-okunur branch'lerdir; tag yalnızca sürümler içindir (`v1.0.0`). Aynı isimde branch ve tag açmayın: `git push origin cp1-bitis` "matches more than one" hatası verir.

Conventional Commits: `<type>(<scope>): <özet>` — type: feat, fix, refactor, test, docs, build, ci, chore, style, perf · scope: core, network, db, ui, nav, weather, list, detail, favorites, release.

## Sıralı tarihçe (repodaki gerçek geçmiş)

### `main` + `develop` — iskelet
```
chore: initialize Android project with Compose template            (main)
build: add core dependencies, hilt and quality tooling             (develop)
chore(core): scaffold clean architecture package structure
test(core): add architecture test for layer dependencies
ci: add pull request pipeline and template
docs: add readme and architecture overview
```
→ `cp1-baslangic`

### `feature/city-list-static` — CP1
```
feat(weather): add city and weather domain models and repository contract
feat(weather): add city catalog and fake weather data source with 20 cities
feat(weather): implement WeatherRepositoryImpl and GetCityWeathersUseCase
feat(weather): add WeatherConditionClassifier policy for wmo codes
feat(ui): add weather condition registry and temperature badge
feat(list): add CityListViewModel exposing ui state flow
feat(list): render scrollable city list with title and subtitle
feat(list): convert list row into CityWeatherCard
test(list): cover CityListViewModel with turbine
fix(test): move classifier test into unit test source set
```
→ `cp1-bitis` / `cp2-baslangic`

### `feature/forecast-detail-navigation` — CP2
```
feat(nav): add type-safe destinations and HavaNavHost
feat(weather): add forecast models and GetForecastUseCase
feat(detail): add ForecastDetailScreen with hourly and daily forecast
feat(nav): navigate from city list to forecast detail
feat(detail): add share button with android share sheet
refactor(ui): extract WeatherUiMapper from view models
test(detail): cover ForecastDetailViewModel
```
→ `cp2-bitis` / `cp3-baslangic`

### `feature/favorite-cities` — CP3
```
fix(weather): keep fake list and forecast temperatures consistent
feat(favorites): add favorite city repository contract and use cases
feat(favorites): add InMemoryFavoriteCityDataSource backed by state flow
feat(favorites): toggle favorite city from list and detail screens
feat(favorites): add favorites tab with bottom navigation
feat(favorites): add empty state to favorites screen
feat(favorites): add undo snackbar on removal
test(favorites): verify list, detail and favorites stay in sync
```
→ `cp3-bitis` / `cp4-baslangic`

### CP4 (dört feature branch'i)
```
# feature/open-meteo-remote-data-source
feat(network): add NetworkModule with forecast and geocoding retrofit clients
feat(weather): add open-meteo dtos and retrofit apis
feat(weather): add OpenMeteoWeatherRemoteDataSource and dto mapper
refactor(weather): bind OpenMeteoWeatherRemoteDataSource instead of fake source
test(weather): cover open-meteo data source with mock web server
# feature/ui-states
feat(network): add NetworkErrorMapper for api failures
refactor(core): bind NetworkErrorMapper instead of default mapper
feat(ui): add loading and error state components
feat(list): map repository results to four ui states with retry
feat(ui): add shimmer placeholder for loading state
# feature/favorites-persistence
feat(db): add HavaDatabase with favorite cities table
feat(favorites): add RoomFavoriteCityDataSource
refactor(favorites): bind room data source instead of in-memory
test(db): cover FavoriteCityDao with in-memory database
# feature/city-search-and-refresh
feat(weather): add city search via open-meteo geocoding
feat(list): add debounced city search
feat(list): add pull-to-refresh on list and forecast detail
test(list): cover search, empty, retry and refresh states
```
→ `cp4-bitis`

### `release/1.0.0`
```
build(release): configure signing config and r8 rules
chore(release): add app icon and splash screen
docs: sync documents with implementation
chore(release): bump version to 1.0.0
```
→ `main` (tag `v1.0.0`) + `develop`'a back-merge

**OCP diff'leri** (yalnızca DI binding değişir): `refactor(weather): bind OpenMeteo…`, `refactor(favorites): bind room…`, `refactor(core): bind NetworkErrorMapper…` — `git show <hash> --stat` ile inceleyin.

## 1.1.0 — `katilimci/htenlik/hava-1.1`

`main` (v1.0.0) üzerinden açıldı; feature branch'leri `--no-ff` merge edildi, sürüm `release/1.1.0` ile kapandı.

### `feature/visual-redesign`
```
feat(ui): map weather conditions to vector icons and accent colors
feat(list): redesign city card with condition icon and colored temperature
feat(detail): add gradient hero header, hourly cards and daily range bars
feat(favorites): show live weather for favorite cities with a local cache
feat(theme): complete the tonal palette and make temperature colors theme-aware
```
### `feature/settings`
```
feat(settings): add user settings domain and DataStore repository
feat(settings): show temperatures in the selected unit
feat(settings): add settings screen with appearance, units and about
feat(theme): apply theme mode and dynamic color from settings
```
### `feature/location`
```
feat(location): add device location domain, LocationManager repository and geocoder
feat(location): show current-location weather card with permission flow
```
### `feature/offline`
```
feat(network): serve cached responses offline and observe connectivity
feat(weather): record when weather data was fetched
feat(ui): add offline banner, last-updated times and reconnect retry
```
### `feature/adaptive-l10n`
```
feat(ui): add English localization, wide-screen layouts and accessibility polish
```
### `release/1.1.0`
```
chore(release): bump version to 1.1.0
ci: build a release apk and run on release and participant branches
fix(detail): use each hour's own day/night flag for hourly icons
fix(detail): extend the hero gradient under the display cutout in landscape
docs: describe 1.1.0 features, architecture additions and history
```
→ `v1.1.0`
