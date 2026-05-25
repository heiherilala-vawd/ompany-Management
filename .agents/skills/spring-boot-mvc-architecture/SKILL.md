---
name: spring-boot-mvc-architecture
description: Implement a clean MVC architecture in Spring Boot with layers (Model, Repository, Service, Mapper, Controller) plus secure pagination and DAO pattern. Use when creating a new domain/entity or setting up the full MVC stack.
---

# Spring Boot MVC Architecture

Implement a clean layered architecture: **Controller → Mapper → Service → Repository → Entity**, with secure pagination, DAO pattern for dynamic queries, and audit fields.

## Vue globale

```
Controller (REST API)  →  Mapper (DTO ↔ Domain)  →  Service (logique métier)  →  Repository (accès DB)  →  Database
```

## 1. Entity (Model)

Toutes les entités étendent une classe de base pour les champs d'audit.

### `src/main/java/{basePackage}/model/CreatAndUpdateEntity.java`

```java
package {basePackage}.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class CreatAndUpdateEntity implements Serializable {

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  private User createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_by")
  private User updatedBy;

  @Column(columnDefinition = "TEXT")
  private String comment;
}
```

### `src/main/java/{basePackage}/model/{EntityName}.java`

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

## 2. Repository

### Repository simple (Spring Data JPA)

```java
package {basePackage}.repository;

import {basePackage}.model.{EntityName};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface {EntityName}Repository extends JpaRepository<{EntityName}, String> {
}
```

### Repository avec `JpaSpecificationExecutor` (filtres dynamiques)

```java
package {basePackage}.repository;

import {basePackage}.model.{EntityName};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface {EntityName}Repository
    extends JpaRepository<{EntityName}, String>, JpaSpecificationExecutor<{EntityName}> {
}
```

### DAO personnalisé avec CriteriaBuilder (filtres avancés)

```java
package {basePackage}.repository.dao;

import {basePackage}.model.{EntityName};
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class {EntityName}Dao {

  private final EntityManager entityManager;

  public List<{EntityName}> findByCriteria(String name, Pageable pageable) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<{EntityName}> query = cb.createQuery({EntityName}.class);
    Root<{EntityName}> root = query.from({EntityName}.class);

    List<Predicate> predicates = new ArrayList<>();

    if (name != null && !name.isEmpty()) {
      predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }

    query.where(predicates.toArray(new Predicate[0]));
    query.orderBy(cb.asc(root.get("name")));

    TypedQuery<{EntityName}> typedQuery = entityManager.createQuery(query);
    typedQuery.setFirstResult((int) pageable.getOffset());
    typedQuery.setMaxResults(pageable.getPageSize());

    return typedQuery.getResultList();
  }

  public long countByCriteria(String name) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> query = cb.createQuery(Long.class);
    Root<{EntityName}> root = query.from({EntityName}.class);

    List<Predicate> predicates = new ArrayList<>();

    if (name != null && !name.isEmpty()) {
      predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }

    query.select(cb.count(root));
    query.where(predicates.toArray(new Predicate[0]));

    return entityManager.createQuery(query).getSingleResult();
  }
}
```

## 3. Pagination

### `src/main/java/{basePackage}/model/BoundedPageSize.java`

```java
package {basePackage}.model;

import {basePackage}.model.exception.BadRequestException;
import lombok.Getter;

public class BoundedPageSize {

  @Getter
  private final int value;

  private static final int MAX_SIZE = 500;

  public BoundedPageSize(String value) {
    int intValue = Integer.parseInt(value);
    if (intValue < 1) {
      throw new BadRequestException("page value must be >=1");
    }
    if (intValue > MAX_SIZE) {
      throw new BadRequestException("page size must be <" + MAX_SIZE);
    }
    this.value = intValue;
  }
}
```

### `src/main/java/{basePackage}/model/PageFromOne.java`

