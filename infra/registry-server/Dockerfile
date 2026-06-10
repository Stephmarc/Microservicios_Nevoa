FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/registry-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 7081
ENTRYPOINT ["java", "-jar", "app.jar"]