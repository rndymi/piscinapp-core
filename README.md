# PiscinApp Core

Backend service of the PiscinApp ecosystem.

Current bootstrap version: `v1.0.0`.

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
- `USER` and `ADMIN` security roles.

To create the first administrator in a fresh local database, provide:

```text
PISCINAPP_BOOTSTRAP_ADMIN_USERNAME
PISCINAPP_BOOTSTRAP_ADMIN_PASSWORD
```

The password must contain between 12 and 128 characters.

Example for PowerShell:

```sh
$env:PISCINAPP_BOOTSTRAP_ADMIN_USERNAME="local.admin"
$env:PISCINAPP_BOOTSTRAP_ADMIN_PASSWORD="local-admin-password"
```

Then run Core normally.

The initial administrator receives both:

```text
USER
ADMIN
```

Bootstrap is idempotent. Once an administrator already exists, restarting Core does not create another administrator and does not modify the existing administrator username or password.

Bootstrap credentials are initial runtime configuration and must never be committed to the repository.

OAuth2/OIDC client authentication, role-aware JWT behavior and account-administration REST APIs belong to later v1.0.0 HUs.

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

The project currently contains the Core transversal platform and the persistent PiscinApp identity foundation. OAuth2/OIDC client authentication, account administration and functional business APIs belong to later HUs and versions.
