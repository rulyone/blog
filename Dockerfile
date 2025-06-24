FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
RUN mkdir -p /srv/images
COPY build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
