# Planım To Do v0.1

Android için çevrimdışı çalışan yapılacaklar ve hedefler uygulaması.

## Özellikler
- Yapılacaklar: günlük / haftalık / aylık periyot
- Periyot içinde 1–10 tekrar hedefi ve ilerleme sayacı
- Opsiyonel saat bazlı hatırlatma
- Hedefleri hedef tarihine göre otomatik kısa / orta / uzun vadeli sınıflandırma
- Hedef tamamlama
- Haftalık analiz ve geçmiş haftaları görüntüleme
- Verileri cihazda saklama

## APK oluşturma
GitHub Actions içindeki **Build APK** iş akışı her `main` push'unda debug APK üretir. Çıktı `PlanimTodo-debug-apk` adlı artifact olarak Actions sayfasında bulunur.
