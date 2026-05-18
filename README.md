# CinemaPE - Aplikasi Manajemen Film

CinemaPE adalah aplikasi Android yang dirancang untuk mengelola daftar film (CRUD) dengan fitur autentikasi pengguna dan penyimpanan favorit secara lokal. Aplikasi ini menggunakan integrasi API eksternal untuk sinkronisasi data film dan SharedPreferences untuk manajemen sesi serta data favorit yang unik untuk setiap pengguna.

## 🚀 Fitur Utama

- **Autentikasi Pengguna**: Fitur Registrasi dan Login untuk menjaga keamanan data pengguna.
- **Manajemen Film (CRUD)**:
    - **View**: Menampilkan daftar film terbaru dari server.
    - **Add**: Menambahkan data film baru (Judul, Deskripsi, Gambar, dll).
    - **Edit**: Memperbarui informasi film yang sudah ada.
    - **Delete**: Menghapus film dari server dan secara otomatis membersihkannya dari daftar favorit lokal.
- **Sistem Favorit (Saved)**: Simpan film favorit Anda ke daftar lokal. Data disimpan secara unik per-username menggunakan `SessionManager`.
- **Pencarian Real-time**: Mencari film berdasarkan judul langsung di halaman utama.

## 🛠️ Tech Stack

- **Bahasa**: Java
- **UI Framework**: Material Design Components & Jetpack Fragments
- **Networking**: [Fuel](https://github.com/kittinunf/fuel) (Kotlin HTTP Networking Library)
- **JSON Parsing**: [Gson](https://github.com/google/gson)
- **Image Loading**: [Glide](https://github.com/bumptech/glide)
- **Persistence**: SharedPreferences (Local Storage)
- **Architecture**: Model-View-Binding pattern
- **Backend API**: MockAPI.io

## 📂 Struktur Proyek

```text
com.kelompoklima.cinemape
├── API             # Integrasi Fuel HTTP & callback handling
├── Autentikasi     # Activity untuk Login & Register
├── CRUD            # Logika manipulasi data (Add, Edit, Delete fragments)
├── Model           # Data classes (POJO) untuk Movie & User
├── Session         # SessionManager untuk SharedPreferences & Local Logic
├── UI              # Fragment utama (Home, Saved, Detail) & Adapter
└── MainActivity    # Container utama aplikasi
```

## ⚙️ Cara Menjalankan

1. Clone repository ini.
2. Buka project di **Android Studio (Koala atau versi terbaru)**.
3. Tunggu proses **Gradle Sync** selesai.
4. Hubungkan perangkat Android atau gunakan Emulator.
5. Klik **Run 'app'**.

## 📝 Konfigurasi API

Aplikasi ini terhubung ke endpoint MockAPI. Jika ingin menggunakan API sendiri, Anda dapat mengubah `BASE_URL` di dalam file:
`app/src/main/java/com/kelompoklima/cinemape/API/ApiService.java`

---
**Kelompok Lima - Mobile Programming**
