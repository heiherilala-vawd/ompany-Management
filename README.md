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
