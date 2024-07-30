FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]