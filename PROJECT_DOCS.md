# 📚 Documentation Technique - Demo2 Project

*Documentation générée le 2026-04-22 pour faciliter l'utilisation par les agents IA*

## 🧠 Vue globale du projet

### Architecture
- **Framework**: Spring Boot 4.0.5 avec Java 21
- **Architecture**: Architecture en couches (Controller → Service → Repository → Database)
- **Base de données**: PostgreSQL avec Flyway pour les migrations
- **Sécurité**: Spring Security avec JWT
- **API**: RESTful avec génération automatique de clients via OpenAPI 3.0
- **Monitoring**: Sentry pour les logs et erreurs

### Organisation du code
- **Convention de nommage**: PascalCase pour les classes, camelCase pour les méthodes
- **Packages**: Séparation claire par responsabilité (endpoint, service, repository, model)
- **DTOs**: Séparation entre modèles internes et modèles API client
- **Mappers**: Conversion automatique entre modèles internes et API
- **Audit**: Toutes les entités étendent `CreatAndUpdateEntity` pour tracking automatique

### Principes et conventions
- **Idempotence**: Les opérations PUT sont idempotentes (client peut fournir l'ID)
- **Validation**: Utilisation de Bean Validation (@NotBlank, etc.)
- **Transactions**: @Transactional sur les services pour la cohérence
- **Sécurité**: @PreAuthorize avec rôles (ADMIN, ADMINISTRATION, etc.)
- **Pagination**: Utilisation de PageFromOne et BoundedPageSize
- **Spécifications**: JPA Criteria API pour les recherches filtrées

## 🔗 Flux de fonctionnement

### Chemin d'une requête HTTP
```
Client HTTP → Controller → Mapper (API→Domain) → Service → Repository → Database
Response: Database → Repository → Service → Mapper (Domain→API) → Controller → Client
```

### Rôle des composants
1. **Controller**: Point d'entrée REST, validation des autorisations, gestion des erreurs
2. **Mapper**: Conversion entre modèles API (client) et modèles internes (domain)
3. **Service**: Logique métier, transactions, appels aux repositories
4. **Repository**: Accès aux données via JPA/Hibernate
5. **ModificationUtils**: Gestion automatique de l'audit (created/updated by/at)

### Gestion des données
- **Input**: DTOs client (`Crupdate*`) → Mapper → Entités domain
- **Output**: Entités domain → Mapper → DTOs client (`*`)
- **Audit**: Automatique via `ModificationUtils.createOrUpdateModel()`
- **Historique**: AOP pour tracking des modifications (optionnel)

## 🧩 Structure des dossiers et fichiers

### Arborescence principale
```
src/main/java/com/example/demo/
├── Demo2Application.java              # Point d'entrée Spring Boot
├── client/                            # Code généré automatiquement
│   ├── api/                          # Interfaces API (JobApi, CompanyApi, etc.)
│   ├── invoker/                      # Configuration HTTP client
│   └── model/                        # DTOs API (CrupdateJob, Job, etc.)
├── endpoint/
│   └── rest/
│       ├── controller/               # Controllers REST
│       │   ├── CompanyController.java
│       │   ├── JobController.java
│       │   └── money/                # Sous-controllers par domaine
│       └── mapper/                   # Mappers DTO ↔ Domain
│           ├── CompanyMapper.java
│           └── EnumMapper.java
├── model/                            # Entités JPA
│   ├── Company.java
│   ├── CreatAndUpdateEntity.java     # Classe de base avec audit
│   ├── criteria/                     # Classes de critères pour filtrage
│   ├── exception/                    # Exceptions personnalisées
│   └── money/                        # Entités métier par domaine
├── repository/                       # Interfaces JPA
│   ├── CompanyRepository.java
│   ├── specification/                # Utilitaires pour JPA Criteria
│   └── money/                        # Repositories par domaine
└── service/                          # Logique métier
    ├── CompanyService.java
    ├── utils/                        # Utilitaires (ModificationUtils)
    └── money/                        # Services par domaine
```

### Éléments importants par catégorie

#### Controllers
- **Convention**: Un controller par entité principale
- **Pattern**: `@RestController` + `@AllArgsConstructor`
- **Sécurité**: `@PreAuthorize` avec rôles
- **Endpoints**: GET (liste/détail), PUT (create/update), DELETE

#### Services
- **Convention**: Un service par entité avec logique métier
- **Pattern**: `@Service` + `@RequiredArgsConstructor` + `@Transactional(readOnly = true)`
- **Méthodes clés**: `findById()`, `findAll()`, `createOrUpdateAll()`, `deleteById()`

#### Repositories
- **Convention**: `JpaRepository<Entity, String>` + `JpaSpecificationExecutor<Entity>`
- **Pattern**: Méthodes de recherche personnalisées + Criteria API

#### Entities
- **Convention**: `@Entity` + extends `CreatAndUpdateEntity`
- **Pattern**: Lombok (`@Getter`, `@Setter`, `@SuperBuilder`) + JPA annotations
- **ID**: `String id` (UUID généré automatiquement si non fourni)

#### Mappers
- **Convention**: Conversion bidirectionnelle API ↔ Domain
- **Pattern**: `@Component` + méthodes `toDomain()` et `toRest*()`

## 🔌 Utilisation des clients API

### Génération automatique
- **Source**: `src/main/resources/api/api.yml` (OpenAPI 3.0)
- **Outil**: OpenAPI Generator Gradle plugin
- **Commande**: `./gradlew generateJavaClient`
- **Publication**: `./gradlew publishJavaClientToMavenLocal`

### Structure générée
```java
// Interface API
public interface JobApi {
    List<Job> crupdateJobs(List<CrupdateJob> jobs);
    Job getJobById(String jobId);
}

// DTOs générés
public class CrupdateJob {
    private String id;        // Optionnel pour création
    private String name;
    private String description;
    // ... autres champs
}

public class Job extends CrupdateJob {
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    // ... champs d'audit
}
```

### Utilisation dans les tests
```java
@Autowired private JobApi jobApi;

@Test
void shouldCreateJob() {
    CrupdateJob input = new CrupdateJob()
        .name("Test Job")
        .description("Description");
    
    List<Job> result = jobApi.crupdateJobs(List.of(input));
    assertThat(result).hasSize(1);
}
```

## 🧪 Stratégie de tests

### Organisation
```
src/test/java/com/example/demo/
├── integration/                      # Tests d'intégration end-to-end
│   ├── JobIT.java                   # Test complet d'une feature
│   ├── conf/                        # Configuration commune
│   └── money/                       # Tests par domaine
├── service/                         # Tests unitaires des services
└── endpoint/                        # Tests des controllers (si nécessaire)
```

### Types de tests
1. **Tests d'intégration** (`*IT.java`): Testcontainers + PostgreSQL + API client
2. **Tests unitaires**: Services isolés avec mocks
3. **Couverture**: JaCoCo avec seuils (50% lignes, 40% branches)

### Pattern des tests d'intégration
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = JobIT.ContextInitializer.class)
class JobIT {
    
