package com.example.demo.repository.hr;

import com.example.demo.model.hr.Leave;
import com.example.demo.model.hr.Leave.LeaveStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository
    extends JpaRepository<Leave, String>, JpaSpecificationExecutor<Leave> {

  List<Leave> findByUserId(String userId);

  List<Leave> findByUserIdAndStatus(String userId, LeaveStatus status);

  List<Leave> findByLeaveTypeId(String leaveTypeId);

  @Query(
      "SELECT COALESCE(SUM(l.durationDays), 0) FROM Leave l WHERE l.user.id = :userId"
          + " AND l.leaveType.deductFromBalance = true AND l.status = 'APPROVED'"
          + " AND YEAR(l.startDate) = :year")
  BigDecimal sumTakenDaysByUserAndYear(@Param("userId") String userId, @Param("year") int year);

  @Query(
      "SELECT l FROM Leave l JOIN l.user u JOIN u.companies c WHERE c.id = :companyId"
          + " AND l.status = :status AND l.startDate >= :from AND l.endDate <= :to")
  List<Leave> findByCompanyIdAndStatusAndDateRange(
      @Param("companyId") String companyId,
      @Param("status") LeaveStatus status,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);

  List<Leave> findByUserIdAndStartDateBetween(String userId, LocalDate start, LocalDate end);
}
