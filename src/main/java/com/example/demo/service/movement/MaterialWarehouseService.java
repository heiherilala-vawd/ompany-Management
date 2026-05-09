package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.MaterialWarehouseCriteria;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.MaterialWarehouseId;
import com.example.demo.repository.movement.MaterialWarehouseRepository;
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
public class MaterialWarehouseService {

  private final MaterialWarehouseRepository materialWarehouseRepository;
  private final MovementValidator movementValidator;

  public Page<MaterialWarehouse> findAll(
      PageFromOne page, BoundedPageSize pageSize, MaterialWarehouseCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return materialWarehouseRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<MaterialWarehouse> incrementQuantities(List<MaterialWarehouse> materialWarehouses) {
    return materialWarehouses.stream().map(this::incrementQuantity).toList();
  }

  @Transactional
  public MaterialWarehouse incrementQuantity(MaterialWarehouse materialWarehouse) {
    movementValidator.validateMaterialWarehouse(materialWarehouse);
    String materialId = materialWarehouse.getMaterial().getId();
    String warehouseId = materialWarehouse.getWarehouse().getId();

    MaterialWarehouse existing =
        materialWarehouseRepository
            .findByMaterial_IdAndWarehouse_Id(materialId, warehouseId)
            .orElse(null);

    if (existing == null) {
      materialWarehouse.setId(new MaterialWarehouseId(materialId, warehouseId));
      return materialWarehouseRepository.save(materialWarehouse);
    }

    existing.setQuantity(existing.getQuantity() + materialWarehouse.getQuantity());
    return materialWarehouseRepository.save(existing);
  }

  @Transactional
  public List<MaterialWarehouse> createOrUpdateAll(List<MaterialWarehouse> materialWarehouses) {
    List<MaterialWarehouse> processed = new ArrayList<>();
    for (MaterialWarehouse mw : materialWarehouses) {
      movementValidator.validateMaterialWarehouse(mw);
      String materialId = mw.getMaterial().getId();
      String warehouseId = mw.getWarehouse().getId();

      MaterialWarehouse existing =
          materialWarehouseRepository
              .findByMaterial_IdAndWarehouse_Id(materialId, warehouseId)
              .orElse(null);

      if (existing == null) {
        mw.setId(new MaterialWarehouseId(materialId, warehouseId));
        processed.add(mw);
      } else {
        existing.setQuantity(mw.getQuantity());
        processed.add(existing);
      }
    }
    return materialWarehouseRepository.saveAll(processed);
  }

  public Optional<MaterialWarehouse> findByMaterialAndWarehouse(
      String materialId, String warehouseId) {
    return materialWarehouseRepository.findByMaterial_IdAndWarehouse_Id(materialId, warehouseId);
  }

  @Transactional
  public MaterialWarehouse decrementQuantity(MaterialWarehouse materialWarehouse) {
    String materialId = materialWarehouse.getMaterial().getId();
    String warehouseId = materialWarehouse.getWarehouse().getId();

    MaterialWarehouse existing =
        materialWarehouseRepository
            .findByMaterial_IdAndWarehouse_Id(materialId, warehouseId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "MaterialWarehouse with material "
                            + materialId
                            + " and warehouse "
                            + warehouseId
                            + " not found"));

    int quantityToRemove =
        materialWarehouse.getQuantity() != null ? materialWarehouse.getQuantity() : 0;
    if (quantityToRemove <= 0) {
      throw new BadRequestException("quantity to decrement must be greater than zero");
    }
    if (existing.getQuantity() == null || existing.getQuantity() < quantityToRemove) {
      throw new BadRequestException("insufficient quantity in source warehouse");
    }

    existing.setQuantity(existing.getQuantity() - quantityToRemove);
    return materialWarehouseRepository.save(existing);
  }

  private Specification<MaterialWarehouse> toSpecification(MaterialWarehouseCriteria criteria) {
    return (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

      if (criteria.getMaterialId() != null) {
        predicates.add(cb.equal(root.get("material").get("id"), criteria.getMaterialId()));
      }
      if (criteria.getWarehouseId() != null) {
        predicates.add(cb.equal(root.get("warehouse").get("id"), criteria.getWarehouseId()));
      }
      if (Boolean.TRUE.equals(criteria.getNotArrived())) {
        predicates.add(cb.greaterThan(root.get("quantity"), 0));
        predicates.add(
            cb.or(
                cb.equal(root.get("warehouse").get("id"), SpecialWarehouseUtils.routeWarehouseId()),
                cb.equal(
                    root.get("warehouse").get("id"), SpecialWarehouseUtils.atSellerWarehouseId())));
      }

      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };
  }
}
