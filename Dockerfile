FROM gradle:8-jdk21 AS build
WORKDIR /app

RUN apt-get update -qq && apt-get install -y -qq maven > /dev/null 2>&1

COPY settings.gradle.kts build.gradle.kts ./
COPY gradle gradle/
COPY config config/
RUN ./gradlew dependencies --no-daemon 2>/dev/null || true

COPY . .
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -jar -Dserver.port=${PORT:-8080} app.jar"]
