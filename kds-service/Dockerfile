FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8089

EXPOSE 8089

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar --server.port=${PORT:-8089}"]
