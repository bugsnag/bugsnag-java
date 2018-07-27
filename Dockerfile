FROM openjdk:8-jdk-alpine

ENV APP_DIR=/app
RUN mkdir -p $APP_DIR
WORKDIR $APP_DIR
EXPOSE 8080

COPY gradle gradle
COPY gradlew .
RUN ./gradlew build
COPY . .

CMD ["./gradlew", ":examples:spring-web:bootrun"]
