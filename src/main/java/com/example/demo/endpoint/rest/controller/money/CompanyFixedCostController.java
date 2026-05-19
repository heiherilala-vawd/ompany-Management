package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CompanyFixedCost;
import com.example.demo.client.model.CrupdateCompanyFixedCost;
import com.example.demo.endpoint.rest.mapper.money.CompanyFixedCostMapper;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.CompanyFixedCostService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CompanyFixedCostController {

  private final CompanyFixedCostService companyFixedCostService;
  private final CompanyFixedCostMapper companyFixedCostMapper;

  @GetMapping("/companies/{comp_id}/fixed-costs/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public CompanyFixedCost getCompanyFixedCostById(
      @PathVariable String comp_id, @PathVariable String id) {
    return companyFixedCostMapper.toRestCompanyFixedCost(
        companyFixedCostService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("CompanyFixedCost with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/fixed-costs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CompanyFixedCost> getCompanyFixedCosts(@PathVariable String comp_id) {
    return companyFixedCostMapper.toRestCompanyFixedCosts(
        companyFixedCostService.findAllByCompanyId(comp_id));
  }

  @PutMapping("/companies/{comp_id}/fixed-costs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CompanyFixedCost> crupdateCompanyFixedCosts(
      @PathVariable String comp_id, @RequestBody List<CrupdateCompanyFixedCost> toWrite) {
    return companyFixedCostMapper.toRestCompanyFixedCosts(
        companyFixedCostService.createOrUpdateAll(
            toWrite.stream().map(companyFixedCostMapper::toDomain).toList()));
  }

  @DeleteMapping("/companies/{comp_id}/fixed-costs/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteCompanyFixedCostById(@PathVariable String comp_id, @PathVariable String id) {
    companyFixedCostService.deleteById(id);
  }
}
