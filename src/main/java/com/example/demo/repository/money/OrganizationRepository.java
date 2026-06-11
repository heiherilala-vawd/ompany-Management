package com.example.demo.repository.money;

import com.example.demo.model.money.Organization;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository
    extends JpaRepository<Organization, String>, JpaSpecificationExecutor<Organization> {
  List<Organization> findByCompany_Id(String companyId);
}
