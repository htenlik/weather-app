# CP1 — İlk ekran ve bileşen yapısı

**1. Gün · 11.00–12.00 · 60 dk** · Branch: `katilimci/htenlik/cp1` (başlangıç: `cp1-baslangic`)

## Uygulama adımları ve karşılıkları

| Adım | Ne yapıldı | Commit |
|---|---|---|
| 1. Uygulama girişini inceleyin | `MainActivity.onCreate` → `enableEdgeToEdge()` → `setContent { HavaTheme { MainScreen(...) } }`. Şablondaki `Greeting("Hava")` kaldırıldı; başlık (`main_title`) ve açıklama (`main_description`) `strings.xml`'e alındı ve `stringResource` ile ekrana verildi. | `feat(main): build MainScreen from ContentCard …` |
| 2. Bileşenleri oluşturun | Arayüz ikiye ayrıldı: `MainScreen(title, description, cards, modifier)` ekranı kurar; `ContentCard(title, description, modifier)` tek bir kartı çizer. Metinler yalnızca parametreyle akar, hiçbir bileşen kendi metnini üretmez. | `feat(ui): add ContentCard component …` |
| 3. Görünümü doğrulayın | Kartta `padding(16.dp)`, dikey `spacedBy(4.dp)`; listede `contentPadding = 16.dp`, kartlar arası `12.dp`. `ContentCard` için "Kısa metin" ve "Uzun metin" önizlemeleri; uzun başlık 2, açıklama 3 satırla sınırlanıp üç nokta ile kırpılır. | `feat(ui): add previews for long text …` |
| 4. Değişiklikleri kaydedin | `cp1-baslangic`'tan `katilimci/htenlik/cp1` açıldı; üç Conventional Commit (`feat(ui)`, `feat(main)`, `feat(ui)`). Cihazda çalıştırma: Android Studio ▸ Run. | — |

## Tamamlanma ölçütleri

- [x] **Uygulama başlatılır; düzenlenen içerik ekranda görüntülenir.** Üst çubukta "Şehir Havası", altında açıklama ve 7 örnek kart.
- [x] **Bileşen, farklı metin parametreleriyle yeniden kullanılabilir.** Aynı `ContentCard`, `sampleContent` listesindeki 7 farklı başlık/açıklama çifti için çizilir.
- [x] **İki önizleme çalışır; uzun metinler taşma oluşturmaz.** `ContentCardShortPreview` ve `ContentCardLongPreview`; `maxLines` + `TextOverflow.Ellipsis` sayesinde kart genişliği sabit kalır, metin kartın dışına çıkmaz.
- [x] **Uygulama girişi ile composable ilişkisi açıklanabilir.** Activity → `setContent` (Compose ağacının kökü) → `HavaTheme` (renk/tipografi) → `MainScreen` (ekran) → `ContentCard` (bileşen). Activity yalnızca bağlar, arayüz kararı vermez.
- [x] **Değişiklikler uygun dalda, açıklayıcı bir commit ile kayıtlıdır.** `git log katilimci/htenlik/cp1 --oneline`.

## İsteğe bağlı uygulama

- [x] Dar ekran (`widthDp = 320`) ve büyük yazı ölçeği (`fontScale = 1.5f`) önizlemeleri hem `ContentCard` hem `MainScreen` için eklendi. Dar ekranda uzun başlık ikinci satıra sarar, büyük yazıda kart yüksekliği artar; hiçbir metin kırpılmadan taşmaz.

## Notlar (kitapçığa yazılacak metin)

> Arayüzü iki sorumluluğa ayırdım: `MainScreen` ekranın iskeletini (üst çubuk, açıklama, liste) kurar; `ContentCard` yalnızca kendisine verilen başlık ve açıklamayı çizer, veri ya da metin üretmez. Bu sayede aynı kart 7 farklı içerik için tekrar kullanıldı.
>
> `Modifier`'ı her bileşende son ve varsayılanlı parametre olarak bıraktım: bileşen iç boşluklarına kendisi karar verir (`padding(16.dp)`), dış yerleşime (genişlik, kenar boşluğu) çağıran taraf karar verir. Bu, Compose'daki "bileşen içini bilir, dışını bilmez" kuralı.
>
> Uzun metinlerde kartın büyüyüp taşmaması için başlığı 2, açıklamayı 3 satırla sınırlayıp `Ellipsis` kullandım; önizlemelerde kısa/uzun metin, 320dp genişlik ve 1.5x yazı ölçeğiyle doğruladım.
>
> Metinleri `strings.xml`'e taşımak, sonradan dil desteği eklerken kodu değiştirmemeyi sağlıyor; örnek kart içeriği ise CP1'de sabit bir Kotlin listesi (`sampleContent`), sonraki checkpoint'lerde veri katmanından gelecek.

## Sunumda söylenecek üç cümle

1. Giriş noktası `MainActivity` yalnızca `setContent` ile temayı ve ekranı bağlar; ekran mantığı composable'larda.
2. `ContentCard` parametre alan, tekrar kullanılabilir bir bileşen; `Modifier` dış yerleşimi çağırana bırakır.
3. Kısa/uzun metin, dar ekran ve büyük yazı önizlemeleriyle yerleşim taşmadan doğrulandı.
