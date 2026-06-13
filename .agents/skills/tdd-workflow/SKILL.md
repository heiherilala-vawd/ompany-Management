---
name: tdd-workflow
description: Apply Test-Driven Development (TDD) workflow in a Spring Boot project. Use when implementing a new feature, adding a new domain, or extending an existing entity — always write the test first, then implement, then verify.
---

# TDD Workflow

Test-Driven Development (TDD) cycle : **Red → Green → Refactor**.

## Workflow

```
0. API.YML (OpenAPI-first)
   └── Modifier api.yml → régénérer le client → utiliser les DTOs générés
1. ANALYSE
   └── Comprendre le besoin, identifier l'entité/le endpoint
2. TEST FIRST (RED)
   └── Écrire le test d'intégration AVANT toute implémentation
   └── Vérifier qu'il échoue (RED)
3. IMPLÉMENTATION (GREEN)
   └── Écrire juste assez de code pour faire passer le test
   └── Ne pas ajouter de fonctionnalité non testée
4. VÉRIFICATION (REFACTOR)
   └── Lancer le test : doit passer (GREEN)
   └── Nettoyer le code (spotlessApply, suppression doublons)
   └── Re-lancer le build complet pour confirmer
```

## Cas particulier : ajouter un paramètre/filtre à un endpoint existant

Quand on ajoute un nouveau paramètre de requête (filtre `job_id`, `consumption_status`, etc.) à un endpoint GET existant :

### Ordre strict

1. **TEST FIRST** — Modifier l'IT existant ou en créer un nouveau AVANT de toucher au DAO/Service/Controller
2. **IMPLÉMENTATION** — Ajouter le paramètre dans le DAO, puis le Service, puis le Controller
3. **VÉRIFICATION** — Lancer le test, il doit passer

### Exemple concret

Soit un endpoint `GET /companies/{compId}/material_consumption` avec un nouveau filtre `consumption_status`.

