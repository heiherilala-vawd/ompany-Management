package com.example.demo.endpoint.rest.controller.hr;

import com.example.demo.client.model.CrupdateLeave;
import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.Leave;
import com.example.demo.client.model.LeaveBalance;
import com.example.demo.client.model.LeaveStatus;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.hr.LeaveMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class LeaveController {

  private final com.example.demo.service.hr.LeaveService leaveService;
  private final LeaveMapper leaveMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/leaves/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'EMPLOYEE')")
  public Leave getLeaveById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return leaveMapper.toRestLeave(
        leaveService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Leave with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/leaves")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getLeaves(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "user_id", required = false) String filterUserId,
      @RequestParam(name = "leave_type_id", required = false) String leaveTypeId,
      @RequestParam(name = "status", required = false) LeaveStatus status,
      @RequestParam(name = "year", required = false) Integer year,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    com.example.demo.model.hr.Leave.LeaveStatus domainStatus =
        status != null ? com.example.demo.model.hr.Leave.LeaveStatus.valueOf(status.name()) : null;
    var result = leaveService.findAll(page, pageSize, filterUserId, leaveTypeId, domainStatus, year);
    return new PaginatedResponse(
        result.stream().map(leaveMapper::toRestLeave).toList(), (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/leaves")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Leave> crupdateLeaves(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateLeave> toWrite) {
    List<com.example.demo.model.hr.Leave> saved =
        leaveService.createOrUpdateAll(toWrite.stream().map(leaveMapper::toDomain).toList());
    return saved.stream().map(leaveMapper::toRestLeave).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/leaves/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteLeaveById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    leaveService.deleteById(id);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/leave_balances")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'EMPLOYEE')")
  public PaginatedResponse getLeaveBalances(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "year") Integer year,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    var list =
        leaveService.computeBalancesByCompany(companyId, year).stream()
            .map(this::toRestLeaveBalance)
            .toList();
    return new PaginatedResponse(list, list.size());
  }

  @GetMapping("/users/{userId}/companies/{companyId}/leave_balances/employees_without_leave")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getEmployeesWithoutLeave(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "year") Integer year,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    var list =
        leaveService.findEmployeesWithoutLeave(companyId, year).stream()
            .map(
                u ->
                    new CrupdateUser()
                        .id(u.getId())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .email(u.getEmail()))
            .toList();
    return new PaginatedResponse(list, list.size());
  }

  private LeaveBalance toRestLeaveBalance(com.example.demo.model.hr.LeaveBalance domain) {
    LeaveBalance rest = new LeaveBalance();
    if (domain.getUser() != null) {
      rest.setUser(
          new CrupdateUser()
              .id(domain.getUser().getId())
              .firstName(domain.getUser().getFirstName())
              .lastName(domain.getUser().getLastName())
              .email(domain.getUser().getEmail()));
    }
    rest.setYear(domain.getYear());
    rest.setAccruedDays(domain.getAccruedDays());
    rest.setTakenDays(domain.getTakenDays());
    rest.setRemainingDays(domain.getRemainingDays());
    return rest;
  }
}
