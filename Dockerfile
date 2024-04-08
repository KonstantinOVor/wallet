FROM maven:3.9.6-eclipse-temurin-17 AS build

COPY src src
COPY  pom.xml pom.xml

RUN mvn clean package

FROM bellsoft/liberica-openjdk-debian:17

RUN adduser --system spring-boot && addgroup --system spring-boot && adduser spring-boot spring-boot
USER spring-boot

WORKDIR /app

COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]