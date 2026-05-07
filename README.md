# SE320 Digital Therapy Assistant

A Spring Boot application for CBT-focused digital therapy workflows, including authentication, guided session chat, diary entries, progress tracking, and crisis support.

## Architecture Overview

This project follows a layered architecture with clear responsibility boundaries:

- **Controller layer** (`controller/`): HTTP API endpoints and request validation.
- **Service layer** (`service/`): Core business workflows (auth, sessions, diary, crisis, dashboard).
- **Persistence layer** (`repository/` + `entity/`): JPA entities and repositories for MySQL storage.
- **AI layer** (`AiService`, `OpenAiService`, `service/rag/`): OpenAI-backed and fallback logic for therapeutic responses, crisis detection, and CBT suggestions.
- **Cross-cutting layer** (`config/`, `exception/`, `dto/`): Security config, global error handling, and API contracts.

### Request/Data Flow

1. A request enters a controller endpoint.
2. Validation and input mapping happen in DTOs.
3. Services orchestrate rules, state changes, and AI calls.
4. Repositories persist and fetch entities from MySQL.
5. Standardized success/error responses are returned.

```text
Client -> Controller -> Service -> Repository -> MySQL
                         |\
                         | -> AI Service (OpenAI + RAG + fallback)
                         |
                         -> Domain/DTO mapping
```

### Security Model

- Spring Security protects all routes by default.
- Public auth routes:
  - `/auth/register`
  - `/auth/login`
  - `/auth/refresh`
- Session routes (`/sessions/**`) require `ROLE_PATIENT` or `ROLE_DOCTOR`.
- Unauthorized and forbidden responses are normalized through JSON error envelopes.

## Core Modules

- **Authentication**: register, login, refresh, logout, user listing, account delete.
- **Sessions**: CBT session library, session start/chat/end lifecycle.
- **Diary**: thought records, entries, insights, and cognitive distortion suggestions.
- **Dashboard/Progress**: trends, burnout recovery, and achievements.
- **Crisis**: crisis detection, coping strategies, and safety plan management.

## Tech Stack

- Java 17
- Spring Boot 4
- Spring MVC + Validation
- Spring Data JPA
- Spring Security
- MySQL
- springdoc OpenAPI (Swagger UI)
- JUnit + Mockito + Spring Test + JaCoCo

## Project Structure

```text
SE320/
├── README.md
└── SE320App/
    ├── pom.xml
    ├── docs/ERD.md
    ├── src/main/java/com/SE320/therapy/
    │   ├── config/
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── exception/
    │   ├── repository/
    │   ├── service/
    │   └── cli/commands/
    └── src/main/resources/
        ├── application.properties
        ├── schema.sql
        └── data.sql
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8+

### 1) Configure environment variables

```bash
export MYSQL_URL="jdbc:mysql://localhost:3306/digitaltherapy_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export MYSQL_USER=[username]
export MYSQL_PASSWORD=[password]

# Enables real OpenAI calls; hardcoded fallback exists for crisis management
export OPENAI_API_KEY="your_api_key"

# Optional: override default insecure dev secret
export JWT_SECRET="replace-with-a-long-random-secret"
```

### 2) Run the app

```bash
cd SE320App
mvn spring-boot:run
```

The legacy CLI remains available for local troubleshooting, but the web app is the default interface. To temporarily run the CLI:

```bash
cd SE320App
mvn spring-boot:run -Dspring-boot.run.arguments=--app.cli.enabled=true
```

### 3) Run the frontend

The primary UI is now the single-page frontend in `SE320App/frontend`. It runs on port `3000` and communicates with the Spring Boot API on port `8080` through REST endpoints.

```bash
cd SE320App/frontend
npm install
npm run dev
```

Open `http://localhost:3000`.

The frontend includes `"proxy": "http://localhost:8080"` in `package.json` and Next.js rewrites for local development, so browser requests to `/auth`, `/sessions`, `/diary`, `/progress`, and `/crisis` are forwarded to the backend.

### 4) Open API docs

- Swagger UI: `http://localhost:8080/swagger-ui`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Key API Endpoints

