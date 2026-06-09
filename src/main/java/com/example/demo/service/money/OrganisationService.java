package com.example.demo.service.money;

import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.money.Organisation;
import com.example.demo.repository.money.OrganisationRepository;
import com.example.demo.service.utils.ModificationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganisationService {

  private final OrganisationRepository organisationRepository;
  private final ModificationUtils modificationUtils;

  public Optional<Organisation> findById(String id) {
    return organisationRepository.findById(id);
  }

  public List<Organisation> findByCompanyId(String companyId) {
    return organisationRepository.findByCompany_Id(companyId);
  }

  @Transactional
  public List<Organisation> createOrUpdateAll(List<Organisation> organisations) {
    List<Organisation> processed = new ArrayList<>();
    for (Organisation organisation : organisations) {
      Organisation existing = organisation.getId() != null
          ? organisationRepository.findById(organisation.getId()).orElse(null)
          : null;
      modificationUtils.createOrUpdateModel(
          organisation, existing, organisation.getId(), modificationUtils.takePrimaryUser());
      processed.add(organisation);
    }
    return organisationRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    organisationRepository.deleteById(id);
  }
}
