package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateOrganization;
import com.example.demo.client.model.Organization;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.OrganizationMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.service.money.OrganizationService;
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
public class OrganizationController {

  private final OrganizationService organizationService;
  private final OrganizationMapper organizationMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/organizations")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getOrganizations(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    var result = organizationService.findByCompanyId(companyId, pageable);
    var list = result.stream().map(organizationMapper::toRest).toList();
    return new PaginatedResponse(list, (int) result.getTotalElements());
  }

  @GetMapping("/users/{userId}/companies/{companyId}/organizations/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public Organization getOrganizationById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return organizationMapper.toRest(organizationService.findById(id));
  }

  @PutMapping("/users/{userId}/companies/{companyId}/organizations")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Organization> crupdateOrganizations(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateOrganization> toWrite) {
    var domains = toWrite.stream().map(s -> organizationMapper.toDomain(s, companyId)).toList();
    return organizationService.createOrUpdateAll(domains).stream()
        .map(organizationMapper::toRest)
        .toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/organizations/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteOrganizationById(@PathVariable String id) {
    organizationService.deleteById(id);
  }
}
