package com.example.demo.endpoint.rest.controller;

import com.example.demo.client.model.Company;
import com.example.demo.client.model.CompanyType;
import com.example.demo.client.model.CrupdateCompany;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.CompanyCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.CompanyService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CompanyController {

  private final CompanyService companyService;
  private final CompanyMapper companyMapper;

  @GetMapping("/users/{userId}/companies/{companyId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER', 'EMPLOYEE')")
  public Company getCompanyById(@PathVariable String userId, @PathVariable String companyId) {
    return companyMapper.toRestCompany(
        companyService.findByIdAndUserId(companyId, userId));
  }

  @GetMapping("/users/{userId}/companies")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER', 'EMPLOYEE')")
  public PaginatedResponse getCompanies(
      @PathVariable String userId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "rib", required = false) String rib,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "company_type", required = false) CompanyType companyType) {
    CompanyCriteria criteria = new CompanyCriteria();
    criteria.setUserId(userId);
    criteria.setName(name);
    criteria.setRib(rib);
    criteria.setDescription(description);
    criteria.setCompanyType(
        companyType != null
            ? com.example.demo.model.Company.CompanyType.valueOf(companyType.name())
            : null);

    var result = companyService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(companyMapper::toRestCompany).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/companies")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public List<Company> crupdateCompanies(@Valid @RequestBody List<CrupdateCompany> toWrite) {
    List<com.example.demo.model.Company> saved =
        companyService.createOrUpdateAll(toWrite.stream().map(companyMapper::toDomain).toList());
    return saved.stream().map(companyMapper::toRestCompany).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public Company deleteCompanyById(@PathVariable String userId, @PathVariable String companyId) {
    Company entity =
        companyMapper.toRestCompany(
            companyService
                .findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company " + companyId + " not found")));
    companyService.deleteById(companyId);
    return entity;
  }
}
