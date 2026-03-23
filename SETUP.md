# Sticky Notes App - Kurulum Talimatları

## Hızlı Başlangıç

### 1. Adım: Projeyi Android Studio'da Açın

1. **Android Studio**'yu açın
2. **File** → **Open** seçin
3. İndirdiğiniz `sticky-notes-app` klasörünü seçin
4. **OK** tıklayın

Android Studio otomatik olarak Gradle'ı senkronize edecektir.

### 2. Adım: Gradle Senkronizasyonunu Bekleyin

- Sağ altta "Gradle build running..." mesajı görüntülenecektir
- Tamamlanana kadar bekleyin (ilk kez 5-10 dakika sürebilir)

### 3. Adım: Emülatörü Başlatın (veya Cihazı Bağlayın)

**Emülatör ile:**
1. **Tools** → **Device Manager** seçin
2. Mevcut bir emülatörü başlatın veya yeni bir tane oluşturun
3. Emülatörün tamamen başlamasını bekleyin

**Gerçek Cihaz ile:**
1. Android cihazınızı USB ile bilgisayara bağlayın
2. Cihazda **Geliştirici Seçenekleri** etkinleştirin
3. **USB Hata Ayıklaması** etkinleştirin

### 4. Adım: Uygulamayı Çalıştırın

1. **Run** → **Run 'app'** seçin (veya Shift+F10)
2. Hedef cihazı seçin
3. **OK** tıklayın

Uygulama derlenecek ve cihazda çalışacaktır.

## Sorun Giderme

### Gradle Sync Hatası

**Hata:** "Failed to resolve dependency"

**Çözüm:**
1. **File** → **Sync Now** seçin
2. Veya **File** → **Invalidate Caches / Restart** seçin
3. Android Studio'yu yeniden başlatın

### SDK Eksik Hatası

**Hata:** "Failed to find SDK"

**Çözüm:**
1. **Tools** → **SDK Manager** seçin
2. **SDK Platforms** sekmesinde **Android 11 (API 30)** yüklü olduğundan emin olun
3. **SDK Tools** sekmesinde aşağıdakileri yüklü olduğundan emin olun:
   - Android SDK Build-Tools 30.0.3 veya daha yeni
   - Android SDK Platform-Tools
   - Android SDK Tools

### Emülatör Başlamıyor

**Hata:** Emülatör başlamıyor veya çok yavaş

**Çözüm:**
1. Mevcut emülatörü silin: **Tools** → **Device Manager** → Sağ tıklama → **Delete**
2. Yeni bir emülatör oluşturun:
   - **Tools** → **Device Manager** → **Create Device**
   - **Pixel 4** seçin
   - **Android 11** (API 30) seçin
   - **Next** → **Finish**

### Derleme Hatası

**Hata:** "Compilation failed"

**Çözüm:**
1. **Build** → **Clean Project** seçin
2. **Build** → **Rebuild Project** seçin
3. Gradle cache'i temizleyin:
   - **File** → **Invalidate Caches / Restart** seçin

## Özellikler

### Not Ekleme
1. Sağ alttaki sarı **+** butonuna dokunun
2. Açılan dialog'da not yazın
3. **Save** butonuna tıklayın

### Not Düzenleme
1. Düzenlemek istediğiniz nota dokunun
2. Dialog'da metni değiştirin
3. **Save** butonuna tıklayın

### Not Silme
1. Silmek istediğiniz nota **uzun basın** (2 saniye)
2. Onay dialog'unda **Delete** seçin

## Geliştirme

### Yeni Özellik Ekleme

Örnek: Notlara renk ekleme

1. `Note.kt` dosyasını açın
2. `color` alanı ekleyin:
   ```kotlin
   data class Note(
       val id: String = System.currentTimeMillis().toString(),
       val content: String = "",
       val color: String = "#FFD700",  // Sarı
       val createdAt: Long = System.currentTimeMillis()
   )
   ```

3. `NoteRepository.kt`'de depolama güncellenir (otomatik)
4. `NotesAdapter.kt`'de renk uygulanır
5. `item_note.xml`'de renk binding'i ekleyin

### Stil Değiştirme

**Renkler:**
- `app/src/main/res/values/colors.xml` dosyasını açın
- Renkleri değiştirin

**Tema:**
- `app/src/main/res/values/themes.xml` dosyasını açın
- Tema özelliklerini değiştirin

**Boyutlar:**
- Yeni bir `dimens.xml` dosyası oluşturun:
  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <resources>
      <dimen name="note_width">160dp</dimen>
      <dimen name="note_height">200dp</dimen>
  </resources>
  ```

## Derleme ve Yayınlama

### APK Oluşturma (Test)

1. **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)** seçin
2. Derleme tamamlandığında notification'da **locate** seçin
3. APK dosyası `app/build/outputs/apk/debug/` klasöründe olacaktır

### Release APK Oluşturma

1. Keystore dosyası oluşturun (ilk kez):
   ```
   keytool -genkey -v -keystore ~/my-release-key.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
   ```

2. **Build** → **Generate Signed Bundle / APK** seçin
3. **APK** seçin ve **Next** tıklayın
4. Keystore dosyasını seçin
5. Şifre girin
6. **Release** seçin
7. **Finish** tıklayın

## Kaynaklar

- [Android Developer Documentation](https://developer.android.com/docs)
- [Kotlin Documentation](https://kotlinlang.org/docs)
- [Material Design Guidelines](https://material.io/design)
- [Android Studio Guide](https://developer.android.com/studio/intro)

## Destek

Sorularınız veya sorunlarınız varsa, lütfen proje README dosyasına bakın.
