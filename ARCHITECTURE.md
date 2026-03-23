# Sticky Notes App - Mimari Belgelendirme

## Genel Yapı

Bu uygulama **Repository Pattern** ve **MVVM** benzeri bir mimarı kullanır. Aşağıda her bileşenin rolü açıklanmıştır.

```
┌─────────────────────────────────────┐
│         MainActivity                │
│    (UI Kontrolü & Etkileşim)       │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│      NotesAdapter                   │
│  (RecyclerView Adaptörü)            │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│      NoteRepository                 │
│   (Veri Yönetimi & Depolama)       │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│    SharedPreferences                │
│   (Yerel Depolama)                  │
└─────────────────────────────────────┘
```

## Bileşenler

### 1. MainActivity.kt

**Sorumluluk:** Uygulamanın ana aktivitesi, UI yönetimi ve kullanıcı etkileşimi

**Ana Fonksiyonlar:**
- `onCreate()`: Uygulamayı başlatır
- `setupRecyclerView()`: RecyclerView'ı yapılandırır
- `setupFAB()`: Floating Action Button'ı ayarlar
- `addNewNote()`: Yeni not ekleme dialog'unu açar
- `editNote()`: Not düzenleme dialog'unu açar
- `deleteNote()`: Not silme onay dialog'unu gösterir
- `showEditDialog()`: Not düzenleme/ekleme dialog'unu gösterir
- `loadNotes()`: Notları yükler ve UI'ı günceller

**Akış:**
```
Kullanıcı Etkileşimi
    ↓
MainActivity
    ↓
NoteRepository (veri işlemi)
    ↓
SharedPreferences (depolama)
    ↓
MainActivity (UI güncelleme)
    ↓
NotesAdapter (liste güncelleme)
```

### 2. NotesAdapter.kt

**Sorumluluk:** RecyclerView için not kartlarını render eder

**Ana Sınıflar:**
- `NotesAdapter`: RecyclerView adaptörü
  - `NoteViewHolder`: Her bir not kartı için view holder
    - `bind()`: Not verilerini UI'a bağlar

**Özellikler:**
- Tıklama dinleyicisi: Not düzenlemesi için
- Uzun basma dinleyicisi: Not silmesi için
- `updateNotes()`: Listeyi güncellemek için

**Kullanım:**
```kotlin
adapter = NotesAdapter(
    onNoteClick = { note -> editNote(note) },
    onNoteLongClick = { note -> deleteNote(note) }
)
```

### 3. NoteRepository.kt

**Sorumluluk:** Notların depolanması ve yönetimi

**Ana Fonksiyonlar:**
- `getAllNotes()`: Tüm notları getirir
- `addNote()`: Yeni not ekler
- `updateNote()`: Mevcut notu günceller
- `deleteNote()`: Notu siler
- `saveNotes()`: Notları SharedPreferences'e kaydeder

**Depolama Formatı:**
```json
[
  {
    "id": "1234567890",
    "content": "Not içeriği",
    "createdAt": 1234567890
  }
]
```

**Veri Akışı:**
```
Kotlin Nesneleri
    ↓
Gson (JSON'a dönüştür)
    ↓
SharedPreferences (sakla)
    ↓
SharedPreferences (oku)
    ↓
Gson (Kotlin'e dönüştür)
    ↓
Kotlin Nesneleri
```

### 4. Note.kt

**Sorumluluk:** Not veri modelini tanımlar

**Alanlar:**
- `id`: Benzersiz tanımlayıcı (timestamp)
- `content`: Not metni
- `createdAt`: Oluşturulma zamanı

**Örnek:**
```kotlin
val note = Note(
    id = "1234567890",
    content = "Yapılacaklar listesi",
    createdAt = System.currentTimeMillis()
)
```

## Veri Akışı

### Not Ekleme

```
1. Kullanıcı FAB'a tıklar
   ↓
2. MainActivity.addNewNote() çağrılır
   ↓
3. showEditDialog(null) açılır
   ↓
4. Kullanıcı metin girer ve Save'e tıklar
   ↓
5. repository.addNote(Note(...)) çağrılır
   ↓
6. NoteRepository.addNote() notu listeye ekler
   ↓
7. NoteRepository.saveNotes() SharedPreferences'e kaydeder
   ↓
8. loadNotes() çağrılır
   ↓
9. adapter.updateNotes() UI'ı günceller
   ↓
10. Yeni not ekranda görünür
```

### Not Düzenleme

```
1. Kullanıcı nota tıklar
   ↓
2. MainActivity.editNote(note) çağrılır
   ↓
3. showEditDialog(note) açılır (mevcut metin doldurulur)
   ↓
4. Kullanıcı metni değiştirir ve Save'e tıklar
   ↓
5. repository.updateNote(note.copy(...)) çağrılır
   ↓
6. NoteRepository.updateNote() notu günceller
   ↓
7. NoteRepository.saveNotes() SharedPreferences'e kaydeder
   ↓
8. loadNotes() çağrılır
   ↓
9. adapter.updateNotes() UI'ı günceller
   ↓
10. Güncellenmiş not ekranda görünür
```

### Not Silme

