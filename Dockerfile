FROM maven:3.9-amazoncorretto-21 AS build
VOLUME /tmp
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM openjdk:21
WORKDIR /app
COPY --from=build app/target/cirestechnologies-0.0.2.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]