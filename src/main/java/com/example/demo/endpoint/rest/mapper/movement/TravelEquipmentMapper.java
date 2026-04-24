package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateTravelEquipment;
import com.example.demo.client.model.TransportStatus;
import com.example.demo.client.model.TravelEquipment;
import com.example.demo.endpoint.rest.mapper.EnumMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.endpoint.rest.mapper.money.TravelExpenseMapper;
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
  private final TravelExpenseMapper travelExpenseMapper;
  private final EquipmentMapper equipmentMapper;

  public com.example.demo.model.movement.TravelEquipment toDomain(
      TravelEquipment restTravelEquipment) {
    if (restTravelEquipment == null) return null;

    return com.example.demo.model.movement.TravelEquipment.builder()
        .id(restTravelEquipment.getId())
        .travel(
            restTravelEquipment.getTravel() != null
                    && restTravelEquipment.getTravel().getId() != null
                ? travelExpenseService
                    .findById(restTravelEquipment.getTravel().getId())
                    .orElse(null)
                : null)
        .equipment(
            restTravelEquipment.getEquipment() != null
                ? equipmentService.findById(restTravelEquipment.getEquipment().getId()).orElse(null)
                : null)
        .quantity(restTravelEquipment.getQuantity())
        .comment(restTravelEquipment.getComment())
        .status(
            EnumMapper.mapEnum(
                restTravelEquipment.getStatus(),
                com.example.demo.model.movement.TravelEquipment.TransportStatus.class))
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
        .comment(restTravelEquipment.getComment())
        .status(
            EnumMapper.mapEnum(
                restTravelEquipment.getStatus(),
                com.example.demo.model.movement.TravelEquipment.TransportStatus.class))
        .build();
  }

  public TravelEquipment toRestTravelEquipment(
      com.example.demo.model.movement.TravelEquipment domainTravelEquipment) {
    if (domainTravelEquipment == null) return null;

    TravelEquipment restTravelEquipment = new TravelEquipment();
    restTravelEquipment.setId(domainTravelEquipment.getId());
    restTravelEquipment.setTravel(
        travelExpenseMapper.toRestCrupdateTravelExpense(domainTravelEquipment.getTravel()));
    restTravelEquipment.setEquipment(
        equipmentMapper.toRestCrupdateEquipment(domainTravelEquipment.getEquipment()));
    restTravelEquipment.setQuantity(domainTravelEquipment.getQuantity());
    restTravelEquipment.setStatus(
        EnumMapper.mapEnum(domainTravelEquipment.getStatus(), TransportStatus.class));
    RestAuditMapperUtils.mapAuditFields(
        domainTravelEquipment,
        restTravelEquipment::setCreatedAt,
        restTravelEquipment::setUpdatedAt,
        restTravelEquipment::setComment,
        restTravelEquipment::setCreatedBy,
        restTravelEquipment::setUpdatedBy);

    return restTravelEquipment;
  }

  public List<TravelEquipment> toRestTravelEquipmentList(
      List<com.example.demo.model.movement.TravelEquipment> domainTravelEquipment) {
    return domainTravelEquipment.stream().map(this::toRestTravelEquipment).toList();
  }
}
