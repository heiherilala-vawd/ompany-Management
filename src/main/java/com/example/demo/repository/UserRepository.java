package com.example.demo.repository;

import com.example.demo.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
  Optional<User> findByEmail(String email);

  List<User> findByCompanyId(String companyId);
}
