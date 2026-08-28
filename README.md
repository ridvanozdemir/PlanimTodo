# Planım v1.1.0

Android için çevrimdışı çalışan görev, hedef ve haftalık ilerleme takip uygulaması.

## Güncel Play Store sürümü
- Resmi Play Store paket adı: `com.ridvanozdemir.planim`
- `versionCode = 5`
- `versionName = 1.1.0`
- Android 16 / API 36 hedeflenir
- Dağıtım sürümleri kalıcı release signing key ile imzalanır
- Google Play dağıtımı şu anda **Kapalı test (Alpha)** kanalı üzerinden yürütülmektedir

## v1.1.0 arayüz güncellemesi
- Görevler / Hedefler / Rapor için 3 sekmeli alt navigasyon
- Daha modern beyaz kartlar, pastel vurgu renkleri ve güçlü tipografi
- Görev kartlarında ilerleme, tekrar hedefi ve hatırlatma bilgisi
- Görev oluşturma akışında daha düzenli ve profesyonel görünüm
- Hedeflerin kısa / orta / uzun vade olarak otomatik gruplandırılması
- Haftalık analiz ekranında istatistik kartları ve görsel ilerleme göstergeleri
- Mevcut yerel veri yapısı ve hatırlatma sistemi korunur

## Özellikler
- Günlük / haftalık / aylık görevler
- Periyot içinde 1–10 tekrar hedefi ve ilerleme sayacı
- Opsiyonel saat bazlı hatırlatma
- Hedefleri hedef tarihine göre otomatik kısa / orta / uzun vadeli sınıflandırma
- Hedef tamamlama
- Haftalık analiz ve geçmiş haftaları görüntüleme
- Verileri cihazda yerel olarak saklama
- Hesap veya internet bağlantısı gerektirmeden kullanım

## Güncelleme ve test dağıtımı
Google Play'de daha önce yayınlanan `1.0.0` sürümünün paket adı da `com.ridvanozdemir.planim` olduğu için `1.1.0` sürümü mevcut test kullanıcılarına **uygulama güncellemesi** olarak sunulabilir. Kullanıcıların uygulamayı kaldırıp yeniden yüklemesi gerekmez; otomatik güncelleme açıksa Play Store güncellemeyi otomatik uygulayabilir, aksi durumda **Güncelle** butonu üzerinden yüklenebilir.

> Geçmiş not: İlk sınırlı dağıtım denemelerinde `com.ridvanozdemir.planimtodo` paket adı kullanılmıştı. Bu eski paket, mevcut Google Play uygulamasından farklıdır ve otomatik güncellenmez.

## APK / AAB oluşturma
- **Build APK** iş akışı `main` güncellemelerinde ve PR kontrollerinde test APK'sı üretir.
- **Build Play AAB** iş akışı manuel olarak çalıştırılır ve Google Play'e yüklenecek, release anahtarıyla imzalanmış Android App Bundle (`.aab`) üretir.

## Gizlilik
Planım v1.1.0 hesap, reklam veya bulut servisi gerektirmez. Görev ve hedef verileri cihaz üzerinde saklanır.
