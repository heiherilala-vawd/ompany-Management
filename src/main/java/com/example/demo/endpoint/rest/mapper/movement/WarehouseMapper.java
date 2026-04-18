package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.Warehouse;
import com.example.demo.service.JobService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WarehouseMapper {

  private final JobService jobService;

  public com.example.demo.model.movement.Warehouse toDomain(Warehouse restWarehouse) {
    if (restWarehouse == null) return null;

    return com.example.demo.model.movement.Warehouse.builder()
        .id(restWarehouse.getId())
        .name(restWarehouse.getName())
        .description(restWarehouse.getDescription())
        .job(
            restWarehouse.getJobId() != null
                ? jobService.findById(restWarehouse.getJobId()).orElse(null)
                : null)
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
        .build();
  }

  public Warehouse toRestWarehouse(com.example.demo.model.movement.Warehouse domainWarehouse) {
    if (domainWarehouse == null) return null;

    Warehouse restWarehouse = new Warehouse();
    restWarehouse.setId(domainWarehouse.getId());
    restWarehouse.setName(domainWarehouse.getName());
    restWarehouse.setDescription(domainWarehouse.getDescription());
    restWarehouse.setJobId(
        domainWarehouse.getJob() != null ? domainWarehouse.getJob().getId() : null);
    restWarehouse.setCreatedAt(domainWarehouse.getCreatedAt());
    restWarehouse.setUpdatedAt(domainWarehouse.getUpdatedAt());
    restWarehouse.setComment(domainWarehouse.getComment());

    if (domainWarehouse.getCreatedBy() != null) {
      restWarehouse.setCreatedBy(domainWarehouse.getCreatedBy().getId());
    }
    if (domainWarehouse.getUpdatedBy() != null) {
      restWarehouse.setUpdatedBy(domainWarehouse.getUpdatedBy().getId());
    }

    return restWarehouse;
  }

  public List<Warehouse> toRestWarehouses(
      List<com.example.demo.model.movement.Warehouse> domainWarehouses) {
    return domainWarehouses.stream().map(this::toRestWarehouse).toList();
  }

  public List<com.example.demo.model.movement.Warehouse> toDomain(List<Warehouse> restWarehouses) {
    return restWarehouses.stream().map(this::toDomain).toList();
  }
}
