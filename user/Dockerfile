# Use Maven to build the application
FROM maven:3.8-openjdk-17-slim AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests=true

# Stage 2: Setup runtime environment
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/user/target/user-0.0.1-SNAPSHOT.jar /app/user.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app/user.jar"]
