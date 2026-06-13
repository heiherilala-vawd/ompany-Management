package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.Warehouse;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.movement.WarehouseMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.WarehouseCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.WarehouseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class WarehouseController {

  private final WarehouseService warehouseService;
  private final WarehouseMapper warehouseMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/warehouses/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public Warehouse getWarehouseById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return warehouseMapper.toRestWarehouse(
        warehouseService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Warehouse with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/warehouses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public PaginatedResponse getWarehouses(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "description", required = false) String description) {
    WarehouseCriteria criteria = new WarehouseCriteria();
    criteria.setJobId(jobId);
    criteria.setName(name);
    criteria.setDescription(description);

    var result = warehouseService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(warehouseMapper::toRestWarehouse).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/warehouses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Warehouse> crupdateWarehouses(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateWarehouse> toWrite) {
    List<com.example.demo.model.movement.Warehouse> saved =
        warehouseService.createOrUpdateAll(
            toWrite.stream().map(warehouseMapper::toDomain).toList());
    return saved.stream().map(warehouseMapper::toRestWarehouse).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/warehouses/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteWarehouseById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    warehouseService.deleteById(id);
  }
}
