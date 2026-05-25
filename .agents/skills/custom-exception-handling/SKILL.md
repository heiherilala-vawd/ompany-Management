---
name: custom-exception-handling
description: Implement custom domain exceptions and a global REST exception handler in a Spring Boot application following a clean architecture pattern. Use when the user needs to add custom exceptions, create an InternalToRestExceptionHandler, or standardize API error responses.
---

# Custom Exception Handling — Spring Boot

Implement a structured, centralized exception handling system in a Spring Boot application.

## Architecture (3 piliers)

1. **Base exception** — `BaseException` hérite de `RuntimeException`, porte un `ExceptionType` (CLIENT_EXCEPTION / SERVER_EXCEPTION)
2. **Exceptions métier** — classes spécialisées par cas métier (400, 401, 403, 404, 409, 429, 500, 501)
3. **Handler global** — `@RestControllerAdvice` intercepte toutes les exceptions et retourne des réponses HTTP standardisées

Les chemins et packages ci-dessous sont des exemples. Adapter `{basePackage}` au package du projet (ex: `com.mycompany.myapp`).

## Files to Create

### 1. `src/main/java/{basePackage}/model/exception/BaseException.java`

```java
package {basePackage}.model.exception;

import lombok.Getter;

public class BaseException extends RuntimeException {

  @Getter
  private final ExceptionType type;

  public BaseException(ExceptionType type, String message) {
    super(message);
    this.type = type;
  }

  public BaseException(ExceptionType type, String message, Throwable cause) {
    super(message, cause);
    this.type = type;
  }

  public BaseException(ExceptionType type, Exception source) {
    super(source);
    this.type = type;
  }

  public enum ExceptionType {
    CLIENT_EXCEPTION,
    SERVER_EXCEPTION
  }
}
```

### 2. Exceptions métier

#### `BadRequestException.java` — 400

```java
package {basePackage}.model.exception;

public class BadRequestException extends BaseException {
  public BadRequestException(String message) {
    super(ExceptionType.CLIENT_EXCEPTION, message);
  }
}
```

#### `UnauthorizedException.java` — 401

```java
package {basePackage}.model.exception;

public class UnauthorizedException extends BaseException {
  public UnauthorizedException(String message) {
    super(ExceptionType.CLIENT_EXCEPTION, message);
  }
}
```

#### `ForbiddenException.java` — 403

```java
package {basePackage}.model.exception;

public class ForbiddenException extends BaseException {

  public ForbiddenException() {
    super(ExceptionType.CLIENT_EXCEPTION, "Access is denied");
  }

  public ForbiddenException(String message) {
    super(ExceptionType.CLIENT_EXCEPTION, message);
  }
}
```

#### `NotFoundException.java` — 404

```java
package {basePackage}.model.exception;

public class NotFoundException extends BaseException {
  public NotFoundException(String message) {
    super(ExceptionType.CLIENT_EXCEPTION, message);
  }
}
```

#### `ConflictException.java` — 409

```java
package {basePackage}.model.exception;

public class ConflictException extends BaseException {
  public ConflictException(String message) {
    super(ExceptionType.CLIENT_EXCEPTION, message);
  }
}
```

#### `TooManyRequestsException.java` — 429

```java
package {basePackage}.model.exception;

public class TooManyRequestsException extends BaseException {
  public TooManyRequestsException(String message) {
    super(ExceptionType.CLIENT_EXCEPTION, message);
  }

  public TooManyRequestsException(Exception source) {
    super(ExceptionType.CLIENT_EXCEPTION, source);
  }
}
```

#### `InternalErrorException.java` — 500

```java
package {basePackage}.model.exception;

public class InternalErrorException extends BaseException {
  public InternalErrorException(String message) {
    super(ExceptionType.SERVER_EXCEPTION, message);
  }

  public InternalErrorException(String message, Throwable cause) {
    super(ExceptionType.SERVER_EXCEPTION, message, cause);
  }
}
```

#### `NotImplementedException.java` — 501

```java
package {basePackage}.model.exception;

public class NotImplementedException extends BaseException {
  public NotImplementedException(String message) {
    super(ExceptionType.SERVER_EXCEPTION, message);
  }
}
```

### 3. `src/main/java/{basePackage}/model/exception/ExceptionResponse.java`

```java
package {basePackage}.model.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {
  private String type;
  private String message;
}
```

### 4. `src/main/java/{basePackage}/endpoint/rest/InternalToRestExceptionHandler.java`

