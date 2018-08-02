FROM openjdk:8-jdk-alpine

ENV APP_DIR=/app
RUN mkdir -p $APP_DIR
WORKDIR $APP_DIR

COPY gradle gradle
COPY gradlew .
COPY . .
