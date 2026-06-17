package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.BankFee;
import com.example.demo.client.model.CrupdateBankFee;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.BankFeeMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.BankFeeCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.BankFeeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class BankFeeController {

  private final BankFeeService bankFeeService;
  private final BankFeeMapper bankFeeMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/bank_fees/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public BankFee getBankFeeById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return bankFeeMapper.toRestBankFee(
        bankFeeService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("BankFee with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/bank_fees")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getBankFees(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "bank_name", required = false) String bankName,
      @RequestParam(name = "description", required = false) String description) {
    BankFeeCriteria criteria = new BankFeeCriteria();
    criteria.setBankName(bankName);
    criteria.setDescription(description);

    var result = bankFeeService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(bankFeeMapper::toRestBankFee).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/bank_fees")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<BankFee> crupdateBankFees(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateBankFee> toWrite) {
    List<com.example.demo.model.money.BankFee> saved =
        bankFeeService.createOrUpdateAll(toWrite.stream().map(bankFeeMapper::toDomain).toList());
    return saved.stream().map(bankFeeMapper::toRestBankFee).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/bank_fees/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteBankFeeById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    bankFeeService.deleteById(id);
  }
}
