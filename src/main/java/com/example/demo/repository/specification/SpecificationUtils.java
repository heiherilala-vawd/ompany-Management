package com.example.demo.repository.specification;

import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

public final class SpecificationUtils {

  private SpecificationUtils() {}

  public static <T> Specification<T> containsIgnoreCase(String value, String... path) {
    return (root, query, criteriaBuilder) -> {
      if (value == null || value.isBlank()) {
        return criteriaBuilder.conjunction();
      }
      Path<String> expression = resolvePath(root, path);
      return criteriaBuilder.like(
          criteriaBuilder.lower(expression), "%" + value.toLowerCase() + "%");
    };
  }

  public static <T> Specification<T> equal(Object value, String... path) {
    return (root, query, criteriaBuilder) -> {
      if (value == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(resolvePath(root, path), value);
    };
  }

  @SuppressWarnings("unchecked")
  private static <T, V> Path<V> resolvePath(
      jakarta.persistence.criteria.From<?, ?> root, String... path) {
    Path<?> current = root.get(path[0]);
    for (int i = 1; i < path.length; i++) {
      current = current.get(path[i]);
    }
    return (Path<V>) current;
  }
}
