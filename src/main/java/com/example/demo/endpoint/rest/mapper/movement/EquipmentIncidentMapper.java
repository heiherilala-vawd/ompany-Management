package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateEquipmentIncident;
import com.example.demo.client.model.EquipmentIncident;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.UserService;
import com.example.demo.service.money.TravelExpenseService;
import com.example.demo.service.movement.EquipmentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EquipmentIncidentMapper {

  private final EquipmentService equipmentService;
  private final UserService userService;
  private final TravelExpenseService travelExpenseService;

  public com.example.demo.model.movement.EquipmentIncident toDomain(
      CrupdateEquipmentIncident rest) {
    if (rest == null) return null;

    return com.example.demo.model.movement.EquipmentIncident.builder()
        .id(rest.getId())
        .incidentType(
            rest.getIncidentType() != null
                ? com.example.demo.model.movement.IncidentType.valueOf(
                    rest.getIncidentType().name())
                : null)
        .equipment(
            rest.getEquipmentId() != null
                ? equipmentService.findById(rest.getEquipmentId()).orElse(null)
                : null)
        .user(rest.getUserId() != null ? userService.getById(rest.getUserId()) : null)
        .travel(
            rest.getTravelId() != null
                ? travelExpenseService.findById(rest.getTravelId()).orElse(null)
                : null)
        .location(rest.getLocation())
        .comment(rest.getComment())
        .build();
  }

  public EquipmentIncident toRest(com.example.demo.model.movement.EquipmentIncident domain) {
    if (domain == null) return null;

    EquipmentIncident rest = new EquipmentIncident();
    rest.setId(domain.getId());
    rest.setIncidentType(
        domain.getIncidentType() != null
            ? com.example.demo.client.model.IncidentType.valueOf(domain.getIncidentType().name())
            : null);
    rest.setEquipmentId(domain.getEquipment() != null ? domain.getEquipment().getId() : null);
    rest.setUserId(domain.getUser() != null ? domain.getUser().getId() : null);
    rest.setTravelId(domain.getTravel() != null ? domain.getTravel().getId() : null);
    rest.setLocation(domain.getLocation());
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);

    return rest;
  }

  public List<EquipmentIncident> toRestList(
      List<com.example.demo.model.movement.EquipmentIncident> domains) {
    return domains.stream().map(this::toRest).toList();
  }
}
