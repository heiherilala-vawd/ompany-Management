package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.MaterialCriteria;
import com.example.demo.model.movement.Material;
import com.example.demo.repository.movement.MaterialRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.service.utils.SpecialWarehouseUtils;
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
public class MaterialService {

  private final MaterialRepository materialRepository;
  private final ModificationUtils modificationUtils;
  private final MovementValidator movementValidator;

  public Optional<Material> findById(String id) {
    return materialRepository.findById(id);
  }

  public Page<Material> findAll(
      PageFromOne page, BoundedPageSize pageSize, MaterialCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    if (Boolean.TRUE.equals(criteria.getNotArrived())) {
      return materialRepository.findNotArrived(
          SpecialWarehouseUtils.routeWarehouseId(),
          SpecialWarehouseUtils.atSellerWarehouseId(),
          pageable);
    }
    return materialRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<Material> createOrUpdateAll(List<Material> materials) {
    movementValidator.validateMaterials(materials);
    List<Material> processedMaterials = new ArrayList<>();
    for (Material material : materials) {
      Material existingMaterial =
          material.getId() == null
              ? null
              : materialRepository.findById(material.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          material, existingMaterial, material.getId(), modificationUtils.takePrimaryUser());
      processedMaterials.add(material);
    }
    return materialRepository.saveAll(processedMaterials);
  }

  @Transactional
  public void deleteById(String id) {
    materialRepository.deleteById(id);
  }

  private Specification<Material> toSpecification(MaterialCriteria criteria) {
    return Specification.<Material>where(containsIgnoreCase(criteria.getName(), "name"))
        .and(containsIgnoreCase(criteria.getDescription(), "description"))
        .and(equal(criteria.getUnit(), "unit"));
  }
}
