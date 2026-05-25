---
name: java-code-quality
description: Configure Spotless formatting, Checkstyle static analysis, Git hooks, and CI pipeline for Java code quality in a Spring Boot project. Use when setting up code style, format checks, or quality gates.
---

# Java Code Quality — Spring Boot

Set up **Spotless** (auto-format), **Checkstyle** (static analysis), **Git hooks** (local validation), and **GitHub Actions CI** (global quality gate).

## 1. Spotless — Formatage automatique

### Plugin

```kotlin
plugins {
    id("com.diffplug.spotless") version "6.25.0"
}
```

### Configuration

```kotlin
spotless {
    java {
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()

        targetExclude(
            "**/build/**",
            "**/client/**",
            "**/generated/**",
            "**/gen/**"
        )
    }
}
```

### Commandes

```bash
./gradlew spotlessApply    # Corrige automatiquement le code
./gradlew spotlessCheck    # Vérifie si le code est bien formaté
```

## 2. Checkstyle — Analyse statique

### Plugin (built-in Gradle)

```kotlin
plugins {
    id("checkstyle")
}
```

### Configuration

```kotlin
checkstyle {
    toolVersion = "10.17.0"
    configFile = file("$rootDir/config/checkstyle/google_checks_custom.xml")
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required = true
        html.required = true
    }
}
```

### Fichier de règles

Créer `config/checkstyle/google_checks_custom.xml` basé sur Google Java Style :

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
          "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
          "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
  <property name="charset" value="UTF-8"/>
  <property name="severity" value="warning"/>
  <property name="fileExtensions" value="java, properties, xml"/>

  <module name="BeforeExecutionExclusionFileFilter">
    <property name="fileNamePattern" value="module\-info\.java$"/>
  </module>

  <module name="FileTabCharacter">
    <property name="eachLine" value="true"/>
  </module>

  <module name="LineLength">
    <property name="fileExtensions" value="java"/>
    <property name="max" value="100"/>
    <property name="ignorePattern"
      value="^package.*|^import.*|a href|href|http://|https://|ftp://"/>
  </module>

  <module name="TreeWalker">
    <module name="OuterTypeFilename"/>
    <module name="IllegalTokenText"/>
    <module name="AvoidEscapedUnicodeCharacters"/>
    <module name="AvoidStarImport"/>
    <module name="OneTopLevelClass"/>
    <module name="NoLineWrap">
      <property name="tokens" value="PACKAGE_DEF, IMPORT, STATIC_IMPORT"/>
    </module>
    <module name="EmptyBlock">
      <property name="option" value="TEXT"/>
    </module>
    <module name="NeedBraces"/>
    <module name="LeftCurly"/>
    <module name="RightCurly"/>
    <module name="WhitespaceAfter"/>
    <module name="WhitespaceAround">
      <property name="allowEmptyConstructors" value="true"/>
      <property name="allowEmptyLambdas" value="true"/>
      <property name="allowEmptyMethods" value="true"/>
      <property name="allowEmptyTypes" value="true"/>
      <property name="allowEmptyLoops" value="true"/>
    </module>
    <module name="OneStatementPerLine"/>
    <module name="MultipleVariableDeclarations"/>
    <module name="ArrayTypeStyle"/>
    <module name="MissingSwitchDefault"/>
    <module name="FallThrough"/>
    <module name="UpperEll"/>
    <module name="ModifierOrder"/>
    <module name="EmptyLineSeparator"/>
    <module name="SeparatorWrap"/>
    <module name="PackageName">
      <property name="format" value="^[a-z]+(\.[a-z][a-z0-9]*)*$"/>
    </module>
    <module name="TypeName"/>
    <module name="MemberName">
      <property name="format" value="^[a-z][a-z0-9][a-zA-Z0-9]*$"/>
    </module>
    <module name="ParameterName">
      <property name="format" value="^[a-z]([a-z0-9][a-zA-Z0-9]*)?$"/>
    </module>
    <module name="LambdaParameterName">
      <property name="format" value="^[a-z]([a-z0-9][a-zA-Z0-9]*)?$"/>
    </module>
    <module name="CatchParameterName">
      <property name="format" value="^[a-z]([a-z0-9][a-zA-Z0-9]*)?$"/>
    </module>
    <module name="LocalVariableName">
      <property name="format" value="^[a-z]([a-z0-9][a-zA-Z0-9]*)?$"/>
    </module>
    <module name="ClassTypeParameterName">
      <property name="format" value="(^[A-Z][0-9]?)$|([A-Z][a-zA-Z0-9]*[T]$)"/>
    </module>
    <module name="MethodTypeParameterName">
      <property name="format" value="(^[A-Z][0-9]?)$|([A-Z][a-zA-Z0-9]*[T]$)"/>
    </module>
    <module name="InterfaceTypeParameterName">
      <property name="format" value="(^[A-Z][0-9]?)$|([A-Z][a-zA-Z0-9]*[T]$)"/>
    </module>
    <module name="NoFinalizer"/>
    <module name="GenericWhitespace"/>
    <module name="Indentation">
      <property name="basicOffset" value="2"/>
      <property name="braceAdjustment" value="2"/>
      <property name="caseIndent" value="2"/>
      <property name="throwsIndent" value="4"/>
      <property name="lineWrappingIndentation" value="4"/>
      <property name="arrayInitIndent" value="2"/>
    </module>
    <module name="AbbreviationAsWordInName">
      <property name="allowedAbbreviationLength" value="0"/>
    </module>
    <module name="OverloadMethodsDeclarationOrder"/>
    <module name="VariableDeclarationUsageDistance"/>
    <module name="CustomImportOrder">
      <property name="customImportOrderRules"
        value="THIRD_PARTY_PACKAGE###STATIC"/>
    </module>
    <module name="MethodParamPad"/>
    <module name="NoWhitespaceBefore"/>
    <module name="ParenPad"/>
    <module name="OperatorWrap">
      <property name="option" value="NL"/>
    </module>
    <module name="AnnotationLocation"/>
    <module name="NonEmptyAtclauseDescription"/>
    <module name="InvalidJavadocPosition"/>
    <module name="JavadocTagContinuationIndentation"/>
    <module name="SummaryJavadoc"/>
    <module name="JavadocParagraph"/>
    <module name="RequireEmptyLineBeforeBlockTagGroup"/>
    <module name="AtclauseOrder">
      <property name="tagOrder"
        value="@param, @return, @throws, @deprecated"/>
    </module>
    <module name="JavadocMethod">
      <property name="accessModifiers" value="public"/>
      <property name="allowMissingParamTags" value="true"/>
      <property name="allowMissingReturnTag" value="true"/>
      <property name="allowedAnnotations" value="Override, Test"/>
    </module>
    <module name="MethodName">
      <property name="format" value="^[a-z][a-z0-9][a-zA-Z0-9_]*$"/>
    </module>
    <module name="SingleLineJavadoc"/>
    <module name="EmptyCatchBlock">
      <property name="exceptionVariableName" value="expected"/>
    </module>
    <module name="CommentsIndentation"/>
  </module>
