FROM openjdk:23-jdk-slim AS builder

WORKDIR /app

COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
COPY src /app/src

RUN chmod +x gradlew
RUN ./gradlew build

FROM openjdk:23-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/taska-*.jar /app/app.jar

EXPOSE 8000

CMD ["java", "-jar", "/app/app.jar"]