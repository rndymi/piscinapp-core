# PiscinApp Core

Backend service of the PiscinApp ecosystem.

Latest stable version: `v1.0.0`.

Current development version: `v1.1.0-SNAPSHOT`.

---

### Technologies

`Java 21` `Spring Boot 4.1.1` `Spring MVC` `Spring Security` `OAuth2/OIDC` `Maven` `PostgreSQL` `Docker`

---

### Local development

#### IntelliJ IDEA

Start PostgreSQL:

```sh
docker compose -f docker-compose-db.yml up -d
```

Run `CoreApplication` from IntelliJ IDEA.

#### Docker

Build and start the local stack:

```sh
docker compose up --build -d
```

Check running services:

```sh
docker compose ps
```

Stop the local stack:

```sh
docker compose down
```

#### PostgreSQL

Open the PostgreSQL console:

```sh
docker exec -it piscinapp-core-postgres psql -U postgres
```

Useful commands:

```text
\l                              List databases
\connect piscinappdb postgres   Connect to PiscinApp database
\dt                             List tables
\q                              Exit
```

#### Health

With Core running locally, check its health:

```sh
curl http://localhost:8080/actuator/health
```

Expected result: a health response with `status` equal to `UP`.

Only the minimum Actuator health capability is exposed.

---

### API documentation

With the `dev` profile, Swagger UI is available at:

- http://localhost:8080/swagger-ui/index.html

OpenAPI JSON is available at:

- http://localhost:8080/v3/api-docs

Swagger UI and OpenAPI documentation are disabled by configuration in the `prod` profile.

---

### Authentication and API access

PiscinApp Core persists application security accounts in PostgreSQL and acts as both an OAuth2/OpenID Connect Authorization Server and a Bearer-token Resource Server.

Accounts contain:

- a stable UUID;
- a normalized username;
- an encoded password;
- enabled/disabled state;
- `USER` and `ADMIN` security roles;
- protected Owner state.

Persisted PiscinApp accounts authenticate through the Authorization Code flow with PKCE.

Access tokens are signed JWTs and contain the security roles required by Core authorization.

#### Development identities

When the `dev` profile starts, Core first guarantees the protected Owner and then recreates the disposable development dataset.

Default DEV identities:

| Identity | Username | Password |
| --- | --- | --- |
| Protected Owner | `local.owner` | `local-owner-password` |
| Development administrator | `dev.admin` | `dev-admin-password` |
| Development user | `dev.user` | `dev-user-password` |

The protected Owner is persistent and is not removed by `DataSeederDev`.

Normal DEV accounts and operational fixtures are recreated on DEV startup.

`DataSeederDev` runs only with the `dev` profile. Automated tests own their fixtures and do not preload the canonical DEV dataset.

These values are development-only credentials. They do not grant access to any real environment and must never be reused for FAKE_PROD or PROD.

A normal local startup requires only:

```bash
docker compose -f docker-compose-db.yml up -d
```

Then run `CoreApplication` normally.

No bootstrap environment variables need to be exported manually and no IntelliJ IDEA Run/Debug environment configuration is required for normal DEV execution.

#### Production-shaped bootstrap

Production-shaped environments must provide the protected Owner bootstrap credentials externally:

```text
PISCINAPP_BOOTSTRAP_OWNER_USERNAME
PISCINAPP_BOOTSTRAP_OWNER_PASSWORD
```

Real production credentials must never be committed to the repository.

The protected Owner bootstrap is idempotent. If the Owner already exists, restarting Core does not create another Owner or replace its persisted credentials.

`DataSeederDev` does not run with the `prod` profile.

#### DEV Swagger authentication

The DEV OAuth2 client is:

```text
piscinapp-swagger
```

It is a public client:

```text
Authorization Code
PKCE required
no client secret
```

The registered redirect URI is:

- http://localhost:8080/swagger-ui/oauth2-redirect.html

