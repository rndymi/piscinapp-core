# PiscinApp Core

Backend service and functional source of truth for the PiscinApp ecosystem.

Latest stable version: `v1.0.0`
Current development version: `v1.1.0-SNAPSHOT`

## Stack

* Java 21
* Spring Boot 4.1.1
* Spring MVC
* Spring Security
* OAuth2 / OpenID Connect
* Spring Data JPA / Hibernate
* PostgreSQL
* Maven
* Docker

## Local development

Start PostgreSQL:

```sh
docker compose -f docker-compose-db.yml up -d
```

Then run `CoreApplication` using the `dev` profile.

To start the complete local Docker stack:

```sh
docker compose up --build -d
```

Check running services:

```sh
docker compose ps
```

Stop them with:

```sh
docker compose down
```

Health endpoint:

```text
http://localhost:8080/actuator/health
```

## API documentation

With the `dev` profile:

```text
Swagger UI
http://localhost:8080/swagger-ui/index.html

OpenAPI
http://localhost:8080/v3/api-docs
```

Swagger UI and OpenAPI are disabled with the `prod` profile.

## Authentication

PiscinApp Core acts as an OAuth2/OpenID Connect Authorization Server and Bearer-token Resource Server.

The application currently uses:

* persisted security accounts;
* JWT access tokens;
* `USER` and `ADMIN` roles;
* Authorization Code with PKCE;
* a protected Owner administrative identity.

Default DEV identities:

| Identity        | Username      | Password               |
| --------------- | ------------- | ---------------------- |
| Protected Owner | `local.owner` | `local-owner-password` |
| Administrator   | `dev.admin`   | `dev-admin-password`   |
| User            | `dev.user`    | `dev-user-password`    |

These credentials are development-only.

Production-shaped environments provide protected Owner credentials through external configuration.

## Current capabilities

The current Core development line supports:

* user accounts and protected Owner governance;
* employees and account association;
* swimming pools;
* maintenance activities and pool applicability;
* crews, memberships and operational supervisors;
* maintenance-visit planning;
* assigned operational work;
* visit execution;
* maintenance-activity completion;
* immutable visit observations;
* operational incidents with `OPEN` / `RESOLVED` lifecycle;
* incident resolution by `ADMIN` or the current crew supervisor;
* visit-level operational supervision and historical readability.

The canonical visit lifecycle is:

```text
PLANNED
↓
IN_PROGRESS
↓
COMPLETED
```

with cancellation available from `PLANNED`.

An open incident does not prevent completion of a visit and may be resolved afterwards.

Operational supervision reuses the persisted planning and execution state; Core does not maintain a separate generic audit or event-sourcing subsystem.

## Main API areas

```text
/api/v1/me
/api/v1/users
/api/v1/employees
/api/v1/pools
/api/v1/maintenance-activities
/api/v1/crews
/api/v1/visits
/api/v1/incidents
```

Visit execution and supervision operations are exposed under the corresponding visit resources.

Administrative operations require `ADMIN` where applicable. Operational access is derived from the authenticated account, associated active employee and assigned crew responsibilities.

## Testing

Focused tests use the `*Test` convention and Maven Surefire:

```sh
./mvnw -B clean test
```

On Windows:

```sh
.\mvnw.cmd -B clean test
```

Full verification uses Maven Failsafe for `*IT` integration/API/security tests backed by PostgreSQL:

```sh
./mvnw -B clean verify
```

On Windows:

```sh
.\mvnw.cmd -B clean verify
```

Verification also generates JaCoCo coverage.

Reports are available under:

```text
target/surefire-reports
target/failsafe-reports
target/site/jacoco
```

CI additionally validates:

* PostgreSQL-backed Maven verification;
* SonarCloud Quality Gate;
* CodeQL analysis;
* production Docker image build.

## Build

```sh
./mvnw clean package
```

Windows:

```sh
.\mvnw.cmd clean package
```

## FAKE_PROD

Core can run with its normal `prod` profile inside the ecosystem-owned FAKE_PROD environment.

Runtime configuration is provided externally, including database, issuer and JWT signing configuration.

Shared Nginx and Floci orchestration belongs to the `rndymi/piscinapp` ecosystem repository.

FAKE_PROD is a production-shaped validation environment and is not a real public production deployment.

## Current status

`v1.1.0-SNAPSHOT` currently contains the minimum operational Core workflow from configuration and planning through execution, incidents and supervision.

Integrated FAKE_PROD validation and stable `v1.1.0` release preparation remain pending.
