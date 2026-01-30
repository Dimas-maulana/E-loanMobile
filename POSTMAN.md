# Postman API Documentation

This documentation outlines the API endpoints for the **EloanMust** system.
Ensure your environment variables are set correctly.

**Base URL**: `http://localhost:8081` or `http://10.10.90.172:8081`  
**Swagger UI**: `http://localhost:8081/swagger-ui/index.html`

## Authentication Strategies
Most endpoints require a Bearer Token.
- **Header**: `Authorization`
- **Value**: `Bearer <your_jwt_token>`

---

## 1. Authentication
**Base Path**: `/api/auth`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login & get Token | Public |
| POST | `/api/auth/logout` | Logout (Invalidate Token) | Authenticated |
| POST | `/api/auth/forgot-password` | Request password reset | Public |
| POST | `/api/auth/reset-password` | Reset password with token | Public |

### Request Bodies

#### POST /api/auth/register
```json
{
  "username": "user123",
  "email": "user@example.com",
  "password": "securePassword",
  "fullname": "John Doe",
  "phone": "08123456789"
}
```

#### POST /api/auth/login
```json
{
  "username": "user123",
  "password": "securePassword",
  "fcmToken": "cfa...your_fcm_token"
}
```

#### POST /api/auth/forgot-password
```json
{
  "email": "user@example.com"
}
```

#### POST /api/auth/reset-password
```json
{
  "token": "reset_token_uuid",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}
```

---

## 2. Customer Profile
**Base Path**: `/api/profile`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/profile` | Get current user's profile | USER |
| PUT | `/api/profile` | Create/Update profile | USER |
| GET | `/api/profile/ktp` | Get KTP Image (Base64) | USER |
| GET | `/api/profile/status` | Check profile completion | USER |

### Request Bodies

#### PUT /api/profile
```json
{
  "fullName": "John Doe",
  "nik": "1234567890123456",
  "birthDate": "1990-01-01",
  "birthPlace": "Jakarta",
  "address": "Jl. Sudirman No. 1",
  "phoneNumber": "08123456789",
  "occupation": "Software Engineer",
  "monthlyIncome": 15000000.0,
  "bankAccountNumber": "1234567890",
  "ktpImage": "base64_encoded_image_string"
}
```

---

## 3. Loan Products (Plafond)
**Base Path**: `/api/plafonds`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/plafonds` | Get active plafonds | Public |
| GET | `/api/plafonds/{id}` | Get plafond by ID | Public |
| GET | `/api/plafonds/detect?amount={amount}` | Detect product by amount | Public |

### Query Parameters

#### GET /api/plafonds/detect
- `amount` (required): Loan amount to detect the appropriate product

**Example**: `/api/plafonds/detect?amount=5000000`

**Response**:
```json
{
  "success": true,
  "message": "Product detected",
  "data": {
    "id": 1,
    "name": "Bronze",
    "minAmount": 1000000,
    "maxAmount": 5000000,
    "maxTenor": 6,
    "interestRate": 2.5,
    "description": "Pinjaman Bronze untuk kebutuhan kecil"
  }
}
```

---

## 4. Loan Applications
**Base Path**: `/api/loans`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/loans/simulate` | Simulate Loan | Public |
| POST | `/api/loans` | Apply for Loan | USER |
| GET | `/api/loans` | Get my loans | USER |
| GET | `/api/loans/{id}` | Get loan by ID | Authenticated |

### Request Bodies

#### POST /api/loans/simulate
```json
{
  "amount": 5000000.0,
  "tenor": 6,
  "plafondId": 1
}
```

#### POST /api/loans
```json
{
  "amount": 5000000.0,
  "tenor": 6,
  "plafondId": 1,
  "purpose": "Business Capital"
}
```

---

## 5. Notifications
**Base Path**: `/api/notifications`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/notifications` | Get all notifications | Authenticated |
| GET | `/api/notifications/unread` | Get unread notifications | Authenticated |
| GET | `/api/notifications/count` | Get unread count | Authenticated |
| PUT | `/api/notifications/{id}/read` | Mark as read | Authenticated |
| PUT | `/api/notifications/read-all` | Mark all as read | Authenticated |

---

## 6. Admin / Backoffice (Reference)
*These endpoints are managed by Admin/Marketing/Manager roles and are not directly used in the Customer App.*

### User Management
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/users` | Get all users | SUPER_ADMIN |
| GET | `/api/users/{id}` | Get user by ID | SUPER_ADMIN |
| POST | `/api/users` | Create user | SUPER_ADMIN |
| PUT | `/api/users/{id}` | Update user | SUPER_ADMIN |
| DELETE | `/api/users/{id}` | Soft delete user | SUPER_ADMIN |

### Loan Review (Marketing)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/reviews/pending` | Get pending reviews | MARKETING |
| POST | `/api/reviews/{loanId}` | Submit review | MARKETING |
| GET | `/api/reviews/my-reviews` | Get reviews by me | MARKETING |

### Loan Approval (Branch Manager)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/approvals/pending` | Get pending approvals | BRANCH_MANAGER |
| POST | `/api/approvals/{loanId}` | Submit approval | BRANCH_MANAGER |
| GET | `/api/approvals/my-approvals` | Get approvals by me | BRANCH_MANAGER |

### Disbursement (Back Office)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/disbursements/pending` | Get pending disbursements | BACK_OFFICE |
| POST | `/api/disbursements/{loanId}` | Process disbursement | BACK_OFFICE |
| GET | `/api/disbursements` | Get all disbursements | BACK_OFFICE |

### Plafond Management
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/plafonds/all` | Get all (inc. inactive) | SUPER_ADMIN |
| POST | `/api/plafonds` | Create new plafond | SUPER_ADMIN |
| PUT | `/api/plafonds/{id}` | Update plafond | SUPER_ADMIN |
| DELETE | `/api/plafonds/{id}` | Deactivate plafond | SUPER_ADMIN |
| POST | `/api/plafonds/{id}/activate` | Activate plafond | SUPER_ADMIN |

---

*For the exact request body of Admin endpoints, please refer to the Swagger UI.*
