package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdatePurchase;
import com.example.demo.client.model.Purchase;
import com.example.demo.endpoint.rest.mapper.money.PurchaseMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.PurchaseCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.PurchaseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class PurchaseController {

  private final PurchaseService purchaseService;
  private final PurchaseMapper purchaseMapper;

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/purchases/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public Purchase getPurchaseById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String id) {
    return purchaseMapper.toRestPurchase(
        purchaseService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Purchase with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/purchases")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Purchase> getPurchases(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "expense_id", required = false) String expenseId,
      @RequestParam(name = "supplier_id", required = false) String supplierId,
      @RequestParam(name = "is_equipment", required = false) Boolean isEquipment) {
    PurchaseCriteria criteria = new PurchaseCriteria();
    criteria.setExpenseId(expenseId);
    criteria.setSupplierId(supplierId);
    criteria.setIsEquipment(isEquipment);

    return purchaseService.findAll(page, pageSize, criteria).stream()
        .map(purchaseMapper::toRestPurchase)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/purchases")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Purchase> crupdatePurchases(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @RequestBody List<CrupdatePurchase> toWrite) {
    List<com.example.demo.model.money.Purchase> saved =
        purchaseService.createOrUpdateAll(toWrite.stream().map(purchaseMapper::toDomain).toList());
    return saved.stream().map(purchaseMapper::toRestPurchase).toList();
  }

  @DeleteMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/purchases/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deletePurchaseById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String id) {
    purchaseService.deleteById(id);
  }
}
