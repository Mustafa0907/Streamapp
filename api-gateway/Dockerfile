# Use the official Amazon Corretto 17 image based on Alpine Linux
FROM amazoncorretto:17-alpine

# Set an argument for the JAR file path
ARG JAR_FILE=target/api-gateway-0.0.1-SNAPSHOT.jar

# Set the working directory inside the container
WORKDIR /opt/app

# Copy the JAR file from the build context to the container and rename it
COPY ${JAR_FILE} app.jar

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]