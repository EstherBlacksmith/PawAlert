# Multi-stage build for Spring Boot application
# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install maven wrapper dependencies
RUN apk add --no-cache maven

# Copy Maven wrapper and pom.xml first for better caching
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built jar file
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