    @BeforeEach
    void setUp() {
        // Configuration JWT + données de test
        TestDataSqlLoader.executeAllSqlScripts(dataSource);
    }
    
    @Test
    void shouldCreateAndUpdateJob() {
        // Given
        CrupdateJob input = createTestJob();
        
        // When
        List<Job> result = jobApi.crupdateJobs(List.of(input));
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Job");
    }
}
```

### Créer un nouveau test
1. **Créer la classe**: `FeatureIT.java` dans `integration/`
2. **Étendre**: `AbstractContextInitializer` pour Testcontainers
3. **Configurer**: JWT mocks et données de test SQL
4. **Tester**: Utiliser les APIs client générées

## ⚙️ Commandes importantes

### Développement
```bash
# Lancer l'application en mode dev
./gradlew bootRun

# Compiler le projet
./gradlew compileJava

# Générer le client API Java
./gradlew generateJavaClient

# Publier le client dans Maven local
./gradlew publishJavaClientToMavenLocal
```

### Tests et qualité
```bash
# Lancer tous les tests
./gradlew test

# Tests avec couverture JaCoCo
./gradlew jacocoTestReport

# Vérifier la couverture (seuils)
./gradlew jacocoTestCoverageVerification

# Analyse statique (Checkstyle)
./gradlew checkstyleMain

# Formattage automatique (Spotless)
./gradlew spotlessApply
```

### Base de données
```bash
# Appliquer les migrations Flyway
./gradlew flywayMigrate

# Nettoyer la base
./gradlew flywayClean
```

### Maintenance
```bash
# Build complet
./gradlew build

# Générer JAR exécutable
./gradlew bootJar

