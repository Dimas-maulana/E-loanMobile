# E-Loan Must - Android Application

## 📱 Overview

E-Loan Must adalah aplikasi Android untuk **Loan Management System** yang diperuntukkan khusus untuk **NASABAH (USER)**. Aplikasi ini memungkinkan nasabah untuk:

- Melakukan registrasi dan login
- Melengkapi profil (KYC) termasuk upload KTP
- Melihat produk pinjaman (Plafond)
- Melakukan simulasi pinjaman
- Mengajukan pinjaman
- Memonitor status pengajuan pinjaman
- Menerima notifikasi push untuk update status

> **Catatan**: Proses administrasi (Review, Approval, Disbursement) dilakukan melalui **WEB ADMIN** dan bukan bagian dari aplikasi Android ini.

---

## 🏗️ Architecture

Aplikasi ini dibangun menggunakan:

- **Feature-First Architecture** - Setiap fitur memiliki folder terpisah
- **Clean Architecture** - Pemisahan ketat antara layers
- **MVVM Pattern** - Separation of concerns untuk UI

### Layer Structure

```
UI (Composable) → ViewModel → UseCase → Repository Interface → Repository Impl → DataSource
```

### Domain Layer Rules
- ❌ Tidak boleh import Retrofit
- ❌ Tidak boleh import Room
- ❌ Tidak boleh import Android SDK
- ✅ Pure Kotlin/Java only

---

## 📁 Folder Structure

```
app/src/main/java/com/example/eloanmust/
├── EloanMustApplication.kt      # Application class
├── MainActivity.kt               # Main entry point
│
├── core/                          # Shared Infrastructure
│   ├── di/                        # Hilt Dependency Injection
│   │   ├── AppModule.kt
│   │   ├── NetworkModule.kt
│   │   ├── DatabaseModule.kt
│   │   └── RepositoryModule.kt
│   ├── network/                   # Networking
│   │   ├── ApiService.kt
│   │   ├── ApiResponse.kt
│   │   ├── AuthInterceptor.kt
│   │   ├── SafeApiCall.kt
│   │   └── NetworkMonitor.kt
│   ├── database/                  # Room Database
│   │   ├── AppDatabase.kt
│   │   ├── Converters.kt
│   │   └── dao/
│   ├── datastore/                 # DataStore Preferences
│   │   └── TokenManager.kt
│   ├── firebase/                  # Firebase Services
│   │   ├── FcmTokenManager.kt
│   │   └── EloanFirebaseMessagingService.kt
│   ├── designsystem/              # Design System
│   │   └── theme/
│   │       ├── Color.kt
│   │       ├── Typography.kt
│   │       ├── Shape.kt
│   │       └── Theme.kt
│   └── common/                    # Common Utilities
│       ├── Resource.kt
│       ├── UiState.kt
│       ├── UiEvent.kt
│       ├── Constants.kt
│       ├── DispatcherProvider.kt
│       └── Extensions.kt
│
├── feature/                       # Feature Modules
│   ├── auth/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── usecase/
│   │   ├── data/
│   │   │   ├── dto/
│   │   │   ├── mapper/
│   │   │   ├── datasource/
│   │   │   └── repository/
│   │   └── presentation/
│   │       ├── login/
│   │       ├── register/
│   │       └── forgot_password/
│   │
│   ├── loan/
│   │   ├── domain/
│   │   ├── data/
│   │   │   ├── local/          # Room Entity
│   │   │   └── ...
│   │   └── presentation/
│   │
│   ├── profile/
│   ├── product/
│   └── notification/
│
└── navigation/
    ├── Screen.kt
    └── NavGraph.kt
```

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin |
| **UI** | Jetpack Compose (Material3) |
| **Architecture** | Clean Architecture + MVVM |
| **DI** | Hilt |
| **Database** | Room |
| **Networking** | Retrofit + OkHttp |
| **Async** | Kotlin Coroutines & Flow |
| **Navigation** | Navigation Compose |
| **Image Loading** | Coil |
| **Push Notification** | Firebase Cloud Messaging |
| **Logging** | Timber |
| **Debug** | Chucker |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 35
- Backend server running at `http://localhost:8081`

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd EloanMust
   ```

2. **Firebase Setup**
   - Buat project di [Firebase Console](https://console.firebase.google.com/)
   - Tambahkan aplikasi Android dengan package name `com.example.eloanmust`
   - Download `google-services.json` dan letakkan di folder `app/`
   - Enable Cloud Messaging di Firebase Console

3. **Backend Configuration**
   - Pastikan backend berjalan di `http://localhost:8081`
   - Untuk emulator Android, gunakan `http://10.0.2.2:8081`
   - Untuk device fisik, gunakan IP address komputer

4. **Build & Run**
   ```bash
   ./gradlew assembleDebug
   ```