</module>
```

### Commandes

```bash
./gradlew checkstyleMain       # Analyse le code principal
./gradlew checkstyleTest       # Analyse les tests
./gradlew check                # Lance toutes les vérifications
```

## 3. Git Hook — Validation locale

Créer `.git/hooks/pre-commit` :

```bash
#!/bin/bash
echo "🧹 Formatage du code..."
./gradlew spotlessApply
if [ $? -ne 0 ]; then
  echo "❌ Erreur Spotless"
  exit 1
fi
echo "🔎 Vérification Checkstyle..."
./gradlew checkstyleMain
if [ $? -ne 0 ]; then
  echo "❌ Erreur Checkstyle"
  exit 1
fi
echo "✅ Code conforme"
```

Activation :

```bash
chmod +x .git/hooks/pre-commit
```

## 4. GitHub Actions CI

Créer `.github/workflows/code-quality.yml` :

```yaml
name: Code Quality

on:
  push:
  pull_request:

jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
      - name: Grant permission
        run: chmod +x gradlew
      - name: Spotless check
        run: ./gradlew spotlessCheck
      - name: Checkstyle
        run: ./gradlew checkstyleMain
      - name: Build project
        run: ./gradlew build
```

## 5. Flux global

```
Développement
    ↓
Git commit → Git Hook (Spotless + Checkstyle)
    ↓
Push GitHub → GitHub Actions CI
    ↓
Validation ou blocage
```

## Vérification

1. Adapter la version de Java (21 ou 17) selon le projet
2. Lancer `./gradlew spotlessApply` — vérifie que le code est formaté
3. Lancer `./gradlew checkstyleMain` — vérifie qu'aucune règle n'est violée
4. Lancer `./gradlew build` — doit compiler sans erreur
