package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateEquipmentUsage;
import com.example.demo.client.model.EquipmentUsage;
import com.example.demo.client.model.UsageStatus;
import com.example.demo.endpoint.rest.mapper.EnumMapper;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.JobService;
import com.example.demo.service.UserService;
import com.example.demo.service.movement.EquipmentService;
import com.example.demo.service.movement.WarehouseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EquipmentUsageMapper {

  private final EquipmentService equipmentService;
  private final JobService jobService;
  private final WarehouseService warehouseService;
  private final UserService userService;
  private final EquipmentMapper equipmentMapper;
  private final JobMapper jobMapper;

  public com.example.demo.model.movement.EquipmentUsage toDomain(CrupdateEquipmentUsage rest) {
    if (rest == null) return null;

    com.example.demo.model.movement.EquipmentUsage.UsageStatus usageStatus = null;
    if (rest.getUsageStatus() != null) {
      usageStatus =
          com.example.demo.model.movement.EquipmentUsage.UsageStatus.valueOf(rest.getUsageStatus());
    }

    com.example.demo.model.movement.EquipmentUsage domain =
        com.example.demo.model.movement.EquipmentUsage.builder()
            .id(rest.getId())
            .equipment(
                rest.getEquipmentId() != null
                    ? equipmentService.findById(rest.getEquipmentId()).orElse(null)
                    : null)
            .job(rest.getJobId() != null ? jobService.findById(rest.getJobId()).orElse(null) : null)
            .startTime(rest.getStartTime())
            .endTime(rest.getEndTime())
            .sourceLocation(
                rest.getSourceLocation() != null
                    ? warehouseService.findById(rest.getSourceLocation()).orElse(null)
                    : null)
            .usageStatus(usageStatus)
            .usedBy(rest.getUsedBy() != null ? findUserById(rest.getUsedBy()) : null)
            .comment(rest.getComment())
            .build();
    domain.setIncidentId(rest.getIncidentId());
    return domain;
  }

  public EquipmentUsage toRestEquipmentUsage(
      com.example.demo.model.movement.EquipmentUsage domain) {
    if (domain == null) return null;

    EquipmentUsage rest = new EquipmentUsage();
    rest.setId(domain.getId());
    rest.setEquipment(equipmentMapper.toRestCrupdateEquipment(domain.getEquipment()));
    rest.setJob(jobMapper.toRestCrupdateJob(domain.getJob()));
    rest.setStartTime(domain.getStartTime());
    rest.setEndTime(domain.getEndTime());
    rest.setSourceLocation(
        domain.getSourceLocation() != null ? domain.getSourceLocation().getId() : null);
    rest.setUsageStatus(
        domain.getUsageStatus() != null
            ? EnumMapper.mapEnum(domain.getUsageStatus(), UsageStatus.class)
            : null);
    rest.setUsedBy(domain.getUsedBy() != null ? domain.getUsedBy().getId() : null);
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);

    return rest;
  }

  public List<EquipmentUsage> toRestEquipmentUsages(
      List<com.example.demo.model.movement.EquipmentUsage> domains) {
    return domains.stream().map(this::toRestEquipmentUsage).toList();
  }

  private com.example.demo.model.User findUserById(String id) {
    try {
      return userService.getById(id);
    } catch (NotFoundException e) {
      return null;
    }
  }
}
