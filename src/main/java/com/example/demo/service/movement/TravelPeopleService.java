package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelPeopleCriteria;
import com.example.demo.model.movement.TravelPeople;
import com.example.demo.repository.movement.TravelPeopleRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MovementValidator;
import java.util.ArrayList;
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
@Transactional(readOnly = true)
public class TravelPeopleService {

  private final TravelPeopleRepository travelPeopleRepository;
  private final ModificationUtils modificationUtils;
  private final MovementValidator movementValidator;

  public Optional<TravelPeople> findById(String id) {
    return travelPeopleRepository.findById(id);
  }

  public Page<TravelPeople> findAll(
      PageFromOne page, BoundedPageSize pageSize, TravelPeopleCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return travelPeopleRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<TravelPeople> createOrUpdateAll(List<TravelPeople> travelPeopleList) {
    movementValidator.validateTravelPeoples(travelPeopleList);
    List<TravelPeople> processedTravelPeoples = new ArrayList<>();
    for (TravelPeople travelPeople : travelPeopleList) {
      TravelPeople existingTravelPeople =
          travelPeopleRepository.findById(travelPeople.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          travelPeople,
          existingTravelPeople,
          travelPeople.getId(),
          modificationUtils.takePrimaryUser());
      processedTravelPeoples.add(travelPeople);
    }
    return travelPeopleRepository.saveAll(processedTravelPeoples);
  }

  @Transactional
  public void deleteById(String id) {
    travelPeopleRepository.deleteById(id);
  }

  private Specification<TravelPeople> toSpecification(TravelPeopleCriteria criteria) {
    return (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

      if (criteria.getTravelId() != null) {
        predicates.add(cb.equal(root.get("travel").get("id"), criteria.getTravelId()));
      }
      if (criteria.getUserId() != null) {
        predicates.add(cb.equal(root.get("user").get("id"), criteria.getUserId()));
      }
      if (criteria.getArrivalLocation() != null) {
        predicates.add(
            cb.equal(root.get("arrivalLocation").get("id"), criteria.getArrivalLocation()));
      }
      if (criteria.getArrivalDateMin() != null) {
        predicates.add(
            cb.greaterThanOrEqualTo(root.get("arrivalDate"), criteria.getArrivalDateMin()));
      }
      if (criteria.getArrivalDateMax() != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("arrivalDate"), criteria.getArrivalDateMax()));
      }
      if (criteria.getNotArrived() != null && criteria.getNotArrived()) {
        predicates.add(
            cb.or(cb.isNull(root.get("arrivalDate")), cb.isNull(root.get("arrivalLocation"))));
      }

      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };
  }
}