### Auth

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `DELETE /auth/delete`
- `GET /auth/users`

### Sessions

- `GET /sessions`
- `GET /sessions/{sessionId}`
- `POST /sessions/{sessionId}/start`
- `POST /sessions/{sessionId}/chat`
- `POST /sessions/{sessionId}/end`

### Diary

- `POST /diary/entries?userId={uuid}`
- `GET /diary/entries?userId={uuid}`
- `GET /diary/entries/{entryId}`
- `DELETE /diary/entries/{entryId}`
- `GET /diary/insights?userId={uuid}`
- `POST /diary/distortions/suggest`

### Crisis

- `GET /crisis?userId={uuid}`
- `POST /crisis/crisis/detect`
- `GET /crisis/crisis/coping-strategies`
- `GET /crisis/crisis/safety-plan?userId={uuid}`
- `PUT /crisis/crisis/safety-plan?userId={uuid}`

### Progress

- `GET /progress?userId={uuid}`
- `GET /progress/progress/monthly?userId={uuid}`
- `GET /progress/progress/weekly?userId={uuid}`
- `GET /progress/progress/burnout?userId={uuid}`
- `GET /progress/progress/achievements?userId={uuid}`
- `POST /progress/progress/achievements?userId={uuid}`
- `PUT /progress/progress/achievements/{achievementId}?userId={uuid}`
- `DELETE /progress/progress/achievements/{achievementId}?userId={uuid}`

## Testing

```bash
cd SE320App
mvn test
```

Generate coverage report:

```bash
cd SE320App
mvn verify
```

JaCoCo report is generated under `SE320App/target/site/jacoco/`.

## MCP Server

The app includes a Spring AI MCP server that exposes the Digital Therapy Assistant service layer to MCP-compatible AI clients. The MCP layer keeps using the existing backend services and OpenAI/RAG implementation, so the AI behavior stays consistent with the REST API.

### Run In STDIO Mode

Build the jar first:

```bash
cd SE320App
mvn package
```

Start the MCP server over standard input/output:

```bash
java -jar target/SE320App-1.0-SNAPSHOT-exec.jar --app.cli.enabled=false --spring.ai.mcp.server.enabled=true --spring.ai.mcp.server.stdio=true --spring.main.web-application-type=none --spring.main.banner-mode=off --logging.level.root=OFF
```

`--app.cli.enabled=false` is required because the old CLI also uses stdin/stdout. In MCP stdio mode, the AI client owns those streams and exchanges JSON-RPC messages with the Spring Boot process.

### Client Configuration

A Claude Desktop-compatible MCP config is provided at:

- `SE320App/docs/claude_desktop_mcp_config.json`

The server is not Claude-specific. Claude Desktop is just a convenient MCP host for testing; the tool implementations still use the existing OpenAI-backed `AiService`.

### Tools

- `start_session(userId, sessionId)`
- `chat_in_session(sessionId, message)`
- `end_session(sessionId, reason)`
- `get_session_library(userId)`
- `get_session_history(userId)`
- `create_diary_entry(userId, situation, automaticThought, emotions)`
- `analyze_thought(thought)`
- `suggest_reframing(thought, distortionIds)`
- `detect_crisis(text)`
- `get_weekly_progress(userId)`
- `get_insights(userId)`
- `get_coping_strategies()`

For `chat_in_session` and `end_session`, use the `userSessionId` returned by `start_session` as the active `sessionId`.

### Resources

- `therapy://sessions/{sessionId}`
- `therapy://diary/{userId}`
- `therapy://diary/entry/{entryId}`
- `therapy://progress/{userId}`
- `therapy://distortions`
- `therapy://crisis/resources`
- `therapy://safety-plan/{userId}`

### Prompts

- `thought_analysis(thought)`
- `session_summary(sessionId)`
- `weekly_check_in(userId)`

## Data Model

The ERD is documented in:

- `SE320App/docs/ERD.md`

## Notes

- `schema.sql` and `data.sql` seed core CBT and coping data on startup.
- If `OPENAI_API_KEY` is not set, AI features still work using fallback logic.
- Authentication uses JWT access and refresh tokens signed with `jwt.secret`.
