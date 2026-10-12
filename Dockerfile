# Multi-stage Docker build for Render
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven descriptor from backend folder
COPY backend/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# Copy backend source code and build package
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
