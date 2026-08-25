# PiscinApp Core

Backend service of the PiscinApp ecosystem.

Current bootstrap version: `v0.0.0`.

### Technologies

`Java 21` `Spring Boot 4.1.1` `Maven` `PostgreSQL` `Docker`

### Local execution with IntelliJ

Start PostgreSQL:

```sh
docker compose -f docker-compose-db.yml up -d
```

Run `CoreApplication` from IntelliJ IDEA.

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

### Tests

With PostgreSQL running:

```sh
mvn test
```

Windows with Maven Wrapper:

```sh
.\mvnw.cmd test
```

### Build

```sh
mvn clean package
```

The project currently contains only the technical bootstrap baseline. Business APIs, authentication, OpenAPI and production deployment will be introduced in later HUs.