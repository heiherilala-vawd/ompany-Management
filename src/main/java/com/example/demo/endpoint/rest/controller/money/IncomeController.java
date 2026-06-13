package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.CrupdateIncomeReceipt;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.client.model.IncomeReceipt;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.IncomeMoneyMapper;
import com.example.demo.endpoint.rest.mapper.money.IncomeReceiptMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.IncomeMoneyCriteria;
import com.example.demo.model.criteria.IncomeReceiptCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.IncomeMoneyService;
import com.example.demo.service.money.IncomeReceiptService;
import com.example.demo.service.utils.ExcelExportUtils;
import jakarta.validation.Valid;
import java.math.BigDecimal;
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
  private final IncomeReceiptService incomeReceiptService;
  private final IncomeReceiptMapper incomeReceiptMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/incomes/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public IncomeMoney getIncomeById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return incomeMoneyMapper.toRestIncomeWithDetails(
        incomeMoneyService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Income with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/incomes")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getIncomes(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "organization_id", required = false) String organizationId,
      @RequestParam(name = "invoice_reference", required = false) String invoiceReference,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "amount", required = false) BigDecimal amount,
      @RequestParam(name = "income_type_id", required = false) String incomeTypeId,
      @RequestParam(name = "money_received", required = false) Boolean moneyReceived) {
    IncomeMoneyCriteria criteria = new IncomeMoneyCriteria();
    criteria.setOrganizationId(organizationId);
    criteria.setInvoiceReference(invoiceReference);
    criteria.setDescription(description);
    criteria.setAmount(amount);
    criteria.setJobId(jobId);
    criteria.setIncomeTypeId(incomeTypeId);
    criteria.setMoneyReceived(moneyReceived);

    var result = incomeMoneyService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(incomeMoneyMapper::toRestIncomeWithDetails).toList(),
        (int) result.getTotalElements());
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/incomes/excel")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public ResponseEntity<byte[]> getIncomesExcel(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(name = "organization_id", required = false) String organizationId,
      @RequestParam(name = "invoice_reference", required = false) String invoiceReference,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "amount", required = false) BigDecimal amount,
      @RequestParam(name = "income_type_id", required = false) String incomeTypeId,
      @RequestParam(name = "money_received", required = false) Boolean moneyReceived) {
    IncomeMoneyCriteria criteria = new IncomeMoneyCriteria();
    criteria.setOrganizationId(organizationId);
    criteria.setInvoiceReference(invoiceReference);
    criteria.setDescription(description);
    criteria.setAmount(amount);
    criteria.setJobId(jobId);
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

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/incomes")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<IncomeMoney> crupdateIncomes(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateIncomeMoney> toWrite) {
    List<com.example.demo.model.money.IncomeMoney> saved =
        incomeMoneyService.createOrUpdateAll(
            toWrite.stream().map(incomeMoneyMapper::toDomain).toList());
    return saved.stream().map(incomeMoneyMapper::toRestIncomeWithDetails).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/incomes/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteIncomeById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    incomeMoneyService.deleteById(id);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/incomes_receipts/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public IncomeReceipt getIncomeReceiptById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return incomeReceiptMapper.toRestIncomeReceipt(
        incomeReceiptService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("Income receipt with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/incomes_receipts")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getIncomeReceipts(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(required = false) String income_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    IncomeReceiptCriteria criteria = new IncomeReceiptCriteria();
    criteria.setIncomeId(income_id);

    var result = incomeReceiptService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(incomeReceiptMapper::toRestIncomeReceipt).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/incomes_receipts")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<IncomeReceipt> crupdateIncomeReceipts(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateIncomeReceipt> toWrite) {
    List<com.example.demo.model.money.IncomeReceipt> saved =
        incomeReceiptService.createOrUpdateAll(
            toWrite.stream().map(incomeReceiptMapper::toDomain).toList());
    return saved.stream().map(incomeReceiptMapper::toRestIncomeReceipt).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/incomes_receipts/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteIncomeReceiptById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    incomeReceiptService.deleteById(id);
  }
}
