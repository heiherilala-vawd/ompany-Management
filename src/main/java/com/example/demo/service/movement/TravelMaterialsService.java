package com.example.demo.service.movement;

import com.example.demo.client.model.ConfirmMaterialArrival;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelMaterialsCriteria;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.TravelMaterialsRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MovementValidator;
import java.time.Instant;
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
  private final MaterialWarehouseService materialWarehouseService;

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

  @Transactional
  public List<TravelMaterials> confirmMaterialArrival(List<ConfirmMaterialArrival> arrivals) {
    movementValidator.validateConfirmMaterialArrival(arrivals);
    List<TravelMaterials> updated = new ArrayList<>();
    for (ConfirmMaterialArrival arrival : arrivals) {
      TravelMaterials travelMaterials =
          travelMaterialsRepository
              .findById(arrival.getId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "TravelMaterials with id " + arrival.getId() + " not found"));

      int quantityReceived =
          arrival.getQuantityReceived() != null ? arrival.getQuantityReceived() : 0;
      int quantityLost = arrival.getQuantityLost() != null ? arrival.getQuantityLost() : 0;

      if (quantityReceived < 0 || quantityLost < 0) {
        throw new BadRequestException("Quantities must be non-negative");
      }
      if (quantityReceived + quantityLost > travelMaterials.getQuantity()) {
        throw new BadRequestException(
            "quantity_received + quantity_lost must not exceed "
                + travelMaterials.getQuantity()
                + " for TravelMaterials "
                + arrival.getId());
      }

      int oldQuantityReceived =
          travelMaterials.getQuantityReceived() != null ? travelMaterials.getQuantityReceived() : 0;
      int deltaReceived = quantityReceived - oldQuantityReceived;

      if (deltaReceived > 0) {
        Warehouse arrivalWarehouse = travelMaterials.getTravel().getArrivalLocation();
        materialWarehouseService.incrementQuantity(
            MaterialWarehouse.builder()
                .material(travelMaterials.getMaterial())
                .warehouse(arrivalWarehouse)
                .quantity(deltaReceived)
                .build());
      } else if (deltaReceived < 0) {
        Warehouse arrivalWarehouse = travelMaterials.getTravel().getArrivalLocation();
        materialWarehouseService.decrementQuantity(
            MaterialWarehouse.builder()
                .material(travelMaterials.getMaterial())
                .warehouse(arrivalWarehouse)
                .quantity(-deltaReceived)
                .build());
      }

      travelMaterials.setQuantityReceived(quantityReceived);
      travelMaterials.setQuantityLost(quantityLost);
      travelMaterials.setArrivalDate(Instant.now());

      TravelMaterials saved = travelMaterialsRepository.save(travelMaterials);
      updated.add(saved);
    }
    return updated;
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
            cb.lessThan(
                cb.sum(
                    cb.coalesce(root.get("quantityReceived"), cb.literal(0)),
                    cb.coalesce(root.get("quantityLost"), cb.literal(0))),
                root.get("quantity")));
      }

      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };
  }
}