### Build Variants

| Variant | BASE_URL | Features |
|---------|----------|----------|
| Debug | `http://10.0.2.2:8081/` | Logging, Chucker |
| Release | `https://api.eloanmust.com/` | ProGuard, Minified |

---

## 📲 Features

### 1. Authentication
- ✅ Register User
- ✅ Login User (with FCM Token)
- ✅ Forgot Password
- ✅ Reset Password
- ✅ Logout

### 2. Profile (KYC)
- ✅ View Profile
- ✅ Update Profile
- ✅ Upload KTP
- ✅ Check Profile Status

### 3. Products (Plafond)
- ✅ List Products
- ✅ Dynamic Product Detection

### 4. Loan
- ✅ Loan Simulation
- ✅ Apply Loan
- ✅ Loan History (Offline-First)
- ✅ Loan Detail

### 5. Notifications
- ✅ Push Notifications (FCM)
- ✅ Notification List
- ✅ Mark as Read

---

## 🔔 Push Notification Flow

```
┌─────────────────────────────────────────────────┐
│                  APP START                       │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│            Generate FCM Token                    │
│         (FcmTokenManager.getToken())            │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│             Save Token Locally                   │
│         (TokenManager.saveFcmToken())           │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                  USER LOGIN                      │
│     Send: username, password, fcmToken          │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│       Backend saves FCM Token to DB             │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│     Backend sends push notification             │
│     when loan status changes                    │
└─────────────────────────────────────────────────┘
```

### Notification Types
- `LOAN_SUBMITTED` - Pengajuan berhasil
- `LOAN_REVIEWED` - Selesai direview
- `LOAN_APPROVED` - Disetujui
- `LOAN_REJECTED` - Ditolak
- `LOAN_DISBURSED` - Dana cair

---

## 📦 Offline-First Strategy

Aplikasi mengimplementasikan strategi **Offline-First** untuk data pinjaman:

1. **Emit data dari Room terlebih dahulu** (jika ada)
2. **Fetch data dari API**
3. **Update Room dengan data baru**
4. **Emit data terbaru**

```kotlin
override fun getMyLoans(): Flow<Resource<List<Loan>>> = flow {
    // 1. Emit cached data first
    val cachedLoans = localDataSource.getLoansByUserIdSync(userId)
    if (cachedLoans.isNotEmpty()) {
        emit(Resource.Success(cachedLoans.map { it.toDomain() }))
    }
    
    // 2. Fetch from remote
    val remoteResult = safeApiCall { remoteDataSource.getMyLoans() }
    
    // 3. Update local cache
    when (remoteResult) {
        is Resource.Success -> {
            localDataSource.insertLoans(remoteResult.data.map { it.toEntity() })
            emit(Resource.Success(remoteResult.data.map { it.toDomain() }))
        }
        // ...
    }
}
```

---

## 🎨 Design Theme

Aplikasi menggunakan tema **Finance Modern** dengan warna dominan:

| Color | Hex | Usage |
|-------|-----|-------|
| **Gold** | `#FFD700` | Primary |
| **Amber** | `#F5A623` | Accent |
| **Black** | `#1A1A1A` | Secondary |
| **White** | `#FFFFFF` | Background |

### Loan Status Colors
| Status | Color |
|--------|-------|
| PENDING_REVIEW | Orange |
| REVIEWED | Blue |
| APPROVED | Green |
| REJECTED | Red |
| DISBURSED | Purple |

---

## 🔧 API Endpoints

Base URL: `http://localhost:8081`

### Authentication
```
POST /api/auth/register
POST /api/auth/login      (with fcmToken!)
POST /api/auth/logout
POST /api/auth/forgot-password
POST /api/auth/reset-password
```

### Profile
```
GET  /api/profile
PUT  /api/profile
GET  /api/profile/ktp
GET  /api/profile/status
```

### Plafond
```
GET  /api/plafonds
GET  /api/plafonds/{id}
GET  /api/plafonds/detect?amount=xxx
```

### Loans
```
POST /api/loans/simulate
POST /api/loans
GET  /api/loans
GET  /api/loans/{id}
```

### Notifications
```
GET  /api/notifications
GET  /api/notifications/unread
GET  /api/notifications/count
PUT  /api/notifications/{id}/read
PUT  /api/notifications/read-all
```

---

## 📝 Notes

1. **JWT Token** disimpan menggunakan DataStore
2. **FCM Token** dikirim saat login dan di-refresh otomatis
3. **ProGuard** sudah dikonfigurasi untuk Retrofit, Room, dan Firebase
4. **Chucker** hanya aktif di debug build
5. **Cleartext traffic** diizinkan untuk development (disable di production)

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

*Built with ❤️ using Jetpack Compose and Clean Architecture*
