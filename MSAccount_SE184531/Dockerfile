# Dockerfile cho MS Account
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy parent pom.xml
COPY ../pom.xml /workspace/parent-pom.xml

# Copy project files
COPY pom.xml .
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the jar file from build stage
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
