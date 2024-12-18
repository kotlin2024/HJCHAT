FROM ubuntu:latest
LABEL authors="wnd2g"

COPY build/libs/HJCHAT-0.0.1-SNAPSHOT.jar /app/HJCHAT.jar

ENTRYPOINT ["top", "-b"]