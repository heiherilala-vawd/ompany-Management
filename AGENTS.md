# AGENTS.md

## Environment Variables
See `.env.example` and `application.properties` for dev defaults. Docker API version is set by Gradle as `systemProperty("api.version", "1.44")`, NOT read from env var `DOCKER_API_VERSION`.

## Commands
- Full build: `./gradlew build`
- Single IT: `./gradlew test --tests "com.example.demo.integration.money.ExpenseIT"`
- Single unit test: `./gradlew test --tests "com.example.demo.Service.UserServiceTest"`
- Generate Java client: `./gradlew generateJavaClient`
- Generate TS client: `./gradlew generateTsClient`
- Publish client + copy to src: `./gradlew publishJavaClientToMavenLocal`
- Format: `./gradlew spotlessApply`
- Checkstyle: `./gradlew checkstyleMain`
- Coverage report: `./gradlew jacocoTestReport`
- Coverage verification: `./gradlew jacocoTestCoverageVerification`
- Run app: `./gradlew bootRun` (or `./run.sh` which sources `.env` first)

## Build gotchas
- `compileJava` **depends on** `publishJavaClientToMavenLocal` — generated code is physically copied by `.shell/publish_gen_to_maven_local.sh` from `build/gen/` to `src/main/java/com/example/demo/client/` and compiled as part of the main source tree.
- JaCoCo thresholds: **80%** line, **40%** branch. Exclusions: `**/client/**`, `**/model/**`, `**/api/**`, `**/invoker/**`, `**/dto/**`, `**/config/**`, `**/*Application.class`, `**/repository/Dao/**`, `**/service/utils/**`
- Spotless excludes: `**/client/**`, `**/gen/**`, `**/generated/**`, `**/build/**`

## Architecture
- **Spring Boot 4.0.5** · Java 21 · PostgreSQL · Flyway · JWT
- OpenAPI-first: `src/main/resources/api/api.yml` is the single source of truth. Edit `api.yml` first, then regenerate the client.
- Layers: Controller → Mapper (generated DTO ↔ domain entity) → Service (`@Transactional`) → Repository (JPA) → Entity
- **Crupdate pattern**: `PUT` endpoints combine create + update. If client-provided ID exists → update; if null/not found → create.
- All entities extend `CreatAndUpdateEntity` (audit: createdAt, updatedAt, createdBy, updatedBy, comment). Use `@SuperBuilder` (not `@Builder`).
- Security: `@EnableMethodSecurity` + `@PreAuthorize` roles (ADMIN, ADMINISTRATION, WAREHOUSE_WORKER, EMPLOYEE). JWT subject = email (used as Spring Security principal).
- `@RestControllerAdvice` global handler in `InternalToRestExceptionHandler` maps domain exceptions → `{type, message}` JSON.
- Pagination: custom 1-indexed `PageFromOne`, `BoundedPageSize` (max 500).
- Jackson timezone: `Indian/Antananarivo`.

## Flyway migrations
- Schema: `src/main/resources/db/migration/V0_<number>__<description>.sql`
- Test data: `src/main/resources/db/testdata/V100_<number>__<description>.sql`

## Test patterns
### Integration tests (`*IT.java`)
- `@SpringBootTest(webEnvironment = RANDOM_PORT)`, inner class `ContextInitializer extends AbstractContextInitializer`, `@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)` on mutation tests.
- Standard `@MockitoBean`: `SentryConf`, `AuthenticationManager`, `JwtUtils` (except AuthIT and YearlyReportIT which use real JWT).
- `TestAuthSupport.setUpJwtService(jwtUtils)` maps token strings → emails. `TestDataSqlLoader.reloadTestData(dataSource)` truncates + reloads testdata SQL.
- Static test constants in `TestUtils.java`: `ADMIN_ID`, `USER1_ID`, `COMPANY1_ID`, etc., plus token constants (`ADMIN_TOKEN` = `"Bearer admin@email.com"`).
- Testcontainers starts real PostgreSQL per test class. Docker socket at `/var/run/docker.sock`.

### Unit tests (`*Test.java`)
- **No** `unit/` directory — tests live directly under `Service/` (capital S), `Service/core/`, or `validator/`, mirroring main source packages.
- Service tests: `@ExtendWith(MockitoExtension.class)`, `@Mock`/`@InjectMocks`, **no** `@SpringBootTest`.
- Use **AssertJ** fluent assertions (`assertThat`, `assertThatThrownBy`, `assertThatCode`) — not JUnit 5 assertions.
- Method naming: `{method}_Should{Expected}_When{Condition}` (e.g., `getById_ShouldReturnUser_WhenUserExists`).
- Structure: Given/When/Then comments. Use `verify()` to assert mock interactions.
- Validator tests: pure JUnit 5 + AssertJ, no Mockito — instantiate validator in `@BeforeEach`.

## Domains & packages
- **core**: `Company`, `Job`, `User`, `History`, `Department`, `Team`
- **money** (21 entities): `BankFee`, `BudgetLine`, `CashAccount`, `CashTransaction`, `CompanyFixedCost`, `EmployeePayment`, `ExpenseMoney`, `IncomeMoney`, `IncomeReceipt`, `IncomeType`, `Loan`, `LoanRepayment`, `MonetaryMovement`, `Organization`, `OtherExpense`, `OtherExpenseType`, `Purchase`, `PurchaseOrder`, `PurchaseOrderLine`, `Supplier`, `TravelExpense`
- **movement** (12 entities): `Equipment`, `EquipmentUsage`, `Maintenance`, `MaintenanceSchedule`, `Material`, `MaterialConsumption`, `MaterialWarehouse`, `MaterialWarehouseId`, `TravelEquipment`, `TravelMaterials`, `TravelPeople`, `Warehouse`
- **hr**: `Leave`, `LeaveType`, `EmployeeLeaveConfig`, `LeaveBalance`, `LeaveAccruedByMonth`
- **task**: `Task`, `TaskSchedule`, `TaskAssignment`, `ScheduleStatus`, `TaskPriority`
- **notification**: `Notification`
- **report**: `JobWithFinancials`, `YearlyReport`
- **dashboard**: 13 response DTOs (equipment, HR, material, monetary breakdowns)

## Repository skills
- `.agents/skills/skill-creator/SKILL.md`
- `.agents/skills/custom-exception-handling/SKILL.md`
- `.agents/skills/openapi-client-generation/SKILL.md`
- `.agents/skills/jacoco-ci-setup/SKILL.md`
- `.agents/skills/spring-boot-mvc-architecture/SKILL.md`
- `.agents/skills/java-code-quality/SKILL.md`
- `.agents/skills/spring-security-jwt/SKILL.md`
- `.agents/skills/spring-boot-testing/SKILL.md`
- `.agents/skills/tdd-workflow/SKILL.md`
- `.agents/skills/api-naming-conventions/SKILL.md`
- `.agents/skills/find-skills/SKILL.md`

## Skill creation rules
- Skills must be **project-agnostic**: use `{basePackage}` placeholders, never concrete packages like `com.example.demo`.
- Exception handler skills must define their own `ExceptionResponse` DTO, not reference generated client classes.
- No "Référence projet" section pointing to this project.
