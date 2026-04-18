package com.example.demo.endpoint.rest.controller;

import com.example.demo.client.model.Company;
import com.example.demo.client.model.CompanyType;
import com.example.demo.client.model.CrupdateCompany;
import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.CompanyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CompanyController {

  private final CompanyService companyService;
  private final CompanyMapper companyMapper;

  @GetMapping("/companies/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public Company getCompanyById(@PathVariable String id) {
    return companyMapper.toRestCompany(
        companyService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Company with id " + id + " not found")));
  }

  @GetMapping("/companies")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Company> getCompanies(
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "company_type", required = false) CompanyType companyType) {

    com.example.demo.model.Company.CompanyType domainType =
        companyType != null
            ? com.example.demo.model.Company.CompanyType.valueOf(companyType.name())
            : null;

    return companyService.findAll(page, pageSize, name, domainType).stream()
        .map(companyMapper::toRestCompany)
        .toList();
  }

  @PutMapping("/companies")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Company> crupdateCompanies(@RequestBody List<CrupdateCompany> toWrite) {
    var saved =
        companyService.createOrUpdateAll(toWrite.stream().map(companyMapper::toDomain).toList());
    return saved.stream().map(companyMapper::toRestCompany).toList();
  }

  @DeleteMapping("/companies")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public void deleteCompanyById(@RequestParam String id) {
    companyService.deleteById(id);
  }
}
