# Demo2

A modular Spring Boot REST API for managing financial operations, asset movements, and core business entities. Built with Java 21, PostgreSQL, and an OpenAPI-first design approach.

## Tech Stack
- **Backend**: Spring Boot 4.0.5, Java 21
- **Database**: PostgreSQL, Flyway (database migrations)
- **API**: OpenAPI 3.0 (defined in `api.yml`), auto-generated Java/TypeScript clients
- **Security**: JWT authentication with role-based access control (`@PreAuthorize`)
- **Testing**: Testcontainers (PostgreSQL), JUnit 5, JaCoCo (coverage reporting)
- **Code Quality**: Spotless (formatting), Checkstyle (static analysis), SonarQube (optional)
- **Build**: Gradle (Kotlin DSL), Lombok

## Architecture
The project follows a layered architecture with clear separation of concerns:
1. **Controller Layer**: REST endpoints handling HTTP requests, defined via the OpenAPI specification
2. **Mapper Layer**: Converts between API DTOs (generated from OpenAPI) and domain models
3. **Service Layer**: Core business logic implementation
4. **Repository Layer**: Data access using Spring Data JPA
5. **Entity Layer**: JPA entities mapped to database tables. All entities extend `CreatAndUpdateEntity` for automatic audit fields (created/updated by/at)

### Key Workflow
- The OpenAPI specification (`src/main/resources/api/api.yml`) is the single source of truth for the API
- Java client code is auto-generated from the OpenAPI spec to `build/gen/`, published to the local Maven repository, and used during compilation
- The `compileJava` task depends on `publishJavaClientToMavenLocal` to ensure the generated client is available before compilation

### Domains
The project is organized into three core business domains:
- **money**: ExpenseMoney, IncomeMoney, EmployeePayment, TravelExpense, Purchase, BankFee, OtherExpense, PurchaseOperation
- **movement**: Material, Equipment, Warehouse, TravelEquipment, TravelPeople, TravelMaterials, TravelOperation, MaterialWarehouse
- **core**: Company, Job, User, History

## Project Structure
```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── endpoint/rest/controller/{domain}/  # REST controllers
│   │   ├── endpoint/rest/mapper/{domain}/      # DTO ↔ Domain object mappers
│   │   ├── model/{domain}/                     # JPA entities
│   │   ├── repository/{domain}/                # Data access interfaces
│   │   ├── service/{domain}/                   # Business logic services
│   │   ├── client/                             # Auto-generated API client (excluded from code checks)
│   │   ├── config/                             # Application configuration
│   │   └── security/                           # JWT and security configuration
│   └── resources/
│       ├── api/api.yml                         # OpenAPI 3.0 specification
│       ├── db/migration/                       # Flyway migration scripts
│       ├── db/testdata/                        # Test data SQL files
│       └── application.properties              # Application configuration (dev profile)
└── test/
    └── java/com/example/demo/integration/{domain}/  # Integration tests (*IT.java)
build/
└── gen/                                         # Auto-generated API client code
```

## Prerequisites
- Java 21 JDK
- Docker (required for Testcontainers to run integration tests)
- Gradle (or use the included Gradle wrapper `./gradlew`)

### Test Environment Setup
Integration tests start PostgreSQL with Testcontainers, so every development machine must have a working local Docker daemon.

On Ubuntu/Debian, install the required tools:
```bash
sudo apt update
sudo apt install -y openjdk-21-jdk docker.io
```

Enable Docker access for the current user:
```bash
sudo usermod -aG docker $USER
```

After running `usermod`, log out completely and log back in. Restart IntelliJ or any terminal that will run the tests.

Verify the environment:
```bash
java -version
id
docker info
```

Expected checks:
- `java -version` must show Java 21
- `id` must include the `docker` group
- `docker info` must show the Docker `Server` section without `permission denied`

Set Docker environment variables before running tests from a terminal:
```bash
export DOCKER_HOST=unix:///var/run/docker.sock
export DOCKER_API_VERSION=1.44
```

Then run:
```bash
./gradlew test
```

