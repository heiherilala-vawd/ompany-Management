package com.example.demo.repository.movement;

import com.example.demo.model.movement.MaterialConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialConsumptionRepository
    extends JpaRepository<MaterialConsumption, String>, JpaSpecificationExecutor<MaterialConsumption> {}
