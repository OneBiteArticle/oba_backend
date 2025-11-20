# ----------- 1단계: Build stage -----------
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

COPY . .
RUN gradle bootJar --no-daemon

# ----------- 2단계: Run stage -----------
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
