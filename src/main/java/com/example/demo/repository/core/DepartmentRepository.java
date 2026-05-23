package com.example.demo.repository.core;

import com.example.demo.model.core.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository
    extends JpaRepository<Department, String>, JpaSpecificationExecutor<Department> {}
