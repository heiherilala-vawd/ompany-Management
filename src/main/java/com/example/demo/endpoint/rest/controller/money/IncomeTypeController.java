package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateIncomeType;
import com.example.demo.client.model.IncomeType;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.IncomeTypeMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.IncomeTypeService;
import com.example.demo.service.utils.PageUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class IncomeTypeController {

  private final IncomeTypeService incomeTypeService;
  private final IncomeTypeMapper incomeTypeMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/income_types/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public IncomeType getIncomeTypeById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return incomeTypeMapper.toRestIncomeType(
        incomeTypeService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("IncomeType with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/income_types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getIncomeTypes(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    var result = incomeTypeService.findAllByCompanyId(companyId, pageable);
    var list = incomeTypeMapper.toRestIncomeTypes(result.getContent());
    return new PaginatedResponse(list, (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/income_types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<IncomeType> crupdateIncomeTypes(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateIncomeType> toWrite) {
    return incomeTypeMapper.toRestIncomeTypes(
        incomeTypeService.createOrUpdateAll(
            toWrite.stream().map(rest -> incomeTypeMapper.toDomain(rest, companyId)).toList()));
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/income_types/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteIncomeTypeById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    incomeTypeService.deleteById(id);
  }
}
