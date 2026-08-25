# Planım — Gizlilik Politikası ve Data Safety Taslağı

Son güncelleme: 25 Ağustos 2026

> Bu belge üretim öncesi taslaktır. Google Play gönderiminden önce son release build ile tekrar doğrulanmalıdır.

## Gizlilik Politikası Taslağı

Planım, görevlerinizi ve hedeflerinizi cihazınızda düzenlemenizi sağlayan çevrimdışı bir Android uygulamasıdır. Geliştirici iletişim adresi: `ridvanozdemir.dev@gmail.com`.

### Veri toplama

Mevcut sürümde Planım:

- Kullanıcı hesabı oluşturmaz.
- E-posta, telefon numarası veya kimlik bilgisi istemez.
- Görev, hedef ve rapor verilerini geliştiriciye ait bir sunucuya göndermez.
- Reklam veya analiz SDK'sı kullanmaz.
- İnternet üzerinden kullanıcı davranışı takibi yapmaz.

Görevler, hedefler, tamamlanma durumları ve haftalık rapor verileri cihaz üzerinde saklanır.

### Bildirimler

Planım isteğe bağlı görev hatırlatmaları için Android bildirim izni isteyebilir. Bildirim izni yalnızca cihaz üzerinde hatırlatma göstermek amacıyla kullanılır.

Uygulama, telefon yeniden başlatıldıktan sonra hatırlatmaları yeniden planlamak için Android'in `BOOT_COMPLETED` sistem olayını kullanabilir.

### Android sistem yedekleri

Uygulamanın Android yedekleme özelliği cihaz ve Google hesabı ayarlarına bağlı olarak uygulama verilerinin Android sistem yedeklerine dahil edilmesine izin verebilir. Bu işlem geliştiricinin kendi sunucusuna veri toplaması anlamına gelmez ve Android/Google hesap yedekleme ayarları tarafından yönetilir.

### Veri paylaşımı

Planım mevcut sürümde kullanıcı verilerini geliştirici sunucusuna toplamadığı için bu verileri üçüncü taraflara satmaz veya ticari amaçla paylaşmaz.

### Veri silme

Planım kullanıcı hesabı oluşturmaz. Kullanıcı uygulamadaki kayıtları uygulama içinden silebilir. Uygulamanın kaldırılması, Android sistem yedekleme davranışı hariç, cihazdaki uygulama verilerini kaldırır. Android yedekleri kullanıcı tarafından Google/Android yedekleme ayarlarından yönetilebilir.

### Çocukların gizliliği

Planım çocuklara özel olarak tasarlanmış bir uygulama değildir. Genel kişisel planlama ve verimlilik amacıyla tasarlanmıştır.

### Değişiklikler

Uygulamaya ileride çevrimiçi hesap, senkronizasyon, analiz veya başka bir veri toplama özelliği eklenirse bu politika ve Google Play Data Safety beyanı güncellenecektir.

### İletişim

Gizlilik ile ilgili sorular için: `ridvanozdemir.dev@gmail.com`

---

## Google Play Data Safety Taslağı

Mevcut uygulama mimarisine göre önerilen beyan:

- Uygulama geliştiriciye kullanıcı verisi topluyor mu? **Hayır**
- Kullanıcı verisi üçüncü taraflarla paylaşılıyor mu? **Hayır**
- Kullanıcı hesabı oluşturuluyor mu? **Hayır**
- Veriler yalnızca cihaz üzerinde mi tutuluyor? **Evet**

### İzinler

| Android izni / özelliği | Kullanım amacı |
|---|---|
| `POST_NOTIFICATIONS` | Opsiyonel görev hatırlatmaları |
| `RECEIVE_BOOT_COMPLETED` | Cihaz yeniden başladığında hatırlatmaları yeniden planlamak |

Bu izinler kullanıcı verisini geliştirici sunucusuna göndermek için kullanılmaz.

### Üretim öncesi tekrar kontrol

- Release build'de INTERNET izni veya çevrimiçi SDK eklenmediği doğrulanmalı.
- Analytics, Crashlytics, reklam SDK'sı veya bulut senkronizasyonu eklenirse Data Safety formu yeniden hazırlanmalı.
- Android sistem yedekleme davranışı gizlilik politikasıyla uyumlu tutulmalı.
