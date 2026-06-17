package com.example.demo.service.movement;

import com.example.demo.client.model.ConfirmEquipmentArrival;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelEquipmentCriteria;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelEquipment.TransportStatus;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.TravelEquipmentRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.service.utils.SpecialWarehouseUtils;
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
public class TravelEquipmentService {

  private final TravelEquipmentRepository travelEquipmentRepository;
  private final ModificationUtils modificationUtils;
  private final MovementValidator movementValidator;
  private final EquipmentService equipmentService;
  private final WarehouseService warehouseService;

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

  @Transactional
  public List<TravelEquipment> confirmEquipmentArrival(List<ConfirmEquipmentArrival> arrivals) {
    movementValidator.validateConfirmEquipmentArrival(arrivals);
    List<TravelEquipment> updated = new ArrayList<>();
    for (ConfirmEquipmentArrival arrival : arrivals) {
      TravelEquipment travelEquipment =
          travelEquipmentRepository
              .findById(arrival.getId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "TravelEquipment with id " + arrival.getId() + " not found"));

      if (travelEquipment.getStatus() != TransportStatus.IN_PROGRESS) {
        throw new BadRequestException(
            "TravelEquipment "
                + arrival.getId()
                + " must be IN_PROGRESS to confirm arrival, current status: "
                + travelEquipment.getStatus());
      }

      TransportStatus newStatus = Enum.valueOf(TransportStatus.class, arrival.getStatus().name());
      Warehouse defaultWarehouse = resolveArrivalWarehouse(travelEquipment, newStatus);

      Warehouse targetWarehouse =
          arrival.getArrivalLocation() != null
              ? warehouseService
                  .findById(arrival.getArrivalLocation())
                  .orElseThrow(
                      () ->
                          new NotFoundException(
                              "Warehouse with id " + arrival.getArrivalLocation() + " not found"))
              : defaultWarehouse;
      Instant arrivalDate =
          arrival.getArrivalDate() != null ? arrival.getArrivalDate() : Instant.now();

      Equipment equipment = travelEquipment.getEquipment();
      equipment.setWarehouse(targetWarehouse);
      equipmentService.createOrUpdateAll(List.of(equipment));

      travelEquipment.setStatus(newStatus);
      travelEquipment.setArrivalDate(arrivalDate);
      travelEquipment.setArrivalLocation(targetWarehouse);

      TravelEquipment saved = travelEquipmentRepository.save(travelEquipment);
      updated.add(saved);
    }
    return updated;
  }

  private Warehouse resolveArrivalWarehouse(
      TravelEquipment travelEquipment, TransportStatus newStatus) {
    return switch (newStatus) {
      case ARRIVED -> travelEquipment.getTravel().getArrivalLocation();
      case LOST -> Warehouse.builder().id(SpecialWarehouseUtils.unfindableWarehouseId()).build();
      case DAMAGED -> Warehouse.builder().id(SpecialWarehouseUtils.damagedWarehouseId()).build();
      default ->
          throw new BadRequestException(
              "Invalid arrival status: " + newStatus + ". Must be ARRIVED, LOST or DAMAGED.");
    };
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
            cb.equal(
                root.get("travel").get("arrivalLocation").get("id"),
                criteria.getArrivalLocation()));
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
