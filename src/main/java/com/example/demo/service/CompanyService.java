package com.example.demo.service;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.Company;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.CompanyCriteria;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.CoreValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

  private final CompanyRepository companyRepository;
  private final ModificationUtils modificationUtils;
  private final CoreValidator coreValidator;

  public Optional<Company> findById(String id) {
    return companyRepository.findById(id);
  }

  public Page<Company> findAll(
      PageFromOne page, BoundedPageSize pageSize, CompanyCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return companyRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<Company> createOrUpdateAll(List<Company> companies) {
    coreValidator.validateCompanies(companies);
    List<Company> processedCompanies = new ArrayList<>();
    for (Company company : companies) {

      Company existingCompany =
          company.getId() == null ? null : companyRepository.findById(company.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          company, existingCompany, company.getId(), modificationUtils.takePrimaryUser());
      processedCompanies.add(company);
    }
    return companyRepository.saveAll(processedCompanies);
  }

  @Transactional
  public void deleteById(String id) {
    companyRepository.deleteById(id);
  }

  private Specification<Company> toSpecification(CompanyCriteria criteria) {
    return Specification.<Company>where(containsIgnoreCase(criteria.getName(), "name"))
        .and(containsIgnoreCase(criteria.getRib(), "rib"))
        .and(containsIgnoreCase(criteria.getDescription(), "description"))
        .and(equal(criteria.getCompanyType(), "companyType"));
  }
}