```java
package {basePackage}.model;

import {basePackage}.model.exception.BadRequestException;
import lombok.Getter;

public class PageFromOne {

  @Getter
  private final int value;

  public PageFromOne(String value) {
    int intValue = Integer.parseInt(value);
    if (intValue < 1) {
      throw new BadRequestException("page value must be >=1");
    }
    this.value = intValue;
  }
}
```

### `src/main/java/{basePackage}/service/utils/PageUtils.java`

```java
package {basePackage}.service.utils;

import {basePackage}.model.BoundedPageSize;
import {basePackage}.model.PageFromOne;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageUtils {
  public static Pageable createPageable(PageFromOne page, BoundedPageSize pageSize) {
    if (page == null) {
      page = new PageFromOne("1");
    }
    if (pageSize == null) {
      pageSize = new BoundedPageSize("15");
    }
    return PageRequest.of(page.getValue() - 1, pageSize.getValue());
  }
}
```

## 4. Service

```java
package {basePackage}.service;

import {basePackage}.model.BoundedPageSize;
import {basePackage}.model.PageFromOne;
import {basePackage}.model.{EntityName};
import {basePackage}.repository.{EntityName}Repository;
import {basePackage}.service.utils.PageUtils;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class {EntityName}Service {

  private final {EntityName}Repository repository;

  public Optional<{EntityName}> findById(String id) {
    return repository.findById(id);
  }

  public List<{EntityName}> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return repository.findAll(pageable).getContent();
  }

  @Transactional
  public List<{EntityName}> createOrUpdateAll(List<{EntityName}> {entityNames}) {
    return repository.saveAll({entityNames});
  }

  @Transactional
  public void deleteById(String id) {
    repository.deleteById(id);
  }
}
```

### ModificationUtils (gestion des champs d'audit)

```java
package {basePackage}.service.utils;

import {basePackage}.model.CreatAndUpdateEntity;
import {basePackage}.model.User;
import {basePackage}.model.exception.BadRequestException;
import {basePackage}.model.exception.NotFoundException;
import {basePackage}.repository.UserRepository;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ModificationUtils {

  private final UserRepository userRepository;

  public void createOrUpdateModel(
      CreatAndUpdateEntity entityToChange,
      CreatAndUpdateEntity entityInDB,
      String entityId,
      User creater) {
    if (entityInDB == null) {
      entityToChange.setCreatedAt(Instant.now());
      entityToChange.setCreatedBy(creater);
    } else {
      entityToChange.setCreatedAt(entityInDB.getCreatedAt());
      entityToChange.setCreatedBy(entityInDB.getCreatedBy());
    }
    entityToChange.setUpdatedAt(Instant.now());
    entityToChange.setUpdatedBy(creater);
  }

  public User takePrimaryUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new BadRequestException("User not authenticated");
    }
    String email = authentication.getName();
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
  }
}
```

## 5. Mapper

```java
package {basePackage}.endpoint.rest.mapper;

import {basePackage}.client.model.{EntityName}Dto;
import {basePackage}.client.model.Crupdate{EntityName}Dto;
import {basePackage}.client.model.{EntityName}TypeDto;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class {EntityName}Mapper {

  public {basePackage}.model.{EntityName} toDomain(Crupdate{EntityName}Dto dto) {
    if (dto == null) return null;
    return {basePackage}.model.{EntityName}.builder()
        .id(dto.getId())
        .name(dto.getName())
        .{entityName}Type(
            dto.get{EntityName}Type() != null
                ? {basePackage}.model.{EntityName}.{EntityName}Type.valueOf(dto.get{EntityName}Type().name())
                : null)
        .build();
  }

  public {EntityName}Dto toRestDto({basePackage}.model.{EntityName} domain) {
    if (domain == null) return null;
    {EntityName}Dto dto = new {EntityName}Dto();
    dto.setId(domain.getId());
    dto.setName(domain.getName());
    dto.set{EntityName}Type(
        domain.get{EntityName}Type() != null
            ? {EntityName}TypeDto.valueOf(domain.get{EntityName}Type().name())
            : null);
    return dto;
  }

  public List<{EntityName}Dto> toRestDtos(List<{basePackage}.model.{EntityName}> domains) {
    return domains.stream().map(this::toRestDto).toList();
  }

  public List<{basePackage}.model.{EntityName}> toDomain(List<Crupdate{EntityName}Dto> dtos) {
    return dtos.stream().map(this::toDomain).toList();
  }
}
```

