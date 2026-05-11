# NeonVertex API

A modular monolith REST API built with Spring Boot 3.5 and Java 21, designed to serve as a unified backend for multiple applications. The architecture prioritizes clean module boundaries, consistent API contracts, and production-ready security patterns.

## Author

**Raimundos Marques** — Software Engineer  
devraimundos@outlook.com

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security 6 + JWT (Auth0) |
| Persistence | Spring Data JPA + Hibernate 6 |
| Database | PostgreSQL 17 |
| Migrations | Flyway 11 |
| Documentation | SpringDoc OpenAPI + Scalar |
| Build | Maven |
| Utilities | Lombok |

---

## Architecture

NeonVertex follows a **Modular Monolith** pattern — a single deployable unit organized into independent modules, each owning its internal layers. Module boundaries are enforced by convention, with a shared kernel for cross-cutting concerns.

```
src/main/java/br/api/neonvertex/
├── NeonVertexApplication.java
├── core/
│   ├── iam/                        # Identity & Access Management
│   │   ├── models/                 # Role, Permission
│   │   └── repositories/
│   └── security/
│       └── SecurityConfig.java
├── modules/
│   ├── auth/                       # Authentication & token lifecycle
│   │   ├── config/
│   │   ├── controllers/
│   │   ├── dto/
│   │   ├── models/
│   │   ├── repositories/
│   │   ├── security/
│   │   └── services/
│   └── users/                      # User management
│       ├── controllers/
│       ├── dto/
│       ├── enums/
│       ├── models/
│       ├── repositories/
│       └── services/
└── shared/
    ├── exception/                  # AppException + GlobalExceptionHandler
    └── response/                   # AppResponse + ToastPayload
```

---

## API Contracts

### Response Envelope

All successful responses follow a consistent envelope:

```json
{
  "data": { },
  "toast": {
    "type": "success",
    "message": "Operation completed."
  }
}
```

Paginated responses include metadata:

```json
{
  "data": [],
  "pagination": {
    "total": 100,
    "perPage": 10,
    "currentPage": 1,
    "lastPage": 10
  },
  "toast": null
}
```

### Error Responses

Errors follow [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457), extended with a `toast` property for frontend notification handling:

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "E-mail already registered.",
  "instance": "/api/users/register",
  "toast": {
    "type": "warning",
    "message": "E-mail already registered."
  }
}
```

Toast types resolve automatically by HTTP status range — `error` for 5xx, `warning` for 4xx, `info` for 2xx.

---

## Authentication

NeonVertex uses a stateless JWT authentication flow with short-lived access tokens and rotating refresh tokens.

```
POST /api/auth/login      → { accessToken, refreshToken }
POST /api/auth/refresh    → { accessToken, refreshToken }
```

- Access token expires in **15 minutes**
- Refresh token expires in **7 days**
- Refresh tokens are single-use — each refresh issues a new pair
- Expired or used tokens are deleted immediately

---

## Authorization

Permissions follow a **RBAC + PBAC** model:

- Users are assigned **Roles** (e.g. `ADMIN`, `USER`)
- Roles carry granular **Permissions** (e.g. `helpdesk.technicians.delete`)
- `ADMIN` role has a full bypass — no permission checks applied
- All other roles are evaluated against their assigned permissions

Permission naming convention: `module.resource.action`

```java
@PreAuthorize("hasAuthority('helpdesk.technicians.delete')")
public ResponseEntity<?> delete(@PathVariable UUID id) { ... }
```

---

## Database

PostgreSQL with Flyway migrations. All objects live in the `home` schema.

```
home/
├── users
├── roles
├── permissions
├── role_permissions
├── user_roles
└── refresh_tokens
```

Migration files are organized by module:

```
db/migration/
├── core/               # IAM tables (roles, permissions, pivot tables)
└── modules/
    ├── auth/           # refresh_tokens
    └── users/          # users
```

---

## Running Locally

**Prerequisites:** Java 21, Docker

```bash
# Start the database
docker compose -f docker/dev/docker-compose.yml up -d

# Configure environment
cp .env.example .env
# Fill in SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME,
# SPRING_DATASOURCE_PASSWORD, JWT_SECRET

# Run
./mvnw spring-boot:run
```

API available at `http://localhost:8080`  
Documentation available at `http://localhost:8080/scalar`

---

## Environment Variables

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key for JWT signing |
| `SPRING_PROFILES_ACTIVE` | `dev` (default) or `prod` |

In `prod` profile, the API documentation endpoints are disabled automatically.

---

## License

MIT