package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateMonetaryMovement;
import com.example.demo.client.model.MonetaryMovement;
import com.example.demo.endpoint.rest.mapper.money.MonetaryMovementMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.MonetaryMovementService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MonetaryMovementController {

  private final MonetaryMovementService monetaryMovementService;
  private final MonetaryMovementMapper monetaryMovementMapper;

  @GetMapping("/monetary_movements/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public MonetaryMovement getMonetaryMovementById(@PathVariable String id) {
    return monetaryMovementMapper.toRestMovement(
        monetaryMovementService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("MonetaryMovement with id " + id + " not found")));
  }

  @GetMapping("/monetary_movements")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<MonetaryMovement> getMonetaryMovements(
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {

    return monetaryMovementService.findAll(page, pageSize).stream()
        .map(monetaryMovementMapper::toRestMovement)
        .toList();
  }

  @PutMapping("/monetary_movements")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<MonetaryMovement> crupdateMonetaryMovements(
      @RequestBody List<CrupdateMonetaryMovement> toWrite) {
    var saved =
        monetaryMovementService.createOrUpdateAll(
            toWrite.stream().map(monetaryMovementMapper::toDomain).toList());
    return saved.stream().map(monetaryMovementMapper::toRestMovement).toList();
  }

  @DeleteMapping("/monetary_movements/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public void deleteMonetaryMovementById(@PathVariable String id) {
    monetaryMovementService.deleteById(id);
  }
}