### EnumMapper utilitaire

```java
package {basePackage}.endpoint.rest.mapper;

public class EnumMapper {
  public static <T extends Enum<T>> T mapEnum(Enum<?> source, Class<T> targetClass) {
    if (source == null) return null;
    return Enum.valueOf(targetClass, source.name());
  }
}
```

## 6. Controller

```java
package {basePackage}.endpoint.rest.controller;

import {basePackage}.client.model.{EntityName}Dto;
import {basePackage}.client.model.Crupdate{EntityName}Dto;
import {basePackage}.endpoint.rest.mapper.{EntityName}Mapper;
import {basePackage}.model.BoundedPageSize;
import {basePackage}.model.PageFromOne;
import {basePackage}.model.exception.NotFoundException;
import {basePackage}.service.{EntityName}Service;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class {EntityName}Controller {

  private final {EntityName}Service {entityName}Service;
  private final {EntityName}Mapper {entityName}Mapper;

  @GetMapping("/{entityNames}/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public {EntityName}Dto getById(@PathVariable String id) {
    return {entityName}Mapper.toRestDto(
        {entityName}Service
            .findById(id)
            .orElseThrow(() -> new NotFoundException("{EntityName} with id " + id + " not found")));
  }

  @GetMapping("/{entityNames}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public List<{EntityName}Dto> getAll(
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return {entityName}Service.findAll(page, pageSize).stream()
        .map({entityName}Mapper::toRestDto)
        .toList();
  }

  @PutMapping("/{entityNames}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public List<{EntityName}Dto> crupdate(@RequestBody List<Crupdate{EntityName}Dto> toWrite) {
    var saved = {entityName}Service.createOrUpdateAll(
        toWrite.stream().map({entityName}Mapper::toDomain).toList());
    return saved.stream().map({entityName}Mapper::toRestDto).toList();
  }

  @DeleteMapping("/{entityNames}/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteById(@PathVariable String id) {
    {entityName}Service.deleteById(id);
  }
}
```

### HealthController

```java
package {basePackage}.endpoint.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping(value = "/ping", produces = "text/plain")
  public String ping() {
    return "pong";
  }
}
```

## 7. Critères de recherche (optionnel)

```java
package {basePackage}.model.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class {EntityName}Criteria {
  private String name;
  private {basePackage}.model.{EntityName}.{EntityName}Type {entityName}Type;
}
```

## Convention Crupdate (Create + Update)

Le pattern **crupdate** combine création et mise à jour dans un seul endpoint `PUT` :

- Si l'ID fourni par le client existe en base → mise à jour de l'entité existante
- Si l'ID n'existe pas → création d'une nouvelle entité
- La gestion est faite dans le Service via `ModificationUtils.createOrUpdateModel()`

```java
@Transactional
public List<{EntityName}> createOrUpdateAll(List<{EntityName}> entities) {
  List<{EntityName}> processed = new ArrayList<>();
  for ({EntityName} entity : entities) {
    {EntityName} existing = entity.getId() == null
        ? null
        : repository.findById(entity.getId()).orElse(null);
    modificationUtils.createOrUpdateModel(entity, existing, entity.getId(), modificationUtils.takePrimaryUser());
    processed.add(entity);
  }
  return repository.saveAll(processed);
}
```

## Vérification

1. Adapter `{basePackage}` au package du projet
2. Lancer `./gradlew build` pour vérifier la compilation
3. Tester les endpoints : `GET /{entityNames}`, `PUT /{entityNames}`, `GET /{entityNames}/{id}`, `DELETE /{entityNames}/{id}`
4. Vérifier que la pagination fonctionne avec `?page=1&page_size=20`
5. Vérifier que les champs d'audit (`createdAt`, `updatedAt`) sont automatiquement remplis
