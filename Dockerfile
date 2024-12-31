FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B clean package -DskipTests

FROM openjdk:21-jdk-slim
COPY --from=build /app/target/finservice-0.0.1-SNAPSHOT.jar finservice.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "finservice.jar"]