package com.example.demo.repository.movement;

import com.example.demo.model.movement.MaterialConsumption;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialConsumptionRepository
    extends JpaRepository<MaterialConsumption, String>,
        JpaSpecificationExecutor<MaterialConsumption> {

  @Query(
      "SELECT COALESCE(SUM(mc.quantity * m.unitPrice), 0) "
          + "FROM MaterialConsumption mc JOIN mc.material m "
          + "WHERE (:jobId IS NULL OR mc.job.id = :jobId) "
          + "AND (:dateFrom IS NULL OR mc.consumptionDate >= :dateFrom) "
          + "AND (:dateTo IS NULL OR mc.consumptionDate <= :dateTo)")
  BigDecimal sumConsumptionCost(
      @Param("jobId") String jobId,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo);

  @Query(
      "SELECT mc.material.id, mc.material.name, mc.material.unit, "
          + "SUM(mc.quantity), SUM(mc.quantity * mc.material.unitPrice) "
          + "FROM MaterialConsumption mc "
          + "WHERE (:jobId IS NULL OR mc.job.id = :jobId) "
          + "AND (:dateFrom IS NULL OR mc.consumptionDate >= :dateFrom) "
          + "AND (:dateTo IS NULL OR mc.consumptionDate <= :dateTo) "
          + "GROUP BY mc.material.id, mc.material.name, mc.material.unit "
          + "ORDER BY SUM(mc.quantity * mc.material.unitPrice) DESC")
  List<Object[]> findTopConsumptionCost(
      @Param("jobId") String jobId,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo);

  @Query(
      "SELECT mc.material.id, mc.material.name, mc.material.unit, "
          + "SUM(mc.quantity), SUM(mc.quantity * mc.material.unitPrice), "
          + "FUNCTION('TO_CHAR', mc.consumptionDate, 'YYYY-MM') "
          + "FROM MaterialConsumption mc "
          + "WHERE (:jobId IS NULL OR mc.job.id = :jobId) "
          + "AND (:dateFrom IS NULL OR mc.consumptionDate >= :dateFrom) "
          + "AND (:dateTo IS NULL OR mc.consumptionDate <= :dateTo) "
          + "GROUP BY mc.material.id, mc.material.name, mc.material.unit, "
          + "FUNCTION('TO_CHAR', mc.consumptionDate, 'YYYY-MM')")
  List<Object[]> findConsumptionByMaterial(
      @Param("jobId") String jobId,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo);
}
