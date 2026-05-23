package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateEquipmentUsage;
import com.example.demo.client.model.EquipmentUsage;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.movement.EquipmentService;
import com.example.demo.service.JobService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EquipmentUsageMapper {

  private final EquipmentService equipmentService;
  private final JobService jobService;

  public com.example.demo.model.movement.EquipmentUsage toDomain(CrupdateEquipmentUsage rest) {
    if (rest == null) return null;

    return com.example.demo.model.movement.EquipmentUsage.builder()
        .id(rest.getId())
        .equipment(
            rest.getEquipmentId() != null
                ? equipmentService.findById(rest.getEquipmentId()).orElse(null)
                : null)
        .job(
            rest.getJobId() != null
                ? jobService.findById(rest.getJobId()).orElse(null)
                : null)
        .startTime(rest.getStartTime())
        .endTime(rest.getEndTime())
        .comment(rest.getComment())
        .build();
  }

  public EquipmentUsage toRestEquipmentUsage(com.example.demo.model.movement.EquipmentUsage domain) {
    if (domain == null) return null;

    EquipmentUsage rest = new EquipmentUsage();
    rest.setId(domain.getId());
    rest.setEquipmentId(domain.getEquipment() != null ? domain.getEquipment().getId() : null);
    rest.setJobId(domain.getJob() != null ? domain.getJob().getId() : null);
    rest.setStartTime(domain.getStartTime());
    rest.setEndTime(domain.getEndTime());
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);

    return rest;
  }

  public List<EquipmentUsage> toRestEquipmentUsages(List<com.example.demo.model.movement.EquipmentUsage> domains) {
    return domains.stream().map(this::toRestEquipmentUsage).toList();
  }
}