# Analyse SonarQube (optionnel)
./gradlew sonarqube
```

## 🧭 Fichiers de référence

### 📋 Modèles pour créer une nouvelle feature

#### 1. Entité JPA (Model)
**Fichier référence**: `src/main/java/com/example/demo/model/Company.java`
```java
@Entity
@Table(name = "company")
@Getter @Setter @ToString @SuperBuilder
@AllArgsConstructor @NoArgsConstructor
public class Company extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;
  
  @NotBlank(message = "Name is mandatory")
  private String name;
  
  // ... autres champs
  
  @Override
  public boolean equals(Object o) {
    // Implementation standard
  }
}
```

#### 2. Repository JPA
**Fichier référence**: `src/main/java/com/example/demo/repository/CompanyRepository.java`
```java
@Repository
public interface CompanyRepository 
    extends JpaRepository<Company, String>, JpaSpecificationExecutor<Company> {
  
  Optional<Company> findByName(String name);
  // ... méthodes de recherche personnalisées
}
```

#### 3. Service métier
**Fichier référence**: `src/main/java/com/example/demo/service/CompanyService.java`
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

  private final CompanyRepository companyRepository;
  private final ModificationUtils modificationUtils;

  @Transactional
  public List<Company> createOrUpdateAll(List<Company> companies) {
    List<Company> processed = new ArrayList<>();
    for (Company company : companies) {
      modificationUtils.ensureEntityId(company);
      Company existing = company.getId() == null ? null : 
        companyRepository.findById(company.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
        company, existing, company.getId(), modificationUtils.takePrimaryUser());
      processed.add(company);
    }
    return companyRepository.saveAll(processed);
  }
}
```

#### 4. Controller REST
**Fichier référence**: `src/main/java/com/example/demo/endpoint/rest/controller/CompanyController.java`
```java
@RestController
@AllArgsConstructor
public class CompanyController {

  private final CompanyService companyService;
  private final CompanyMapper companyMapper;

  @PutMapping("/companies")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public List<Company> crupdateCompanies(@RequestBody List<CrupdateCompany> toWrite) {
    var saved = companyService.createOrUpdateAll(
      toWrite.stream().map(companyMapper::toDomain).toList());
    return saved.stream().map(companyMapper::toRestCompany).toList();
  }
}
```

#### 5. Mapper DTO ↔ Domain
**Fichier référence**: `src/main/java/com/example/demo/endpoint/rest/mapper/CompanyMapper.java`
```java
@Component
@AllArgsConstructor
public class CompanyMapper {

  public Company toDomain(CrupdateCompany rest) {
    return Company.builder()
        .id(rest.getId())
        .name(rest.getName())
        // ... mapping des champs
        .build();
  }

  public Company toRestCompany(Company domain) {
    Company rest = new Company();
    rest.setId(domain.getId());
    rest.setName(domain.getName());
    // ... mapping + audit fields
    return rest;
  }
}
```

#### 6. Test d'intégration
**Fichier référence**: `src/test/java/com/example/demo/integration/JobIT.java`
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = FeatureIT.ContextInitializer.class)
class FeatureIT {

  @BeforeEach
  void setUp() throws Exception {
    TestDataSqlLoader.executeAllSqlScripts(dataSource);
  }

  @Test
  void shouldCreateAndUpdateFeature() {
    // Given
    CrupdateFeature input = createTestFeature();
    
    // When
    List<Feature> result = featureApi.crupdateFeatures(List.of(input));
    
    // Then
    assertThat(result).hasSize(1);
  }
}
```

### 🎯 Checklist création feature
1. **API YAML**: Ajouter la définition dans `api.yml`
2. **Générer client**: `./gradlew publishJavaClientToMavenLocal`
3. **Entité**: Créer `Feature.java` extends `CreatAndUpdateEntity`
4. **Repository**: Créer `FeatureRepository.java`
5. **Service**: Créer `FeatureService.java` avec `createOrUpdateAll()`
6. **Mapper**: Créer `FeatureMapper.java`
7. **Controller**: Créer `FeatureController.java`
8. **Test**: Créer `FeatureIT.java`
9. **Migration**: Ajouter script Flyway si nouvelle table

## 🔄 Mise à jour récente - Relation Job avec Expense et Income

### Modifications apportées
- **API**: Ajout du champ `job_id` dans `ExpenseMoney` et `IncomeMoney` (schémas et DTOs)
- **Modèle**: Ajout de la relation `@ManyToOne` vers `Job` dans `ExpenseMoney` et `IncomeMoney`
- **Migration**: Nouvelle migration `V0_10__add_job_relation_to_money.sql` pour ajouter les colonnes et contraintes FK
- **Mapper**: Mise à jour des `ExpenseMoneyMapper` et `IncomeMoneyMapper` pour gérer la relation Job
- **Documentation**: Mise à jour de cette section pour refléter les changements

### Impact
Les dépenses et revenus peuvent maintenant être associés à un chantier spécifique via la relation `job_id`. Cette relation permet de:
- Filtrer les mouvements financiers par chantier
- Suivre les coûts et revenus par projet
- Maintenir l'intégrité référentielle avec les chantiers

### Endpoints affectés
- `GET/PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/expenses`
- `GET/PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/incomes`
