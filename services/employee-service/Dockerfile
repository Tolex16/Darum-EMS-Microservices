# ===== Build Stage =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the entire repository (includes parent POM)
COPY ../../ /app

# Build only this module using the parent POM
RUN mvn -q -f /app/pom.xml -pl services/employee-service -am -DskipTests package

# ===== Runtime Stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/services/employee-service/target/*.jar app.jar
EXPOSE 8201
ENTRYPOINT ["java", "-jar", "app.jar"]
