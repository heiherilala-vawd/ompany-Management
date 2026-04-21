package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelEquipmentCriteria;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.repository.movement.TravelEquipmentRepository;
import com.example.demo.service.utils.PageUtils;
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
    return travelEquipmentRepository.saveAll(equipmentList);
  }

  @Transactional
  public void deleteById(String id) {
    travelEquipmentRepository.deleteById(id);
  }

  private Specification<TravelEquipment> toSpecification(TravelEquipmentCriteria criteria) {
    return Specification.<TravelEquipment>where(equal(criteria.getTravelId(), "travel", "id"))
        .and(equal(criteria.getEquipmentId(), "equipment", "id"))
        .and(equal(criteria.getQuantity(), "quantity"))
        .and(equal(criteria.getStatus(), "status"));
  }
}
