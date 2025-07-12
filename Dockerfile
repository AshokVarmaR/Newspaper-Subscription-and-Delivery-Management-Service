# Use OpenJDK 21 base image
FROM eclipse-temurin:21-jdk

# Argument to locate your JAR
ARG JAR_FILE=target/*.jar

# Copy the JAR into the container
COPY ${JAR_FILE} app.jar

# Expose the port (Render provides this dynamically)
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app.jar"]