```java
package {basePackage}.endpoint.rest;

import {basePackage}.model.exception.*;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Order
@RestControllerAdvice
@Slf4j
public class InternalToRestExceptionHandler {

  @ExceptionHandler(value = {BadRequestException.class})
  ResponseEntity<ExceptionResponse> handleBadRequest(BadRequestException e) {
    log.info("Bad request: {}", e.getMessage());
    return new ResponseEntity<>(toRest(e, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {MissingServletRequestParameterException.class})
  ResponseEntity<ExceptionResponse> handleMissingParam(MissingServletRequestParameterException e) {
    log.info("Missing parameter: {}", e.getMessage());
    return handleBadRequest(new BadRequestException(e.getMessage()));
  }

  @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
  ResponseEntity<ExceptionResponse> handleConversionFailed(MethodArgumentTypeMismatchException e) {
    log.info("Conversion failed: {}", e.getMessage());
    String message = e.getCause() != null && e.getCause().getCause() != null
        ? e.getCause().getCause().getMessage()
        : e.getMessage();
    return handleBadRequest(new BadRequestException(message));
  }

  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  ResponseEntity<ExceptionResponse> handleValidationError(MethodArgumentNotValidException e) {
    log.info("Validation error: {}", e.getMessage());
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .reduce((a, b) -> a + "; " + b)
        .orElse(e.getMessage());
    return handleBadRequest(new BadRequestException(message));
  }

  @ExceptionHandler(value = {HttpMessageNotReadableException.class})
  ResponseEntity<ExceptionResponse> handleNotReadable(HttpMessageNotReadableException e) {
    log.info("Malformed request body: {}", e.getMessage());
    return handleBadRequest(new BadRequestException("Malformed JSON request body"));
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  ResponseEntity<ExceptionResponse> handleConstraintViolation(ConstraintViolationException e) {
    log.info("Constraint violation: {}", e.getMessage());
    return handleBadRequest(new BadRequestException(e.getMessage()));
  }

  @ExceptionHandler(value = {UnauthorizedException.class})
  ResponseEntity<ExceptionResponse> handleUnauthorized(UnauthorizedException e) {
    log.info("Unauthorized: {}", e.getMessage());
    return new ResponseEntity<>(toRest(e, HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = {TooManyRequestsException.class})
  ResponseEntity<ExceptionResponse> handleTooManyRequests(TooManyRequestsException e) {
    log.info("Too many requests: {}", e.getMessage());
    return new ResponseEntity<>(
        toRest(e, HttpStatus.TOO_MANY_REQUESTS), HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(
      value = {
        LockAcquisitionException.class,
        CannotAcquireLockException.class,
        OptimisticLockException.class
      })
  ResponseEntity<ExceptionResponse> handleLockAcquisitionException(Exception e) {
    log.warn("Database lock could not be acquired: too many requests assumed", e);
    return handleTooManyRequests(new TooManyRequestsException(e));
  }

  @ExceptionHandler(
      value = {
        AccessDeniedException.class,
        AuthorizationDeniedException.class,
        BadCredentialsException.class,
        ForbiddenException.class
      })
  ResponseEntity<ExceptionResponse> handleForbidden(Exception e) {
    log.info("Forbidden: {}", e.getMessage());
    return new ResponseEntity<>(
        new ExceptionResponse(HttpStatus.FORBIDDEN.toString(), e.getMessage()),
        HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  ResponseEntity<ExceptionResponse> handleNotFound(NotFoundException e) {
    log.info("Not found: {}", e.getMessage());
    return new ResponseEntity<>(toRest(e, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {ConflictException.class})
  ResponseEntity<ExceptionResponse> handleConflict(ConflictException e) {
    log.info("Conflict: {}", e.getMessage());
    return new ResponseEntity<>(toRest(e, HttpStatus.CONFLICT), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(value = {InternalErrorException.class})
  ResponseEntity<ExceptionResponse> handleInternalError(InternalErrorException e) {
    log.error("Internal error", e);
    return new ResponseEntity<>(
        toRest(e, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {NotImplementedException.class})
  ResponseEntity<ExceptionResponse> handleNotImplemented(NotImplementedException e) {
    log.error("Not implemented", e);
    return new ResponseEntity<>(
        toRest(e, HttpStatus.NOT_IMPLEMENTED), HttpStatus.NOT_IMPLEMENTED);
  }

  @ExceptionHandler(value = {Exception.class})
  ResponseEntity<ExceptionResponse> handleDefault(Exception e) {
    log.error("Internal error", e);
    return new ResponseEntity<>(
        toRest(e, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ExceptionResponse toRest(Exception e, HttpStatus status) {
    return ExceptionResponse.builder()
        .type(status.toString())
        .message(e.getMessage())
        .build();
  }
}
```

## Exception → HTTP Mapping

