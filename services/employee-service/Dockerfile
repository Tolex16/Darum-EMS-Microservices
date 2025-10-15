# ====== Build ======
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY ../../pom.xml /app/pom.xml
# copy parent and necessary module poms for proper dependency resolution
COPY . /app
RUN mvn -q -DskipTests package

# ====== Run ======
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/services/employee-service/target/*.jar app.jar
EXPOSE 8201
ENTRYPOINT ["java","-jar","/app/app.jar"]
