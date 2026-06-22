package com.example.demo.repository.movement;

import com.example.demo.model.movement.EquipmentIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentIncidentRepository
    extends JpaRepository<EquipmentIncident, String>, JpaSpecificationExecutor<EquipmentIncident> {}
