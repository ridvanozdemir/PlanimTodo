# Planım — Google Play Release Checklist

Son güncelleme: 25 Ağustos 2026

## Play Console

- [ ] Geliştirici hesabı kimlik doğrulaması tamamlandı
- [ ] İletişim telefonu doğrulandı
- [ ] Planım Play Console'da oluşturuldu
- [ ] Uygulama dili Türkçe olarak ayarlandı
- [ ] Ücretsiz uygulama olarak ayarlandı

## Teknik yayın paketi

- [x] Paket adı: `com.ridvanozdemir.planimtodo`
- [x] `targetSdk = 36`
- [x] `compileSdk = 36`
- [x] Kalıcı release signing yaklaşımı mevcut
- [ ] Release keystore güvenli biçimde yedeklendi
- [ ] Keystore ve parolalar GitHub Secrets'a eklendi; repoya commit edilmedi
- [ ] GitHub Actions `bundleRelease` ile imzalı `.aab` üretiyor
- [ ] Play App Signing etkinleştirildi
- [ ] İlk Play sürümü için versionCode/versionName kesinleştirildi

## Uygulama işlevleri

- [x] Günlük / haftalık / aylık yapılacaklar
- [x] 1–10 tekrar hedefi ve ilerleme sayacı
- [x] Opsiyonel hatırlatma
- [x] Kısa / orta / uzun vadeli hedef sınıflandırması
- [x] Hedef tamamlama
- [x] Haftalık analiz
- [x] Geçmiş haftaları görüntüleme
- [x] Çevrimdışı cihaz içi veri saklama

## Politika ve gizlilik

- [ ] Gizlilik politikası herkese açık HTTPS URL'de yayınlandı
- [ ] Gizlilik politikası uygulama içinden erişilebilir hale getirildi
- [ ] Data Safety formu son release build ile doğrulandı
- [ ] İçerik derecelendirme anketi tamamlandı
- [ ] Hedef kitle seçimi tamamlandı; çocuklara özel tasarlanmadığı belirtildi
- [ ] Reklam içerip içermediği doğru beyan edildi (mevcut sürüm: hayır)

## Mağaza varlıkları

- [ ] 512 × 512 PNG Play Store simgesi
- [ ] 1024 × 500 feature graphic
- [ ] En az 2 telefon ekran görüntüsü; öneri 5–7 adet
- [ ] Kısa açıklama ≤80 karakter
- [ ] Tam açıklama ≤4000 karakter
- [ ] Destek e-postası: `ridvanozdemir.dev@gmail.com`

### Önerilen screenshot sırası

1. Ana sayfa
2. Yapılacaklar listesi
3. Yeni görev ekleme ve tekrar/periyot ayarları
4. Hedefler ve kısa/orta/uzun vadeli gruplama
5. Hatırlatma ayarı
6. Haftalık rapor
7. Geçmiş hafta görünümü

## Test ve yayın

- [ ] Release AAB Internal Testing'e yüklendi
- [ ] Play Store üzerinden en az iki gerçek cihazda kurulum testi yapıldı
- [ ] Bildirim izni ve hatırlatma akışı Android 13+ cihazda test edildi
- [ ] Cihaz yeniden başlatıldıktan sonra hatırlatma yeniden planlama test edildi
- [ ] Closed Testing tester listesi hazırlandı
- [ ] Yeni kişisel geliştirici hesabı gereksinimi geçerliyse en az 12 tester 14 gün kesintisiz opted-in kaldı
- [ ] Production access başvurusu tamamlandı
- [ ] Production release review'a gönderildi

## Mevcut en önemli teknik fark

Şu anki GitHub Actions yalnızca debug APK oluşturuyor. Google Play'deki yeni uygulama yayını için imzalı Android App Bundle (`.aab`) üretimine geçilmelidir. Bu işlem release signing anahtarını GitHub Secrets üzerinden güvenli biçimde kullanacak ayrı bir workflow ile yapılmalıdır.
