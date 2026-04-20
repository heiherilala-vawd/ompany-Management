package com.example.demo.repository;

import com.example.demo.model.Company;
import com.example.demo.model.Company.CompanyType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository
    extends JpaRepository<Company, String>, JpaSpecificationExecutor<Company> {
  Optional<Company> findByName(String name);

  Page<Company> findByNameContainingIgnoreCase(String name, Pageable pageable);

  Page<Company> findByCompanyType(CompanyType companyType, Pageable pageable);

  Page<Company> findByNameContainingIgnoreCaseAndCompanyType(
      String name, CompanyType companyType, Pageable pageable);

  boolean existsByName(String name);
}
