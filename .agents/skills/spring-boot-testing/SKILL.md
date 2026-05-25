---
name: spring-boot-testing
description: Set up integration tests and unit tests for a Spring Boot project with Testcontainers, Mockito, and the generated API client. Use when creating test infrastructure, writing integration tests, or configuring test databases.
---

# Spring Boot Testing — Testcontainers + Integration Tests + Unit Tests

Set up a complete testing infrastructure with **Testcontainers** (PostgreSQL in Docker), **integration tests** (full HTTP flow), **unit tests** (Mockito), **test data management**, and **API client** in tests.

## Architecture

```
Integration Tests (*IT.java)
    ↓
Client API généré (ApiClient) → endpoints HTTP
    ↓
Application Spring Boot complète (random port)
    ↓
Testcontainers → PostgreSQL réel (Docker)
    ↓
Flyway migrations + testdata SQL (V99_*)

Unit Tests (*Test.java)
    ↓
Mockito (@Mock, @InjectMocks) → Service pur (pas de DB)
```

## 1. Dépendances (`build.gradle.kts`)

```kotlin
dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:{version}")
    testImplementation("org.testcontainers:postgresql:{version}")
    testImplementation("org.testcontainers:junit-jupiter:{version}")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("testcontainers.dockerclient.strategy",
        "org.testcontainers.dockerclient.EnvironmentAndSystemPropertyClientProviderStrategy")
}
```

## 2. Configuration Docker — `testcontainers.properties`

Créer `src/test/resources/testcontainers.properties` :

```properties
docker.client.strategy=org.testcontainers.dockerclient.EnvironmentAndSystemPropertyClientProviderStrategy
docker.host=unix:///var/run/docker.sock
ryuk.disabled=false
testcontainers.reuse.enable=true
```

## 3. `AbstractContextInitializer.java`

Initialise le conteneur PostgreSQL et injecte les propriétés :

```java
package {basePackage}.integration.conf;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final PostgreSQLContainer<?> postgresContainer;

  static {
    postgresContainer =
        new PostgreSQLContainer<>("postgres:15.2")
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test");
    postgresContainer.start();
  }

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    String flywayTestdataPath = "classpath:/db/testdata";
    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        applicationContext,
        "server.port=" + this.getServerPort(),
        "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
        "spring.datasource.username=" + postgresContainer.getUsername(),
        "spring.datasource.password=" + postgresContainer.getPassword(),
        "spring.flyway.locations=classpath:/db/migration," + flywayTestdataPath,
        "jwt.secret.key=test-secret-key-test-secret-key-test",
        "jwt.expiration.time=86400000");
  }

  public abstract int getServerPort();
}
```

## 4. `TestUtils.java` — Constantes et helpers

```java
package {basePackage}.integration.conf;

import {basePackage}.client.invoker.ApiClient;
import {basePackage}.client.invoker.auth.HttpBearerAuth;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TestUtils {

  // IDs constants pour les tests
  public static final String ADMIN_ID = "admin1_id";
  public static final String USER1_ID = "user1_id";
  public static final String EMPLOYEE_ID = "employee1_id";
  public static final String WAREHOUSE_ID = "warehouse1_id";
  public static final String COMPANY1_ID = "company1_id";

  // Emails pour chaque rôle
  public static final String ADMIN_EMAIL = "admin@email.com";
  public static final String USER1_EMAIL = "user1@email.com";
  public static final String EMPLOYEE_EMAIL = "employee@email.com";
  public static final String WAREHOUSE_EMAIL = "warehouse@email.com";

  // Tokens (convention: "Bearer " + email)
  public static final String BAD_TOKEN = "bad_token";
  public static final String ADMIN_TOKEN = "Bearer " + ADMIN_EMAIL;
  public static final String USER1_TOKEN = "Bearer " + USER1_EMAIL;
  public static final String EMPLOYEE_TOKEN = "Bearer " + EMPLOYEE_EMAIL;
  public static final String WAREHOUSE_TOKEN = "Bearer " + WAREHOUSE_EMAIL;

  // Crée un client API avec le token Bearer
  public static ApiClient anApiClient(String token, int serverPort) {
    var client = new ApiClient();
    client.setScheme("http");
    client.setHost("localhost");
    client.setPort(serverPort);
    client.setRequestInterceptor(builder -> builder.header("Authorization", token));
    return client;
  }

  // Vérifie qu'une exception API est levée
  public static void assertThrowsApiException(String expectedMessage, Runnable runnable) {
    try {
      runnable.run();
      throw new AssertionError("Expected ApiException was not thrown");
    } catch (Exception e) {
      if (!e.getMessage().contains(expectedMessage)) {
        throw new AssertionError(
            "Expected message containing '" + expectedMessage + "' but got: " + e.getMessage());
      }
    }
  }

  // Trouve un port aléatoire disponible
  public static int anAvailableRandomPort() {
    try (var socket = new java.net.ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (Exception e) {
      return 8080;
    }
  }
}
```

## 5. Mock JWT — `TestAuthSupport.java`

```java
package {basePackage}.integration.conf;

import {basePackage}.endpoint.rest.security.jwt.JwtUtils;
import org.mockito.BDDMockito;

public class TestAuthSupport {

  public static void setUpJwtService(JwtUtils jwtUtils) {
    // Mappe un token "Bearer email@..." → email
    BDDMockito.given(jwtUtils.getUserEmailFromJwtToken(BDDMockito.anyString()))
        .willAnswer(invocation -> {
          String token = invocation.getArgument(0);
          return token.replace("Bearer ", "");
        });
    BDDMockito.given(jwtUtils.validateJwtToken(BDDMockito.anyString())).willReturn(true);
  }
}
```