**RED** (écrire le test d'abord) :
```java
// Dans MaterialConsumptionIT.java
@Test
void admin_can_filter_material_consumptions_by_consumption_status() throws Exception {
    var api = new MaterialConsumptionApi(anApiClient(ADMIN_TOKEN));
    List<MaterialConsumption> result = api.getMaterialConsumptions(COMPANY1_ID, 1, 100, "COMPLETED", null);
    assertEquals(2, result.size());
}
```

**Compiler** — le test ne compile pas encore car le paramètre n'existe pas dans le client généré (RED). C'est normal : c'est la preuve que le test est nécessaire.

**api.yml** — Ajouter le paramètre `consumption_status` dans la spec OpenAPI de l'endpoint.

**Régénérer le client** — `./gradlew publishJavaClientToMavenLocal`

**Compiler** — le test compile (GREEN). Lancer le test : il échoue car l'implémentation DAO ne filtre pas encore (RED).

**Implémenter** — Modifier : `MaterialConsumptionDao` → `MaterialConsumptionService` → `MaterialConsumptionController` (GREEN).

**Lancer le test** — il passe (GREEN).

### Règle absolue
> **Ne jamais** modifier un DAO, Service ou Controller AVANT d'avoir écrit le test qui valide le changement.
> Si la modification est dans le DAO, le test d'intégration doit exister en premier.

## Architecture des tests

```
test/
└── java/{basePackage}/
    ├── integration/
    │   └── {domain}/
    │       └── {EntityName}IT.java        # Test d'intégration complet
    ├── Service/  (ou unit/ selon le projet)
    │   ├── {layer}/
    │   │   └── {EntityName}ServiceTest.java  # Test unitaire (Mockito)
    │   └── {EntityName}ServiceTest.java
    └── validator/
        └── {EntityName}ValidatorTest.java    # Test validateur (pur JUnit)
```

## Prérequis : OpenAPI-first

Ce projet est **OpenAPI-first**. Avant d'écrire du code ou des tests :

1. **Modifier** `src/main/resources/api/api.yml`
   - Ajouter le nouveau type d'entité dans les `schemas` (DTO Request + Response)
   - Ajouter les endpoints (PUT, GET, DELETE, etc.)
   - Respecter la convention de nommage existante

2. **Régénérer le client API** :
   ```bash
   ./gradlew publishJavaClientToMavenLocal
   ```
   Cela génère les classes Java dans `build/gen/` et les copie dans `src/main/java/{basePackage}/client/`.

3. **Vérifier** que les DTOs générés sont bien présents dans `client/model/{EntityName}Dto.java`

4. **Compiler** pour vérifier qu'il n'y a pas d'erreur :
   ```bash
   ./gradlew compileJava
   ```

## Étapes détaillées

### 1. Analyser le besoin

Avant d'écrire du code, définir :

| Élément | Question |
|---------|----------|
| Entité | Quelle entité métier ? (`{EntityName}`) |
| Endpoint | PUT / GET / DELETE ? (toujours PUT pour créer/mettre à jour) |
| Règles métier | Quelles validations, quels cas limites ? |
| Rôles | Qui peut faire quoi ? (ADMIN, ADMINISTRATION, WAREHOUSE_WORKER, EMPLOYEE) |
| Tables DB | Nouvelle table ou ajout à une table existante ? Migration Flyway ? |
| Tests existants | Regarder les ITs similaires pour copier le pattern (`{domain}/*IT.java`) |

### 2. Écrire le test d'intégration (RED)

#### Pattern obligatoire d'un IT

```java
package {basePackage}.integration.{domain};

import static {basePackage}.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import {basePackage}.SentryConf;
import {basePackage}.client.api.{EntityName}Api;
import {basePackage}.client.invoker.ApiClient;
import {basePackage}.client.model.{EntityName}Dto;
import {basePackage}.client.model.Crupdate{EntityName};
import {basePackage}.endpoint.rest.security.jwt.JwtUtils;
import {basePackage}.integration.conf.AbstractContextInitializer;
import {basePackage}.integration.conf.TestDataSqlLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(initializers = {EntityName}IT.ContextInitializer.class)
class {EntityName}IT {

    @MockitoBean
    private SentryConf sentryConf;

    @MockitoBean
    private AuthenticationManager authenticationManagerMock;

    @MockitoBean
    private JwtUtils jwtServiceMock;

    @Autowired
    private DataSource dataSource;

    @LocalServerPort
    private int serverPort;

    private {EntityName}Api adminApi;
    private {EntityName}Api employeeApi;

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.setUpJwtService(jwtServiceMock);
        TestUtils.setUpAuthenticationManager(authenticationManagerMock);
        TestDataSqlLoader.executeAllSqlScripts(dataSource);

        adminApi = new {EntityName}Api(TestUtils.anApiClient(ADMIN_TOKEN, serverPort));
        employeeApi = new {EntityName}Api(TestUtils.anApiClient(EMPLOYEE_TOKEN, serverPort));
    }

    @Test
    void should_create_{entityName}_when_data_is_valid() throws Exception {
        Crupdate{EntityName} toCreate = TestUtils.someCreatable{EntityName}();

        {EntityName}Dto result = adminApi.createOrUpdate{EntityName}(toCreate);

        assertNotNull(result.getId());
        assertEquals(toCreate.getName(), result.getName());
    }

    @Test
    void should_return_403_when_employee_creates_{entityName}() {
        Crupdate{EntityName} toCreate = TestUtils.someCreatable{EntityName}();

        assertThrowsForbiddenException(() -> employeeApi.createOrUpdate{EntityName}(toCreate));
    }

    @Test
    void should_return_400_when_{entityName}_data_is_invalid() {
        Crupdate{EntityName} invalid = new Crupdate{EntityName}();

        assertThrowsApiException(
            "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Validation failed\"}",
            () -> adminApi.createOrUpdate{EntityName}(invalid));
    }

    static class ContextInitializer extends AbstractContextInitializer {
        public static final int SERVER_PORT = TestUtils.anAvailableRandomPort();

        @Override
        public int getServerPort() {
            return SERVER_PORT;
        }
    }
}
```

#### Règles pour le test d'intégration

- Nommer le test en `should_{expected_behavior}_when_{condition}`
- Tester **un seul comportement** par test
- Inclure systématiquement : cas nominal (succès), cas sécurité (403), cas validation (400)
- Utiliser les constantes de token : `ADMIN_TOKEN`, `EMPLOYEE_TOKEN`, `WAREHOUSE_TOKEN`, `ADMINISTRATION_TOKEN`
- Utiliser les fixtures de `TestUtils` : `someCreatable{EntityName}()` pour les données valides
- Faire échouer le test d'abord (vérifier qu'il est rouge → `BUILD FAILED`)
- Ajouter `@DirtiesContext` sur les tests qui mutent l'état (create, update, delete)

