FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
RUN mkdir -p /srv/images
COPY build/libs/*-SNAPSHOT.jar app.jar
# Copy resources to allow faster debugging
COPY src/main/resources/templates /app/src/main/resources/templates
COPY src/main/resources/static /app/src/main/resources/static
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
