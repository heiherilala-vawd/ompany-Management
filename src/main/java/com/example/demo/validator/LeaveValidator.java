package com.example.demo.validator;

import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.hr.Leave;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LeaveValidator {

  public void validateLeave(Leave leave) {
    List<String> errors = new ArrayList<>();
    if (leave == null) {
      errors.add("Leave cannot be null");
    }
    if (leave != null && (leave.getUser() == null || leave.getUser().getId() == null)) {
      errors.add("Leave must be associated with a user");
    }
    if (leave != null && (leave.getLeaveType() == null || leave.getLeaveType().getId() == null)) {
      errors.add("Leave type is mandatory");
    }
    if (leave != null && leave.getStartDate() == null) {
      errors.add("Start date is mandatory");
    }
    if (leave != null && leave.getEndDate() == null) {
      errors.add("End date is mandatory");
    }
    if (leave != null
        && leave.getStartDate() != null
        && leave.getEndDate() != null
        && leave.getEndDate().isBefore(leave.getStartDate())) {
      errors.add("End date cannot be before start date");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
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
