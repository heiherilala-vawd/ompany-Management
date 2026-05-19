package com.example.demo.service.hr;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.hr.EmployeeLeaveConfig;
import com.example.demo.model.hr.Leave;
import com.example.demo.model.hr.Leave.LeaveStatus;
import com.example.demo.model.hr.LeaveBalance;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.hr.LeaveRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.LeaveValidator;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveService {

  private final LeaveRepository leaveRepository;
  private final LeaveValidator leaveValidator;
  private final ModificationUtils modificationUtils;
  private final UserService userService;
  private final UserRepository userRepository;

  public Optional<Leave> findById(String id) {
    return leaveRepository.findById(id);
  }

  public Page<Leave> findAll(
      PageFromOne page,
      BoundedPageSize pageSize,
      String userId,
      String leaveTypeId,
      LeaveStatus status,
      Integer year) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return leaveRepository.findAll(toSpecification(userId, leaveTypeId, status, year), pageable);
  }

  public List<Leave> findAll(String userId, String leaveTypeId, LeaveStatus status, Integer year) {
    return leaveRepository.findAll(toSpecification(userId, leaveTypeId, status, year));
  }

  @Transactional
  public List<Leave> createOrUpdateAll(List<Leave> leaves) {
    for (Leave leave : leaves) {
      leaveValidator.validateLeave(leave);
      Leave existing = leaveRepository.findById(leave.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          leave, existing, leave.getId(), modificationUtils.takePrimaryUser());
    }
    return leaveRepository.saveAll(leaves);
  }

  @Transactional
  public void deleteById(String id) {
    leaveRepository.deleteById(id);
  }

  public LeaveBalance computeBalance(String userId, int year) {
    User user = userService.getById(userId);
    EmployeeLeaveConfig config = user.getEmployeeLeaveConfig();

    if (config == null) {
      return LeaveBalance.builder()
          .user(user)
          .year(year)
          .accruedDays(BigDecimal.ZERO)
          .takenDays(BigDecimal.ZERO)
          .remainingDays(BigDecimal.ZERO)
          .build();
    }

    LocalDate hireDate =
        config.getHireDate() != null
            ? config.getHireDate()
            : user.getCreatedAt() != null
                ? user.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                : LocalDate.of(year, 1, 1);

    int monthsSinceStart = 0;
    LocalDate periodStart = LocalDate.of(year, 1, 1);
    if (hireDate.isBefore(periodStart)) {
      monthsSinceStart = 12;
    } else if (hireDate.getYear() == year) {
      monthsSinceStart =
          (int)
              ChronoUnit.MONTHS.between(
                  hireDate.withDayOfMonth(1), periodStart.plusYears(1).withDayOfMonth(1));
      if (monthsSinceStart < 0) monthsSinceStart = 0;
    }

    BigDecimal accrued =
        config.getVacationDaysPerMonth().multiply(BigDecimal.valueOf(monthsSinceStart));
    BigDecimal taken = leaveRepository.sumTakenDaysByUserAndYear(userId, year);
    if (taken == null) taken = BigDecimal.ZERO;
    BigDecimal remaining = accrued.subtract(taken);
    if (remaining.compareTo(BigDecimal.ZERO) < 0) remaining = BigDecimal.ZERO;

    return LeaveBalance.builder()
        .user(user)
        .year(year)
        .accruedDays(accrued)
        .takenDays(taken)
        .remainingDays(remaining)
        .build();
  }

  public List<LeaveBalance> computeBalancesByCompany(String companyId, int year) {
    List<User> users = userRepository.findByCompanyId(companyId);
    List<LeaveBalance> balances = new ArrayList<>();
    for (User user : users) {
      balances.add(computeBalance(user.getId(), year));
    }
    return balances;
  }

  public List<User> findEmployeesWithoutLeave(String companyId, int year) {
    List<LeaveBalance> balances = computeBalancesByCompany(companyId, year);
    return balances.stream()
        .filter(b -> b.getTakenDays().compareTo(BigDecimal.ZERO) == 0)
        .map(LeaveBalance::getUser)
        .toList();
  }

  private Specification<Leave> toSpecification(
      String userId, String leaveTypeId, LeaveStatus status, Integer year) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (userId != null && !userId.isBlank()) {
        predicates.add(cb.equal(root.get("user").get("id"), userId));
      }
      if (leaveTypeId != null && !leaveTypeId.isBlank()) {
        predicates.add(cb.equal(root.get("leaveType").get("id"), leaveTypeId));
      }
      if (status != null) {
        predicates.add(cb.equal(root.get("status"), status));
      }
      if (year != null) {
        predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("startDate")), year));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
