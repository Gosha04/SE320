# SE320App Digital Therapy Assistant

SE320App is a full-stack Digital Therapy Assistant project. The backend is a Spring Boot API with H2 persistence, JWT authentication, OpenAPI/Swagger documentation, and optional OpenAI-powered therapy support. The frontend is a Next.js application in the `frontend` directory.

## Deployed Application

The deployed project is available at:

- Frontend: http://3.23.255.252:3000
- Swagger UI: http://3.23.255.252:8080/swagger-ui/index.html

## Local Run Info 
- docker compose up -d --build
- docker compose down
- This is handled via github action because of API keys and stuff but one can do it manually if they wish

## Tech Stack

- Java 17
- Spring Boot
- Maven
- H2 database
- Next.js
- React
- Docker and Docker Compose

## Run With Docker Compose

From the project root:

```bash
docker compose up --build
```

Then open:

- Frontend: http://localhost:3000
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

To stop the application:

```bash
docker compose down
```

Docker Compose creates persistent volumes for the H2 database and vector store.

## Run Locally

### Backend

From the project root:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--app.cli.enabled=false
```

The backend runs on http://localhost:8080.

Useful backend URLs:

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- H2 Console: http://localhost:8080/h2-console

### Frontend

In a second terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on http://localhost:3000.

## Environment Variables

The backend supports the following optional environment variables:

- `OPENAI_API_KEY`: API key for OpenAI-backed assistant features.
- `OPENAI_BASE_URL`: OpenAI-compatible API base URL. Defaults to `https://api.openai.com/v1`.
- `OPENAI_CHAT_MODEL`: Chat model name.
- `JWT_SECRET`: Secret used to sign JWTs.
- `JWT_EXPIRATION`: Access token expiration in milliseconds.
- `JWT_REFRESH_EXPIRATION`: Refresh token expiration in milliseconds.
- `H2_URL`: H2 JDBC URL.
- `H2_USER`: H2 username.
- `H2_PASSWORD`: H2 password.
- `VECTOR_STORE_PATH`: Path to the vector store file.
- `MCP_ENABLED`: Enables the MCP server when set to `true`.
- `MCP_STDIO`: Enables MCP stdio mode when set to `true`.

## Testing

Run backend tests from the project root:

```bash
mvn test
```

Run frontend linting from the `frontend` directory:

```bash
npm run lint
```
