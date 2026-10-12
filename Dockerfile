# Stage 1: Build application with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache Maven dependencies
COPY backend/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# Copy backend source code and build package
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Containerized runtime environment
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Render assigns PORT dynamically; default to 8080
ENV PORT=8080
EXPOSE 8080

# Configure container memory limits and bind to 0.0.0.0:$PORT
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -jar app.jar"]
