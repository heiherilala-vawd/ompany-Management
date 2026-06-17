package com.example.demo.integration.conf;

import com.example.demo.client.model.CrupdateCompany;
import com.example.demo.client.model.CrupdateEmployeeLeaveConfig;
import com.example.demo.client.model.CrupdateLeave;
import com.example.demo.client.model.CrupdateLeaveType;
import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.EmployeeLeaveConfig;
import com.example.demo.client.model.Leave;
import com.example.demo.client.model.LeaveStatus;
import com.example.demo.client.model.LeaveType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

final class TestHrFixtures {

  private TestHrFixtures() {}

  static LeaveType leaveType1() {
    LeaveType leaveType = new LeaveType();
    leaveType.setId(TestUtils.LEAVE_TYPE1_ID);
    leaveType.setName("Congé payé");
    leaveType.setDescription("Congés annuels payés");
    leaveType.setPaid(true);
    leaveType.setDeductFromBalance(true);
    leaveType.setColor("#4CAF50");
    leaveType.setDaysPerYear(30);
    leaveType.setCompany(new CrupdateCompany().id(TestUtils.COMPANY1_ID));
    return leaveType;
  }

  static LeaveType leaveType2() {
    LeaveType leaveType = new LeaveType();
    leaveType.setId(TestUtils.LEAVE_TYPE2_ID);
    leaveType.setName("Congé maladie");
    leaveType.setDescription("Arrêt maladie");
    leaveType.setPaid(true);
    leaveType.setDeductFromBalance(false);
    leaveType.setColor("#F44336");
    leaveType.setCompany(new CrupdateCompany().id(TestUtils.COMPANY1_ID));
    return leaveType;
  }

  static CrupdateLeaveType leaveTypeToCrupdateLeaveType(LeaveType leaveType) {
    CrupdateLeaveType crupdate = new CrupdateLeaveType();
    crupdate.setId(leaveType.getId());
    crupdate.setName(leaveType.getName());
    crupdate.setDescription(leaveType.getDescription());
    crupdate.setPaid(leaveType.getPaid());
    crupdate.setDeductFromBalance(leaveType.getDeductFromBalance());
    crupdate.setColor(leaveType.getColor());
    crupdate.setDaysPerYear(leaveType.getDaysPerYear());
    crupdate.setCompanyId(leaveType.getCompany() != null ? leaveType.getCompany().getId() : null);
    crupdate.setComment(leaveType.getComment());
    return crupdate;
  }

  static CrupdateLeaveType someCreatableLeaveType() {
    CrupdateLeaveType crupdate = new CrupdateLeaveType();
    crupdate.setId(UUID.randomUUID().toString());
    crupdate.setName("Congé sans solde");
    crupdate.setDescription("Congé non rémunéré");
    crupdate.setPaid(false);
    crupdate.setDeductFromBalance(false);
    crupdate.setColor("#9E9E9E");
    crupdate.setDaysPerYear(15);
    crupdate.setCompanyId(TestUtils.COMPANY1_ID);
    return crupdate;
  }

  static EmployeeLeaveConfig config1() {
    EmployeeLeaveConfig config = new EmployeeLeaveConfig();
    config.setId(TestUtils.CONFIG1_ID);
    config.setHireDate(LocalDate.of(2023, 6, 1));
    config.setContractType("CDI");
    config.setVacationDaysPerMonth(new BigDecimal("2.5"));
    return config;
  }

  static EmployeeLeaveConfig config2() {
    EmployeeLeaveConfig config = new EmployeeLeaveConfig();
    config.setId(TestUtils.CONFIG2_ID);
    config.setHireDate(LocalDate.of(2024, 1, 15));
    config.setContractType("CDD");
    config.setVacationDaysPerMonth(new BigDecimal("2.0"));
    return config;
  }

  static CrupdateEmployeeLeaveConfig configToCrupdateConfig(EmployeeLeaveConfig config) {
    CrupdateEmployeeLeaveConfig crupdate = new CrupdateEmployeeLeaveConfig();
    crupdate.setId(config.getId());
    crupdate.setHireDate(config.getHireDate());
    crupdate.setContractType(config.getContractType());
    crupdate.setVacationDaysPerMonth(config.getVacationDaysPerMonth());
    crupdate.setComment(config.getComment());
    return crupdate;
  }

  static CrupdateEmployeeLeaveConfig someCreatableConfig() {
    CrupdateEmployeeLeaveConfig crupdate = new CrupdateEmployeeLeaveConfig();
    crupdate.setId(UUID.randomUUID().toString());
    crupdate.setHireDate(LocalDate.of(2025, 1, 1));
    crupdate.setContractType("CDI");
    crupdate.setVacationDaysPerMonth(new BigDecimal("2.5"));
    return crupdate;
  }

  static Leave leave1() {
    Leave leave = new Leave();
    leave.setId(TestUtils.LEAVE1_ID);
    leave.setUser(userToCrupdateUser(TestUtils.employee1()));
    leave.setLeaveType(leaveType1());
    leave.setStartDate(LocalDate.of(2026, 6, 1));
    leave.setEndDate(LocalDate.of(2026, 6, 15));
    leave.setDurationDays(new BigDecimal("11.0"));
    leave.setStatus(LeaveStatus.APPROVED);
    leave.setReason("Vacances annuelles");
    return leave;
  }

  static Leave leave2() {
    Leave leave = new Leave();
    leave.setId(TestUtils.LEAVE2_ID);
    leave.setUser(userToCrupdateUser(TestUtils.employee1()));
    leave.setLeaveType(leaveType2());
    leave.setStartDate(LocalDate.of(2026, 3, 10));
    leave.setEndDate(LocalDate.of(2026, 3, 12));
    leave.setDurationDays(new BigDecimal("3.0"));
    leave.setStatus(LeaveStatus.PENDING);
    leave.setReason("Rendez-vous médical");
    return leave;
  }

  static CrupdateLeave leaveToCrupdateLeave(Leave leave) {
    CrupdateLeave crupdate = new CrupdateLeave();
    crupdate.setId(leave.getId());
    crupdate.setUserId(leave.getUser() != null ? leave.getUser().getId() : null);
    crupdate.setLeaveTypeId(leave.getLeaveType() != null ? leave.getLeaveType().getId() : null);
    crupdate.setStartDate(leave.getStartDate());
    crupdate.setEndDate(leave.getEndDate());
    crupdate.setDurationDays(leave.getDurationDays());
    crupdate.setStatus(leave.getStatus());
    crupdate.setReason(leave.getReason());
    crupdate.setComment(leave.getComment());
    return crupdate;
  }

  static CrupdateLeave someCreatableLeave() {
    CrupdateLeave crupdate = new CrupdateLeave();
    crupdate.setId(UUID.randomUUID().toString());
    crupdate.setUserId(TestUtils.EMPLOYEE_ID);
    crupdate.setLeaveTypeId(TestUtils.LEAVE_TYPE1_ID);
    crupdate.setStartDate(LocalDate.of(2026, 8, 1));
    crupdate.setEndDate(LocalDate.of(2026, 8, 10));
    crupdate.setDurationDays(new BigDecimal("7.5"));
    crupdate.setStatus(LeaveStatus.PENDING);
    crupdate.setReason("Congé d'été");
    return crupdate;
  }

  private static CrupdateUser userToCrupdateUser(com.example.demo.client.model.User user) {
    return new CrupdateUser()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail());
  }
}
