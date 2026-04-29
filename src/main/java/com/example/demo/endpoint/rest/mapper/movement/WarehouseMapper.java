package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.Warehouse;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.JobService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WarehouseMapper {

  private final JobService jobService;
  private final JobMapper jobMapper;

  public com.example.demo.model.movement.Warehouse toDomain(Warehouse restWarehouse) {
    if (restWarehouse == null) return null;

    return com.example.demo.model.movement.Warehouse.builder()
        .id(restWarehouse.getId())
        .name(restWarehouse.getName())
        .description(restWarehouse.getDescription())
        .job(
            restWarehouse.getJob() != null && restWarehouse.getJob().getId() != null
                ? jobService.findById(restWarehouse.getJob().getId()).orElse(null)
                : null)
        .comment(restWarehouse.getComment())
        .build();
  }

  public com.example.demo.model.movement.Warehouse toDomain(CrupdateWarehouse restWarehouse) {
    if (restWarehouse == null) return null;

    return com.example.demo.model.movement.Warehouse.builder()
        .id(restWarehouse.getId())
        .name(restWarehouse.getName())
        .description(restWarehouse.getDescription())
        .job(
            restWarehouse.getJobId() != null
                ? jobService.findById(restWarehouse.getJobId()).orElse(null)
                : null)
        .comment(restWarehouse.getComment())
        .build();
  }

  public Warehouse toRestWarehouse(com.example.demo.model.movement.Warehouse domainWarehouse) {
    if (domainWarehouse == null) return null;

    Warehouse restWarehouse = new Warehouse();
    restWarehouse.setId(domainWarehouse.getId());
    restWarehouse.setName(domainWarehouse.getName());
    restWarehouse.setDescription(domainWarehouse.getDescription());
    restWarehouse.setJob(jobMapper.toRestCrupdateJob(domainWarehouse.getJob()));
    RestAuditMapperUtils.mapAuditFields(
        domainWarehouse,
        restWarehouse::setCreatedAt,
        restWarehouse::setUpdatedAt,
        restWarehouse::setComment,
        restWarehouse::setCreatedBy,
        restWarehouse::setUpdatedBy);

    return restWarehouse;
  }

  public CrupdateWarehouse toRestCrupdateWarehouse(
      com.example.demo.model.movement.Warehouse domainWarehouse) {
    if (domainWarehouse == null) return null;

    return new CrupdateWarehouse()
        .id(domainWarehouse.getId())
        .name(domainWarehouse.getName())
        .description(domainWarehouse.getDescription())
        .jobId(domainWarehouse.getJob() != null ? domainWarehouse.getJob().getId() : null)
        .comment(domainWarehouse.getComment());
  }

  public List<Warehouse> toRestWarehouses(
      List<com.example.demo.model.movement.Warehouse> domainWarehouses) {
    return domainWarehouses.stream().map(this::toRestWarehouse).toList();
  }

  public List<com.example.demo.model.movement.Warehouse> toDomain(List<Warehouse> restWarehouses) {
    return restWarehouses.stream().map(this::toDomain).toList();
  }
}
