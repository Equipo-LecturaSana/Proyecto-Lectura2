FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/LecturaSana-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar","--server.port=8081","--spring.profiles.active=prod"]