| Exception(s) | HTTP Status |
|---|---|
| `BadRequestException` | 400 BAD_REQUEST |
| `MissingServletRequestParameterException` | 400 BAD_REQUEST |
| `MethodArgumentTypeMismatchException` | 400 BAD_REQUEST |
| `MethodArgumentNotValidException` | 400 BAD_REQUEST |
| `HttpMessageNotReadableException` | 400 BAD_REQUEST |
| `ConstraintViolationException` | 400 BAD_REQUEST |
| `UnauthorizedException` | 401 UNAUTHORIZED |
| `AccessDeniedException`, `AuthorizationDeniedException`, `BadCredentialsException`, `ForbiddenException` | 403 FORBIDDEN |
| `NotFoundException` | 404 NOT_FOUND |
| `ConflictException` | 409 CONFLICT |
| `TooManyRequestsException` | 429 TOO_MANY_REQUESTS |
| `LockAcquisitionException`, `CannotAcquireLockException`, `OptimisticLockException` | 429 TOO_MANY_REQUESTS |
| `InternalErrorException` | 500 INTERNAL_SERVER_ERROR |
| `NotImplementedException` | 501 NOT_IMPLEMENTED |
| `Exception` (catch-all) | 500 INTERNAL_SERVER_ERROR |

## API Spec (OpenAPI)

Si le projet utilise OpenAPI, ajouter dans `api.yml` :

```yaml
components:
  schemas:
    ExceptionResponse:
      type: object
      properties:
        type:
          type: string
        message:
          type: string
    BadRequestException:
      allOf:
        - $ref: '#/components/schemas/ExceptionResponse'
    UnauthorizedException:
      allOf:
        - $ref: '#/components/schemas/ExceptionResponse'
    NotAuthorizedException:
      allOf:
        - $ref: '#/components/schemas/ExceptionResponse'
    ResourceNotFoundException:
      allOf:
        - $ref: '#/components/schemas/ExceptionResponse'
    ConflictException:
      allOf:
        - $ref: '#/components/schemas/ExceptionResponse'
    TooManyRequestsException:
      allOf:
        - $ref: '#/components/schemas/ExceptionResponse'
    InternalServerException:
      allOf:
        - $ref: '#/components/schemas/ExceptionResponse'
```

Puis référencer ces réponses dans chaque endpoint :

```yaml
responses:
  '400':
    description: Bad request
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/BadRequestException'
  '401':
    description: Unauthorized
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/UnauthorizedException'
  '403':
    description: Forbidden
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/NotAuthorizedException'
  '404':
    description: Not found
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/ResourceNotFoundException'
  '409':
    description: Conflict
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/ConflictException'
  '429':
    description: Too many requests
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/TooManyRequestsException'
  '500':
    description: Internal server error
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/InternalServerException'
```

## Usage dans les services

```java
// Ressource non trouvée
throw new NotFoundException("Company identified by " + id + " not found");

// Paramètre invalide
throw new BadRequestException("Invalid input: " + field);

// Accès refusé
throw new ForbiddenException("Access denied to this resource");

// Non authentifié
throw new UnauthorizedException("Authentication required");

// Conflit (doublon, état invalide)
throw new ConflictException("Email " + email + " is already in use");

// Erreur interne
throw new InternalErrorException("Failed to process request", cause);

// Fonctionnalité non implémentée
throw new NotImplementedException("This feature is not yet implemented");
```

## Tests

### Test unitaire du handler

```java
@ExtendWith(MockitoExtension.class)
class InternalToRestExceptionHandlerTest {

  private final InternalToRestExceptionHandler handler = new InternalToRestExceptionHandler();

  @Test
  void handleBadRequest_ShouldReturn400() {
    var response = handler.handleBadRequest(new BadRequestException("Invalid input"));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getType()).isEqualTo("400 BAD_REQUEST");
    assertThat(response.getBody().getMessage()).isEqualTo("Invalid input");
  }

  @Test
  void handleNotFound_ShouldReturn404() {
    var response = handler.handleNotFound(new NotFoundException("Not found"));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void handleDefault_ShouldReturn500() {
    var response = handler.handleDefault(new RuntimeException("Unexpected"));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
```

### Test d'intégration

```java
@Test
void getEntity_withInvalidId_shouldReturn404() {
  var api = new EntityApi(adminClient);
  var exception = assertThrows(ApiException.class,
      () -> api.getEntityById("nonexistent-id"));
  assertThat(exception.getCode()).isEqualTo(404);
}
```

## Logging levels

| HTTP Status | Log Level | Pourquoi |
|---|---|---|
| 4xx (client error) | `log.info` | Erreur du client, pas un bug serveur |
| 429, lock exceptions | `log.warn` | Problème temporaire, peut nécessiter attention |
| 5xx (server error) | `log.error` | Bug ou erreur inattendue du serveur |

## Intégration avec Spring Security

Le handler fonctionne avec `@PreAuthorize` et `@EnableMethodSecurity` :

```java
@EnableMethodSecurity
@Configuration
public class SecurityConfiguration { ... }

@RestController
public class MyController {

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/entities/{id}")
  public void delete(@PathVariable String id) {
    // Si pas ADMIN → AccessDeniedException → handler → 403 FORBIDDEN
  }
}
```

## Vérification

1. Adapter `{basePackage}` au package réel du projet
2. Lancer `./gradlew build` (ou `mvn compile`) pour vérifier la compilation
3. Vérifier que le handler catch bien toutes les exceptions via des tests
4. Le format de réponse doit toujours être `{ "type": "...", "message": "..." }`