## 6. Test Data SQL

Créer `src/main/resources/db/testdata/V99_1__testdata.sql` :

```sql
DELETE FROM "{table}";

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at)
VALUES ('admin1_id', 'ADMIN', 'Admin', 'System', 'M', 'admin@email.com',
        '$2a$10$...', NOW(), NOW());

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at)
VALUES ('employee1_id', 'EMPLOYEE', 'John', 'Doe', 'M', 'employee@email.com',
        '$2a$10$...', NOW(), NOW());

INSERT INTO "company" (id, name, description, created_at, updated_at)
VALUES ('company1_id', 'Test Company', 'Description', NOW(), NOW());
```

Utiliser des IDs fixes et prédictibles pour les tests.

## 7. `TestDataSqlLoader.java` (optionnel)

Pour recharger les données avant chaque test :

```java
package {basePackage}.integration.conf;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;

public class TestDataSqlLoader {

  private static final List<String> SCRIPTS = List.of(
      "db/testdata/V99_1__testdata.sql"
  );

  public static void reloadTestData(DataSource dataSource) {
    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      stmt.execute("SET session_replication_role = 'replica';");
      // Truncate all tables here
      stmt.execute("TRUNCATE TABLE users, company CASCADE;");
      stmt.execute("SET session_replication_role = 'origin';");
      // Execute each script
      for (String script : SCRIPTS) {
        // read and execute SQL file
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
```

## 8. Pattern d'un test d'intégration (`*IT.java`)

```java
package {basePackage}.integration;

import static org.junit.jupiter.api.Assertions.*;

import {basePackage}.client.invoker.ApiClient;
import {basePackage}.client.invoker.ApiException;
import {basePackage}.endpoint.rest.security.jwt.JwtUtils;
import {basePackage}.integration.conf.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import javax.sql.DataSource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = {EntityIT.ContextInitializer.class})
class EntityIT {

  @Autowired private DataSource dataSource;

  @MockitoBean private JwtUtils jwtUtils;
  @MockitoBean private AuthenticationManager authenticationManager;

  private ApiClient adminClient;
  private int serverPort;

  @BeforeEach
  void setUp() {
    // 1. Mocker JWT
    TestAuthSupport.setUpJwtService(jwtUtils);

    // 2. Recharger les données de test
    TestDataSqlLoader.reloadTestData(dataSource);

    // 3. Créer le client API
    serverPort = Integer.parseInt(System.getProperty("server.port", "8080"));
    adminClient = TestUtils.anApiClient(TestUtils.ADMIN_TOKEN, serverPort);
  }

  @Test
  void admin_can_get_entity_by_id() {
    var api = new EntityApi(adminClient);
    var entity = api.getEntityById(TestUtils.COMPANY1_ID);
    assertNotNull(entity);
    assertEquals(TestUtils.COMPANY1_ID, entity.getId());
  }

  @Test
  void user_with_bad_token_cannot_get_entity() {
    var badClient = TestUtils.anApiClient(TestUtils.BAD_TOKEN, serverPort);
    var api = new EntityApi(badClient);
    assertThrows(ApiException.class, () -> api.getEntityById(TestUtils.COMPANY1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    @Override
    public int getServerPort() {
      return TestUtils.anAvailableRandomPort();
    }
  }
}
```

## 9. Pattern d'un test unitaire (`*Test.java`)

```java
package {basePackage}.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import {basePackage}.model.EntityName;
import {basePackage}.repository.EntityNameRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EntityNameServiceTest {

  @Mock private EntityNameRepository repository;

  @InjectMocks private EntityNameService service;

  private EntityName entity;

  @BeforeEach
  void setUp() {
    entity = EntityName.builder()
        .id("test-id")
        .name("Test")
        .build();
  }

  @Test
  void findById_ShouldReturnEntity_WhenExists() {
    when(repository.findById("test-id")).thenReturn(Optional.of(entity));
    var result = service.findById("test-id");
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Test");
  }

  @Test
  void findById_ShouldReturnEmpty_WhenNotExists() {
    when(repository.findById("unknown")).thenReturn(Optional.empty());
    var result = service.findById("unknown");
    assertThat(result).isEmpty();
  }
}
```

## 10. Smoke Test

```java
package {basePackage};

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

  @Test
  void contextLoads() {}
}
```

## 11. Bonnes pratiques

- **Tests indépendants** : chaque test recharge ses données (`@DirtiesContext` ou `reloadTestData`)
- **Un seul comportement par test** : une méthode = une assertion principale
- **Noms explicites** : `admin_can_get_entity_by_id`, `user_cannot_delete_entity`
- **Mock uniquement les services externes** : Sentry, JWT, AuthenticationManager
- **IDs fixes** : utiliser des IDs prédictibles (pas de UUID aléatoires)
- **API client généré** : utiliser `ApiClient` avec Bearer token pour les appels HTTP
- **Pas de H2** : utiliser Testcontainers avec PostgreSQL réel
- **Pas de `Thread.sleep`** : utiliser des health checks

## 12. Commandes

```bash
./gradlew test                    # Tous les tests
./gradlew test --tests "*UserIT"  # Test spécifique
./gradlew test --info             # Avec logs détaillés
```

## Vérification

1. Lancer `./gradlew test` — tous les tests doivent passer
2. Vérifier que Docker est en cours d'exécution (Testcontainers le nécessite)
3. Vérifier que les tests d'intégration utilisent un vrai PostgreSQL
4. Vérifier que les tests unitaires sont rapides (pas de DB)
