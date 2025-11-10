# Dockerfile cho MS Brand
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy parent pom.xml first
COPY pom.xml /workspace/pom.xml

# Copy project pom.xml
COPY MSBrand_SE184531/pom.xml /workspace/MSBrand_SE184531/pom.xml

# Copy source code
COPY MSBrand_SE184531/src /workspace/MSBrand_SE184531/src

# Set working directory to the module
WORKDIR /workspace/MSBrand_SE184531

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the jar file from build stage
COPY --from=build /workspace/MSBrand_SE184531/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
