# AGENTS.md

## Environment Variables

See `.env.example` and `.github/workflows/ci.yml` for the full set. Dev defaults are in `application.properties`.

## Commands
- Full build (generate client → compile → test → check): `./gradlew build`
- Single IT: `./gradlew test --tests "com.example.demo.integration.money.ExpenseIT"`
- Generate API client only: `./gradlew generateJavaClient`
- Publish client + copy to src: `./gradlew publishJavaClientToMavenLocal`
- Format all Java: `./gradlew spotlessApply`
- Checkstyle: `./gradlew checkstyleMain`
- Coverage report: `./gradlew jacocoTestReport`

## Build gotchas
- `compileJava` **depends on** `publishJavaClientToMavenLocal` — the API client must be generated & copied into `src/main/java/.../client/` before compilation. This happens automatically via `./gradlew build`.
- Generated client code is physically copied by `.shell/publish_gen_to_maven_local.sh` from `build/gen/` to `src/main/java/com/example/demo/client/`. It is then compiled as part of the main source tree (not pulled from Maven).
- JaCoCo thresholds: **80%** line, **40%** branch coverage. Build fails if not met.
- JaCoCo exclusions: `**/client/**`, `**/model/**`, `**/api/**`, `**/invoker/**`, `**/dto/**`, `**/config/**`, `**/*Application.class`, `**/repository/Dao/**`, `**/service/utils/**`
- Spotless excludes: `**/client/**`, `**/gen/**`, `**/generated/**`, `**/build/**`
- Docker system property set by Gradle: `api.version=1.44` (mapped as `systemProperty`, not read from env var `DOCKER_API_VERSION`)

## Architecture
- **Spring Boot 4.0.5** · Java 21 · PostgreSQL · Flyway · JWT
- OpenAPI-first: `src/main/resources/api/api.yml` is the single source of truth. Always edit `api.yml` first, then regenerate the client.
- Layers: Controller → Mapper (generated DTO ↔ domain entity) → Service (@Transactional) → Repository (JPA) → Entity
- **Crupdate pattern**: `PUT` endpoints combine create + update. If the client-provided ID exists → update; if null/not found → create.
- All entities extend `CreatAndUpdateEntity` (audit fields: createdAt, updatedAt, createdBy, updatedBy, comment). Use `@SuperBuilder` (not `@Builder`).
- Security: `@EnableMethodSecurity` + `@PreAuthorize` roles (ADMIN, ADMINISTRATION, WAREHOUSE_WORKER, EMPLOYEE).
- **SelfMatcher**: custom `RequestMatcher` that lets users access their own resources by comparing URL ID with authenticated user ID.
- `@RestControllerAdvice` global handler in `InternalToRestExceptionHandler` maps domain exceptions → standardized JSON `{type, message}`.

## Test patterns
- **ITs** follow: `@SpringBootTest(webEnvironment = RANDOM_PORT)`, inner class `ContextInitializer extends AbstractContextInitializer`, `@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)`.
- Standard mocks with `@MockitoBean`: `SentryConf`, `AuthenticationManager`, and `JwtUtils` (except AuthIT and YearlyReportIT which use real JWT).
- `TestAuthSupport.setUpJwtService(jwtUtils)` maps token strings → emails in the mock.
- `TestDataSqlLoader.reloadTestData(dataSource)` truncates all tables and re-runs testdata SQL before each test (no `@Transactional`).
- Static test constants in `TestUtils.java`: `ADMIN_ID`, `USER1_ID`, `COMPANY1_ID`, etc., plus token constants like `ADMIN_TOKEN` (= `"Bearer admin@email.com"`).
- Unit tests use pure Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`/`@InjectMocks`).
- Testcontainers starts a real PostgreSQL per test class. Docker socket at `/var/run/docker.sock` must be available.

## Domains & packages
- **core**: `Company`, `Job`, `User`, `History`, `Department`, `Team`
- **money**: `ExpenseMoney`, `IncomeMoney`, `EmployeePayment`, `TravelExpense`, `Purchase`, `BankFee`, `OtherExpense`, `Loan`, `IncomeReceipt`, `PurchaseOperation`, `CompanyFixedCost`
- **movement**: `Material`, `Equipment`, `Warehouse`, `TravelEquipment`, `TravelPeople`, `TravelMaterials`, `TravelOperation`, `MaterialWarehouse`, `EquipmentUsage`, `MaterialConsumption`
- **hr**: `Leave`, `LeaveType`, `EmployeeLeaveConfig`
- **task**: `Task`, `TaskSchedule`

## Repository skills
- `.agents/skills/skill-creator/SKILL.md`
- `.agents/skills/custom-exception-handling/SKILL.md`
- `.agents/skills/openapi-client-generation/SKILL.md`
- `.agents/skills/jacoco-ci-setup/SKILL.md`
- `.agents/skills/spring-boot-mvc-architecture/SKILL.md`
- `.agents/skills/java-code-quality/SKILL.md`
- `.agents/skills/spring-security-jwt/SKILL.md`
- `.agents/skills/spring-boot-testing/SKILL.md`

## Skill creation rules
- Skills must be **project-agnostic**: use `{basePackage}` placeholders, never concrete packages like `com.example.demo`.
- Exception handler skills must define their own `ExceptionResponse` DTO, not reference generated client classes.
- No "Référence projet" section pointing to this project.
