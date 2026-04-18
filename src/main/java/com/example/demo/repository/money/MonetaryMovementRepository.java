package com.example.demo.repository.money;

import com.example.demo.model.money.MonetaryMovement;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MonetaryMovementRepository extends JpaRepository<MonetaryMovement, String> {
  Page<MonetaryMovement> findByAmountBetween(
      Integer minAmount, Integer maxAmount, Pageable pageable);

  List<MonetaryMovement> findByCreatedAtBetween(Instant startDate, Instant endDate);

  @Query("SELECT SUM(m.amount) FROM MonetaryMovement m")
  Long getTotalAmount();

  @Query(
      "SELECT SUM(m.amount) FROM MonetaryMovement m WHERE m.createdAt BETWEEN :startDate AND :endDate")
  Long getTotalAmountBetweenDates(
      @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}
