package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdatePurchaseOrder;
import com.example.demo.client.model.PurchaseOrder;
import com.example.demo.endpoint.rest.mapper.money.PurchaseOrderMapper;
import com.example.demo.service.money.PurchaseOrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PurchaseOrderController {

  private final PurchaseOrderService purchaseOrderService;
  private final PurchaseOrderMapper purchaseOrderMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/purchase_orders")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<PurchaseOrder> getPurchaseOrders(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId) {
    return purchaseOrderService.findByCompanyId(companyId, jobId).stream()
        .map(purchaseOrderMapper::toRest)
        .toList();
  }

  @GetMapping("/users/{userId}/companies/{companyId}/purchase_orders/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PurchaseOrder getPurchaseOrderById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return purchaseOrderMapper.toRest(purchaseOrderService.findById(id));
  }

  @PutMapping("/users/{userId}/companies/{companyId}/purchase_orders")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<PurchaseOrder> crupdatePurchaseOrders(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdatePurchaseOrder> toWrite) {
    var domains = toWrite.stream().map(po -> purchaseOrderMapper.toDomain(po, companyId)).toList();
    return purchaseOrderService.createOrUpdateAll(domains).stream()
        .map(purchaseOrderMapper::toRest)
        .toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/purchase_orders/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deletePurchaseOrderById(@PathVariable String id) {
    purchaseOrderService.deleteById(id);
  }
}
