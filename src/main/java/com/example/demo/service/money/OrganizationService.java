package com.example.demo.service.money;

import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.money.Organization;
import com.example.demo.repository.money.OrganizationRepository;
import com.example.demo.service.utils.ModificationUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService {

  private final OrganizationRepository organizationRepository;
  private final ModificationUtils modificationUtils;

  public Organization findById(String id) {
    return organizationRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Organization with id " + id + " not found"));
  }

  public List<Organization> findByCompanyId(String companyId) {
    return organizationRepository.findByCompany_Id(companyId);
  }

  public Page<Organization> findByCompanyId(String companyId, Pageable pageable) {
    return organizationRepository.findByCompany_Id(companyId, pageable);
  }

  @Transactional
  public List<Organization> createOrUpdateAll(List<Organization> organizations) {
    List<Organization> processed = new ArrayList<>();
    for (Organization organization : organizations) {
      Organization existing =
          organization.getId() != null
              ? organizationRepository.findById(organization.getId()).orElse(null)
              : null;
      modificationUtils.createOrUpdateModel(
          organization, existing, organization.getId(), modificationUtils.takePrimaryUser());
      processed.add(organization);
    }
    return organizationRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    organizationRepository.deleteById(id);
  }
}
