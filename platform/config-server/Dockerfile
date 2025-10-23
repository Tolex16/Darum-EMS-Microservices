# ===== Build Stage =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy entire project (to include parent POM)
COPY ../../ /app

# Build only the config-server module and its dependencies
RUN mvn -q -pl platform/config-server -am -DskipTests package

# ===== Runtime Stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/platform/config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
