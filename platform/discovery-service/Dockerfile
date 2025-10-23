# ===== Build Stage =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY ../../ /app
WORKDIR /app/platform/discovery-service
RUN mvn -q -pl platform/discovery-service -am -DskipTests package

# ===== Run Stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/platform/discovery-service/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","app.jar"]