#### Helpers disponibles dans `TestUtils`

```java
// Tokens (headers Authorization: Bearer xxx)
ADMIN_TOKEN           → rôle ADMIN
EMPLOYEE_TOKEN        → rôle EMPLOYEE
WAREHOUSE_TOKEN       → rôle WAREHOUSE_WORKER
ADMINISTRATION_TOKEN  → rôle ADMINISTRATION
USER1_TOKEN           → rôle EMPLOYEE (user1)
USER2_TOKEN           → rôle EMPLOYEE (user2)
BAD_TOKEN             → token invalide

// IDs de test
ADMIN_ID, EMPLOYEE_ID, COMPANY1_ID, JOB1_ID, WAREHOUSE1_ID, etc.

// Clients HTTP
anApiClient(token, port) → retourne un ApiClient pré-configuré avec le token

// Assertions
assertThrowsForbiddenException(executable)   → vérifie 403
assertThrowsNotAuthorizedException(executable) → vérifie 401
assertThrowsApiException(expectedJson, executable) → vérifie une erreur JSON
isValidUUID(candidate) → vérifie un UUID

// Fixtures (à créer si le domaine n'existe pas encore)
someCreatable{EntityName}()       → DTO valide pour création
{entityName}1() / {entityName}2() → objets de test pré-remplis
{entityName}ToCrupdate{EntityName}(entity) → transforme l'objet en DTO de mise à jour
```

### 3. Écrire le test unitaire (RED)

Deux styles selon la couche testée :

#### Style A : Service test (Mockito `@ExtendWith`)
Utiliser `@ExtendWith(MockitoExtension.class)`, `@Mock` pour les dépendances, `@InjectMocks` pour le service testé. Pas de `@SpringBootTest`.

```java
package {basePackage}.Service.{layer};  // ou {basePackage}.unit.{layer}

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import {basePackage}.model.BoundedPageSize;
import {basePackage}.model.PageFromOne;
import {basePackage}.model.{EntityName};
import {basePackage}.model.exception.NotFoundException;
import {basePackage}.repository.{domain}.{EntityName}Repository;
import {basePackage}.service.{domain}.{EntityName}Service;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class {EntityName}ServiceTest {

    @Mock
    private {EntityName}Repository {entityName}Repository;

    @InjectMocks
    private {EntityName}Service {entityName}Service;

    private {EntityName} existingEntity;
    private String entityId;

    @BeforeEach
    void setUp() {
        entityId = "test-id-123";
        existingEntity = {EntityName}.builder()
            .id(entityId)
            .name("Test Name")
            .build();
    }

    // --- getById ---

    @Test
    void getById_ShouldReturnEntity_WhenExists() {
        // Given
        when({entityName}Repository.findById(entityId))
            .thenReturn(Optional.of(existingEntity));

        // When
        {EntityName} result = {entityName}Service.getById(entityId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entityId);
        assertThat(result.getName()).isEqualTo("Test Name");
        verify({entityName}Repository).findById(entityId);
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenNotExists() {
        // Given
        String badId = "invalid";
        when({entityName}Repository.findById(badId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> {entityName}Service.getById(badId))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("{EntityName} with id " + badId + " not found");

        verify({entityName}Repository).findById(badId);
    }

    // --- findAll with pagination ---

    @Test
    void findAll_ShouldReturnPagedResults() {
        // Given
        PageFromOne page = new PageFromOne("1");
        BoundedPageSize pageSize = new BoundedPageSize("10");

        when({entityName}Repository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(existingEntity)));

        // When
        List<{EntityName}> result = {entityName}Service.findAll(page, pageSize);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(entityId);
        verify({entityName}Repository).findAll(any(Pageable.class));
    }

    // --- delete ---

    @Test
    void deleteById_ShouldDelete_WhenExists() {
        // Given
        when({entityName}Repository.findById(entityId))
            .thenReturn(Optional.of(existingEntity));

        // When
        {entityName}Service.deleteById(entityId);

        // Then
        verify({entityName}Repository).delete(existingEntity);
    }

    @Test
    void deleteById_ShouldThrow_WhenNotExists() {
        // Given
        when({entityName}Repository.findById("invalid"))
            .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> {entityName}Service.deleteById("invalid"))
            .isInstanceOf(NotFoundException.class);

        verify({entityName}Repository, never()).delete(any());
    }
}
```

