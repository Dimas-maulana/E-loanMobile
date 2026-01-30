# Android CLI Run Guide (Windows - PowerShell)

> [!IMPORTANT]
> Karena `adb` belum terdaftar di Environment Path Windows Anda, Anda harus menggunakan path lengkap atau simbol `&` di PowerShell untuk menjalankannya.

## 1. Cek Perangkat Terhubung
Copy dan paste perintah ini di terminal:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
```

## 2. Build & Install
Pastikan Anda berada di folder root project (`EloanMust`), lalu jalankan:
```powershell
.\gradlew.bat installDebug
```
*(Gunakan `.\` di depan `gradlew` agar dikenali oleh PowerShell)*

## 3. Jalankan Aplikasi
Setelah install selesai, jalankan perintah ini untuk membuka aplikasi:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.eloanmust/.MainActivity
```

## 4. Tips Tambahan
Jika Anda ingin mengetik `adb` saja (tanpa path panjang), Anda perlu menambahkan folder `platform-tools` ke **Environment Variables** Windows Anda:
1. Cari "Edit the system environment variables" di Start Menu.
2. Klik **Environment Variables**.
3. Di bawah "User variables", pilih **Path** dan klik **Edit**.
4. Klik **New** dan masukkan: `%LOCALAPPDATA%\Android\Sdk\platform-tools`
5. Klik OK dan **Restart Terminal/VS Code** Anda.

## 4. Troubleshooting & Log (Opsional)
- **Hapus Build Sebelumnya (Pembersihan):**
  ```powershell
  .\gradlew.bat clean
  ```
- **Lihat Log Aplikasi (Logcat):**
  ```powershell
  & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" logcat *:E
  ```
  *(Menampilkan log error saja)*

- **Uninstall Aplikasi:**
  ```powershell
  & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" uninstall com.example.eloanmust
  ```
