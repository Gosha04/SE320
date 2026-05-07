# C4 Architecture Diagrams

This folder contains the C4 architecture for the Digital Therapy Assistant.

Files:

- `c4-context.puml`: system context diagram
- `c4-container.puml`: container diagram
- `c4-component.puml`: component diagram
- `c4-code.puml`: code-level diagrams, including one class diagram and two sequence diagrams

Project notes:

- The project root application module is `SE320App/`.
- The Java package is `com.SE320.therapy`.
- The running backend is a Spring Boot application with both REST API endpoints and an embedded CLI menu.
- The current datasource is H2.
- `UserController` is the authentication and user management controller.
- `DashboardController` is the progress and analytics controller.
- `JwtService` is the JWT generation and validation service.
- Crisis detection logic is implemented primarily in `OpenAiService.detectCrisis(...)` and `CrisisService`, rather than a standalone `CrisisDetector` class.
- External systems such as Email Service and EHR/FHIR are shown as integration points in the context diagram. They are not fully implemented in the current codebase.

PNG export:

- Source diagrams are ready in PlantUML format.
- To export PNGs locally, install a PlantUML renderer and generate:
  - `c4-context.png`
  - `c4-container.png`
  - `c4-component.png`
  - `c4-code.png`

Suggested VS Code setup:

1. Install the `PlantUML` extension by `jebbs`.
2. Keep Java installed.
3. For local rendering, install PlantUML and Graphviz, or configure the extension to use a PlantUML server.
4. Open each `.puml` file and export the PNG from the preview or command palette.
