# Planım v1.1.5

Android için çevrimdışı çalışan görev, hedef ve haftalık ilerleme takip uygulaması.

## Güncel Play Store sürümü
- Resmi Play Store paket adı: `com.ridvanozdemir.planim`
- `versionCode = 10`
- `versionName = 1.1.5`
- Android 16 / API 36 hedeflenir
- Dağıtım sürümleri kalıcı release signing key ile imzalanır
- Google Play dağıtımı şu anda **Kapalı test (Alpha)** kanalı üzerinden yürütülmektedir

## v1.1.5 adaptive launcher ikon düzeltmesi
- Telefon menüsündeki launcher ikonunun varsayılan Android ikonu olarak görünmesi sorunu giderildi
- Android 8+ adaptive icon yapısı (`mipmap-anydpi-v26`) eklendi
- Mevcut Planım checklist + hedef bayrağı görseli güvenli alana alınarak kenarlardan kırpılma azaltıldı
- Bayrak ve hedef öğeleri farklı launcher maskelerinde tamamen görünür kalacak şekilde içeri taşındı
- Çalışan uygulama kodu, görev/hedef verileri ve hatırlatma sistemi değiştirilmedi

## v1.1.4 launcher ikon güncellemesi
- Planım için özel launcher ikonu eklendi
- Android manifesti `@mipmap/ic_launcher` ikonunu kullanacak şekilde güncellendi
- Paket adı değiştirilmedi; mevcut Play kurulumu normal güncelleme alır
- Uygulama işlevleri ve yerel veri yapısı değiştirilmedi

## v1.1.3 kararlı geri dönüş sürümü
- Son sorunsuz çalışan `1.1.0` uygulama kodu temel alınmıştır
- Android 15/16 uyumluluk denemelerinde eklenen ve bazı cihazlarda açılış sorununa yol açabilecek değişiklikler geri alınmıştır
- Paket adı değiştirilmemiştir; mevcut Play kurulumu normal güncelleme alır
- Görev, hedef ve yerel kullanıcı verilerinin saklama biçimi değiştirilmemiştir

## Özellikler
- Günlük / haftalık / aylık görevler
- Periyot içinde 1–10 tekrar hedefi ve ilerleme sayacı
- Opsiyonel saat bazlı hatırlatma
- Hedefleri hedef tarihine göre otomatik kısa / orta / uzun vadeli sınıflandırma
- Hedef tamamlama
- Haftalık analiz ve geçmiş haftaları görüntüleme
- Verileri cihazda yerel olarak saklama
- Hesap veya internet bağlantısı gerektirmeden kullanım

## APK / AAB oluşturma
- **Build APK** iş akışı `main` güncellemelerinde ve PR kontrollerinde test APK'sı üretir.
- **Build Play AAB** iş akışı manuel olarak çalıştırılır ve Google Play'e yüklenecek, release anahtarıyla imzalanmış Android App Bundle (`.aab`) üretir.
