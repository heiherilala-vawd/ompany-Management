package com.example.demo.repository.movement;

import com.example.demo.model.movement.EquipmentUsage;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentUsageRepository
    extends JpaRepository<EquipmentUsage, String>, JpaSpecificationExecutor<EquipmentUsage> {

  @Query(
      value =
          "SELECT COALESCE(SUM(EXTRACT(EPOCH FROM (eu.end_time - eu.start_time)) / 3600), 0) "
              + "FROM equipment_usage eu "
              + "WHERE (CAST(:jobId AS text) IS NULL OR eu.job_id = :jobId)",
      nativeQuery = true)
  BigDecimal sumUsageHours(@Param("jobId") String jobId);

  @Query(
      value =
          "SELECT COALESCE(SUM(EXTRACT(EPOCH FROM (eu.end_time - eu.start_time)) / 3600), 0) "
              + "FROM equipment_usage eu "
              + "WHERE (CAST(:jobId AS text) IS NULL OR eu.job_id = :jobId) "
              + "AND (CAST(:dateFrom AS timestamptz) IS NULL OR eu.start_time >= :dateFrom) "
              + "AND (CAST(:dateTo AS timestamptz) IS NULL OR eu.end_time <= :dateTo)",
      nativeQuery = true)
  BigDecimal sumUsageHours(
      @Param("jobId") String jobId,
      @Param("dateFrom") java.time.Instant dateFrom,
      @Param("dateTo") java.time.Instant dateTo);

  @Query(
      value =
          "SELECT eu.equipment_id, e.name, "
              + "SUM(EXTRACT(EPOCH FROM (eu.end_time - eu.start_time)) / 3600) "
              + "FROM equipment_usage eu "
              + "JOIN equipment e ON e.id = eu.equipment_id "
              + "WHERE (CAST(:jobId AS text) IS NULL OR eu.job_id = :jobId) "
              + "GROUP BY eu.equipment_id, e.name",
      nativeQuery = true)
  List<Object[]> findUsageHoursByEquipment(@Param("jobId") String jobId);

  @Query(
      value =
          "SELECT eu.job_id, j.description, "
              + "SUM(EXTRACT(EPOCH FROM (eu.end_time - eu.start_time)) / 3600) "
              + "FROM equipment_usage eu "
              + "JOIN job j ON j.id = eu.job_id "
              + "WHERE eu.job_id IS NOT NULL "
              + "GROUP BY eu.job_id, j.description",
      nativeQuery = true)
  List<Object[]> findUsageHoursByJob();
}
