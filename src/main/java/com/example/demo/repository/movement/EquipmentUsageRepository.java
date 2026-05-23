package com.example.demo.repository.movement;

import com.example.demo.model.movement.EquipmentUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentUsageRepository
    extends JpaRepository<EquipmentUsage, String>, JpaSpecificationExecutor<EquipmentUsage> {}