#### Style B : Validator test (pur JUnit, sans Mockito)
Les validateurs sont des classes sans dépendances. Les instancier dans `@BeforeEach`, pas de Mockito.

```java
package {basePackage}.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import {basePackage}.model.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class {EntityName}ValidatorTest {

    private {EntityName}Validator validator;

    @BeforeEach
    void setUp() {
        validator = new {EntityName}Validator();
    }

    @Test
    void validate_ShouldNotThrow_WhenValid() {
        assertThatCode(() -> validator.validate(someValidInput()))
            .doesNotThrowAnyException();
    }

    @Test
    void validate_ShouldThrow_WhenNull() {
        assertThatThrownBy(() -> validator.validate(null))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("cannot be null");
    }

    @Test
    void validate_ShouldThrow_WhenInvalidField() {
        assertThatThrownBy(() -> validator.validate(someInvalidInput()))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("invalid");
    }
}
```

### 4. Lancer le test pour vérifier qu'il échoue (RED)

```bash
# Test d'intégration
./gradlew test --tests "*{EntityName}IT*"

# Test unitaire
./gradlew test --tests "*{EntityName}ServiceTest*"
```

**Le test doit échouer** (`BUILD FAILED`). Si ce n'est pas le cas, le test ne teste rien.

### 5. Implémenter la fonctionnalité (GREEN)

Suivre l'architecture MVC dans cet ordre :

#### 5a. Exception custom (si nécessaire)

```java
package {basePackage}.service.{domain}.exception;

public class {EntityName}NotFoundException extends RuntimeException {
    public {EntityName}NotFoundException(String id) {
        super("{EntityName} not found with id: " + id);
    }
}

public class {EntityName}ServiceException extends RuntimeException {
    public {EntityName}ServiceException(String message) {
        super(message);
    }
}
```

Puis enregistrer dans `InternalToRestExceptionHandler` :
```java
@ExceptionHandler({EntityName}NotFoundException.class)
public ResponseEntity<ExceptionResponse> handle{EntityName}NotFound({EntityName}NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ExceptionResponse("404 NOT_FOUND", ex.getMessage()));
}
```

#### 5b. Entité JPA

```java
package {basePackage}.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "{table_name}")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class {EntityName} extends CreatAndUpdateEntity implements Serializable {

    @Id
    private String id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private {EntityName}Type {entityName}Type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        {EntityName} that = ({EntityName}) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public enum {EntityName}Type {
        TYPE_A,
        TYPE_B
    }
}
```

#### 5c. Repository

```java
package {basePackage}.repository.{domain};

import {basePackage}.model.{EntityName};
import org.springframework.data.jpa.repository.JpaRepository;

public interface {EntityName}Repository extends JpaRepository<{EntityName}, String> {
}
```

#### 5d. Service

```java
package {basePackage}.service.{domain};

import {basePackage}.client.model.{EntityName}Dto;
import {basePackage}.client.model.Crupdate{EntityName};
import {basePackage}.endpoint.rest.mapper.{EntityName}Mapper;
import {basePackage}.model.{EntityName};
import {basePackage}.repository.{domain}.{EntityName}Repository;
import {basePackage}.service.{domain}.exception.{EntityName}NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class {EntityName}Service {

    private final {EntityName}Repository {entityName}Repository;
    private final {EntityName}Mapper {entityName}Mapper;

    @Transactional
    public {EntityName}Dto create(Crupdate{EntityName} dto) {
        {EntityName} entity = {entityName}Mapper.toDomain(dto);
        entity.setId(dto.getId()); // null → création, existant → mise à jour
        {EntityName} saved = {entityName}Repository.save(entity);
        return {entityName}Mapper.toRest{EntityName}(saved);
    }

    public {EntityName}Dto getById(String id) {
        return {entityName}Repository.findById(id)
            .map({entityName}Mapper::toRest{EntityName})
            .orElseThrow(() -> new {EntityName}NotFoundException(id));
    }
}
```

#### 5e. Mapper Spring (manuel, pas MapStruct)

