package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateMaintenanceSchedule;
import com.example.demo.client.model.MaintenanceSchedule;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.MaintenanceSchedule.MaintenanceScheduleStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MaintenanceScheduleMapper {

  public com.example.demo.model.movement.MaintenanceSchedule toDomain(
      CrupdateMaintenanceSchedule rest, String companyId) {
    if (rest == null) return null;
    com.example.demo.model.movement.MaintenanceSchedule.MaintenanceScheduleBuilder<?, ?> builder =
        com.example.demo.model.movement.MaintenanceSchedule.builder()
            .id(rest.getId())
            .description(rest.getDescription())
            .scheduledDate(rest.getScheduledDate())
            .frequency(rest.getFrequency())
            .comment(rest.getComment());
    if (rest.getStatus() != null) {
      builder.status(MaintenanceScheduleStatus.valueOf(rest.getStatus().getValue()));
    } else {
      builder.status(MaintenanceScheduleStatus.PENDING);
    }
    if (rest.getEquipmentId() != null) {
      builder.equipment(Equipment.builder().id(rest.getEquipmentId()).build());
    }
    if (companyId != null) {
      builder.company(com.example.demo.model.Company.builder().id(companyId).build());
    }
    return builder.build();
  }

  public MaintenanceSchedule toRestMaintenanceSchedule(
      com.example.demo.model.movement.MaintenanceSchedule domain) {
    if (domain == null) return null;
    MaintenanceSchedule rest = new MaintenanceSchedule();
    rest.setId(domain.getId());
    if (domain.getEquipment() != null) {
      rest.setEquipmentId(domain.getEquipment().getId());
    }
    rest.setDescription(domain.getDescription());
    rest.setScheduledDate(domain.getScheduledDate());
    rest.setFrequency(domain.getFrequency());
    if (domain.getStatus() != null) {
      rest.setStatus(
          com.example.demo.client.model.MaintenanceScheduleStatus.fromValue(
              domain.getStatus().name()));
    }
    if (domain.getCompany() != null) {
      rest.setCompanyId(domain.getCompany().getId());
    }
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);
    return rest;
  }
}
