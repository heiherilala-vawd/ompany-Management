package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.Warehouse;
import com.example.demo.endpoint.rest.mapper.movement.WarehouseMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.WarehouseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class WarehouseController {

  private final WarehouseService warehouseService;
  private final WarehouseMapper warehouseMapper;

  @GetMapping("/companies/{comp_id}/warehouses/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public Warehouse getWarehouseById(@PathVariable String comp_id, @PathVariable String id) {
    return warehouseMapper.toRestWarehouse(
        warehouseService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Warehouse with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/warehouses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Warehouse> getWarehouses(
      @PathVariable String comp_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "job_id", required = false) String jobId) {

    return warehouseService.findAll(page, pageSize, jobId).stream()
        .map(warehouseMapper::toRestWarehouse)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/warehouses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Warehouse> crupdateWarehouses(
      @PathVariable String comp_id, @RequestBody List<CrupdateWarehouse> toWrite) {
    var saved =
        warehouseService.createOrUpdateAll(
            toWrite.stream().map(warehouseMapper::toDomain).toList());
    return saved.stream().map(warehouseMapper::toRestWarehouse).toList();
  }

  @DeleteMapping("/companies/{comp_id}/warehouses/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public void deleteWarehouseById(@PathVariable String comp_id, @PathVariable String id) {
    warehouseService.deleteById(id);
  }
}
