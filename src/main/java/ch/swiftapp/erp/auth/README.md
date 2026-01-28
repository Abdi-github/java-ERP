# 🔐 Auth Module

> Authentication, authorization, user management, role-based access control (RBAC) and JWT tokens.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.auth` |
| **Public API** | `AuthModuleApi` |
| **Entities** | User, Role, Permission |
| **Security** | Dual filter chains — Session (web) + JWT (API) |
| **Permissions** | `AUTH:VIEW`, `AUTH:CREATE`, `AUTH:EDIT`, `AUTH:DELETE` |
| **Web Routes** | `/app/auth/users`, `/app/auth/roles`, `/auth/login`, `/auth/logout` |
| **API Routes** | `/api/v1/auth/login`, `/api/v1/users`, `/api/v1/roles` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                         AUTH MODULE                                  │
│                                                                      │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                │
│   │ Login Page  │  │ User Mgmt   │  │ Role Mgmt   │                │
│   │ /auth/login │  │ /app/auth/  │  │ /app/auth/  │                │
│   │             │  │ users       │  │ roles       │                │
│   └──────┬──────┘  └──────┬──────┘  └──────┬──────┘                │
│          │                │                │                         │
│   ┌──────┴──────┐  ┌──────┴──────┐  ┌──────┴──────┐                │
│   │ Auth Login  │  │ User        │  │ Role        │                │
│   │ POST /api/  │  │ RestCtrl    │  │ RestCtrl    │                │
│   │ v1/auth/    │  │ /api/v1/    │  │ /api/v1/    │                │
│   │ login       │  │ users       │  │ roles       │                │
│   └──────┬──────┘  └──────┬──────┘  └──────┬──────┘                │
│          │                │                │                         │
│          └────────────────┼────────────────┘                         │
│                           │                                          │
│     ┌─────────────────────┼─────────────────────────┐               │
│     │            Security Layer                      │               │
│     │  ┌──────────────────┐  ┌───────────────────┐  │               │
│     │  │ Web Filter Chain │  │ API Filter Chain  │  │               │
│     │  │ Session-based    │  │ JWT-based         │  │               │
│     │  │ CSRF enabled     │  │ Stateless         │  │               │
│     │  │ /app/** + /auth  │  │ /api/v1/**        │  │               │
│     │  └──────────────────┘  └───────────────────┘  │               │
│     └─────────────────────┬─────────────────────────┘               │
│                           │                                          │
│              ┌────────────┴────────────┐                            │
│              │ AuthService / UserSvc   │                            │
│              │ RoleService             │                            │
│              │ JwtTokenProvider        │                            │
│              │ CustomUserDetailsSvc    │                            │
│              └────────────┬────────────┘                            │
│                           │                                          │
│  ┌────────────────────────┼──────────────────────────────────────┐  │
│  │                    DATABASE                                   │  │
│  │  users ─M:N─ user_roles ─M:N─ roles                          │  │
│  │  roles ─M:N─ role_permissions ─M:N─ permissions               │  │
│  └───────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│    users     │         │  user_roles  │         │    roles     │
│──────────────│         │──────────────│         │──────────────│
│ id (PK)      │ M:N     │ user_id (FK) │         │ id (PK)      │
│ username     │────────►│ role_id (FK) │────────►│ name         │
│ email        │         └──────────────┘         │ description  │
│ password_hash│                                   └──────┬───────┘
│ first_name   │                                          │ M:N
│ last_name    │                                          │
│ enabled      │         ┌──────────────────┐    ┌────────┴──────┐
│ locked       │         │role_permissions  │    │  permissions  │
└──────────────┘         │──────────────────│    │───────────────│
                         │ role_id (FK)     │◄───│ id (PK)       │
                         │ permission_id(FK)│    │ code (UNIQUE) │
                         └──────────────────┘    │ description   │
                                                  │ module        │
                                                  └───────────────┘
```

---

## Authentication Flow

### Web Login (Session-Based)

```
Browser: POST /auth/login  (username, password, _csrf)
         │
         ▼
Spring Security WebFilterChain
         │
         ├── UsernamePasswordAuthenticationFilter
         ├── CustomUserDetailsService.loadUserByUsername()
         ├── BCryptPasswordEncoder.matches()
         ├── ✅ Success → Session created → redirect /app/dashboard
         └── ❌ Failure → redirect /auth/login?error
```

