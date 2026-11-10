# 📦 Paket Komponen: BCA Promo Animated Banner (Finish Edition)

Paket ini berisi komponen banner promosi dashboard m-BCA 1 kolom mandiri (*reusable*) lengkap dengan 2 iklan animasi bergantian ("The New Gacha Berhadiah" & "Total 600.000+ Hadiah Siap Dimenangkan!").

---

## 📁 Struktur File dalam Paket

```text
EXPORT_BCA_PROMO_BANNER/
├── components/
│   └── BcaPromoBanner.kt          <- Kode komponen Jetpack Compose mandiri
├── drawable/
│   ├── banner_base_static.png     <- Layer 1: Frame biru BCA + Tombol "Main & Dapatkan Hadiah >"
│   ├── banner_fg_gacha.png        <- Layer 2: Iklan 1 ("The New Gacha Berhadiah")
│   └── banner_fg_600k.png         <- Layer 2: Iklan 2 ("Total 600.000+ Hadiah Siap Dimenangkan!")
└── README_CARA_PAKAI.md           <- Panduan integrasi & kata perintah cepat
```

---

## ⚡ Kata Perintah Cepat (Trigger Command) untuk AI / Asisten

Kapan pun Anda membuat aplikasi baru atau ingin memasang banner ini lagi, cukup ketik salah satu perintah berikut:

> **Perintah Rekomendasi Utama:**
> 💬 **"Pasang komponen BcaPromoBanner dari folder EXPORT_BCA_PROMO_BANNER"**

Atau bisa juga dengan kata kunci santai:
> 💬 **"Pasang banner promo animasi 600k dan gacha BCA"**  
> 💬 **"Ambil banner iklan 2 sembulan BCA yang sudah jadi"**

Dengan perintah di atas, asisten AI akan langsung otomatis mengenali struktur 2 layer, menyalin ketiga drawable, dan memanggil fungsi `BcaPromoBanner()` tanpa perlu setting ulang dari awal.

---

## 🚀 Cara Memasang di Aplikasi Lain (Manual)

### Langkah 1: Salin Gambar ke Proyek Baru
Salin 3 file gambar dari folder `drawable/` ke folder `app/src/main/res/drawable/` aplikasi tujuan:
- `banner_base_static.png`
- `banner_fg_gacha.png`
- `banner_fg_600k.png`

### Langkah 2: Salin File Komponen
Salin file `BcaPromoBanner.kt` ke package UI aplikasi Anda (misal `com.example.app.ui.components`).  
*Sesuaikan baris `package ...` dan import `R` sesuai nama package aplikasi tujuan jika berbeda.*

### Langkah 3: Panggil Komponen di Layar / Dashboard
Di mana pun Anda ingin menampilkan banner ini, cukup panggil satu baris:

```kotlin
import com.bca.mobile.ui.components.BcaPromoBanner // sesuaikan package Anda

// Contoh pemakaian di dalam Column / Dashboard:
BcaPromoBanner(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
    onClick = {
        // Aksi ketika banner diklik (misal buka halaman promo/gacha)
    }
)
```

---

## 🎬 Rincian Mekanisme Animasi (Double Sembulan)

- **Rasio Banner:** `636 : 84` (responsif mengikuti lebar layar smartphone).
- **Layer 1 (Statis):** Base frame gradien biru BCA + tombol interaktif `"Main & Dapatkan Hadiah >"`.
- **Layer 2 (Dinamis Animasi):**
  1. **Fade In (500 ms):** Skala membesar dari `0.88` ke `1.00`, transparansi `0%` ke `100%`.
  2. **Sembulan ke-1 (4.000 ms):** Zoom In `1.00` ➔ `1.035` (2.000 ms) & Zoom Out `1.035` ➔ `1.00` (2.000 ms).
  3. **Sembulan ke-2 (4.000 ms):** Zoom In `1.00` ➔ `1.035` (2.000 ms) & Zoom Out `1.035` ➔ `1.00` (2.000 ms).
  4. **Fade Out (500 ms):** Skala mengecil dari `1.00` ke `0.88`, transparansi `100%` ke `0%`.
  5. **Jeda Transisi (150 ms):** Pergantian index iklan secara halus ke iklan berikutnya.
  6. **Looping:** Berulang tanpa henti bergantian antara Iklan Gacha dan Iklan 600.000+.
