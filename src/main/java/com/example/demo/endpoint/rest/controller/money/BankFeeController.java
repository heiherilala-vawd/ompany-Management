package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.BankFee;
import com.example.demo.client.model.CrupdateBankFee;
import com.example.demo.endpoint.rest.mapper.money.BankFeeMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.BankFeeService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class BankFeeController {

  private final BankFeeService bankFeeService;
  private final BankFeeMapper bankFeeMapper;

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/bank_fees/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public BankFee getBankFeeById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String id) {
    return bankFeeMapper.toRestBankFee(
        bankFeeService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("BankFee with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/bank_fees")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<BankFee> getBankFees(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {

    return bankFeeService.findAll(page, pageSize).stream()
        .map(bankFeeMapper::toRestBankFee)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/bank_fees")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<BankFee> crupdateBankFees(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @RequestBody List<CrupdateBankFee> toWrite) {
    var saved =
        bankFeeService.createOrUpdateAll(toWrite.stream().map(bankFeeMapper::toDomain).toList());
    return saved.stream().map(bankFeeMapper::toRestBankFee).toList();
  }

  @DeleteMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/bank_fees/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteBankFeeById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String id) {
    bankFeeService.deleteById(id);
  }
}
