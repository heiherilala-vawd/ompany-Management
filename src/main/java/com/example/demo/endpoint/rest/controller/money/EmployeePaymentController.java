package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateEmployeePayment;
import com.example.demo.client.model.EmployeePayment;
import com.example.demo.client.model.PaymentType;
import com.example.demo.endpoint.rest.mapper.money.EmployeePaymentMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.EmployeePaymentCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.EmployeePaymentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class EmployeePaymentController {

  private final EmployeePaymentService employeePaymentService;
  private final EmployeePaymentMapper employeePaymentMapper;

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/employee_payments/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #user_id == authentication.principal.id")
  public EmployeePayment getEmployeePaymentById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String id) {
    return employeePaymentMapper.toRestPayment(
        employeePaymentService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("EmployeePayment with id " + id + " not found")));
  }

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/employee_payments")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #user_id == authentication.principal.id")
  public List<EmployeePayment> getEmployeePayments(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "employee_id", required = false) String employeeId,
      @RequestParam(name = "expense_id", required = false) String expenseId,
      @RequestParam(name = "payment_description", required = false) String paymentDescription,
      @RequestParam(name = "payment_type", required = false) PaymentType paymentType) {
    EmployeePaymentCriteria criteria = new EmployeePaymentCriteria();
    criteria.setEmployeeId(employeeId);
    criteria.setExpenseId(expenseId);
    criteria.setPaymentDescription(paymentDescription);
    criteria.setPaymentType(
        paymentType != null
            ? com.example.demo.model.money.EmployeePayment.PaymentType.valueOf(paymentType.name())
            : null);

    return employeePaymentService.findAll(page, pageSize, criteria).stream()
        .map(employeePaymentMapper::toRestPayment)
        .toList();
  }

  @PutMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/employee_payments")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #user_id == authentication.principal.id")
  public List<EmployeePayment> crupdateEmployeePayments(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @RequestBody List<CrupdateEmployeePayment> toWrite) {
    var saved =
        employeePaymentService.createOrUpdateAll(
            toWrite.stream().map(employeePaymentMapper::toDomain).toList());
    return saved.stream().map(employeePaymentMapper::toRestPayment).toList();
  }

  @DeleteMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/employee_payments/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteEmployeePaymentById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String id) {
    employeePaymentService.deleteById(id);
  }
}
