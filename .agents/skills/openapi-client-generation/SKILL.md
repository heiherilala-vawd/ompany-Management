---
name: openapi-client-generation
description: Configure OpenAPI Generator in a Spring Boot project to auto-generate Java REST client classes from an api.yml spec. Use when the user needs to generate API client, DTOs, or set up OpenAPI generation from an OpenAPI spec.
---

# OpenAPI Client Generation — Spring Boot

Configure OpenAPI Generator to auto-generate Java REST client classes (DTOs, API interfaces, HTTP client) from an OpenAPI spec (`api.yml`).

## Architecture

```
src/main/resources/api/api.yml  (OpenAPI spec)
        │
        ▼
  build/gen/                     (generateJavaClient task — OpenAPI Generator)
        │
        ▼
  Maven local install + copy     (publish_gen_to_maven_local.sh)
        │
        ▼
  src/main/java/{basePackage}/client/  (generated code used in compilation)
```

## Étapes

### 1. Plugin Gradle

Dans `build.gradle.kts` :

```kotlin
plugins {
    id("org.openapi.generator") version "7.6.0"
}
```

### 2. Dépendances

```kotlin
dependencies {
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
}
```

### 3. Fichier OpenAPI

Placer le fichier à `src/main/resources/api/api.yml` :

```yaml
openapi: 3.0.3
info:
  title: My API
  version: '1.0'
paths: {}
```

### 4. Tâche `generateJavaClient`

```kotlin
val generateJavaClient by tasks.registering(GenerateTask::class) {
    generatorName.set("java")
    groupId.set("{groupId}")
    id.set("{artifactId}")

    outputDir.set("$buildDir/gen")
    inputSpec.set("$projectDir/src/main/resources/api/api.yml")

    apiPackage.set("{basePackage}.client.api")
    invokerPackage.set("{basePackage}.client.invoker")
    modelPackage.set("{basePackage}.client.model")

    library.set("native")

    configOptions.set(
        mapOf(
            "serializationLibrary" to "jackson",
            "dateLibrary" to "custom",
            "useJakartaEe" to "true"
        )
    )

    typeMappings.set(
        mapOf(
            "Date" to "java.time.LocalDate",
            "DateTime" to "java.time.Instant"
        )
    )

    skipValidateSpec.set(false)
    logToStderr.set(true)
    generateApiTests.set(false)
    generateModelTests.set(false)
}
```

### 5. Scripts de publication

Créer `.shell/publish_gen_to_maven_local.sh` :

```bash
cd build/gen && mvn clean install
set -e

SRC="./src/main/java/{basePackage}/client"
DEST="../../src/main/java/{basePackage}/client"

echo "📦 Suppression de l'ancien dossier..."
rm -rf "$DEST"

echo "📁 Copie du nouveau dossier..."
mkdir -p "$(dirname "$DEST")"
cp -r "$SRC" "$DEST"

echo "✅ Copie terminée avec succès."
```

Créer `.shell/publish_gen_to_maven_local.bat` (pour Windows) :

```bat
cd build/gen && mvn clean install

set SRC=.\src\main\java\{basePackage}\client
set DEST=..\..\src\main\java\{basePackage}\client

echo 📦 Suppression de l'ancien dossier...
if exist "%DEST%" (
    rmdir /s /q "%DEST%"
)

echo 📁 Copie du nouveau dossier...
mkdir "%DEST%" 2>nul
xcopy "%SRC%" "%DEST%" /E /I /Y

echo ✅ Copie terminée avec succès.
endlocal
pause
```

Rendre le script exécutable :

```bash
chmod +x .shell/publish_gen_to_maven_local.sh
```

### 6. Tâche `publishJavaClientToMavenLocal`

```kotlin
val publishJavaClientToMavenLocal by tasks.registering(Exec::class) {
    dependsOn(generateJavaClient)

    if (OperatingSystem.current().isWindows) {
        commandLine("cmd", "/c", "./.shell/publish_gen_to_maven_local.bat")
    } else {
        commandLine("./.shell/publish_gen_to_maven_local.sh")
    }
}
```

### 7. Compilation dépend du publish

```kotlin
tasks.named("compileJava") {
    dependsOn(publishJavaClientToMavenLocal)
}
```

### 8. Exclure le code généré

Dans la configuration Spotless/Checkstyle/JaCoCo, exclure `**/client/**`, `**/gen/**` :

```kotlin
// Exemple Spotless
java {
    targetExclude("**/client/**", "**/gen/**", "**/generated/**")
}

// Exemple JaCoCo
afterEvaluate {
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it).apply {
                exclude("**/client/**", "**/model/**", "**/api/**", "**/invoker/**")
            }
        })
    )
}
```

## Utilisation

Générer le client :

```bash
./gradlew generateJavaClient
```

Build complet (compile = génère + compile) :

```bash
./gradlew build
```

Le code généré se trouve dans `src/main/java/{basePackage}/client/` avec 3 sous-packages :

| Package | Contenu |
|---|---|
| `{basePackage}.client.api` | Classes API avec méthodes HTTP |
| `{basePackage}.client.invoker` | Client HTTP, configuration, exceptions |
| `{basePackage}.client.model` | DTOs de requête/réponse |

## Mapping des types

| Type OpenAPI | Type Java |
|---|---|
| `Date` | `java.time.LocalDate` |
| `DateTime` | `java.time.Instant` |

## Vérification

1. Adapter `{basePackage}`, `{groupId}` et `{artifactId}` au projet
2. Lancer `./gradlew generateJavaClient` — vérifier `build/gen/`
3. Lancer `./gradlew build` — doit compiler sans erreur
4. Vérifier que `src/main/java/{basePackage}/client/` contient les classes générées