### API Login (JWT)

```
Client: POST /api/v1/auth/login  {"username":"admin","password":"admin123"}
         │
         ▼
UserRestController.login()
         │
         ├── AuthenticationManager.authenticate()
         ├── CustomUserDetailsService.loadUserByUsername()
         ├── BCryptPasswordEncoder.matches()
         ├── ✅ → JwtTokenProvider.generateToken(user)
         │       └── Return JwtResponse { token, expiresIn, roles }
         └── ❌ → 401 Unauthorized (RFC 7807)
```

### JWT Token Validation (on subsequent API requests)

```
Client: GET /api/v1/products
        Authorization: Bearer eyJhbGciOi...
         │
         ▼
JwtAuthenticationFilter (API filter chain)
         │
         ├── Extract token from Authorization header
         ├── JwtTokenProvider.validateToken(token)
         │     ├── Verify signature (HMAC-SHA256)
         │     ├── Check expiration
         │     └── Extract username + authorities
         ├── CustomUserDetailsService.loadUserByUsername()
         ├── Set SecurityContextHolder
         └── Continue to controller
```

---

## RBAC Permission Model

```
Permission codes follow: {MODULE}:{ACTION}

Modules: AUTH, MASTERDATA, SALES, PURCHASING, PRODUCTION, INVENTORY,
         NOTIFICATION, ACCOUNTING, HR, CRM, QUALITY_CONTROL

Actions: VIEW, CREATE, EDIT, DELETE

Example permission set for "Sales Manager" role:
  ┌─────────────────┐
  │ SALES:VIEW      │
  │ SALES:CREATE    │
  │ SALES:EDIT      │
  │ MASTERDATA:VIEW │  (can see products to add to orders)
  │ INVENTORY:VIEW  │  (can check stock)
  └─────────────────┘
```

---

## API Endpoints

### Authentication (`/api/v1/auth`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/v1/auth/login` | None | Authenticate and receive JWT |

### Users (`/api/v1/users`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/users` | `AUTH:VIEW` | List users (paginated) |
| `GET` | `/api/v1/users/{id}` | `AUTH:VIEW` | Get user by ID |
| `POST` | `/api/v1/users` | `AUTH:CREATE` | Create user |
| `PUT` | `/api/v1/users/{id}` | `AUTH:EDIT` | Update user |
| `DELETE` | `/api/v1/users/{id}` | `AUTH:DELETE` | Delete user |

### Roles (`/api/v1/roles`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/roles` | `AUTH:VIEW` | List all roles |
| `GET` | `/api/v1/roles/{id}` | `AUTH:VIEW` | Get role by ID |
| `POST` | `/api/v1/roles` | `AUTH:CREATE` | Create role |
| `PUT` | `/api/v1/roles/{id}` | `AUTH:EDIT` | Update role |
| `DELETE` | `/api/v1/roles/{id}` | `AUTH:DELETE` | Delete role |
| `GET` | `/api/v1/roles/permissions` | `AUTH:VIEW` | List all permissions |
| `GET` | `/api/v1/roles/permissions/grouped` | `AUTH:VIEW` | Permissions grouped by module |

---

## Domain Events

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `UserCreatedEvent` | New user registered | NotificationListener |

---

## Security Configuration

- **Password encoding**: BCrypt (strength 10)
- **JWT secret**: Configurable via `app.jwt.secret`
- **JWT expiration**: Configurable via `app.jwt.expiration` (default 24h)
- **CSRF**: Enabled for web, disabled for API
- **CORS**: Configurable for API endpoints
- **Session**: HTTP-only, Secure cookies for web

---

## Module Dependencies

```
auth ──depends on──► shared (BaseEntity)
ALL modules ──use──► auth (via Spring Security annotations: @PreAuthorize)
```

---

## File Inventory

```
auth/
├── AuthModuleApi.java
├── package-info.java
├── model/  (User, Role, Permission)
├── repository/  (UserRepository, RoleRepository, PermissionRepository)
├── service/  (UserService, RoleService, AuthService, JwtTokenProvider, CustomUserDetailsService)
├── dto/  (LoginRequest, JwtResponse, UserRequest, UserResponse, RoleRequest, RoleResponse, PermissionResponse)
├── web/  (UserViewController, RoleViewController, LoginViewController)
├── api/  (UserRestController, RoleRestController)
└── event/  (UserCreatedEvent)
```

