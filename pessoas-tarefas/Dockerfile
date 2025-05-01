# ---------- STAGE 1: build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
RUN mvn dependency:go-offline -B


COPY src ./src
RUN mvn package \
    -DskipTests \
    -Dquarkus.package.type=uber-jar \
    -B

# ---------- STAGE 2: runtime ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o jar gerado (artifactId-version-runner.jar)
ARG JAR_FILE=quarkus-tarefas-api-1.0.0-SNAPSHOT-runner.jar
COPY --from=build /workspace/target/${JAR_FILE} app.jar

EXPOSE 8080

# Configura Quarkus para escutar em todas as interfaces
ENV QUARKUS_HTTP_HOST=0.0.0.0
ENV QUARKUS_HTTP_PORT=8080

ENTRYPOINT ["java","-jar","app.jar"]
