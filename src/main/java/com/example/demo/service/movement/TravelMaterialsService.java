package com.example.demo.service.movement;

import com.example.demo.client.model.ConfirmMaterialArrival;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelMaterialsCriteria;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.TravelMaterialsArrivalLog;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.TravelMaterialsArrivalLogRepository;
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
  private final TravelMaterialsArrivalLogRepository travelMaterialsArrivalLogRepository;
  private final ModificationUtils modificationUtils;
  private final MovementValidator movementValidator;
  private final MaterialWarehouseService materialWarehouseService;
  private final WarehouseService warehouseService;

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

      int arrivalReceived =
          arrival.getQuantityReceived() != null ? Math.max(arrival.getQuantityReceived(), 0) : 0;
      int arrivalLost =
          arrival.getQuantityLost() != null ? Math.max(arrival.getQuantityLost(), 0) : 0;

      int currentReceived =
          travelMaterials.getQuantityReceived() != null ? travelMaterials.getQuantityReceived() : 0;
      int currentLost =
          travelMaterials.getQuantityLost() != null ? travelMaterials.getQuantityLost() : 0;
      Warehouse arrivalWarehouse = travelMaterials.getTravel().getArrivalLocation();

      // Resolve per-log arrival location and date from request or defaults
      Warehouse logWarehouse =
          arrival.getArrivalLocation() != null
              ? warehouseService
                  .findById(arrival.getArrivalLocation())
                  .orElseThrow(
                      () ->
                          new NotFoundException(
                              "Warehouse with id " + arrival.getArrivalLocation() + " not found"))
              : arrivalWarehouse;
      Instant logDate = arrival.getArrivalDate() != null ? arrival.getArrivalDate() : Instant.now();

      // Check for existing log (idempotency: replace & re-apply)
      TravelMaterialsArrivalLog existingLog = null;
      for (TravelMaterialsArrivalLog log : travelMaterials.getArrivalLogs()) {
        if (arrival.getLogId().equals(log.getId())) {
          existingLog = log;
          break;
        }
      }
      if (existingLog != null) {
        int oldReceived =
            existingLog.getQuantityReceived() != null ? existingLog.getQuantityReceived() : 0;
        int oldLost = existingLog.getQuantityLost() != null ? existingLog.getQuantityLost() : 0;
        // Reverse old values
        if (oldReceived > 0) {
          materialWarehouseService.decrementQuantity(
              MaterialWarehouse.builder()
                  .material(travelMaterials.getMaterial())
                  .warehouse(arrivalWarehouse)
                  .quantity(oldReceived)
                  .build());
        }
        currentReceived -= oldReceived;
        currentLost -= oldLost;
        // Update existing log in-place
        existingLog.setQuantityReceived(arrivalReceived);
        existingLog.setQuantityLost(arrivalLost);
        existingLog.setArrivalDate(logDate);
        existingLog.setArrivalLocation(logWarehouse);
      } else {
        TravelMaterialsArrivalLog newLog =
            TravelMaterialsArrivalLog.builder()
                .id(arrival.getLogId())
                .travelMaterials(travelMaterials)
                .quantityReceived(arrivalReceived)
                .quantityLost(arrivalLost)
                .arrivalDate(logDate)
                .arrivalLocation(logWarehouse)
                .build();
        travelMaterials.getArrivalLogs().add(newLog);
      }

      int newTotalReceived = currentReceived + arrivalReceived;
      int newTotalLost = currentLost + arrivalLost;

      if (newTotalReceived + newTotalLost > travelMaterials.getQuantity()) {
        throw new BadRequestException(
            "Total received ("
                + newTotalReceived
                + ") + total lost ("
                + newTotalLost
                + ") must not exceed quantity ("
                + travelMaterials.getQuantity()
                + ") for TravelMaterials "
                + arrival.getId());
      }

      if (arrivalReceived > 0) {
        materialWarehouseService.incrementQuantity(
            MaterialWarehouse.builder()
                .material(travelMaterials.getMaterial())
                .warehouse(arrivalWarehouse)
                .quantity(arrivalReceived)
                .build());
      }

      travelMaterials.setQuantityReceived(newTotalReceived);
      travelMaterials.setQuantityLost(newTotalLost);

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
            cb.equal(
                root.get("travel").get("arrivalLocation").get("id"),
                criteria.getArrivalLocation()));
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
