package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelEquipmentCriteria;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.repository.movement.TravelEquipmentRepository;
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
public class TravelEquipmentService {

  private final TravelEquipmentRepository travelEquipmentRepository;
  private final ModificationUtils modificationUtils;
  private final MovementValidator movementValidator;

  public Optional<TravelEquipment> findById(String id) {
    return travelEquipmentRepository.findById(id);
  }

  public Page<TravelEquipment> findAll(
      PageFromOne page, BoundedPageSize pageSize, TravelEquipmentCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return travelEquipmentRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<TravelEquipment> createOrUpdateAll(List<TravelEquipment> equipmentList) {
    movementValidator.validateTravelEquipments(equipmentList);
    List<TravelEquipment> processedTravelEquipments = new ArrayList<>();
    for (TravelEquipment travelEquipment : equipmentList) {
      TravelEquipment existingTravelEquipment =
          travelEquipmentRepository.findById(travelEquipment.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          travelEquipment,
          existingTravelEquipment,
          travelEquipment.getId(),
          modificationUtils.takePrimaryUser());
      processedTravelEquipments.add(travelEquipment);
    }
    return travelEquipmentRepository.saveAll(processedTravelEquipments);
  }

  @Transactional
  public void deleteById(String id) {
    travelEquipmentRepository.deleteById(id);
  }

  private Specification<TravelEquipment> toSpecification(TravelEquipmentCriteria criteria) {
    return (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

      if (criteria.getTravelId() != null) {
        predicates.add(cb.equal(root.get("travel").get("id"), criteria.getTravelId()));
      }
      if (criteria.getEquipmentId() != null) {
        predicates.add(cb.equal(root.get("equipment").get("id"), criteria.getEquipmentId()));
      }
      if (criteria.getQuantity() != null) {
        predicates.add(cb.equal(root.get("quantity"), criteria.getQuantity()));
      }
      if (criteria.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
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
