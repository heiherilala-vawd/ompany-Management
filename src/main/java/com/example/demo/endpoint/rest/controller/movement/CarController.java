package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.dto.movement.CarResponse;
import com.example.demo.endpoint.rest.dto.movement.CrupdateCar;
import com.example.demo.endpoint.rest.mapper.movement.CarMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.CarService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CarController {

  private final CarService carService;
  private final CarMapper carMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/cars/{equipmentId}/{warehouseId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public CarResponse getCarById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String equipmentId,
      @PathVariable String warehouseId) {
    return carMapper.toResponse(
        carService
            .findById(equipmentId, warehouseId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Car with equipmentId "
                            + equipmentId
                            + " and warehouseId "
                            + warehouseId
                            + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/cars")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public PaginatedResponse getCars(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    var result = carService.findAll(page, pageSize);
    return new PaginatedResponse(
        carMapper.toResponseList(result.getContent()), (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/cars")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CarResponse> crupdateCars(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateCar> toWrite) {
    var saved = carService.createOrUpdateAll(toWrite.stream().map(carMapper::toDomain).toList());
    return carMapper.toResponseList(saved);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/cars/{equipmentId}/{warehouseId}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteCarById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String equipmentId,
      @PathVariable String warehouseId) {
    carService.deleteById(equipmentId, warehouseId);
  }
}
