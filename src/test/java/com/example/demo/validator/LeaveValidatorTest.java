package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.model.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.hr.Leave;
import com.example.demo.model.hr.LeaveType;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LeaveValidatorTest {

  private LeaveValidator validator;

  @BeforeEach
  void setUp() {
    validator = new LeaveValidator();
  }

  @Test
  void validateLeave_valid_doesNotThrow() {
    Leave leave =
        Leave.builder()
            .id("l1")
            .user(User.builder().id("user1").build())
            .leaveType(LeaveType.builder().id("lt1").build())
            .startDate(LocalDate.of(2024, 1, 1))
            .endDate(LocalDate.of(2024, 1, 10))
            .build();
    assertThatCode(() -> validator.validateLeave(leave)).doesNotThrowAnyException();
  }

  @Test
  void validateLeave_null_throws() {
    assertThatThrownBy(() -> validator.validateLeave(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Leave cannot be null");
  }

  @Test
  void validateLeave_userNull_throws() {
    Leave leave =
        Leave.builder()
            .id("l1")
            .user(null)
            .leaveType(LeaveType.builder().id("lt1").build())
            .startDate(LocalDate.of(2024, 1, 1))
            .endDate(LocalDate.of(2024, 1, 10))
            .build();
    assertThatThrownBy(() -> validator.validateLeave(leave))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Leave must be associated with a user");
  }

  @Test
  void validateLeave_userWithoutId_throws() {
    Leave leave =
        Leave.builder()
            .id("l1")
            .user(new User())
            .leaveType(LeaveType.builder().id("lt1").build())
            .startDate(LocalDate.of(2024, 1, 1))
            .endDate(LocalDate.of(2024, 1, 10))
            .build();
    assertThatThrownBy(() -> validator.validateLeave(leave))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Leave must be associated with a user");
  }

  @Test
  void validateLeave_leaveTypeNull_throws() {
    Leave leave =
        Leave.builder()
            .id("l1")
            .user(User.builder().id("user1").build())
            .leaveType(null)
            .startDate(LocalDate.of(2024, 1, 1))
            .endDate(LocalDate.of(2024, 1, 10))
            .build();
    assertThatThrownBy(() -> validator.validateLeave(leave))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Leave type is mandatory");
  }

  @Test
  void validateLeave_leaveTypeWithoutId_throws() {
    Leave leave =
        Leave.builder()
            .id("l1")
            .user(User.builder().id("user1").build())
            .leaveType(new LeaveType())
            .startDate(LocalDate.of(2024, 1, 1))
            .endDate(LocalDate.of(2024, 1, 10))
            .build();
    assertThatThrownBy(() -> validator.validateLeave(leave))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Leave type is mandatory");
  }

  @Test
  void validateLeave_startDateNull_throws() {
    Leave leave =
        Leave.builder()
            .id("l1")
            .user(User.builder().id("user1").build())
            .leaveType(LeaveType.builder().id("lt1").build())
            .startDate(null)
            .endDate(LocalDate.of(2024, 1, 10))
            .build();
    assertThatThrownBy(() -> validator.validateLeave(leave))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Start date is mandatory");
  }

  @Test
  void validateLeave_endDateNull_throws() {
    Leave leave =
        Leave.builder()
            .id("l1")
            .user(User.builder().id("user1").build())
            .leaveType(LeaveType.builder().id("lt1").build())
            .startDate(LocalDate.of(2024, 1, 1))
            .endDate(null)
            .build();
    assertThatThrownBy(() -> validator.validateLeave(leave))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("End date is mandatory");
  }

  @Test
  void validateLeave_endDateBeforeStartDate_throws() {
    Leave leave =
        Leave.builder()
            .id("l1")
            .user(User.builder().id("user1").build())
            .leaveType(LeaveType.builder().id("lt1").build())
            .startDate(LocalDate.of(2024, 1, 10))
            .endDate(LocalDate.of(2024, 1, 1))
            .build();
    assertThatThrownBy(() -> validator.validateLeave(leave))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("End date cannot be before start date");
  }

  @Test
  void validateLeave_allFieldsInvalid_throws() {
    Leave leave =
        Leave.builder()
            .id("l1")
            .user(null)
            .leaveType(null)
            .startDate(LocalDate.of(2024, 1, 10))
            .endDate(LocalDate.of(2024, 1, 1))
            .build();
    assertThatThrownBy(() -> validator.validateLeave(leave))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Leave must be associated with a user")
        .hasMessageContaining("Leave type is mandatory")
        .hasMessageContaining("End date cannot be before start date");
  }

  @Test
  void calculateWorkingDays_basic() {
    assertThatCode(
            () ->
                validator.calculateWorkingDays(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5)))
        .doesNotThrowAnyException();
  }
}
