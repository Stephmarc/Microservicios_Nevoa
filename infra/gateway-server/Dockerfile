FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/gateway-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 7091
ENTRYPOINT ["java", "-jar", "app.jar"]