```java
package {basePackage}.endpoint.rest.mapper;

import {basePackage}.client.model.{EntityName}Dto;
import {basePackage}.client.model.Crupdate{EntityName};
import {basePackage}.client.model.{EntityName}Type;
import {basePackage}.model.{EntityName};
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class {EntityName}Mapper {

    private final UserService userService;  // si besoin de l'utilisateur courant

    public {EntityName} toDomain(Crupdate{EntityName} dto) {
        if (dto == null) return null;

        return {EntityName}.builder()
            .id(dto.getId())
            .name(dto.getName())
            .{entityName}Type(EnumMapper.mapEnum(dto.get{EntityName}Type(), {EntityName}.{EntityName}Type.class))
            .build();
    }

    public {EntityName}Dto toRest{EntityName}({EntityName} entity) {
        if (entity == null) return null;

        {EntityName}Dto dto = new {EntityName}Dto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.set{EntityName}Type(EnumMapper.mapEnum(entity.get{EntityName}Type(), {EntityName}Type.class));
        RestAuditMapperUtils.mapAuditFields(
            entity,
            dto::setCreatedAt, dto::setUpdatedAt,
            dto::setComment, dto::setCreatedBy, dto::setUpdatedBy);
        return dto;
    }

    public Crupdate{EntityName} toRestCrupdate{EntityName}({EntityName} entity) {
        if (entity == null) return null;

        return new Crupdate{EntityName}()
            .id(entity.getId())
            .name(entity.getName())
            .{entityName}Type(EnumMapper.mapEnum(entity.get{EntityName}Type(), {EntityName}Type.class));
    }
}
```

#### 5f. Controller

```java
package {basePackage}.endpoint.rest.controller;

import {basePackage}.client.model.{EntityName}Dto;
import {basePackage}.client.model.Crupdate{EntityName};
import {basePackage}.service.{domain}.{EntityName}Service;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/{entityNames}")
@AllArgsConstructor
public class {EntityName}Controller {

    private final {EntityName}Service {entityName}Service;

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public {EntityName}Dto createOrUpdate{EntityName}(@RequestBody Crupdate{EntityName} dto) {
        return {entityName}Service.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public {EntityName}Dto get{EntityName}ById(@PathVariable String id) {
        return {entityName}Service.getById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete{EntityName}(@PathVariable String id) {
        {entityName}Service.delete(id);
    }
}
```

**Important** : Le pattern `@PutMapping` est un **crupdate** (create + update). Si l'ID fourni existe → mise à jour ; si l'ID est null ou inexistant → création.

### 6. Vérifier que le test passe (GREEN)

```bash
# Générer le client (si api.yml a changé)
./gradlew publishJavaClientToMavenLocal

# Lancer le test d'intégration spécifique
./gradlew test --tests "*{EntityName}IT*"

# Lancer le test unitaire spécifique
./gradlew test --tests "*{EntityName}ServiceTest*"

# Lancer tous les tests du domaine
./gradlew test --tests "*{domain}*"

# Build complet (tests + coverage + style)
./gradlew build
```

**Le test doit passer** (`BUILD SUCCESSFUL`).

### 7. Refactor (REFACTOR)

- Nettoyer le code : supprimer les commentaires inutiles, les imports inutilisés
- Formater : `./gradlew spotlessApply`
- Vérifier le style : `./gradlew checkstyleMain`
- Vérifier la couverture : `./gradlew jacocoTestCoverageVerification`
- Re-lancer le build complet : `./gradlew build`

## Vérification finale

```bash
# Build complet
./gradlew build

# Vérifications qualité
./gradlew spotlessCheck
./gradlew checkstyleMain
./gradlew jacocoTestCoverageVerification
```

Tout doit être vert (`BUILD SUCCESSFUL`). Si un test échoue, corriger l'implémentation, pas le test.

## Anti-patterns à éviter

| Anti-pattern | Problème | Solution |
|---|---|---|
| Écrire l'implémentation avant le test | On ne sait pas si le code est testable | Toujours écrire le test d'abord (RED) |
| Oublier de modifier `api.yml` | Les DTOs générés ne correspondent pas | Toujours commencer par l'API spec |
| Test qui passe sans rien tester | Faux positif | Vérifier qu'il échoue d'abord (RED) |
| Tester plusieurs choses dans un seul test | Difficulté à identifier le problème | Un test = un comportement |
| Ignorer les cas d'erreur et de sécurité | Code fragile en production | Tester 200 + 400 + 403 |
| Modifier le test pour qu'il passe | Le test ne protège plus rien | Corriger l'implémentation, pas le test |
| Ne pas régénérer le client après api.yml | Les DTOs sont obsolètes | Toujours lancer `publishJavaClientToMavenLocal` |
| Oublier `@DirtiesContext` sur les tests mutants | Pollution entre tests | Ajouter `@DirtiesContext` sur chaque test qui modifie la DB |
