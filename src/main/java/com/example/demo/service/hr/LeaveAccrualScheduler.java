package com.example.demo.service.hr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveAccrualScheduler {

  private final LeaveAccruedByMonthService leaveAccruedByMonthService;

  @Scheduled(cron = "0 0 6 1 * *")
  public void accrueMonthlyLeave() {
    log.info("Starting monthly leave accrual...");
    leaveAccruedByMonthService.accrueForCurrentMonth();
    log.info("Monthly leave accrual submitted.");
  }
}
