# AGENTS.md

## Commands
- Build: `./gradlew build`
- Test all: `./gradlew test`
- Single test: `./gradlew test --tests "com.example.demo.integration.money.ExpenseIT"`
- Format code: `./gradlew spotlessApply`
- Checkstyle: `./gradlew checkstyleMain`
- Generate API client: `./gradlew generateJavaClient`
- Publish client locally: `./gradlew publishJavaClientToMavenLocal`

## Workflow
- `compileJava` depends on `publishJavaClientToMavenLocal` (API client must be generated before compilation)
- Testcontainers requires Docker socket at `/var/run/docker.sock`
- JaCoCo coverage thresholds: 50% lines, 40% branches (fails build if not met)
- Flyway migrations in `src/main/resources/db/migration/`, test data in `db/testdata/`

## Architecture
- Spring Boot 4.0.5, Java 21, PostgreSQL with Flyway
- API defined in `src/main/resources/api/api.yml` (OpenAPI 3.0)
- Generated client code: `build/gen/` → published to Maven local → used in compilation
- Layers: Controller → Mapper (API↔Domain) → Service → Repository → Entity
- All entities extend `CreatAndUpdateEntity` for audit fields (created/updated by/at)
- Security: JWT with `@PreAuthorize` roles (ADMIN, ADMINISTRATION, etc.)

## Conventions
- Tests: `*IT.java` in `src/test/java/.../integration/` use Testcontainers + PostgreSQL
- Excluded from coverage: `**/client/**`, `**/model/**`, `**/api/**`, `**/invoker/**`, `**/dto/**`, `**/config/**`
- Spotless excludes generated client code (`**/client/**`)
- Checkstyle config: `config/checkstyle/google_checks_custom.xml`

## File Map - Read these files based on your task

### Task: Add/modify API endpoint
1. `src/main/resources/api/api.yml` - OpenAPI definition
2. `src/main/java/com/example/demo/endpoint/rest/controller/{domain}/` - Controller
3. `src/main/java/com/example/demo/endpoint/rest/mapper/{domain}/` - Mapper
4. `src/main/java/com/example/demo/client/model/` - Generated DTOs (after build)
5. `src/test/java/com/example/demo/integration/{domain}/` - Integration tests

### Task: Add new entity/domain
1. `src/main/resources/api/api.yml` - Add schema and paths
2. `src/main/java/com/example/demo/model/{domain}/` - Entity class
3. `src/main/java/com/example/demo/repository/{domain}/` - Repository
4. `src/main/java/com/example/demo/service/{domain}/` - Service
5. `src/main/java/com/example/demo/endpoint/rest/controller/{domain}/` - Controller
6. `src/main/java/com/example/demo/endpoint/rest/mapper/{domain}/` - Mapper
7. `src/main/resources/db/migration/` - New Flyway migration
8. `src/main/resources/db/testdata/` - Test data SQL
9. `src/test/java/com/example/demo/integration/{domain}/` - Integration test

### Task: Fix bug in existing feature
1. Identify domain from error: `model/{domain}/`, `service/{domain}/`, `controller/{domain}/`
2. `src/test/java/com/example/demo/integration/{domain}/` - Related tests
3. `src/main/resources/db/migration/` - Related migrations if data issue

### Task: Understand architecture/flow
1. `src/main/java/com/example/demo/Demo2Application.java` - Entry point
2. `src/main/resources/api/api.yml` - API surface
3. `src/main/java/com/example/demo/endpoint/rest/controller/` - All endpoints
4. `src/main/java/com/example/demo/service/` - Business logic
5. `build.gradle.kts` - Dependencies and build config

### Task: Run or debug tests
1. `src/test/java/com/example/demo/integration/conf/` - Test config (AbstractContextInitializer, TestUtils, TestDataSqlLoader)
2. `src/main/resources/db/testdata/` - Test data SQL files
3. `src/main/resources/db/testdata/testcontainers.properties` - Docker config
4. Specific `*IT.java` file in `src/test/java/com/example/demo/integration/{domain}/`

### Important file references
- `src/main/java/com/example/demo/model/CreatAndUpdateEntity.java` - Base entity class
- `src/main/java/com/example/demo/service/utils/ModificationUtils.java` - Audit field handling
- `src/main/java/com/example/demo/endpoint/rest/security/` - JWT/Security config
- `src/main/resources/application.properties` - App config (dev profile)
- `build.gradle.kts` - Build, dependencies, code generation config
- `config/checkstyle/google_checks_custom.xml` - Checkstyle rules

### Domains in this project
- **money**: ExpenseMoney, IncomeMoney, EmployeePayment, TravelExpense, Purchase, BankFee, OtherExpense, PurchaseOperation
- **movement**: Material, Equipment, Warehouse, TravelEquipment, TravelPeople, TravelMaterials, TravelOperation, MaterialWarehouse
- **core**: Company, Job, User, History

## Maintenance
Update this File Map when:
- New domains/features are added (add to Domains list)
- New file patterns emerge (e.g., new layer, new config)
- File purposes change (refactoring)
- New test patterns or config files are introduced
