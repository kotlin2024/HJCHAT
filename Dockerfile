## Base image
#FROM ubuntu:latest
#
## Maintainer information
#LABEL authors="wnd2g"
#
## Install Java Runtime Environment
#RUN apt-get update && apt-get install -y openjdk-17-jre-headless && apt-get clean
#
## Set working directory
#WORKDIR /app
#
## Copy jar file
#COPY build/libs/HJCHAT-0.0.1-SNAPSHOT.jar /app/HJCHAT.jar
#
## Expose port
#EXPOSE 8080
#
## Run the application
#ENTRYPOINT ["java", "-jar", "HJCHAT.jar"]

# Base image
# 1️⃣ Gradle 빌드 단계
# 1️⃣ Gradle 빌드 단계
# OpenJDK 17 Slim 버전 사용
#FROM openjdk:17-jdk-slim
#
## 작업 디렉토리 설정
#WORKDIR /app
#
## 로컬에서 EC2로 전송한 JAR 파일을 컨테이너 내부로 복사
#COPY HJCHAT.jar /app/HJCHAT.jar
#
## 포트 설정
#EXPOSE 8080
#
## 실행 명령어
#CMD ["java", "-jar", "/app/HJCHAT.jar"]

# Base image
FROM eclipse-temurin:17-jre-alpine

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY HJCHAT.jar /app/HJCHAT.jar

# 포트 노출
EXPOSE 8081

# 실행 명령어
CMD ["java", "-jar", "/app/HJCHAT.jar"]
