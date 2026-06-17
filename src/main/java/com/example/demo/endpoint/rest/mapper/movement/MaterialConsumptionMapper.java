package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateMaterialConsumption;
import com.example.demo.client.model.MaterialConsumption;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.JobService;
import com.example.demo.service.movement.MaterialService;
import com.example.demo.service.movement.WarehouseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MaterialConsumptionMapper {

  private final MaterialService materialService;
  private final WarehouseService warehouseService;
  private final JobService jobService;
  private final MaterialMapper materialMapper;
  private final WarehouseMapper warehouseMapper;
  private final JobMapper jobMapper;

  public com.example.demo.model.movement.MaterialConsumption toDomain(
      CrupdateMaterialConsumption rest) {
    if (rest == null) return null;

    com.example.demo.model.movement.MaterialConsumption.ConsumptionStatus status = null;
    if (rest.getConsumptionStatus() != null) {
      status =
          com.example.demo.model.movement.MaterialConsumption.ConsumptionStatus.valueOf(
              rest.getConsumptionStatus());
    }

    return com.example.demo.model.movement.MaterialConsumption.builder()
        .id(rest.getId())
        .material(
            rest.getMaterialId() != null
                ? materialService.findById(rest.getMaterialId()).orElse(null)
                : null)
        .warehouse(
            rest.getWarehouseId() != null
                ? warehouseService.findById(rest.getWarehouseId()).orElse(null)
                : null)
        .quantity(rest.getQuantity())
        .consumptionDate(rest.getConsumptionDate())
        .job(rest.getJobId() != null ? jobService.findById(rest.getJobId()).orElse(null) : null)
        .reason(rest.getReason())
        .consumptionStatus(status)
        .comment(rest.getComment())
        .build();
  }

  public MaterialConsumption toRestMaterialConsumption(
      com.example.demo.model.movement.MaterialConsumption domain) {
    if (domain == null) return null;

    MaterialConsumption rest = new MaterialConsumption();
    rest.setId(domain.getId());
    rest.setMaterial(materialMapper.toRestCrupdateMaterial(domain.getMaterial()));
    rest.setWarehouse(warehouseMapper.toRestCrupdateWarehouse(domain.getWarehouse()));
    rest.setQuantity(domain.getQuantity());
    rest.setConsumptionDate(domain.getConsumptionDate());
    rest.setJob(jobMapper.toRestCrupdateJob(domain.getJob()));
    rest.setReason(domain.getReason());
    rest.setConsumptionStatus(
        domain.getConsumptionStatus() != null ? domain.getConsumptionStatus().name() : null);
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);

    return rest;
  }

  public List<MaterialConsumption> toRestMaterialConsumptions(
      List<com.example.demo.model.movement.MaterialConsumption> domains) {
    return domains.stream().map(this::toRestMaterialConsumption).toList();
  }
}
