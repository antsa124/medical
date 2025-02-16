# Use Java 21 with Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the project files and build
COPY . .
RUN mvn clean package -DskipTests

# Run the built JAR file
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
