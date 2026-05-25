---
name: jacoco-ci-setup
description: Configure JaCoCo coverage and GitHub Actions CI pipeline in a Spring Boot project. Use when the user needs to set up code coverage, CI workflows, Checkstyle, Spotless, or SonarQube integration.
---

# JaCoCo & CI Setup — Spring Boot

Configure JaCoCo code coverage with thresholds, GitHub Actions CI pipeline, Checkstyle, Spotless, and optional SonarQube integration.

## 1. JaCoCo dans `build.gradle.kts`

### Plugin

```kotlin
plugins {
    id("jacoco")
}
```

### Version

```kotlin
jacoco {
    toolVersion = "0.8.11"
}
```

### Exclusion patterns

```kotlin
val jacocoExcludePatterns = listOf(
    "**/client/**",
    "**/model/**",
    "**/api/**",
    "**/invoker/**",
    "**/dto/**",
    "**/config/**",
    "**/*Application.class",
    "**/*Application*.*"
)
```

### Rapport

```kotlin
tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        html.required = true
        xml.required = true
        csv.required = false
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it).matching {
                exclude(jacocoExcludePatterns)
            }
        })
    )
}
```

### Seuils de couverture

```kotlin
tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it).matching {
                exclude(jacocoExcludePatterns)
            }
        })
    )

    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.50".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.40".toBigDecimal()
            }
        }
    }
}
```

### Lier JaCoCo aux tests

```kotlin
tasks.withType<Test> {
    finalizedBy(tasks.jacocoTestReport)
}
```

### Lier la vérification à `check`

```kotlin
tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
```

## 2. GitHub Actions CI — `.github/workflows/ci.yml`

### Workflow complet

```yaml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

env:
  JAVA_VERSION: "21"

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15.2
        env:
          POSTGRES_DB: testdb
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: 00001111
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK ${{ env.JAVA_VERSION }}
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: temurin
          cache: gradle

      - name: Grant execute permission
        run: chmod +x gradlew

      - name: Build & Test
        run: ./gradlew build
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb
          SPRING_DATASOURCE_USERNAME: postgres
          SPRING_DATASOURCE_PASSWORD: 00001111

      - name: Upload JaCoCo Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: build/reports/jacoco/

      - name: Upload Test Results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: build/reports/tests/
```

### Alternative multi-job (checkstyle + spotless séparés)

```yaml
name: CI - Tests et Analyse de Code

on:
  push:
    branches: [ "main", "develop" ]
  pull_request:
    branches: [ "main" ]
  workflow_dispatch:

jobs:
  test:
    name: Tests et Couverture
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'gradle'
      - name: Make gradlew executable
        run: chmod +x gradlew
      - name: Run tests with JaCoCo
        run: ./gradlew test jacocoTestReport --no-daemon --info --stacktrace
      - name: Check coverage thresholds
        run: ./gradlew jacocoTestCoverageVerification --no-daemon
      - name: Upload JaCoCo report
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: build/reports/jacoco/html/
          retention-days: 7
      - name: Upload test report
        uses: actions/upload-artifact@v4
        with:
          name: test-report
          path: build/reports/tests/test/
          retention-days: 7

  checkstyle:
    name: Checkstyle
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Run Checkstyle
        run: ./gradlew checkstyleMain checkstyleTest --no-daemon
      - name: Upload Checkstyle report
        uses: actions/upload-artifact@v4
        with:
          name: checkstyle-report
          path: build/reports/checkstyle/
          retention-days: 7

  spotless:
    name: Spotless - Formatage
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Check code formatting
        run: ./gradlew spotlessCheck --no-daemon
```

### Testcontainers

Si le projet utilise Testcontainers (pas de service PostgreSQL direct), ajouter cette étape dans le job de test :

```yaml
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3
```

Et ne pas ajouter de `services: postgres` (Testcontainers gère son propre conteneur).

## 3. SonarQube (optionnel)

### Plugin Gradle

```kotlin
plugins {
    id("org.sonarqube") version "{version}"
}
```

### Configuration dans `build.gradle.kts`

```kotlin
sonarqube {
    properties {
        property("sonar.projectKey", "{sonar-project-key}")
        property("sonar.organization", "{sonar-organization}")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/jacoco/jacocoTestReport.xml").get().asFile.toString())
        property("sonar.java.checkstyle.reportPaths",
            layout.buildDirectory.file("reports/checkstyle/main.xml").get().asFile.toString())
        property("sonar.exclusions", "**/client/**, **/model/**, **/dto/**, **/config/**")
    }
}
```

### `sonar-project.properties` (alternative)

```properties
sonar.projectKey={sonar-project-key}
sonar.projectName={project-name}
sonar.projectVersion=1.0

sonar.sources=src/main/java
sonar.tests=src/test/java

sonar.language=java
sonar.java.source=21

sonar.exclusions=**/client/**, **/model/**, **/dto/**, **/config/**

sonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/jacocoTestReport.xml
sonar.java.checkstyle.reportPaths=build/reports/checkstyle/main.xml
```

### Job SonarCloud dans le CI

```yaml
  sonar:
    needs: build-and-test
    runs-on: ubuntu-latest
    if: github.event_name != 'pull_request' || github.event.pull_request.head.repo.owner.login == github.repository_owner

    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: gradle
      - name: Cache SonarCloud packages
        uses: actions/cache@v4
        with:
          path: ~/.sonar/cache
          key: ${{ runner.os }}-sonar
          restore-keys: ${{ runner.os }}-sonar
      - name: Build and analyze
        run: ./gradlew build sonar --info
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

## 4. Badges (README.md)

```markdown
![CI](https://github.com/{owner}/{repo}/actions/workflows/ci.yml/badge.svg)
![Coverage](https://img.shields.io/badge/coverage-XX%25-brightgreen)
```

## 5. Commandes utiles

```bash
./gradlew test                    # Exécuter les tests
./gradlew jacocoTestReport        # Générer le rapport
./gradlew jacocoTestCoverageVerification  # Vérifier les seuils
./gradlew checkstyleMain          # Vérifier le style
./gradlew spotlessCheck           # Vérifier le formatage
./gradlew clean test jacocoTestReport jacocoTestCoverageVerification  # Tout en un
```

## Vérification

1. Adapter les placeholders `{version}`, `{owner}`, `{repo}`, `{sonar-project-key}` au projet
2. Lancer `./gradlew test jacocoTestReport` — vérifier `build/reports/jacoco/html/index.html`
3. Lancer `./gradlew jacocoTestCoverageVerification` — doit passer ou échouer selon les seuils
4. Vérifier que le CI GitHub Actions s'exécute sur push
