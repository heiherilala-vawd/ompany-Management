package com.example.demo.repository.hr;

import com.example.demo.model.hr.LeaveAccruedByMonth;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveAccruedByMonthRepository extends JpaRepository<LeaveAccruedByMonth, String> {

  List<LeaveAccruedByMonth> findByUserIdAndYear(String userId, Integer year);

  @Query(
      "SELECT COALESCE(SUM(l.accruedDays), 0) FROM LeaveAccruedByMonth l WHERE l.user.id = :userId AND l.year = :year")
  Optional<BigDecimal> sumAccruedDaysByUserAndYear(String userId, Integer year);

  boolean existsByUserIdAndYearAndMonth(String userId, Integer year, Integer month);
}
