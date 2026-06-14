package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CompanyFixedCost;
import com.example.demo.client.model.CrupdateCompanyFixedCost;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.CompanyFixedCostMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.CompanyFixedCostService;
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
public class CompanyFixedCostController {

  private final CompanyFixedCostService companyFixedCostService;
  private final CompanyFixedCostMapper companyFixedCostMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/fixed_costs/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public CompanyFixedCost getCompanyFixedCostById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return companyFixedCostMapper.toRestCompanyFixedCost(
        companyFixedCostService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("CompanyFixedCost with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/fixed_costs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getCompanyFixedCosts(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    var result = companyFixedCostService.findAllByCompanyId(companyId, pageable);
    var list = companyFixedCostMapper.toRestCompanyFixedCosts(result.getContent());
    return new PaginatedResponse(list, (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/fixed_costs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CompanyFixedCost> crupdateCompanyFixedCosts(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateCompanyFixedCost> toWrite) {
    return companyFixedCostMapper.toRestCompanyFixedCosts(
        companyFixedCostService.createOrUpdateAll(
            toWrite.stream()
                .map(rest -> companyFixedCostMapper.toDomain(rest, companyId))
                .toList()));
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/fixed_costs/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteCompanyFixedCostById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    companyFixedCostService.deleteById(id);
  }
}
