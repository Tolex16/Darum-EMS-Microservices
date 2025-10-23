# ===== Build Stage =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY ../../ /app
WORKDIR /app/services/auth-service
RUN mvn -q -pl services/auth-service -am -DskipTests package

# ===== Run Stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/services/auth-service/target/*.jar app.jar
EXPOSE 8101
ENTRYPOINT ["java","-jar","app.jar"]