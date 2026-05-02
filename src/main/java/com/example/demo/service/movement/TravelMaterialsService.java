package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelMaterialsCriteria;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.repository.movement.TravelMaterialsRepository;
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
public class TravelMaterialsService {

  private final TravelMaterialsRepository travelMaterialsRepository;
  private final ModificationUtils modificationUtils;
  private final MovementValidator movementValidator;

  public Optional<TravelMaterials> findById(String id) {
    return travelMaterialsRepository.findById(id);
  }

  public Page<TravelMaterials> findAll(
      PageFromOne page, BoundedPageSize pageSize, TravelMaterialsCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return travelMaterialsRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<TravelMaterials> createOrUpdateAll(List<TravelMaterials> materialsList) {
    movementValidator.validateTravelMaterialsList(materialsList);
    List<TravelMaterials> processedTravelMaterials = new ArrayList<>();
    for (TravelMaterials travelMaterials : materialsList) {
      TravelMaterials existingTravelMaterials =
          travelMaterialsRepository.findById(travelMaterials.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          travelMaterials,
          existingTravelMaterials,
          travelMaterials.getId(),
          modificationUtils.takePrimaryUser());
      processedTravelMaterials.add(travelMaterials);
    }
    return travelMaterialsRepository.saveAll(processedTravelMaterials);
  }

  @Transactional
  public void deleteById(String id) {
    travelMaterialsRepository.deleteById(id);
  }

  private Specification<TravelMaterials> toSpecification(TravelMaterialsCriteria criteria) {
    return (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

      if (criteria.getTravelId() != null) {
        predicates.add(cb.equal(root.get("travel").get("id"), criteria.getTravelId()));
      }
      if (criteria.getMaterialId() != null) {
        predicates.add(cb.equal(root.get("material").get("id"), criteria.getMaterialId()));
      }
      if (criteria.getQuantity() != null) {
        predicates.add(cb.equal(root.get("quantity"), criteria.getQuantity()));
      }
      if (criteria.getQuantityReceived() != null) {
        predicates.add(cb.equal(root.get("quantityReceived"), criteria.getQuantityReceived()));
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
