package com.example.demo.repository.movement;

import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.MaterialWarehouseId;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialWarehouseRepository
    extends JpaRepository<MaterialWarehouse, MaterialWarehouseId>,
        JpaSpecificationExecutor<MaterialWarehouse> {

  @Query(
      "SELECT COALESCE(SUM(mw.quantity * m.unitPrice), 0) "
          + "FROM MaterialWarehouse mw JOIN mw.material m "
          + "WHERE mw.warehouse.id NOT IN :excludeIds")
  BigDecimal sumStockValueExcludingWarehouses(@Param("excludeIds") List<String> excludeIds);

  @Query(
      "SELECT mw.material.id, mw.material.name, mw.material.unit, "
          + "SUM(mw.quantity), SUM(mw.quantity * mw.material.unitPrice) "
          + "FROM MaterialWarehouse mw "
          + "WHERE mw.warehouse.id NOT IN :excludeIds "
          + "GROUP BY mw.material.id, mw.material.name, mw.material.unit "
          + "ORDER BY SUM(mw.quantity * mw.material.unitPrice) DESC")
  List<Object[]> findTopStockValue(@Param("excludeIds") List<String> excludeIds);

  @Query(
      "SELECT mw.material.id, mw.material.name, mw.material.unit, "
          + "mw.warehouse.id, mw.warehouse.name, "
          + "mw.quantity, (mw.quantity * mw.material.unitPrice) "
          + "FROM MaterialWarehouse mw "
          + "WHERE mw.warehouse.id NOT IN :excludeIds "
          + "ORDER BY (mw.quantity * mw.material.unitPrice) DESC")
  List<Object[]> findStockByMaterial(@Param("excludeIds") List<String> excludeIds);

  Optional<MaterialWarehouse> findByMaterialIdAndWarehouseId(String materialId, String warehouseId);

  Optional<MaterialWarehouse> findByMaterial_IdAndWarehouse_Id(
      String materialId, String warehouseId);
}
