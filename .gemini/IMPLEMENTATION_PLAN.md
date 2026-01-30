# E-Loan Must Android - Implementation Plan

## 📋 Project Overview

E-Loan Must adalah aplikasi Android untuk **Loan Management System** yang diperuntukkan khusus untuk **NASABAH (USER)**. Aplikasi ini dibangun menggunakan **Jetpack Compose** dengan **Clean Architecture** dan **Feature-First** structure.

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │   Screen    │ ─► │  ViewModel  │ ─► │   UiState   │      │
│  │ (Composable)│    │ (StateFlow) │    │   UiEvent   │      │
│  └─────────────┘    └─────────────┘    └─────────────┘      │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                            │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │   UseCase   │ ─► │  Repository │ ─► │   Entity    │      │
│  │             │    │ (Interface) │    │   (Model)   │      │
│  └─────────────┘    └─────────────┘    └─────────────┘      │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │ Repository  │ ─► │ DataSource  │ ─► │  DTO/Entity │      │
│  │   (Impl)    │    │ Remote/Local│    │   Mapper    │      │
│  └─────────────┘    └─────────────┘    └─────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Target Folder Structure

```
app/src/main/java/com/example/eloanmust/
├── EloanMustApplication.kt
├── MainActivity.kt
│
├── core/                           # Shared Infrastructure
│   ├── di/                         # Dependency Injection
│   │   ├── NetworkModule.kt
│   │   ├── DatabaseModule.kt
│   │   ├── FirebaseModule.kt
│   │   └── RepositoryModule.kt
│   │
│   ├── network/                    # Networking
│   │   ├── ApiService.kt
│   │   ├── RetrofitClient.kt
│   │   ├── AuthInterceptor.kt
│   │   ├── ApiResponse.kt
│   │   └── NetworkMonitor.kt
│   │
│   ├── database/                   # Local Database
│   │   ├── AppDatabase.kt
│   │   ├── TypeConverters.kt
│   │   └── dao/
│   │       ├── UserDao.kt
│   │       ├── LoanDao.kt
│   │       ├── NotificationDao.kt
│   │       └── PlafondDao.kt
│   │
│   ├── datastore/                  # DataStore Preferences
│   │   ├── UserPreferences.kt
│   │   └── TokenManager.kt
│   │
│   ├── firebase/                   # Firebase Services
│   │   ├── FirebaseMessagingService.kt
│   │   └── FcmTokenManager.kt
│   │
│   ├── designsystem/               # Design System
│   │   ├── theme/
│   │   │   ├── Theme.kt
│   │   │   ├── Color.kt
│   │   │   ├── Typography.kt
│   │   │   └── Shape.kt
│   │   └── components/
│   │       ├── ELoanButton.kt
│   │       ├── ELoanTextField.kt
│   │       ├── ELoanCard.kt
│   │       ├── ELoanTopAppBar.kt
│   │       ├── ELoanBottomNavBar.kt
│   │       ├── LoadingIndicator.kt
│   │       └── ErrorDialog.kt
│   │
│   └── common/                     # Common Utilities
│       ├── Resource.kt             # Sealed class for API states
│       ├── UiState.kt
│       ├── UiEvent.kt
│       ├── DispatcherProvider.kt
│       ├── Extensions.kt
│       └── Constants.kt
│
├── feature/                        # Feature Modules
│   ├── auth/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   └── User.kt
│   │   │   ├── repository/
│   │   │   │   └── AuthRepository.kt
│   │   │   └── usecase/
│   │   │       ├── LoginUseCase.kt
│   │   │       ├── RegisterUseCase.kt
│   │   │       ├── LogoutUseCase.kt
│   │   │       └── ForgotPasswordUseCase.kt
│   │   ├── data/
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.kt
│   │   │   │   ├── LoginResponse.kt
│   │   │   │   ├── RegisterRequest.kt
│   │   │   │   └── RegisterResponse.kt
│   │   │   ├── mapper/
│   │   │   │   └── AuthMapper.kt
│   │   │   ├── datasource/
│   │   │   │   ├── AuthRemoteDataSource.kt
│   │   │   │   └── AuthLocalDataSource.kt
│   │   │   └── repository/
│   │   │       └── AuthRepositoryImpl.kt
│   │   └── presentation/
│   │       ├── login/
│   │       │   ├── LoginScreen.kt
│   │       │   └── LoginViewModel.kt
│   │       ├── register/
│   │       │   ├── RegisterScreen.kt
│   │       │   └── RegisterViewModel.kt
│   │       └── forgot_password/
│   │           ├── ForgotPasswordScreen.kt
│   │           └── ForgotPasswordViewModel.kt
│   │
│   ├── home/
│   │   ├── domain/
│   │   ├── data/
│   │   └── presentation/
│   │       ├── HomeScreen.kt
│   │       └── HomeViewModel.kt
│   │
│   ├── profile/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   └── CustomerProfile.kt
│   │   │   ├── repository/
│   │   │   │   └── ProfileRepository.kt
│   │   │   └── usecase/
│   │   │       ├── GetProfileUseCase.kt
│   │   │       ├── UpdateProfileUseCase.kt
│   │   │       ├── UploadKtpUseCase.kt
│   │   │       └── GetProfileStatusUseCase.kt
│   │   ├── data/
│   │   │   ├── dto/
│   │   │   ├── mapper/
│   │   │   ├── datasource/
│   │   │   └── repository/
│   │   └── presentation/
│   │       ├── ProfileScreen.kt
│   │       ├── ProfileViewModel.kt
│   │       └── components/
│   │
│   ├── product/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   └── Plafond.kt
│   │   │   ├── repository/
│   │   │   │   └── PlafondRepository.kt
│   │   │   └── usecase/
│   │   │       ├── GetPlafondsUseCase.kt
│   │   │       └── DetectPlafondUseCase.kt
│   │   ├── data/
│   │   └── presentation/
│   │       ├── ProductListScreen.kt
│   │       └── ProductViewModel.kt
│   │
│   ├── loan/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Loan.kt
│   │   │   │   └── LoanSimulation.kt
│   │   │   ├── repository/
│   │   │   │   └── LoanRepository.kt
│   │   │   └── usecase/
│   │   │       ├── SimulateLoanUseCase.kt
│   │   │       ├── ApplyLoanUseCase.kt
│   │   │       ├── GetMyLoansUseCase.kt
│   │   │       └── GetLoanDetailUseCase.kt
│   │   ├── data/
│   │   │   ├── dto/
│   │   │   ├── mapper/
│   │   │   ├── datasource/
│   │   │   │   ├── LoanRemoteDataSource.kt
│   │   │   │   └── LoanLocalDataSource.kt
│   │   │   ├── local/
│   │   │   │   └── LoanEntity.kt
│   │   │   └── repository/
│   │   │       └── LoanRepositoryImpl.kt
│   │   └── presentation/
│   │       ├── apply/
│   │       │   ├── LoanApplyScreen.kt
│   │       │   └── LoanApplyViewModel.kt
│   │       ├── simulation/
│   │       │   ├── LoanSimulationScreen.kt
│   │       │   └── LoanSimulationViewModel.kt
│   │       ├── history/
│   │       │   ├── LoanHistoryScreen.kt
│   │       │   └── LoanHistoryViewModel.kt
│   │       └── detail/
│   │           ├── LoanDetailScreen.kt
│   │           └── LoanDetailViewModel.kt
│   │
│   ├── notification/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   └── Notification.kt
│   │   │   ├── repository/
│   │   │   │   └── NotificationRepository.kt
│   │   │   └── usecase/
│   │   │       ├── GetNotificationsUseCase.kt
│   │   │       ├── MarkAsReadUseCase.kt
│   │   │       └── GetUnreadCountUseCase.kt
│   │   ├── data/
│   │   └── presentation/
│   │       ├── NotificationScreen.kt
│   │       └── NotificationViewModel.kt
│   │
│   └── transaction/
│       ├── domain/
│       ├── data/
│       └── presentation/
│
└── navigation/
    ├── NavGraph.kt
    ├── Screen.kt
    └── BottomNavItem.kt
```

