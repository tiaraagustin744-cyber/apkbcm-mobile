import os
import shutil

src_components = r"F:\Bca Mobile Android\app\src\main\java\com\bca\mobile\ui\components\BcaPromoBanner.kt"
src_drawable_base = r"F:\Bca Mobile Android\app\src\main\res\drawable\banner_base_static.png"
src_drawable_gacha = r"F:\Bca Mobile Android\app\src\main\res\drawable\banner_fg_gacha.png"
src_drawable_600k = r"F:\Bca Mobile Android\app\src\main\res\drawable\banner_fg_600k.png"

desktop_dest = r"C:\Users\Administrator\Desktop\iklan banner"
flashdisk_dest = r"F:\iklan banner"

readme_content = """# 📦 Paket Komponen: BCA Promo Animated Banner (Drop-in Ready)

Paket ini berisi komponen banner promosi dashboard m-BCA 1 kolom mandiri (*reusable*) lengkap dengan 2 iklan animasi bergantian ("The New Gacha Berhadiah" & "Total 600.000+ Hadiah Siap Dimenangkan!").
Animasi telah dilengkapi fitur **mundur menciut ke belakang (750 ms)** dan **masuk dari kecil mengembang menyembul ke depan (680 ms)** dengan ritme tenang & berwibawa.

---

## 📁 Struktur File dalam Paket Ini

```text
iklan banner/
├── BcaPromoBanner.kt              <- Komponen Jetpack Compose mandiri siap pakai
├── components/
│   └── BcaPromoBanner.kt          <- File komponen dalam folder components
├── res/drawable/
│   ├── banner_base_static.png     <- Layer 1: Frame biru BCA + Tombol "Main & Dapatkan Hadiah >"
│   ├── banner_fg_gacha.png        <- Layer 2: Stiker Iklan 1 ("The New Gacha Berhadiah")
│   └── banner_fg_600k.png         <- Layer 2: Stiker Iklan 2 ("Total 600.000+ Hadiah Siap Dimenangkan!")
├── drawable/                      <- Salinan langsung folder drawable
│   ├── banner_base_static.png
│   ├── banner_fg_gacha.png
│   └── banner_fg_600k.png
├── CARA_PAKAI_CEPAT.txt           <- Petunjuk singkat teks biasa (bisa dibuka langsung di Notepad)
└── README_PANDUAN_LENGKAP.md      <- Panduan lengkap integrasi
```

---

## ⚡ Kata Perintah Cepat untuk Asisten AI (ChatGPT / Gemini / Copilot / Claude)

Kapan pun Anda membuat aplikasi baru atau ingin memasang banner ini lagi, cukup ketik salah satu perintah berikut:

> 💬 **"Pasang komponen BcaPromoBanner dari folder 'iklan banner'"**  
> 💬 **"Pasang banner promo animasi 600k dan gacha BCA dari desktop/flashdisk saya"**

---

## 🚀 Cara Memasang Manual di Aplikasi Baru (3 Langkah Mudah)

### Langkah 1: Salin Gambar ke Proyek Baru
Salin 3 file gambar dari folder `drawable/` ke folder `app/src/main/res/drawable/` aplikasi baru Anda:
- `banner_base_static.png`
- `banner_fg_gacha.png`
- `banner_fg_600k.png`

### Langkah 2: Salin File Komponen
Salin file `BcaPromoBanner.kt` ke dalam folder kode UI aplikasi Anda (misal `com.example.app.ui.components`).  
*Catatan: Ubah baris pertama `package ...` dan import `R` sesuai dengan nama package aplikasi baru Anda jika berbeda.*

### Langkah 3: Panggil Komponen di Layar
Di layar mana pun (misal Dashboard / Home), panggil fungsi berikut:

```kotlin
import com.bca.mobile.ui.components.BcaPromoBanner // Sesuaikan package Anda

// Contoh pemanggilan di dalam Column / LazyColumn:
BcaPromoBanner(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
    onClick = {
        // Aksi ketika banner diklik (misal navigasi ke halaman promo / info)
    }
)
```

---

## 🎬 Spesifikasi Animasi

1. **Rasio Tampilan:** `636 : 84` (responsif otomatis menyesuaikan lebar layar smartphone).
2. **Layer 1 (Statis):** Base frame gradien biru BCA + tombol interaktif `"Main & Dapatkan Hadiah >"`.
3. **Layer 2 (Outgoing Transisi):** Iklan lama mundur menciut mengecil ke belakang ($1.00 \to 0.50$, $750\text{ ms}$).
4. **Layer 3 (Incoming Transisi):** Iklan baru masuk dari kecil ($0.50$), mengembang menyembul ke depan ($0.50 \to 1.025 \to 1.00$, $680\text{ ms}$).
5. **Denyutan Kalem (2x Sembulan):** Zoom In `1.00` ➔ `1.035` ($2.000\text{ ms}$) & Zoom Out `1.035` ➔ `1.00` ($2.000\text{ ms}$) berturut-turut selama 8 detik sebelum berpindah iklan.
"""

cara_cepat_txt = """========================================================================
PANDUAN CEPAT MEMASANG IKLAN BANNER PROMO BCA KE APLIKASI LAIN
========================================================================

Cukup 3 langkah mudah:

1. COPY 3 GAMBAR:
   Copy semua file dari folder 'drawable':
   - banner_base_static.png
   - banner_fg_gacha.png
   - banner_fg_600k.png
   Paste ke dalam folder proyek baru Anda di:
   --> app/src/main/res/drawable/

2. COPY KODE KOMPONEN:
   Copy file 'BcaPromoBanner.kt'
   Paste ke folder kode UI Compose Anda (misal: ui/components/).
   *Pastikan baris 'import com.bca.mobile.R' disesuaikan dengan package R proyek baru Anda.

3. PANGGIL DI LAYAR:
   BcaPromoBanner(
       modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
       onClick = { /* aksi klik banner */ }
   )

SELESAI! Banner akan otomatis beranimasi 2x sembulan denyut, mundur menciut
ke belakang, dan iklan berikutnya mengembang menyembul ke depan tanpa perlu setting apa pun lagi.
========================================================================
"""

for target_dir in [desktop_dest, flashdisk_dest]:
    os.makedirs(target_dir, exist_ok=True)
    os.makedirs(os.path.join(target_dir, "components"), exist_ok=True)
    os.makedirs(os.path.join(target_dir, "drawable"), exist_ok=True)
    os.makedirs(os.path.join(target_dir, "res", "drawable"), exist_ok=True)

    # Copy component file directly at root and in components/
    shutil.copyfile(src_components, os.path.join(target_dir, "BcaPromoBanner.kt"))
    shutil.copyfile(src_components, os.path.join(target_dir, "components", "BcaPromoBanner.kt"))

    # Copy drawables in drawable/ and res/drawable/
    for img in [src_drawable_base, src_drawable_gacha, src_drawable_600k]:
        fname = os.path.basename(img)
        shutil.copyfile(img, os.path.join(target_dir, "drawable", fname))
        shutil.copyfile(img, os.path.join(target_dir, "res", "drawable", fname))

    # Write documentation
    with open(os.path.join(target_dir, "README_PANDUAN_LENGKAP.md"), "w", encoding="utf-8") as f:
        f.write(readme_content)

    with open(os.path.join(target_dir, "CARA_PAKAI_CEPAT.txt"), "w", encoding="utf-8") as f:
        f.write(cara_cepat_txt)

    print(f"Successfully populated: {target_dir}")

print("All export operations completed successfully!")
