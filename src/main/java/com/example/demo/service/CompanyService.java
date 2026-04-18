package com.example.demo.service;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.Company;
import com.example.demo.model.Company.CompanyType;
import com.example.demo.model.PageFromOne;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.service.utils.PageUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

  private final CompanyRepository companyRepository;

  public Optional<Company> findById(String id) {
    return companyRepository.findById(id);
  }

  public Optional<Company> findByName(String name) {
    return companyRepository.findByName(name);
  }

  public Page<Company> findAll(
      PageFromOne page, BoundedPageSize pageSize, String name, CompanyType companyType) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    if (name != null && companyType != null) {
      return companyRepository.findByNameContainingIgnoreCaseAndCompanyType(
          name, companyType, pageable);
    }
    if (name != null) {
      return companyRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    if (companyType != null) {
      return companyRepository.findByCompanyType(companyType, pageable);
    }

    return companyRepository.findAll(pageable);
  }

  public List<Company> findAll() {
    return companyRepository.findAll();
  }

  @Transactional
  public Company create(Company company) {
    return companyRepository.save(company);
  }

  @Transactional
  public Company update(Company company) {
    return companyRepository.save(company);
  }

  @Transactional
  public List<Company> createOrUpdateAll(List<Company> companies) {
    return companyRepository.saveAll(companies);
  }

  @Transactional
  public void deleteById(String id) {
    companyRepository.deleteById(id);
  }

  public boolean existsById(String id) {
    return companyRepository.existsById(id);
  }
}
