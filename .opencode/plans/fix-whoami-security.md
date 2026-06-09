# Plan : whoami + security fix

## Étape 1 : Ajouter company_ids à AuthResponse dans api.yml

Éditer `src/main/resources/api/api.yml` — trouver `AuthResponse:` (ligne ~14254) et ajouter après `role` :

```yaml
        role:
          type: string
          example: "EMPLOYEE"
        company_ids:
          type: array
          items:
            type: string
          example: ["comp_btp001", "comp_btp002"]
```

## Étape 2 : Régénérer le client

```bash
./gradlew clean publishJavaClientToMavenLocal
```

## Étape 3 : Restaurer setCompanyIds dans AuthService.java

Éditer `src/main/java/com/example/demo/endpoint/rest/security/service/AuthService.java` :
- Dans `authenticateUser()` (ligne ~56) : ajouter après `authResponse.setToken(jwt);`
- Dans `registerUser()` (ligne ~87) : ajouter après `authResponse.setToken(jwt);`  
- Dans `whoami()` (ligne ~114) : ajouter après `authResponse.setToken(token);`

Code à ajouter :
```java
    var companies = user.getCompanies();
    if (companies != null && !companies.isEmpty()) {
      authResponse.setCompanyIds(
          companies.stream().map(Company::getId).collect(java.util.stream.Collectors.toList()));
    }
```

Ajouter l'import si manquant :
```java
import com.example.demo.model.Company;
```

## Étape 4 : Corriger CompanyController URLs

Éditer `src/main/java/com/example/demo/endpoint/rest/controller/CompanyController.java`

**getCompanyById :**
AVANT : `@GetMapping("/companies/{id}")` avec `@PathVariable String id`
APRÈS : `@GetMapping("/users/{userId}/companies/{companyId}")` avec `@PathVariable String userId, @PathVariable String companyId`

**deleteCompanyById :**
AVANT : `@DeleteMapping("/companies/{id}")` avec `@PathVariable String id`
APRÈS : `@DeleteMapping("/users/{userId}/companies/{companyId}")` avec `@PathVariable String userId, @PathVariable String companyId`

## Étape 5 : Ajouter patterns sécurité

Dans `src/main/java/com/example/demo/endpoint/rest/security/SecurityConfiguration.java` :

Après les patterns COMPANY (ligne ~76), AJOUTER :
```java
                    .requestMatchers(GET, "/users/*/companies/*")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*")
                    .hasRole("ADMIN")
```

## Étape 6 : Compiler et tester

```bash
./gradlew compileJava && echo "✅ Compilation OK"
./run.sh
```
