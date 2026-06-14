package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateEmployeePayment;
import com.example.demo.client.model.EmployeePayment;
import com.example.demo.client.model.PaymentType;
import com.example.demo.endpoint.rest.mapper.UserMapper;
import com.example.demo.endpoint.rest.mapper.core.TeamMapper;
import com.example.demo.service.UserService;
import com.example.demo.service.core.TeamService;
import com.example.demo.service.money.ExpenseMoneyService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmployeePaymentMapper {

  private final ExpenseMoneyService expenseMoneyService;
  private final UserService userService;
  private final TeamService teamService;
  private final ExpenseMoneyMapper expenseMoneyMapper;
  private final TeamMapper teamMapper;
  private final UserMapper userMapper;

  public com.example.demo.model.money.EmployeePayment toDomain(EmployeePayment restPayment) {
    if (restPayment == null) return null;

    List<com.example.demo.model.User> users = null;
    if (restPayment.getUsers() != null) {
      users =
          restPayment.getUsers().stream()
              .filter(u -> u.getId() != null)
              .map(u -> userService.getById(u.getId()))
              .collect(Collectors.toList());
    }

    com.example.demo.model.core.Team team = null;
    if (restPayment.getTeam() != null && restPayment.getTeam().getId() != null) {
      team = teamService.findById(restPayment.getTeam().getId()).orElse(null);
    }

    return com.example.demo.model.money.EmployeePayment.builder()
        .id(restPayment.getId())
        .expense(
            restPayment.getExpense() != null && restPayment.getExpense().getId() != null
                ? expenseMoneyService.findById(restPayment.getExpense().getId()).orElse(null)
                : null)
        .users(users)
        .isForTeam(restPayment.getIsForTeam())
        .team(team)
        .paymentDescription(restPayment.getPaymentDescription())
        .paymentType(
            restPayment.getPaymentType() != null
                ? com.example.demo.model.money.EmployeePayment.PaymentType.valueOf(
                    restPayment.getPaymentType().name())
                : null)
        .build();
  }

  public com.example.demo.model.money.EmployeePayment toDomain(
      CrupdateEmployeePayment restPayment) {
    if (restPayment == null) return null;

    List<com.example.demo.model.User> users = null;
    if (restPayment.getUserIds() != null) {
      users =
          restPayment.getUserIds().stream()
              .filter(id -> id != null)
              .map(userService::getById)
              .collect(Collectors.toList());
    }

    com.example.demo.model.core.Team team = null;
    if (restPayment.getTeamId() != null) {
      team = teamService.findById(restPayment.getTeamId()).orElse(null);
    }

    return com.example.demo.model.money.EmployeePayment.builder()
        .id(restPayment.getId())
        .expense(
            restPayment.getExpense() != null
                ? expenseMoneyMapper.toDomain(restPayment.getExpense())
                : null)
        .users(users)
        .isForTeam(restPayment.getIsForTeam())
        .team(team)
        .paymentDescription(restPayment.getPaymentDescription())
        .paymentType(
            restPayment.getPaymentType() != null
                ? com.example.demo.model.money.EmployeePayment.PaymentType.valueOf(
                    restPayment.getPaymentType().name())
                : null)
        .build();
  }

  public EmployeePayment toRestPayment(com.example.demo.model.money.EmployeePayment domainPayment) {
    if (domainPayment == null) return null;

    EmployeePayment restPayment = new EmployeePayment();
    restPayment.setId(domainPayment.getId());
    restPayment.setExpense(expenseMoneyMapper.toRestCrupdateExpense(domainPayment.getExpense()));
    restPayment.setIsForTeam(domainPayment.getIsForTeam());
    if (domainPayment.getTeam() != null) {
      restPayment.setTeam(teamMapper.toRestTeam(domainPayment.getTeam()));
    }
    if (domainPayment.getUsers() != null) {
      restPayment.setUsers(
          domainPayment.getUsers().stream()
              .map(userMapper::toRestUser)
              .collect(Collectors.toList()));
    }
    restPayment.setPaymentDescription(domainPayment.getPaymentDescription());
    restPayment.setPaymentType(
        domainPayment.getPaymentType() != null
            ? PaymentType.valueOf(domainPayment.getPaymentType().name())
            : null);
    return restPayment;
  }

  public List<EmployeePayment> toRestPayments(
      List<com.example.demo.model.money.EmployeePayment> domainPayments) {
    return domainPayments.stream().map(this::toRestPayment).toList();
  }
}
