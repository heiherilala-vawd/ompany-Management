package com.example.demo.repository.movement;

import com.example.demo.model.movement.Material;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository
    extends JpaRepository<Material, String>, JpaSpecificationExecutor<Material> {
  List<Material> findByUnit(Material.Unit unit);

  Optional<Material> findByNameAndUnit(String name, Material.Unit unit);

  boolean existsByNameAndUnit(String name, Material.Unit unit);
}
