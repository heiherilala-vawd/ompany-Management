package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdatePurchase;
import com.example.demo.client.model.Purchase;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.PurchaseMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.PurchaseCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.PurchaseService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class PurchaseController {

  private final PurchaseService purchaseService;
  private final PurchaseMapper purchaseMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/purchases/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public Purchase getPurchaseById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return purchaseMapper.toRestPurchase(
        purchaseService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Purchase with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/purchases")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public PaginatedResponse getPurchases(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "source_warehouse_id", required = false) String sourceWarehouseId,
      @RequestParam(name = "supplier_id", required = false) String supplierId,
      @RequestParam(name = "is_equipment", required = false) Boolean isEquipment,
      @RequestParam(name = "invoice_date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate invoiceDateFrom,
      @RequestParam(name = "invoice_date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate invoiceDateTo,
      @RequestParam(name = "paid", required = false) Boolean paid) {
    PurchaseCriteria criteria = new PurchaseCriteria();
    criteria.setSourceWarehouseId(sourceWarehouseId);
    criteria.setSupplierId(supplierId);
    criteria.setIsEquipment(isEquipment);
    criteria.setInvoiceDateFrom(invoiceDateFrom);
    criteria.setInvoiceDateTo(invoiceDateTo);
    criteria.setPaid(paid);

    var result = purchaseService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(purchaseMapper::toRestPurchase).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/purchases")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Purchase> crupdatePurchases(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdatePurchase> toWrite) {
    List<com.example.demo.model.money.Purchase> saved =
        purchaseService.createOrUpdateAll(toWrite.stream().map(purchaseMapper::toDomain).toList());
    return saved.stream().map(purchaseMapper::toRestPurchase).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/purchases/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deletePurchaseById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    purchaseService.deleteById(id);
  }
}
