FROM eclipse-temurin:21 AS build
WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts gradle /app/
RUN chmod +x gradlew

COPY . /app

RUN ./gradlew dependencies --no-daemon

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
