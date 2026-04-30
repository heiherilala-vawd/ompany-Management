package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.Equipment;
import com.example.demo.endpoint.rest.mapper.movement.EquipmentMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.EquipmentCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.EquipmentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class EquipmentController {

  private final EquipmentService equipmentService;
  private final EquipmentMapper equipmentMapper;

  @GetMapping("/companies/{comp_id}/equipment/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER', 'EMPLOYEE')")
  public Equipment getEquipmentById(@PathVariable String comp_id, @PathVariable String id) {
    return equipmentMapper.toRestEquipment(
        equipmentService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Equipment with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/equipment")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER', 'EMPLOYEE')")
  public List<Equipment> getEquipment(
      @PathVariable String comp_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "warehouse_id", required = false) String warehouseId,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "floor_number", required = false) Integer floorNumber,
      @RequestParam(name = "storage_number", required = false) Integer storageNumber) {
    EquipmentCriteria criteria = new EquipmentCriteria();
    criteria.setWarehouseId(warehouseId);
    criteria.setName(name);
    criteria.setDescription(description);
    criteria.setFloorNumber(floorNumber);
    criteria.setStorageNumber(storageNumber);

    return equipmentService.findAll(page, pageSize, criteria).stream()
        .map(equipmentMapper::toRestEquipment)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/equipment")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Equipment> crupdateEquipment(
      @PathVariable String comp_id, @RequestBody List<CrupdateEquipment> toWrite) {
    System.out.println("----------------------------------------");
    System.out.println(toWrite.toString());
    System.out.println("----------------------------------------");
    List<com.example.demo.model.movement.Equipment> saved =
        equipmentService.createOrUpdateAll(
            toWrite.stream().map(equipmentMapper::toDomain).toList());
    return saved.stream().map(equipmentMapper::toRestEquipment).toList();
  }

  @DeleteMapping("/companies/{comp_id}/equipment/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteEquipmentById(@PathVariable String comp_id, @PathVariable String id) {
    equipmentService.deleteById(id);
  }
}
