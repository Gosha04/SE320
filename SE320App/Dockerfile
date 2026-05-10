FROM maven:3.9-eclipse-temurin-22 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package \
    && JAR_FILE="$(mvn -q -DforceStdout help:evaluate -Dexpression=project.build.finalName)" \
    && cp "target/${JAR_FILE}-exec.jar" target/app.jar

FROM eclipse-temurin:22-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app \
    && mkdir -p /app/data/h2 /app/data/vector \
    && chown -R app:app /app/data

ENV OPENAI_API_KEY="key-here" \
    OPENAI_CHAT_MODEL="gpt-4o-mini" \
    OPENAI_BASE_URL="https://api.openai.com/v1" \
    JWT_SECRET="secret-here" \
    JWT_EXPIRATION="3600000" \
    JWT_REFRESH_EXPIRATION="86400000"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget -q -O /dev/null http://localhost:8080/v3/api-docs || exit 1

COPY --from=build --chown=app:app /app/target/app.jar ./app.jar

USER app
