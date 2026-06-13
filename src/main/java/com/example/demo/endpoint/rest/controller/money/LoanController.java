package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateLoan;
import com.example.demo.client.model.CrupdateLoanRepayment;
import com.example.demo.client.model.Loan;
import com.example.demo.client.model.LoanRepayment;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.LoanMapper;
import com.example.demo.endpoint.rest.mapper.money.LoanRepaymentMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.LoanCriteria;
import com.example.demo.model.criteria.LoanRepaymentCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.LoanRepaymentService;
import com.example.demo.service.money.LoanService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class LoanController {

  private final LoanService loanService;
  private final LoanMapper loanMapper;
  private final LoanRepaymentService loanRepaymentService;
  private final LoanRepaymentMapper loanRepaymentMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/loans/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public Loan getLoanById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return loanMapper.toRestLoanWithDetails(
        loanService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Loan with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/loans")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getLoans(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "amount", required = false) BigDecimal amount,
      @RequestParam(name = "organization_id", required = false) String organizationId) {
    LoanCriteria criteria = new LoanCriteria();
    criteria.setDescription(description);
    criteria.setAmount(amount);
    criteria.setOrganizationId(organizationId);
    criteria.setJobId(jobId);

    var result = loanService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(loanMapper::toRestLoanWithDetails).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/loans")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Loan> crupdateLoans(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateLoan> toWrite) {
    List<com.example.demo.model.money.Loan> saved =
        loanService.createOrUpdateAll(toWrite.stream().map(loanMapper::toDomain).toList());
    return saved.stream().map(loanMapper::toRestLoanWithDetails).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/loans/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteLoanById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    loanService.deleteById(id);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/loan_repayments/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public LoanRepayment getLoanRepaymentById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return loanRepaymentMapper.toRestLoanRepayment(
        loanRepaymentService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("Loan repayment with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/loan_repayments")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getLoanRepayments(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(required = false) String loan_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    LoanRepaymentCriteria criteria = new LoanRepaymentCriteria();
    criteria.setLoanId(loan_id);

    var result = loanRepaymentService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(loanRepaymentMapper::toRestLoanRepayment).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/loan_repayments")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<LoanRepayment> crupdateLoanRepayments(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateLoanRepayment> toWrite) {
    List<com.example.demo.model.money.LoanRepayment> saved =
        loanRepaymentService.createOrUpdateAll(
            toWrite.stream().map(loanRepaymentMapper::toDomain).toList());
    return saved.stream().map(loanRepaymentMapper::toRestLoanRepayment).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/loan_repayments/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteLoanRepaymentById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    loanRepaymentService.deleteById(id);
  }
}
