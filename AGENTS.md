# AGENTS.md

## Règles générales
- **Ne jamais lire ni analyser le contenu du dossier `./build`** (y compris `build/gen/`, `build/reports/`, etc.). Ces fichiers sont générés automatiquement et ne doivent jamais être pris en compte. En cas de besoin, s’arrêter et demander confirmation.
- **Respecter l’architecture en couches** : Controller → Mapper → Service → Repository → Entity.
- Les mappers (ex: MapStruct) servent exclusivement à la conversion entre DTOs et objets domaine. **Aucune logique métier** ne doit y figurer.
- Les contrôleurs sont uniquement des points d’entrée REST, ils délèguent immédiatement aux services. **Pas de logique métier dans les contrôleurs.**
- Toute la logique métier, les validations complexes et les orchestrations sont à implémenter dans les services.
- Les repositories ne contiennent que les requêtes d’accès aux données (méthodes de recherche). Pas de logique métier.

## Classification des fonctionnalités
- **Fonctionnalités CRUD standards** : La grande majorité des entités (création, lecture, mise à jour, suppression). Pour celles-ci, appliquer un modèle identique : contrôleur simple, service de CRUD générique si possible, mappeur basique.
- **Fonctionnalités à part** : `purchaseOperation` et `travelOperation`. Ce sont des processus métier distincts avec une logique spécifique, potentiellement multi-étapes. Elles nécessitent un soin particulier, et peuvent déroger au pattern CRUD standard si nécessaire. Bien vérifier leur implémentation existante avant toute modification.

## Commandes
- Build : `./gradlew build`
- Test tous : `./gradlew test`
- Test unique : `./gradlew test --tests "com.example.demo.integration.money.ExpenseIT"`
- Formater le code : `./gradlew spotlessApply`
- Checkstyle : `./gradlew checkstyleMain`
- Générer le client API : `./gradlew generateJavaClient`
- Publier le client en local : `./gradlew publishJavaClientToMavenLocal`

## Workflow
- `compileJava` dépend de `publishJavaClientToMavenLocal` (le client API doit être généré avant la compilation).
- Testcontainers nécessite le socket Docker à `/var/run/docker.sock`.
- Seuils de couverture JaCoCo : 50% de lignes, 40% de branches (le build échoue si non atteints).
- Migrations Flyway dans `src/main/resources/db/migration/`, données de test dans `db/testdata/`.

## Architecture
- Spring Boot 4.0.5, Java 21, PostgreSQL avec Flyway.
- API définie dans `src/main/resources/api/api.yml` (OpenAPI 3.0).
- Code client généré : `build/gen/` → publié dans le dépôt Maven local → utilisé lors de la compilation.
- Couches : Controller → Mapper (API↔Domaine) → Service → Repository → Entity.
- Toutes les entités étendent `CreatAndUpdateEntity` pour les champs d’audit (créé/mis à jour par/à).
- Sécurité : JWT avec annotations `@PreAuthorize` (rôles ADMIN, ADMINISTRATION, etc.).

## Conventions
- Tests : `*IT.java` dans `src/test/java/.../integration/` utilisent Testcontainers + PostgreSQL.
- Exclus de la couverture : `**/client/**`, `**/model/**`, `**/api/**`, `**/invoker/**`, `**/dto/**`, `**/config/**`.
- Spotless exclut le code client généré (`**/client/**`).
- Configuration Checkstyle : `config/checkstyle/google_checks_custom.xml`.

## Bonnes pratiques supplémentaires
- Injection de dépendances par constructeur (pas de `@Autowired` sur les champs).
- Transactions dans les services, pas dans les contrôleurs.
- Lancer `spotlessApply` avant chaque commit pour garantir un formatage homogène.
- Éviter la duplication : si une logique CRUD est répétée, envisager une classe abstraite ou un service générique.