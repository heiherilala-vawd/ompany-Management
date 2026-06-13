package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateEmployeePayment;
import com.example.demo.client.model.EmployeePayment;
import com.example.demo.client.model.PaymentType;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.EmployeePaymentMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.EmployeePaymentCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.EmployeePaymentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class EmployeePaymentController {

  private final EmployeePaymentService employeePaymentService;
  private final EmployeePaymentMapper employeePaymentMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/employee_payments/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #userId == authentication.principal.id")
  public EmployeePayment getEmployeePaymentById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return employeePaymentMapper.toRestPayment(
        employeePaymentService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("EmployeePayment with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/employee_payments")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #userId == authentication.principal.id")
  public PaginatedResponse getEmployeePayments(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "user_ids", required = false) List<String> userIds,
      @RequestParam(name = "payment_description", required = false) String paymentDescription,
      @RequestParam(name = "payment_type", required = false) PaymentType paymentType) {
    EmployeePaymentCriteria criteria = new EmployeePaymentCriteria();
    criteria.setUserIDs(userIds);
    criteria.setPaymentDescription(paymentDescription);
    criteria.setPaymentType(
        paymentType != null
            ? com.example.demo.model.money.EmployeePayment.PaymentType.valueOf(paymentType.name())
            : null);

    var result = employeePaymentService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(employeePaymentMapper::toRestPayment).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/employee_payments")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public List<EmployeePayment> crupdateEmployeePayments(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateEmployeePayment> toWrite) {
    List<com.example.demo.model.money.EmployeePayment> saved =
        employeePaymentService.createOrUpdateAll(
            toWrite.stream().map(employeePaymentMapper::toDomain).toList());
    return saved.stream().map(employeePaymentMapper::toRestPayment).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/employee_payments/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteEmployeePaymentById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    employeePaymentService.deleteById(id);
  }
}
