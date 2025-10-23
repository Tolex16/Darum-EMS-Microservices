# ===== Build stage =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy entire project to allow parent POM resolution
COPY ../../ /app

# Build only this module
WORKDIR /app/platform/config-server
RUN mvn -q -pl platform/config-server -am -DskipTests package

# ===== Runtime stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/platform/config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","app.jar"]