Use the Swagger Authorize action to authenticate through the real PiscinApp Authorization Server.

The disposable `dev.admin` account can be used for normal local ADMIN validation.

The protected `local.owner` account is reserved as the persistent administrative recovery identity.

Swagger UI and OpenAPI documentation remain disabled under the `prod` profile.

Final OAuth2 client registrations for `piscinapp-control` and `piscinapp-field` are not defined yet. They will be introduced when those clients have real redirect and deployment contracts.

---

### Identity API

Identity and Access Management is exposed under:

- `/api/v1/me`
- `/api/v1/users`

Authenticated users can inspect their current security account and change their own password.

Account creation, listing, role management, status management and administrator password replacement require the `ADMIN` role.

The API uses OAuth2 Bearer access tokens and machine-readable `ProblemDetail` error responses.

Password values and password hashes are never returned by the API.

Account role, status and password changes do not immediately revoke already-issued JWT access tokens. Existing tokens remain subject to their configured lifetime.

---

### Operational API

PiscinApp Core manages the operational configuration and lifecycle required to organize swimming-pool maintenance work.

#### Employees

Employee administration is exposed under:

- `/api/v1/employees`

Employees contain:

- a stable employee UUID;
- first name and family name;
- active/inactive operational state;
- an optional associated security-account UUID.

Employee administration requires the `ADMIN` role.

The employee collection supports bounded pagination, active-state filtering, case-insensitive name search and controlled sorting.

Employee lifecycle and security-account lifecycle remain independent.

Normal employee hard deletion is not exposed.

#### Pools and maintenance

Swimming-pool administration is exposed under:

- `/api/v1/pools`

Swimming pools contain:

- a stable UUID;
- an operational name;
- an address;
- active/inactive state.

Maintenance-activity administration is exposed under:

- `/api/v1/maintenance-activities`

Maintenance activities contain:

- a stable UUID;
- a name;
- an optional plain-text description;
- active/inactive state.

Administrators configure which maintenance activities are applicable to each swimming pool through:

- `/api/v1/pools/{poolId}/maintenance-activities`

Pool, maintenance-activity and applicability administration requires the `ADMIN` role.

Collections support bounded pagination, active-state filtering, case-insensitive search and controlled sorting where applicable.

Deactivation preserves configured master data. Normal hard deletion of swimming pools and maintenance activities is not exposed.

#### Crews

Crew administration is exposed under:

- `/api/v1/crews`

A crew contains:

- a stable crew UUID;
- an operational name;
- active/inactive state;
- employee membership;
- one optional designated supervisor employee.

Administrators can add and remove employee memberships and explicitly assign, change or clear the crew supervisor.

Only active employees may be newly added to a crew or designated as supervisor. The supervisor must already belong to the same crew.

Crew membership and supervisor responsibility are operational concepts and do not create or modify Spring Security roles.

Crew deactivation preserves existing membership and supervisor configuration. Normal hard deletion of crews is not exposed.

#### Visits

Visit planning and execution use the same canonical visit.

A visit contains:

- a stable visit UUID;
- one swimming-pool reference;
- one assigned crew;
- a planned date and time;
- one or more selected maintenance activities;
- lifecycle state;
- optional planning notes;
- execution timestamps and actor identifiers when work is performed.

The supported visit lifecycle is:

```text
PLANNED
↓
IN_PROGRESS
↓
COMPLETED
```

with the existing cancellation path:

```text
PLANNED
↓
CANCELLED
```

Visit planning is exposed under:

- `/api/v1/visits`

Planning operations remain administrative. Administrators can create, retrieve, search, update and cancel planned visits.

Planning validates that:

- the swimming pool is active;
- the assigned crew is operationally assignable;
- every selected maintenance activity is active and applicable to the selected pool.

Planned visits can be updated only while they remain in the `PLANNED` state.

Cancellation preserves the visit and its planned configuration. Normal hard deletion of visits is not exposed.