For a one-shot command:
```bash
DOCKER_HOST=unix:///var/run/docker.sock DOCKER_API_VERSION=1.44 ./gradlew test
```

When running tests from IntelliJ with Gradle, add these values in `Run > Edit Configurations > Environment variables`:
```text
DOCKER_HOST=unix:///var/run/docker.sock;DOCKER_API_VERSION=1.44
```

When running a JUnit test directly from IntelliJ instead of Gradle, also add this VM option:
```text
-Dapi.version=1.44
```

The Gradle test task already sets `api.version=1.44` for Testcontainers/docker-java, which is required for Docker versions whose minimum supported API version is 1.44.

## Environment Variables

The project uses environment variables for configuration. Spring Boot maps env vars automatically (e.g. `SPRING_DATASOURCE_URL` → `spring.datasource.url`).

### All Variables

| Variable | Description | Default (dev) | Required In |
|---|---|---|---|
| Variable | Description | Exemple de valeur | Requis |
|---|---|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL PostgreSQL | `jdbc:postgresql://localhost:5432/ma_base` | Production |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur DB | `postgres` ou `admin` | Production |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe DB | `monMotDePasse123` | Production |
| `JWT_SECRET_KEY` | Clé HMAC-SHA256 (≥256 bits) | `a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1` | Production, Tests |
| `JWT_EXPIRATION_TIME` | Durée validité token (ms) | `86400000` (24h) | Optional |
| `SENTRY_DSN` | DSN Sentry | `https://examplePublicKey@o123456.ingest.sentry.io/1234567` | Production |
| `ENV` | Environnement de déploiement | `development`, `staging`, `production` | Production |
| `SECURITY_BCRYPT_STRENGTH` | Nombre de rounds BCrypt | `10` | Optional |
| `DOCKER_HOST` | Chemin du socket Docker | `unix:///var/run/docker.sock` | Tests |
| `DOCKER_API_VERSION` | Version de l'API Docker | `1.44` | Tests |
| `TC_POSTGRES_IMAGE` | Tag de l'image PostgreSQL Testcontainers | `postgres:15.2` | Tests |
| `TC_DB_NAME` | Nom de la base de test | `test-db` | Tests |
| `TC_DB_USERNAME` | Utilisateur DB de test | `test` | Tests |
| `TC_DB_PASSWORD` | Mot de passe DB de test | `test` | Tests |
| `TESTCONTAINERS_REUSE_ENABLE` | Réutiliser les conteneurs | `true` | Tests |
| `SONAR_PROJECT_KEY` | Clé projet SonarCloud | `mon-projet-key` | CI |
| `SONAR_ORGANIZATION` | Organisation SonarCloud | `mon-org-github` | CI |
| `SONAR_HOST_URL` | URL du serveur Sonar | `https://sonarcloud.io` | CI |
| `SONAR_TOKEN` | Token d'authentification Sonar | *(secret GitHub)* | CI |

### Copy-paste for IntelliJ

Replace `<...>` placeholders with your actual values before use.

#### Dev (Run Configuration > Environment variables)
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/<db-name>;SPRING_DATASOURCE_USERNAME=<db-user>;SPRING_DATASOURCE_PASSWORD=<db-password>;JWT_SECRET_KEY=<jwt-secret-key-min-256-bits>;JWT_EXPIRATION_TIME=86400000;ENV=development;SECURITY_BCRYPT_STRENGTH=10
```

#### Tests (Run Configuration > Environment variables)
```
DOCKER_API_VERSION=1.44;DOCKER_HOST=unix:///var/run/docker.sock;TC_POSTGRES_IMAGE=postgres:15.2;TC_DB_NAME=<test-db-name>;TC_DB_USERNAME=<test-db-user>;TC_DB_PASSWORD=<test-db-password>;JWT_SECRET_KEY=<jwt-secret-key>;JWT_EXPIRATION_TIME=86400000
```

#### Production / CI (GitHub Secrets)
```
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<db>;SPRING_DATASOURCE_USERNAME=<user>;SPRING_DATASOURCE_PASSWORD=<pass>;JWT_SECRET_KEY=<256-bit-key>;JWT_EXPIRATION_TIME=86400000;SENTRY_DSN=<sentry-dsn>;ENV=production
```

### Reference Files
- `.env.example` — All documented variables with placeholders
- `.github/workflows/ci.yml` — CI pipeline with env vars configured

## Getting Started
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd demo2
   ```
