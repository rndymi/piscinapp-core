# PiscinApp Core

Backend service of the PiscinApp ecosystem.

Latest stable version: `v1.0.0`.

Current development version: `v1.1.0-SNAPSHOT`.

---

### Technologies

`Java 21` `Spring Boot 4.1.1` `Spring MVC` `Spring Security` `OAuth2/OIDC` `Maven` `PostgreSQL` `Docker`

---

### Local execution with IntelliJ

Start PostgreSQL:

```sh
docker compose -f docker-compose-db.yml up -d
```

Run `CoreApplication` from IntelliJ IDEA.

---

### Local execution with Docker

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

---

### PostgreSQL

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

---

### Health

With Core running locally, check its health:

```sh
curl http://localhost:8080/actuator/health
````

Expected result: a health response with `status` equal to `UP`.

Only the minimum Actuator health capability is exposed by the current bootstrap.

---

### API documentation

With the dev profile, Swagger UI is available at:

* Cliente Web: http://localhost:8080/swagger-ui/index.html

OpenAPI JSON is available at:

* http://localhost:8080/v3/api-docs

Swagger UI and OpenAPI documentation are disabled by configuration in the prod profile.

---

### Identity bootstrap

PiscinApp Core persists application security accounts in PostgreSQL.

Accounts contain:

- a stable UUID;
- a normalized username;
- an encoded password;
- enabled/disabled state;
- `USER` and `ADMIN` security roles;
- protected Owner state.

---

### Development identities

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

---

### Production-shaped bootstrap

Production-shaped environments must provide the protected Owner bootstrap credentials externally:

```text
PISCINAPP_BOOTSTRAP_OWNER_USERNAME
PISCINAPP_BOOTSTRAP_OWNER_PASSWORD
```

Real production credentials must never be committed to the repository.

The protected Owner bootstrap is idempotent. If the Owner already exists, restarting Core does not create another Owner or replace its persisted credentials.

`DataSeederDev` does not run with the `prod` profile.

PiscinApp Core provides persistent security accounts, OAuth2/OpenID Connect authentication, role-aware JWT authorization and the versioned `/api/v1` Identity and Access Management API.

---

### OAuth2 / OpenID Connect authentication

PiscinApp Core acts as both an OAuth2/OpenID Connect Authorization Server and a Bearer-token Resource Server.

Persisted PiscinApp accounts authenticate through the Authorization Code flow with PKCE. Access tokens are signed JWTs and include the account security roles required by Core authorization.

---

### DEV Swagger authentication

Swagger UI is available in DEV at:

* http://localhost:8080/swagger-ui/index.html

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

* http://localhost:8080/swagger-ui/oauth2-redirect.html

Use the Swagger Authorize action to authenticate through the real PiscinApp Authorization Server.

The disposable `dev.admin` account recreated by `DataSeederDev` can be used for normal local ADMIN validation.

The protected `local.owner` account is reserved as the persistent administrative recovery identity.

Bearer access tokens protect application resources and contain PiscinApp USER / ADMIN role information.

Swagger UI and OpenAPI documentation remain disabled under the prod profile.

Final OAuth2 client registrations for piscinapp-control and piscinapp-field are not defined yet. They will be introduced when those clients have real redirect and deployment contracts.

---

### Identity API

The first functional Core API is exposed under:

* `/api/v1/me`
* `/api/v1/users`

Authenticated users can inspect their current security account and change
their own password.

Account creation, listing, role management, status management and
administrator password replacement require the `ADMIN` role.

The API uses OAuth2 Bearer access tokens and machine-readable
`ProblemDetail` error responses.

Password values and password hashes are never returned by the API.

Account role, status and password changes do not immediately revoke
already-issued JWT access tokens. Existing tokens remain subject to
their configured lifetime.

---

### Employee API

The `v1.1.0` development line introduces operational employee management.

Employees are independent from security accounts and contain:

- a stable employee UUID;
- first name and family name;
- active/inactive operational state;
- an optional associated security-account UUID.

Employee administration is exposed under:

* `/api/v1/employees`

Employee administration requires the `ADMIN` role.

The employee collection supports bounded pagination, active-state filtering, case-insensitive name search and controlled sorting.

An employee may optionally be associated with an existing PiscinApp security account. Employee lifecycle and account lifecycle remain independent.

Normal employee hard deletion is not exposed.

---

### Pool and maintenance configuration API

The `v1.1.0` development line provides swimming-pool and maintenance-activity master-data configuration.

Swimming pools contain:

- a stable UUID;
- an operational name;
- an address;
- active/inactive state.

Pool administration is exposed under:

* `/api/v1/pools`

Maintenance activities are configurable persisted records containing:

- a stable UUID;
- a name;
- an optional plain-text description;
- active/inactive state.

Maintenance-activity administration is exposed under:

* `/api/v1/maintenance-activities`

Administrators can configure which maintenance activities are applicable to each swimming pool through:

* `/api/v1/pools/{poolId}/maintenance-activities`

Pool, maintenance-activity and applicability administration requires the `ADMIN` role.

Pool and maintenance-activity collections support bounded pagination, active-state filtering, case-insensitive search and controlled sorting.

Deactivation preserves master data and existing applicability configuration. Normal hard deletion of swimming pools and maintenance activities is not exposed.

---

### Crew organization API

The `v1.1.0` development line provides operational crew organization.

A crew contains:

- a stable crew UUID;
- an operational name;
- active/inactive state;
- employee membership;
- one optional designated supervisor employee.

Crew administration is exposed through:

* `/api/v1/crews`

Administrators can add and remove employee memberships and explicitly assign, change or clear the crew supervisor.

Only active employees may be newly added to a crew or designated as supervisor. The supervisor must already belong to the same crew.

Crew membership and supervisor responsibility are operational concepts and do not create or modify Spring Security roles.

Crew deactivation preserves existing membership and supervisor configuration. Normal hard deletion of crews is not exposed.

---

### Scheduled visit planning API

The `v1.1.0` development line provides explicit operational visit planning.

A planned visit contains:

- a stable visit UUID;
- one swimming-pool reference;
- one assigned crew;
- a planned date;
- a planned time;
- one or more selected maintenance activities;
- a lifecycle state;
- optional plain-text planning notes.

Visit planning is exposed through:

* `/api/v1/visits`

Administrators can create, retrieve, search, update and cancel planned visits.

New visits start in the `PLANNED` state.

Planning validates that the swimming pool is active, the assigned crew is currently operationally assignable and every selected maintenance activity is active and applicable to the selected swimming pool.

Planned visits can be updated only while they remain in the `PLANNED` state.

Cancellation preserves the visit and its planned configuration. Normal hard deletion of visits is not exposed.

The visit collection supports bounded pagination, exact-date and inclusive date-range filtering, lifecycle-state filtering, pool and crew filtering, and controlled sorting.

Visit execution, activity completion, observations and incidents are not implemented by the current planning capability.

---

### Tests

With PostgreSQL running:

```sh
mvn test
```

Windows with Maven Wrapper:

```sh
.\mvnw.cmd test
```

---

### Automated verification

The complete local verification lifecycle is:

```sh
mvn -B verify
```
Windows:

```sh
.\mvnw.cmd -B verify
```

Verification executes the current Core test suite and generates the JaCoCo coverage report at:

```text
target/site/jacoco/index.html
```

GitHub Actions performs the same Maven verification against PostgreSQL, runs SonarCloud analysis, evaluates the configured Quality Gate, and validates that the production-oriented Core Docker image remains buildable.

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

The project currently contains the Core transversal platform and the first stable PiscinApp Identity and Access Management capability.

Future business modules and definitive OAuth2 client integrations for `piscinapp-control` and `piscinapp-field` will evolve in subsequent versions as their real integration contracts are defined.
