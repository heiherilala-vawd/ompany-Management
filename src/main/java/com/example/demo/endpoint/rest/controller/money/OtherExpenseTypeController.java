package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateOtherExpenseType;
import com.example.demo.client.model.OtherExpenseType;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.OtherExpenseTypeMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.OtherExpenseTypeService;
import com.example.demo.service.utils.PageUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
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
public class OtherExpenseTypeController {

  private final OtherExpenseTypeService otherExpenseTypeService;
  private final OtherExpenseTypeMapper otherExpenseTypeMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/other_expense_types/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public OtherExpenseType getOtherExpenseTypeById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return otherExpenseTypeMapper.toRestOtherExpenseType(
        otherExpenseTypeService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("OtherExpenseType with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/other_expense_types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public PaginatedResponse getOtherExpenseTypes(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    var result = otherExpenseTypeService.findAllByCompanyId(companyId, pageable);
    var list = otherExpenseTypeMapper.toRestOtherExpenseTypes(result.getContent());
    return new PaginatedResponse(list, (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/other_expense_types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<OtherExpenseType> crupdateOtherExpenseTypes(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateOtherExpenseType> toWrite) {
    return otherExpenseTypeMapper.toRestOtherExpenseTypes(
        otherExpenseTypeService.createOrUpdateAll(
            toWrite.stream()
                .map(rest -> otherExpenseTypeMapper.toDomain(rest, companyId))
                .toList()));
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/other_expense_types/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteOtherExpenseTypeById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    otherExpenseTypeService.deleteById(id);
  }
}
