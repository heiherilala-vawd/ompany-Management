package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.EquipmentIncidentCriteria;
import com.example.demo.model.movement.EquipmentIncident;
import com.example.demo.repository.movement.EquipmentIncidentRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
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
public class EquipmentIncidentService {

  private final EquipmentIncidentRepository equipmentIncidentRepository;
  private final ModificationUtils modificationUtils;

  public Optional<EquipmentIncident> findById(String id) {
    return equipmentIncidentRepository.findById(id);
  }

  public Page<EquipmentIncident> findAll(
      PageFromOne page, BoundedPageSize pageSize, EquipmentIncidentCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return equipmentIncidentRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<EquipmentIncident> createOrUpdateAll(List<EquipmentIncident> incidents) {
    List<EquipmentIncident> processed = new ArrayList<>();
    for (EquipmentIncident incident : incidents) {
      EquipmentIncident existing =
          equipmentIncidentRepository.findById(incident.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          incident, existing, incident.getId(), modificationUtils.takePrimaryUser());
      processed.add(incident);
    }
    return equipmentIncidentRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    equipmentIncidentRepository.deleteById(id);
  }

  private Specification<EquipmentIncident> toSpecification(EquipmentIncidentCriteria criteria) {
    return (root, query, cb) -> {
      var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

      if (criteria.getEquipmentId() != null) {
        predicates.add(cb.equal(root.get("equipment").get("id"), criteria.getEquipmentId()));
      }
      if (criteria.getIncidentType() != null) {
        predicates.add(cb.equal(root.get("incidentType"), criteria.getIncidentType()));
      }
      if (criteria.getUserId() != null) {
        predicates.add(cb.equal(root.get("user").get("id"), criteria.getUserId()));
      }
      if (criteria.getTravelId() != null) {
        predicates.add(cb.equal(root.get("travel").get("id"), criteria.getTravelId()));
      }

      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };
  }
}
