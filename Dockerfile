# Base image
FROM ubuntu:latest

# Maintainer information
LABEL authors="wnd2g"

# Install Java Runtime Environment
RUN apt-get update && apt-get install -y openjdk-17-jre-headless && apt-get clean

# Set working directory
WORKDIR /app

# Copy jar file
COPY build/libs/HJCHAT-0.0.1-SNAPSHOT.jar /app/HJCHAT.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "HJCHAT.jar"]