The visit collection supports bounded pagination, exact-date and inclusive date-range filtering, lifecycle-state filtering, pool and crew filtering, and controlled sorting.

Operational employees can discover their currently assigned work through:

- `GET /api/v1/visits/assigned`

The authenticated account is resolved internally to its associated employee. The client does not choose an `employeeId` for execution authority.

An employee may execute a visit only when:

- the authenticated account is associated with an employee;
- the employee is active;
- the employee currently belongs to the visit's assigned crew.

Administrative authority does not automatically grant operational execution authority.

Visit execution endpoints include:

- `GET /api/v1/visits/{visitId}/execution`
- `PUT /api/v1/visits/{visitId}/start`
- `PUT /api/v1/visits/{visitId}/activities/{activityId}/complete`
- `POST /api/v1/visits/{visitId}/observations`
- `GET /api/v1/visits/{visitId}/observations`
- `PUT /api/v1/visits/{visitId}/complete`

Starting a visit changes:

```text
PLANNED
→ IN_PROGRESS
```

and records:

- execution start timestamp;
- starting account UUID;
- starting employee UUID.

Selected maintenance activities begin in:

```text
PENDING
```

and may transition to:

```text
COMPLETED
```

Activity completion records:

- completion timestamp;
- account UUID;
- employee UUID.

A visit may become `COMPLETED` only when all selected maintenance activities are `COMPLETED`.

Execution observations are immutable plain-text records created only while the visit is `IN_PROGRESS`.

Each observation preserves:

- its text;
- creation timestamp;
- account UUID;
- employee UUID.

Completed and cancelled visits reject further execution mutations.

Incidents, evidence attachments, visit reopening, activity undo and advanced execution states are not implemented yet.

---

### Testing and verification

Core separates fast or focused tests from tests requiring the integrated application and PostgreSQL infrastructure.

Maven Surefire executes tests using the `*Test` naming convention.

```sh
mvn test
```

Windows with Maven Wrapper:

```sh
.\mvnw.cmd test
```

Integration, API and security tests use the `*IT` naming convention and are executed by Maven Failsafe during the `verify` lifecycle.

Before complete local verification, start PostgreSQL:

```sh
docker compose -f docker-compose-db.yml up -d
```

Run:

```sh
mvn -B verify
```

Windows:

```sh
.\mvnw.cmd -B verify
```

Verification executes:

```text
Surefire
→ unit and focused `*Test` tests

Failsafe
→ integrated `*IT` tests
→ PostgreSQL-backed integration/API/security behavior

JaCoCo
→ coverage report
```

The JaCoCo report is generated at:

```text
target/site/jacoco/index.html
```

Maven test reports are available under:

```text
target/surefire-reports
target/failsafe-reports
```

GitHub Actions performs the PostgreSQL-backed Maven verification, runs SonarCloud analysis and its Quality Gate, performs GitHub CodeQL security analysis, and validates that the production-oriented Core Docker image remains buildable.

---

### FAKE_PROD

Core participates in the ecosystem-owned FAKE_PROD environment using its normal Spring `prod` profile.

The runtime contract is provided through external configuration such as:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
PISCINAPP_SECURITY_ISSUER
JWT_KEYSTORE_BASE64
JWT_KEYSTORE_PASSWORD
JWT_KEY_PASSWORD
JWT_KEY_ALIAS
JWT_KEY_ID
```

Shared Nginx and Floci orchestration belongs to the `rndymi/piscinapp` repository.

FAKE_PROD validates the production-shaped Core container, PostgreSQL connectivity and Nginx integration using emulated AWS-compatible infrastructure. It does not represent a real public AWS production deployment.

---

### Build

```sh
mvn clean package
```

The current `v1.1.0-SNAPSHOT` development line provides the operational Core required to manage employees, pools, maintenance activities, crews, scheduled visits and authenticated visit execution.

Incidents, supervision and the final `v1.1.0` release remain outside the current implemented scope.
