package com.example.demo.validator;

import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.hr.Leave;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class LeaveValidator {

  public void validateLeave(Leave leave) {
    if (leave == null) {
      throw new BadRequestException("Leave cannot be null");
    }
    if (leave.getUser() == null || leave.getUser().getId() == null) {
      throw new BadRequestException("Leave must be associated with a user");
    }
    if (leave.getLeaveType() == null || leave.getLeaveType().getId() == null) {
      throw new BadRequestException("Leave type is mandatory");
    }
    if (leave.getStartDate() == null) {
      throw new BadRequestException("Start date is mandatory");
    }
    if (leave.getEndDate() == null) {
      throw new BadRequestException("End date is mandatory");
    }
    if (leave.getEndDate().isBefore(leave.getStartDate())) {
      throw new BadRequestException("End date cannot be before start date");
    }
    if (leave.getDurationDays() == null
        || leave.getDurationDays().compareTo(java.math.BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Duration days must be positive");
    }
  }

  public long calculateWorkingDays(LocalDate start, LocalDate end) {
    long days = ChronoUnit.DAYS.between(start, end) + 1;
    long workingDays = 0;
    LocalDate current = start;
    for (int i = 0; i < days; i++) {
      if (current.getDayOfWeek().getValue() <= 5) {
        workingDays++;
      }
      current = current.plusDays(1);
    }
    return workingDays;
  }
}
