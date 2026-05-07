# MindBridge Frontend Integration

This folder contains the Next.js single-page frontend for the Digital Therapy Assistant Spring Boot backend.

## Run Locally

From `SE320App`, start the backend API:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--app.cli.enabled=false
```

From `SE320App/frontend`, start the frontend:

```bash
npm install
npm run dev
```

Open `http://localhost:3000`.

## Backend Connection

The frontend uses relative REST API calls such as `/auth/login`, `/sessions`, and `/diary/entries`. During development, `package.json` includes:

```json
{
  "proxy": "http://localhost:8080"
}
```

`next.config.mjs` also defines rewrites that forward `/auth`, `/sessions`, `/diary`, `/progress`, and `/crisis` from port `3000` to the Spring Boot backend on port `8080`.

Override the API base URL only when you do not want to use the dev proxy:

```bash
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

## Authentication

The app has dedicated registration and login forms. JWT access tokens, refresh tokens, and the user id are stored in `localStorage`; on reload, the app calls `/auth/refresh` to restore the session. Logout calls `/auth/logout` and clears local token storage.

The Spring backend remains the source of truth. The frontend API service layer lives in `lib/api.ts`.
