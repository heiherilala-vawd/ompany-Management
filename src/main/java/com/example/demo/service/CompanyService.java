package com.example.demo.service;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.Company;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.criteria.CompanyCriteria;
import com.example.demo.model.exception.ForbiddenException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.CoreValidator;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.HashSet;
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
  private final UserRepository userRepository;

  public Optional<Company> findById(String id) {
    return companyRepository.findById(id);
  }

  public Company findByIdAndUserId(String companyId, String userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    Company company =
        companyRepository
            .findById(companyId)
            .orElseThrow(
                () -> new NotFoundException("Company with id " + companyId + " not found"));
    if (user.getCompanies() == null || !user.getCompanies().contains(company)) {
      throw new ForbiddenException("Company not associated with the user");
    }
    return company;
  }

  public Page<Company> findAll(
      PageFromOne page, BoundedPageSize pageSize, CompanyCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return companyRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<Company> createOrUpdateAll(List<Company> companies) {
    coreValidator.validateCompanies(companies);
    User currentUser = modificationUtils.takePrimaryUser();
    List<Company> processedCompanies = new ArrayList<>();
    for (Company company : companies) {

      Company existingCompany =
          company.getId() == null ? null : companyRepository.findById(company.getId()).orElse(null);
      boolean isNew = existingCompany == null;
      modificationUtils.createOrUpdateModel(company, existingCompany, company.getId(), currentUser);
      Company saved = companyRepository.save(company);
      processedCompanies.add(saved);

      if (isNew) {
        if (currentUser.getCompanies() == null) {
          currentUser.setCompanies(new HashSet<>());
        }
        currentUser.getCompanies().add(saved);
        userRepository.save(currentUser);
      }
    }
    return processedCompanies;
  }

  @Transactional
  public void deleteById(String id) {
    companyRepository.deleteById(id);
  }

  private Specification<Company> toSpecification(CompanyCriteria criteria) {
    Specification<Company> spec =
        Specification.<Company>where(containsIgnoreCase(criteria.getName(), "name"))
            .and(containsIgnoreCase(criteria.getRib(), "rib"))
            .and(containsIgnoreCase(criteria.getDescription(), "description"))
            .and(equal(criteria.getCompanyType(), "companyType"));
    if (criteria.getUserId() != null) {
      spec = spec.and(userIdFilter(criteria.getUserId()));
    }
    return spec;
  }

  private Specification<Company> userIdFilter(String userId) {
    return (root, query, cb) -> {
      Subquery<String> subquery = query.subquery(String.class);
      Root<User> userRoot = subquery.from(User.class);
      Join<User, Company> companiesJoin = userRoot.join("companies");
      subquery.select(companiesJoin.get("id"));
      subquery.where(cb.equal(userRoot.get("id"), userId));
      return cb.in(root.get("id")).value(subquery);
    };
  }
}
