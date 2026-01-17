# Build stage
FROM maven:3.9.12-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -e -C dependency:resolve dependency:resolve-plugins
COPY src ./src
RUN mvn -B -DskipTests clean package

# Runtime stage
FROM eclipse-temurin:21-jre-noble
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]