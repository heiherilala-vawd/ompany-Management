package com.example.demo.repository.money;

import com.example.demo.model.money.Organisation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, String> {

  List<Organisation> findByCompany_Id(String companyId);
}
