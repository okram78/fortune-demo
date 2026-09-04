FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app
RUN apt-get update \
    && apt-get install --no-install-recommends --yes fortune-mod fortunes \
    && rm -rf /var/lib/apt/lists/*

ENV PATH="/usr/games:${PATH}"

COPY --from=build /workspace/build/libs/fortune-service-0.1.0.jar app.jar

EXPOSE 12500
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