2. Build the project (generates API client, compiles code, runs tests and quality checks):
   ```bash
   ./gradlew build
   ```
3. Run the application (ensure a PostgreSQL instance is running and configured in `application.properties` for the dev profile):
   ```bash
   ./gradlew bootRun
   ```

## Available Commands
| Command | Description |
|---------|-------------|
| `./gradlew build` | Full build: generate client, compile, test, run code quality checks |
| `./gradlew test` | Run all tests |
| `./gradlew test --tests "com.example.demo.integration.money.ExpenseIT"` | Run a single integration test |
| `./gradlew spotlessApply` | Format code using Google Java Format |
| `./gradlew checkstyleMain` | Run static code analysis with Checkstyle |
| `./gradlew generateJavaClient` | Generate Java API client from OpenAPI spec |
| `./gradlew generateTsClient` | Generate TypeScript Axios client from OpenAPI spec |
| `./gradlew publishJavaClientToMavenLocal` | Publish generated Java client to local Maven repository |
| `./gradlew jacocoTestReport` | Generate code coverage report |
| `./gradlew bootRun` | Start the Spring Boot application |

## Code Quality
### Formatting (Spotless)
Uses Google Java Format. Auto-format code with `./gradlew spotlessApply`. Generated client code (`**/client/**`) is excluded from formatting rules.

### Static Analysis (Checkstyle)
Uses custom Google Checkstyle rules defined in `config/checkstyle/google_checks_custom.xml`. Run checks with `./gradlew checkstyleMain`.

### Coverage (JaCoCo)
Enforces minimum coverage thresholds:
- 50% line coverage
- 40% branch coverage
The build will fail if these thresholds are not met. The following are excluded from coverage reports: `**/client/**`, `**/model/**`, `**/api/**`, `**/invoker/**`, `**/dto/**`, `**/config/**`.

### SonarQube (Optional)
Configure SonarQube properties in `build.gradle.kts` to enable continuous code analysis.

## Testing
- Integration tests use **Testcontainers** with a PostgreSQL Docker container
- Test files follow the `*IT.java` naming convention and are located in `src/test/java/.../integration/{domain}/`
- Test data is loaded from SQL files in `src/main/resources/db/testdata/`
- Docker socket must be available at `/var/run/docker.sock` for Testcontainers to work

Run all tests:
```bash
./gradlew test
```

Run a single test:
```bash
./gradlew test --tests "com.example.demo.integration.money.ExpenseIT"
```

Generate a coverage report:
```bash
./gradlew jacocoTestReport
```
The HTML report is available at `build/reports/jacoco/jacocoTestReport/html/index.html`.

## Contributing
1. Fork the repository and create a new branch for your feature or bugfix.
2. Follow the project's conventions and architecture:
   - Use the OpenAPI-first workflow: update `src/main/resources/api/api.yml` first for any API changes
   - Add Flyway migrations for database changes in `src/main/resources/db/migration/`
   - Add test data in `src/main/resources/db/testdata/` if required
   - Write integration tests for all new features
   - All new entities must extend `CreatAndUpdateEntity` to include audit fields
3. Ensure all code quality checks pass before committing:
   ```bash
   ./gradlew spotlessApply
   ./gradlew checkstyleMain
   ./gradlew test
   ```
4. Commit your changes (follow conventional commit message guidelines if possible).
5. Open a pull request to the main branch.

## Important Notes
- Integration tests require Docker to be running (Testcontainers dependency)
- The API client is automatically generated and published to local Maven before compilation; do not manually modify code in `build/gen/`
- JaCoCo coverage thresholds are enforced, and the build will fail if they are not met
