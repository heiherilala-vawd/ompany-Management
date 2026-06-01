---
name: api-naming-conventions
description: Enforce `snake_case` (underscores, never hyphens) in all REST API endpoint paths, operationId, query parameters, and schema properties. Trigger every time the OpenAPI spec (`api.yml`) or any `@RestController` / `@RequestMapping` controller is created or edited.
---

# API Naming Conventions

## Contexte / Problème

Les endpoints API REST, paramètres de requête, et propriétés de schéma doivent suivre une convention de nommage cohérente. Ce skill documente la règle suivante : **toujours utiliser des underscores `_` — jamais de traits d'union `-`** dans les chemins d'endpoint, paramètres de requête, et noms de propriétés.

## Règles

### 1. Chemins d'endpoint (URL paths)

Toujours utiliser `snake_case` :

```
✅ /companies/{comp_id}/equipment_usage/{id}/return
❌ /companies/{comp-id}/equipment-usage/{id}/return
```

Exceptions : les standards qui imposent le kebab-case (ex: `openapi.yml` → `api.yml`). Mais dans les chemins REST du projet, c'est `_`.

### 2. Paramètres de chemin (`@PathVariable`)

Les noms de variables dans le path template suivent `snake_case` :

```yaml
parameters:
  - name: comp_id      # ✅ snake_case
  - name: equipment_id # ✅ snake_case
```

```java
@GetMapping("/companies/{comp_id}/equipment/{equipment_id}")
public Response getByEquipment(@PathVariable String comp_id,
                                @PathVariable String equipment_id)
```

### 3. Paramètres de requête (`@RequestParam`)

```yaml
parameters:
  - name: page_size       # ✅
  - name: arrival_date    # ✅
```

```java
@RequestParam(name = "page_size") BoundedPageSize pageSize
```

### 4. Propriétés de schéma (OpenAPI components)

Les propriétés des schémas utilisent `snake_case` :

```yaml
properties:
  company_id:      # ✅
  usage_status:    # ✅
  consumption_status: # ✅
```

### 5. OpérationId (génération du client)

Les `operationId` utilisent **lowerCamelCase** (convention OpenAPI Generator) :

```yaml
operationId: getEquipmentUsageById   # ✅ camelCase
operationId: crupdateEquipmentUsages # ✅ camelCase
```

## Vérification

- Parcourir `api.yml` : chercher les occurences de `-` dans les chemins avec `grep "\-{" src/main/resources/api/api.yml`
- Vérifier que tous les `@PathVariable` et `@RequestParam` dans les contrôleurs Java utilisent `snake_case`
- Vérifier que les `operationId` sont en `camelCase`
