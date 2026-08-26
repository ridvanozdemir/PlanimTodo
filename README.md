# Planım To Do v1.0.0

Android için çevrimdışı çalışan yapılacaklar ve hedefler uygulaması.

## Play Store v1.0 hazırlığı
- Resmi Play Store paket adı: `com.ridvanozdemir.planim`
- `versionCode = 4`
- `versionName = 1.0.0`
- Android 16 / API 36 hedeflenir
- Dağıtım sürümleri kalıcı release signing key ile imzalanır

## Önceki dağıtım notu
- Önceki test/Limited Distribution paket adı `com.ridvanozdemir.planimtodo` idi.
- Google Play'deki yeni uygulama farklı paket adıyla yayınlanacağı için eski test kurulumları Play sürümüne otomatik güncellenmez.

## Özellikler
- Yapılacaklar: günlük / haftalık / aylık periyot
- Periyot içinde 1–10 tekrar hedefi ve ilerleme sayacı
- Opsiyonel saat bazlı hatırlatma
- Hedefleri hedef tarihine göre otomatik kısa / orta / uzun vadeli sınıflandırma
- Hedef tamamlama
- Haftalık analiz ve geçmiş haftaları görüntüleme
- Verileri cihazda saklama

## APK oluşturma
GitHub Actions içindeki **Build APK** iş akışı `main` güncellemelerinde ve PR kontrollerinde test APK üretir. Google Play yayını için ayrıca kalıcı release anahtarıyla imzalı Android App Bundle (`.aab`) üretilecektir.
