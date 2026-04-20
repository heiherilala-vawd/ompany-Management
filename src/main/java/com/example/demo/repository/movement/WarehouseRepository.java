package com.example.demo.repository.movement;

import com.example.demo.model.movement.Warehouse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository
    extends JpaRepository<Warehouse, String>, JpaSpecificationExecutor<Warehouse> {
  Page<Warehouse> findByJobId(String jobId, Pageable pageable);

  List<Warehouse> findByJobId(String jobId);

  Optional<Warehouse> findByNameAndJobId(String name, String jobId);

  List<Warehouse> findByJobIdOrderByName(String jobId);

  boolean existsByNameAndJobId(String name, String jobId);
}
