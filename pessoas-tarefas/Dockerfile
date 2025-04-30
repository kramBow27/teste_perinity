# ---------- stage 1: build ----------
    FROM maven:3.9.6-eclipse-temurin-21 AS build
    WORKDIR /build
    COPY pom.xml .
    COPY src ./src
    RUN mvn -B package -DskipTests
    
    # ---------- stage 2: runtime ----------
    FROM eclipse-temurin:21-jre-alpine
    WORKDIR /app
    COPY --from=build /build/target/quarkus-app/ /app/
    EXPOSE 8080
    ENV QUARKUS_HTTP_PORT=8080
    ENTRYPOINT ["java","-jar","/app/quarkus-run.jar"]