```
1. Kullanıcı nota uzun basır
   ↓
2. MainActivity.deleteNote(note) çağrılır
   ↓
3. Onay dialog'u gösterilir
   ↓
4. Kullanıcı Delete'e tıklar
   ↓
5. repository.deleteNote(noteId) çağrılır
   ↓
6. NoteRepository.deleteNote() notu listeden çıkarır
   ↓
7. NoteRepository.saveNotes() SharedPreferences'e kaydeder
   ↓
8. loadNotes() çağrılır
   ↓
9. adapter.updateNotes() UI'ı günceller
   ↓
10. Not ekrandan kaybolur
```

## UI Bileşenleri

### Layouts

#### activity_main.xml
- **RecyclerView**: Notları grid formatında gösterir (2 sütun)
- **FloatingActionButton**: Yeni not ekleme butonu
- **TextView**: Boş durum mesajı

#### item_note.xml
- **MaterialCardView**: Sarı not kartı
- **TextView**: Not içeriği

#### dialog_edit_note.xml
- **EditText**: Not metni giriş alanı
- **Button (Cancel)**: Dialog'u kapat
- **Button (Save)**: Notu kaydet

### Renkler

| Renk | Hex Kodu | Kullanım |
|------|----------|---------|
| Sarı | #FFD700 | Not kartları, FAB |
| Siyah | #000000 | Arka plan |
| Beyaz | #FFFFFF | Metin |
| Koyu Gri | #1a1a1a | Dialog arka planı |
| Açık Gri | #333333 | Giriş alanı arka planı |

## Bağımlılıklar

| Kütüphane | Sürüm | Kullanım |
|-----------|-------|---------|
| AndroidX Core | 1.3.2 | Modern Android API'ları |
| AndroidX AppCompat | 1.2.0 | Uyumluluk |
| Material Design | 1.2.1 | Material bileşenleri |
| RecyclerView | 1.1.0 | Verimli liste |
| Gson | 2.8.6 | JSON serileştirme |
| Kotlin Stdlib | 1.4.32 | Kotlin standart kütüphanesi |

## Genişletilebilirlik

### Yeni Özellik Ekleme: Notlara Renk

1. **Note.kt'yi güncelle:**
   ```kotlin
   data class Note(
       val id: String = System.currentTimeMillis().toString(),
       val content: String = "",
       val color: String = "#FFD700",  // Yeni alan
       val createdAt: Long = System.currentTimeMillis()
   )
   ```

2. **item_note.xml'i güncelle:**
   ```xml
   <com.google.android.material.card.MaterialCardView
       ...
       android:id="@+id/noteCard"
       ...>
   ```

3. **NotesAdapter.kt'de renk uygulanır:**
   ```kotlin
   fun bind(note: Note) {
       binding.noteText.text = note.content
       binding.noteCard.setCardBackgroundColor(Color.parseColor(note.color))
       ...
   }
   ```

4. **MainActivity.kt'de renk seçimi eklenir:**
   ```kotlin
   private fun showColorPicker() {
       // Renk seçici dialog'u
   }
   ```

### Yeni Özellik Ekleme: Notları Kategorilere Ayırma

1. **Note.kt'yi güncelle:**
   ```kotlin
   data class Note(
       ...
       val category: String = "General"  // Yeni alan
   )
   ```

2. **NoteRepository.kt'ye filtre fonksiyonu ekle:**
   ```kotlin
   fun getNotesByCategory(category: String): List<Note> {
       return getAllNotes().filter { it.category == category }
   }
   ```

3. **MainActivity.kt'de kategori seçimi ekle**

## Performans Optimizasyonları

### Mevcut Optimizasyonlar
- **RecyclerView**: Verimli liste render'ı
- **ViewBinding**: Null-safe view erişimi
- **Gson**: Hızlı JSON işleme

### Gelecek Optimizasyonlar
- **Room Database**: SharedPreferences yerine (büyük veri setleri için)
- **LiveData**: Reaktif veri yönetimi
- **Coroutines**: Asenkron işlemler
- **Paging**: Büyük listeler için sayfalandırma

## Güvenlik

### Mevcut Güvenlik Önlemleri
- **SharedPreferences**: Uygulama-spesifik depolama
- **ProGuard**: Kod obfuskasyonu (release build)
- **Manifest**: Gerekli izinler tanımlanmış

### Gelecek Güvenlik Geliştirmeleri
- **Şifreleme**: Hassas veriler için
- **Backup Şifresi**: Yedekleme koruması
- **Biometric Auth**: Parmak izi doğrulaması

## Test Stratejisi

### Unit Testler
```kotlin
@Test
fun testAddNote() {
    val note = Note(content = "Test")
    repository.addNote(note)
    val notes = repository.getAllNotes()
    assertTrue(notes.contains(note))
}
```

### UI Testler
```kotlin
@Test
fun testAddNoteUI() {
    onView(withId(R.id.fabAddNote)).perform(click())
    onView(withId(R.id.editNoteText)).perform(typeText("Test"))
    onView(withId(R.id.btnSave)).perform(click())
    onView(withText("Test")).check(matches(isDisplayed()))
}
```

## Sonuç

Bu mimari, uygulamanın:
- **Bakımlanabilir** olmasını sağlar (bileşenler ayrıştırılmış)
- **Test edilebilir** olmasını sağlar (dependency injection)
- **Genişletilebilir** olmasını sağlar (yeni özellikler kolayca eklenebilir)
- **Performanslı** olmasını sağlar (verimli veri yönetimi)
