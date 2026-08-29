# PiscinApp Core

Backend service of the PiscinApp ecosystem.

Current bootstrap version: `v0.0.0`.

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

### Security bootstrap

PiscinApp Core currently provides the technical foundation for:

- Spring Security;
- OAuth2 Authorization Server;
- OpenID Connect;
- OAuth2 Resource Server;
- Bearer JWT protected resources.

The current bootstrap does not yet provide real PiscinApp users, roles, account administration or final client registrations.

Development and test environments use non-production runtime-generated signing keys. Production signing material must be supplied externally and is never stored in the repository.

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

The project currently contains the Core bootstrap and transversal Web, operational and security platform. Functional business APIs, real identity management and production delivery belong to later HUs and versions.
