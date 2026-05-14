# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B \
    && JAR_FILE="$(mvn -q -DforceStdout help:evaluate -Dexpression=project.build.finalName)" \
    && cp "target/${JAR_FILE}-exec.jar" target/app.jar

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/app.jar app.jar
RUN mkdir -p /app/data
VOLUME /app/data
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
