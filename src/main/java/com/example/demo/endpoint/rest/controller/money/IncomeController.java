package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.endpoint.rest.mapper.money.IncomeMoneyMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.IncomeMoneyCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.IncomeMoneyService;
import com.example.demo.service.utils.ExcelExportUtils;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class IncomeController {

  private final IncomeMoneyService incomeMoneyService;
  private final IncomeMoneyMapper incomeMoneyMapper;

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/incomes/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public IncomeMoney getIncomeById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String id) {
    return incomeMoneyMapper.toRestIncome(
        incomeMoneyService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Income with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/incomes")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<IncomeMoney> getIncomes(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "source_organization", required = false) String sourceOrganization,
      @RequestParam(name = "invoice_reference", required = false) String invoiceReference,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "amount", required = false) Integer amount,
      @RequestParam(name = "income_type_id", required = false) String incomeTypeId,
      @RequestParam(name = "money_received", required = false) Boolean moneyReceived) {
    IncomeMoneyCriteria criteria = new IncomeMoneyCriteria();
    criteria.setSourceOrganization(sourceOrganization);
    criteria.setInvoiceReference(invoiceReference);
    criteria.setDescription(description);
    criteria.setAmount(amount);
    criteria.setJobId(job_id);
    criteria.setIncomeTypeId(incomeTypeId);
    criteria.setMoneyReceived(moneyReceived);

    return incomeMoneyService.findAll(page, pageSize, criteria).stream()
        .map(incomeMoneyMapper::toRestIncome)
        .toList();
  }

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/incomes/excel")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public ResponseEntity<byte[]> getIncomesExcel(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestParam(name = "source_organization", required = false) String sourceOrganization,
      @RequestParam(name = "invoice_reference", required = false) String invoiceReference,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "amount", required = false) Integer amount,
      @RequestParam(name = "income_type_id", required = false) String incomeTypeId,
      @RequestParam(name = "money_received", required = false) Boolean moneyReceived) {
    IncomeMoneyCriteria criteria = new IncomeMoneyCriteria();
    criteria.setSourceOrganization(sourceOrganization);
    criteria.setInvoiceReference(invoiceReference);
    criteria.setDescription(description);
    criteria.setAmount(amount);
    criteria.setJobId(job_id);
    criteria.setIncomeTypeId(incomeTypeId);
    criteria.setMoneyReceived(moneyReceived);

    List<com.example.demo.model.money.IncomeMoney> result = incomeMoneyService.findAll(criteria);

    byte[] excelBytes = ExcelExportUtils.generateExcel(result, "Incomes").readAllBytes();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(
        MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    headers.setContentDispositionFormData("attachment", "incomes.xlsx");
    headers.setContentLength(excelBytes.length);

    return ResponseEntity.ok().headers(headers).body(excelBytes);
  }

  @PutMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/incomes")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<IncomeMoney> crupdateIncomes(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestBody List<CrupdateIncomeMoney> toWrite) {
    // Set job_id from path if not provided
    toWrite.forEach(
        income -> {
          if (income.getJobId() == null) {
            income.setJobId(job_id);
          }
        });
    List<com.example.demo.model.money.IncomeMoney> saved =
        incomeMoneyService.createOrUpdateAll(
            toWrite.stream().map(incomeMoneyMapper::toDomain).toList());
    return saved.stream().map(incomeMoneyMapper::toRestIncome).toList();
  }

  @DeleteMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/incomes/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteIncomeById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String id) {
    incomeMoneyService.deleteById(id);
  }
}
