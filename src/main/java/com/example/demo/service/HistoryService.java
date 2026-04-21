package com.example.demo.service;

import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.*;
import com.example.demo.model.criteria.HistoryCriteria;
import com.example.demo.repository.HistoryRepository;
import com.example.demo.service.utils.PageUtils;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryService {

  private final HistoryRepository historyRepository;

  public Optional<History> findById(String id) {
    return historyRepository.findById(id);
  }

  public Page<History> findAll(
      PageFromOne page, BoundedPageSize pageSize, HistoryCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return historyRepository.findAll(toSpecification(criteria), pageable);
  }

  public List<History> findAll() {
    return historyRepository.findAll();
  }

  @Transactional
  public History save(History history) {
    return historyRepository.save(history);
  }

  @Transactional
  public List<History> saveAll(List<History> histories) {
    return historyRepository.saveAll(histories);
  }

  private Specification<History> toSpecification(HistoryCriteria criteria) {
    return Specification.<History>where(equal(criteria.getUserId(), "user", "id"))
        .and(equal(criteria.getEntityType(), "entityType"))
        .and(equal(criteria.getEntityId(), "entityId"))
        .and(greaterThanOrEqualTo(criteria.getDateFrom(), "modifiedAt"))
        .and(lessThanOrEqualTo(criteria.getDateTo(), "modifiedAt"));
  }

  private static <T> Specification<T> greaterThanOrEqualTo(
      java.time.Instant value, String fieldName) {
    return (root, query, criteriaBuilder) -> {
      if (value == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), value);
    };
  }

  private static <T> Specification<T> lessThanOrEqualTo(java.time.Instant value, String fieldName) {
    return (root, query, criteriaBuilder) -> {
      if (value == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), value);
    };
  }

  public History uploadHistory(
      CreatAndUpdateEntity oldCreatAndUpdate,
      CreatAndUpdateEntity newCreatAndUpdateEntity,
      String entityId,
      User creater) {
    String oldValue = "";
    String newValue = "";
    if (History.EntityType.valueOf(newCreatAndUpdateEntity.getClass().getSimpleName().toUpperCase())
        == History.EntityType.USER) {
      User odlUser = (User) oldCreatAndUpdate;
      User newUser = (User) newCreatAndUpdateEntity;
      odlUser.setPassword("");
      newUser.setPassword("");
      oldValue = odlUser.toString();
      newValue = newUser.toString();
    } else {
      oldValue = oldCreatAndUpdate == null ? null : oldCreatAndUpdate.toString();
      newValue = newCreatAndUpdateEntity.toString();
    }

    History history =
        History.builder()
            .user(creater)
            .modifiedAt(Instant.now())
            .entityType(
                History.EntityType.valueOf(
                    newCreatAndUpdateEntity.getClass().getSimpleName().toUpperCase()))
            .entityId(entityId)
            .previousValue(oldValue)
            .newValue(newValue)
            .build();
    historyRepository.save(history);
    return history;
  }
}
