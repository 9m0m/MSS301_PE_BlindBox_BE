# Dockerfile cho API Gateway
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy parent pom.xml first
COPY pom.xml /workspace/pom.xml

# Copy project pom.xml
COPY APIGateway_SE184531/pom.xml /workspace/APIGateway_SE184531/pom.xml

# Copy source code
COPY APIGateway_SE184531/src /workspace/APIGateway_SE184531/src

# Set working directory to the module
WORKDIR /workspace/APIGateway_SE184531

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the jar file from build stage
COPY --from=build /workspace/APIGateway_SE184531/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
