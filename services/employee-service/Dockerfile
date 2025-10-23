# ===== Build Stage =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY ../../ /app
WORKDIR /app/services/employee-service
RUN mvn -q -pl services/employee-service -am -DskipTests package

# ===== Run Stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/services/employee-service/target/*.jar app.jar
EXPOSE 8201
ENTRYPOINT ["java","-jar","app.jar"]
