# Stage 1
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests


# Stage 2
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S app \
    && adduser -S app -G app

COPY --from=build --chown=app:app /app/target/*.jar app.jar

USER app

CMD ["java", "-jar", "app.jar"]