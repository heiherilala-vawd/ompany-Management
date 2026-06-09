package com.example.demo.service.hr;

import com.example.demo.model.User;
import com.example.demo.model.hr.EmployeeLeaveConfig;
import com.example.demo.model.hr.LeaveAccruedByMonth;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.hr.LeaveAccruedByMonthRepository;
import com.example.demo.service.utils.ModificationUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveAccruedByMonthService {

  private final LeaveAccruedByMonthRepository leaveAccruedByMonthRepository;
  private final UserRepository userRepository;
  private final ModificationUtils modificationUtils;

  public BigDecimal getAccruedDaysForYear(String userId, int year) {
    return leaveAccruedByMonthRepository
        .sumAccruedDaysByUserAndYear(userId, year)
        .orElse(BigDecimal.ZERO);
  }

  public List<LeaveAccruedByMonth> findByUserIdAndYear(String userId, int year) {
    return leaveAccruedByMonthRepository.findByUserIdAndYear(userId, year);
  }

  @Transactional
  public List<LeaveAccruedByMonth> createOrUpdateAll(List<LeaveAccruedByMonth> entries) {
    for (LeaveAccruedByMonth entry : entries) {
      LeaveAccruedByMonth existing =
          leaveAccruedByMonthRepository.findById(entry.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          entry, existing, entry.getId(), modificationUtils.takePrimaryUser());
    }
    return leaveAccruedByMonthRepository.saveAll(entries);
  }

  @Async
  public void accrueForCurrentMonth() {
    LocalDate now = LocalDate.now();
    int year = now.getYear();
    int month = now.getMonthValue();

    int page = 0;
    Page<User> userPage;
    do {
      userPage = userRepository.findAll(PageRequest.of(page, 100));
      List<LeaveAccruedByMonth> toSave = new ArrayList<>();
      for (User user : userPage.getContent()) {
        EmployeeLeaveConfig config = user.getEmployeeLeaveConfig();
        if (config == null) continue;
        if (leaveAccruedByMonthRepository.existsByUserIdAndYearAndMonth(
            user.getId(), year, month)) {
          continue;
        }
        toSave.add(
            LeaveAccruedByMonth.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .year(year)
                .month(month)
                .accruedDays(config.getVacationDaysPerMonth())
                .build());
      }
      if (!toSave.isEmpty()) {
        leaveAccruedByMonthRepository.saveAll(toSave);
      }
      page++;
    } while (userPage.hasNext());
  }
}