---

## 📦 Dependencies (libs.versions.toml)

```toml
[versions]
agp = "8.13.2"
kotlin = "2.0.21"
coreKtx = "1.17.0"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
lifecycleRuntimeKtx = "2.8.7"
activityCompose = "1.12.2"
composeBom = "2024.12.01"
navigationCompose = "2.8.5"

# Hilt
hilt = "2.51.1"
hiltNavigationCompose = "1.2.0"

# Retrofit & OkHttp
retrofit = "2.11.0"
okhttp = "4.12.0"
gsonConverter = "2.11.0"

# Room
room = "2.6.1"

# DataStore
datastore = "1.1.1"

# Firebase
firebaseBom = "33.7.0"

# Coil (Image Loading)
coil = "2.7.0"

# Accompanist
accompanist = "0.36.0"

# Splash Screen
splashscreen = "1.0.1"

# Chucker (Debug)
chucker = "4.1.0"
```

---

## 🎨 Design System Theme

### Color Palette (Finance Modern - Gold/Black)
- **Primary**: Gold (#FFD700, #FFC107)
- **Secondary**: Dark (#1A1A1A, #2D2D2D)
- **Background**: Off-white (#FAFAFA), Dark (#121212)
- **Surface**: White (#FFFFFF), Dark Surface (#1E1E1E)
- **Accent**: Warm Gold (#F5A623)
- **Error**: Red (#E53935)
- **Success**: Green (#43A047)

---

## 📋 Implementation Steps

### Phase 1: Project Setup & Core Infrastructure ✅
- [x] Step 1.1: Update `libs.versions.toml` with all dependencies
- [x] Step 1.2: Update `build.gradle.kts` (project & app level)
- [x] Step 1.3: Setup Hilt (Application class, modules)
- [x] Step 1.4: Create Design System (Theme, Colors, Typography)
- [x] Step 1.5: Create Reusable Components (Button, TextField, Card)
- [x] Step 1.6: Setup Room Database (AppDatabase, DAOs)
- [x] Step 1.7: Setup Retrofit & Network Layer (ApiService, Interceptors)
- [x] Step 1.8: Setup DataStore for Token Management
- [x] Step 1.9: Create Common utilities (Resource, UiState, Extensions)

### Phase 2: Firebase Integration ✅
- [x] Step 2.1: Configure Firebase project (google-services.json)
- [x] Step 2.2: Create FirebaseMessagingService
- [x] Step 2.3: Create FcmTokenManager
- [x] Step 2.4: Handle foreground/background notifications

### Phase 3: Feature - Authentication ✅
- [x] Step 3.1: Auth Domain Layer (Model, Repository Interface, UseCases)
- [x] Step 3.2: Auth Data Layer (DTOs, Mapper, DataSource, Repository Impl)
- [x] Step 3.3: Login Screen & ViewModel
- [x] Step 3.4: Register Screen & ViewModel
- [x] Step 3.5: Forgot Password Screen & ViewModel

### Phase 4: Feature - Home & Navigation ✅
- [x] Step 4.1: Setup Navigation Graph
- [x] Step 4.2: Create Bottom Navigation
- [x] Step 4.3: Home Screen UI
- [x] Step 4.4: Home ViewModel with dynamic data

### Phase 5: Feature - Profile (KYC) ✅
- [x] Step 5.1: Profile Domain Layer
- [x] Step 5.2: Profile Data Layer (with Room for offline)
- [x] Step 5.3: Profile Screen (View & Edit)
- [ ] Step 5.4: KTP Upload functionality (Not Implemented - requires Multipart)
- [x] Step 5.5: Profile Status validation

### Phase 6: Feature - Product (Plafond) ✅
- [x] Step 6.1: Product Domain Layer
- [x] Step 6.2: Product Data Layer (with Room cache)
- [x] Step 6.3: Product List Screen (Integrated in Home)
- [x] Step 6.4: Dynamic Plafond Detection

### Phase 7: Feature - Loan ✅
- [x] Step 7.1: Loan Domain Layer (Model, Repository, UseCases)
- [x] Step 7.2: Loan Data Layer (Offline-first with Room)
- [x] Step 7.3: Loan Simulation Screen
- [x] Step 7.4: Loan Application Screen
- [x] Step 7.5: Loan History Screen
- [x] Step 7.6: Loan Detail Screen (with status tracking)

### Phase 8: Feature - Notification ✅
- [x] Step 8.1: Notification Domain Layer
- [x] Step 8.2: Notification Data Layer (with Room)
- [x] Step 8.3: Notification Screen
- [x] Step 8.4: Integration with FCM

### Phase 9: Polish & Testing
- [x] Step 9.1: Error Handling & Loading States
- [x] Step 9.2: Animations & Transitions
- [ ] Step 9.3: Unit Tests for UseCases
- [ ] Step 9.4: Integration Tests
- [ ] Step 9.5: UI Tests with Compose

### Phase 10: Production Ready
- [ ] Step 10.1: ProGuard rules
- [x] Step 10.2: Build variants (debug/release)
- [ ] Step 10.3: App signing configuration
- [ ] Step 10.4: Performance optimization
- [ ] Step 10.5: Final documentation

---

## 🔑 API Endpoints Reference

Base URL: `http://localhost:8081`

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login (with fcmToken) |
| POST | `/api/auth/logout` | Logout |
| POST | `/api/auth/forgot-password` | Request password reset |
| POST | `/api/auth/reset-password` | Reset password with token |

### Profile
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/profile` | Get current user's profile |
| PUT | `/api/profile` | Update profile (KYC) |
| GET | `/api/profile/ktp` | Get KTP Image |
| GET | `/api/profile/status` | Check profile completion |

### Plafond (Product)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/plafonds` | Get active plafonds |
| GET | `/api/plafonds/{id}` | Get plafond by ID |
| GET | `/api/plafonds/detect?amount=xxx` | Detect product by amount |

### Loans
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/loans/simulate` | Simulate Loan |
| POST | `/api/loans` | Apply for Loan |
| GET | `/api/loans` | Get my loans |
| GET | `/api/loans/{id}` | Get loan by ID |

### Notifications
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notifications` | Get all notifications |
| GET | `/api/notifications/unread` | Get unread notifications |
| GET | `/api/notifications/count` | Get unread count |
| PUT | `/api/notifications/{id}/read` | Mark as read |
| PUT | `/api/notifications/read-all` | Mark all as read |

---

## 📱 Screen Flow

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Splash     │ ──► │   Landing    │ ──► │    Login     │
│   Screen     │     │   Screen     │     │   Screen     │
└──────────────┘     └──────────────┘     └──────────────┘
                           │                      │
                           ▼                      ▼
                     ┌──────────────┐     ┌──────────────┐
                     │   Register   │     │     Home     │
                     │   Screen     │     │   (Main)     │
                     └──────────────┘     └──────────────┘
                                                │
                    ┌───────────────────────────┼───────────────────────────┐
                    ▼                           ▼                           ▼
             ┌──────────────┐          ┌──────────────┐          ┌──────────────┐
             │   Products   │          │    Loans     │          │   Profile    │
             │    List      │          │   History    │          │   (KYC)      │
             └──────────────┘          └──────────────┘          └──────────────┘
                    │                         │
                    ▼                         ▼
             ┌──────────────┐          ┌──────────────┐
             │    Loan      │          │    Loan      │
             │  Simulation  │          │   Detail     │
             └──────────────┘          └──────────────┘
                    │
                    ▼
             ┌──────────────┐
             │    Apply     │
             │    Loan      │
             └──────────────┘
```

---

## 🔒 Security Considerations

1. **JWT Token Storage**: Menggunakan EncryptedSharedPreferences / DataStore
2. **Certificate Pinning**: Implementasi untuk production
3. **API Key Protection**: Menggunakan BuildConfig
4. **ProGuard**: Obfuscation untuk release build
5. **Biometric Auth**: Optional untuk fitur keamanan tambahan

---

## 📝 Notes & Assumptions

1. Backend sudah tersedia di `http://localhost:8081`
2. Firebase project sudah dikonfigurasi (`google-services.json` sudah ada)
3. Minimum SDK: 24 (Android 7.0)
4. Target SDK: 36 (Android 15)
5. Aplikasi hanya untuk role USER (Nasabah)
6. Proses Review, Approval, Disbursement dilakukan di Web Admin

---

*Document Version: 1.0*
*Last Updated: 2026-01-26*
