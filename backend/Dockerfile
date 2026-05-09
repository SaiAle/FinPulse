FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S finpulse && adduser -S finpulse -G finpulse
COPY --from=builder /app/target/*.jar app.jar
USER finpulse
EXPOSE 8080
ENTRYPOINT ["java", "--enable-preview", "-XX:+UseVirtualThreads", "-jar", "app.jar"]
