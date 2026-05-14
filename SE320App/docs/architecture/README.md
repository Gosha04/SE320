# C4 Architecture Diagrams

This folder contains the C4 architecture and delivery diagrams for the Digital Therapy Assistant.

Files:

- `c4-context.puml` / `c4-context.png`: Level 1 system context showing users, AI clients over MCP, GitHub automation, GHCR, AWS EC2, and external services.
- `c4-container.puml` / `c4-container.png`: Level 2 container view showing the Nginx frontend on port 3000, Spring Boot backend on port 8080, embedded MCP stdio server, H2 volume, SimpleVector volume, and reverse proxy flow.
- `c4-component.puml` / `c4-component.png`: Level 3 backend component view from the earlier assignment.
- `c4-code.puml` / `c4-code.png`: code-level diagrams, including one class diagram and two sequence diagrams.
- `c4-deployment.puml` / `c4-deployment.png`: deployment view for AWS EC2, Elastic IP access, Docker Compose, security group rules, secrets, caches, image pulls, and persistent volume mounts.
- `c4-pipeline.puml` / `c4-pipeline.png`: CI/CD/CD stage-gate pipeline showing CI build/test/quality/security gates, image build and smoke test, and EC2 deployment verification.

Architecture notes:

- The project root application module is `SE320App/`.
- The Java package is `com.SE320.therapy`.
- The production stack is orchestrated by `docker-compose.yaml`.
- The frontend container serves the React build through Nginx on port `3000`.
- Nginx reverse proxies `/auth`, `/sessions`, `/diary`, `/progress`, and `/crisis` traffic to the backend service at `backend:8080`.
- The backend container runs the Spring Boot Java application on port `8080`.
- The MCP server runs in stdio mode inside the Spring Boot backend process when `MCP_ENABLED` and `MCP_STDIO` are enabled.
- H2 data is persisted in the `h2-data` Docker volume mounted at `/app/data/h2`.
- SimpleVector data is persisted in the `vector-store` Docker volume mounted at `/app/data/vector`.
- The deployment target is an AWS EC2 instance with an Elastic IP and security group ingress for SSH `22`, frontend `3000`, and backend `8080`.
- GitHub Actions provide the CI/CD/CD pipeline, GitHub Container Registry stores Docker images, and GitHub secrets provide EC2 and application credentials during deployment.
- External systems such as Email Service and EHR/FHIR are shown as integration points. They are not fully implemented in the current codebase.

Pipeline summary:

- CI runs on pushes and pull requests to `main` and `develop`.
- CI builds the backend and frontend first, then runs unit tests, integration tests, code quality checks, dependency checks, and security scanning in parallel.
- CD build runs on pushes to `main`, builds Docker images, pushes them to GHCR, and smoke tests the composed stack.
- CD deploy runs after successful delivery or manual dispatch, deploys to EC2 with Docker Compose, and verifies the backend OpenAPI endpoint plus the frontend root route.

PNG export:

- Source diagrams are in PlantUML format.
- Current PNG exports are committed beside their `.puml` sources.
- To regenerate locally, install PlantUML and Graphviz, then run PlantUML over this directory.

Suggested VS Code setup:

1. Install the `PlantUML` extension by `jebbs`.
2. Keep Java installed.
3. For local rendering, install PlantUML and Graphviz, or configure the extension to use a PlantUML server.
4. Open each `.puml` file and export the PNG from the preview or command palette.
