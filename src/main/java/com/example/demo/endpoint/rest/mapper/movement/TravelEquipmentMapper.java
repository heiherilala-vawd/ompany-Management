package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateTravelEquipment;
import com.example.demo.client.model.TransportStatus;
import com.example.demo.client.model.TravelEquipment;
import com.example.demo.service.money.TravelExpenseService;
import com.example.demo.service.movement.EquipmentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TravelEquipmentMapper {

  private final TravelExpenseService travelExpenseService;
  private final EquipmentService equipmentService;

  public com.example.demo.model.movement.TravelEquipment toDomain(
      TravelEquipment restTravelEquipment) {
    if (restTravelEquipment == null) return null;

    return com.example.demo.model.movement.TravelEquipment.builder()
        .id(restTravelEquipment.getId())
        .travel(
            restTravelEquipment.getTravelId() != null
                ? travelExpenseService.findById(restTravelEquipment.getTravelId()).orElse(null)
                : null)
        .equipment(
            restTravelEquipment.getEquipment() != null
                ? equipmentService.findById(restTravelEquipment.getEquipment()).orElse(null)
                : null)
        .quantity(restTravelEquipment.getQuantity())
        .status(
            restTravelEquipment.getStatus() != null
                ? com.example.demo.model.movement.TravelEquipment.TransportStatus.valueOf(
                    restTravelEquipment.getStatus().name())
                : null)
        .build();
  }

  public com.example.demo.model.movement.TravelEquipment toDomain(
      CrupdateTravelEquipment restTravelEquipment) {
    if (restTravelEquipment == null) return null;

    return com.example.demo.model.movement.TravelEquipment.builder()
        .id(restTravelEquipment.getId())
        .travel(
            restTravelEquipment.getTravelId() != null
                ? travelExpenseService.findById(restTravelEquipment.getTravelId()).orElse(null)
                : null)
        .equipment(
            restTravelEquipment.getEquipment() != null
                ? equipmentService.findById(restTravelEquipment.getEquipment()).orElse(null)
                : null)
        .quantity(restTravelEquipment.getQuantity())
        .status(
            restTravelEquipment.getStatus() != null
                ? com.example.demo.model.movement.TravelEquipment.TransportStatus.valueOf(
                    restTravelEquipment.getStatus().name())
                : null)
        .build();
  }

  public TravelEquipment toRestTravelEquipment(
      com.example.demo.model.movement.TravelEquipment domainTravelEquipment) {
    if (domainTravelEquipment == null) return null;

    TravelEquipment restTravelEquipment = new TravelEquipment();
    restTravelEquipment.setId(domainTravelEquipment.getId());
    restTravelEquipment.setTravelId(
        domainTravelEquipment.getTravel() != null
            ? domainTravelEquipment.getTravel().getId()
            : null);
    restTravelEquipment.setEquipment(
        domainTravelEquipment.getEquipment() != null
            ? domainTravelEquipment.getEquipment().getId()
            : null);
    restTravelEquipment.setQuantity(domainTravelEquipment.getQuantity());
    restTravelEquipment.setStatus(
        domainTravelEquipment.getStatus() != null
            ? TransportStatus.valueOf(domainTravelEquipment.getStatus().name())
            : null);
    restTravelEquipment.setCreatedAt(domainTravelEquipment.getCreatedAt());
    restTravelEquipment.setUpdatedAt(domainTravelEquipment.getUpdatedAt());
    restTravelEquipment.setComment(domainTravelEquipment.getComment());

    if (domainTravelEquipment.getCreatedBy() != null) {
      restTravelEquipment.setCreatedBy(domainTravelEquipment.getCreatedBy().getId());
    }
    if (domainTravelEquipment.getUpdatedBy() != null) {
      restTravelEquipment.setUpdatedBy(domainTravelEquipment.getUpdatedBy().getId());
    }

    return restTravelEquipment;
  }

  public List<TravelEquipment> toRestTravelEquipmentList(
      List<com.example.demo.model.movement.TravelEquipment> domainTravelEquipment) {
    return domainTravelEquipment.stream().map(this::toRestTravelEquipment).toList();
  }
}
