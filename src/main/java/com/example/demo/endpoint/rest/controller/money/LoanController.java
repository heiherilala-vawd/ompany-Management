package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateLoan;
import com.example.demo.client.model.CrupdateLoanRepayment;
import com.example.demo.client.model.Loan;
import com.example.demo.client.model.LoanRepayment;
import com.example.demo.endpoint.rest.mapper.money.LoanMapper;
import com.example.demo.endpoint.rest.mapper.money.LoanRepaymentMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.LoanCriteria;
import com.example.demo.model.criteria.LoanRepaymentCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.LoanRepaymentService;
import com.example.demo.service.money.LoanService;
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

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/loans/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public Loan getLoanById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String id) {
    return loanMapper.toRestLoan(
        loanService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Loan with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/loans")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Loan> getLoans(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "amount", required = false) Integer amount,
      @RequestParam(name = "lender", required = false) String lender,
      @RequestParam(name = "status", required = false) String status) {
    LoanCriteria criteria = new LoanCriteria();
    criteria.setDescription(description);
    criteria.setAmount(amount);
    criteria.setLender(lender);
    criteria.setStatus(status);
    criteria.setJobId(job_id);

    return loanService.findAll(page, pageSize, criteria).stream()
        .map(loanMapper::toRestLoan)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/loans")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Loan> crupdateLoans(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestBody List<CrupdateLoan> toWrite) {
    toWrite.forEach(
        loan -> {
          if (loan.getJobId() == null) {
            loan.setJobId(job_id);
          }
        });
    List<com.example.demo.model.money.Loan> saved =
        loanService.createOrUpdateAll(toWrite.stream().map(loanMapper::toDomain).toList());
    return saved.stream().map(loanMapper::toRestLoan).toList();
  }

  @DeleteMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/loans/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteLoanById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String id) {
    loanService.deleteById(id);
  }

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/loans/{loan_id}/repayments/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public LoanRepayment getLoanRepaymentById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String loan_id,
      @PathVariable String id) {
    return loanRepaymentMapper.toRestLoanRepayment(
        loanRepaymentService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("Loan repayment with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/loans/{loan_id}/repayments")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<LoanRepayment> getLoanRepayments(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String loan_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    LoanRepaymentCriteria criteria = new LoanRepaymentCriteria();
    criteria.setLoanId(loan_id);

    return loanRepaymentService.findAll(page, pageSize, criteria).stream()
        .map(loanRepaymentMapper::toRestLoanRepayment)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/loans/{loan_id}/repayments")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<LoanRepayment> crupdateLoanRepayments(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String loan_id,
      @RequestBody List<CrupdateLoanRepayment> toWrite) {
    toWrite.forEach(
        repayment -> {
          if (repayment.getLoanId() == null) {
            repayment.setLoanId(loan_id);
          }
        });
    List<com.example.demo.model.money.LoanRepayment> saved =
        loanRepaymentService.createOrUpdateAll(
            toWrite.stream().map(loanRepaymentMapper::toDomain).toList());
    return saved.stream().map(loanRepaymentMapper::toRestLoanRepayment).toList();
  }

  @DeleteMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/loans/{loan_id}/repayments/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteLoanRepaymentById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String loan_id,
      @PathVariable String id) {
    loanRepaymentService.deleteById(id);
  }
}
