package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateOrganisation;
import com.example.demo.client.model.Organisation;
import com.example.demo.endpoint.rest.mapper.money.OrganisationMapper;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.OrganisationService;
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
public class OrganisationController {

  private final OrganisationService organisationService;
  private final OrganisationMapper organisationMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/organizations/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public Organisation getOrganisationById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return organisationMapper.toRest(
        organisationService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Organisation with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/organizations")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Organisation> getOrganisations(
      @PathVariable String userId, @PathVariable String companyId) {
    return organisationMapper.toRest(organisationService.findByCompanyId(companyId));
  }

  @PutMapping("/users/{userId}/companies/{companyId}/organizations")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Organisation> crupdateOrganisations(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateOrganisation> toWrite) {
    List<com.example.demo.model.money.Organisation> saved =
        organisationService.createOrUpdateAll(
            toWrite.stream().map(dto -> organisationMapper.toDomain(dto, companyId)).toList());
    return organisationMapper.toRest(saved);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/organizations/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteOrganisationById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    organisationService.deleteById(id);
  }
}
