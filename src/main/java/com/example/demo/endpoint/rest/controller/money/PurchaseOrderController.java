package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdatePurchaseOrder;
import com.example.demo.client.model.PurchaseOrder;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.PurchaseOrderMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.service.money.PurchaseOrderService;
import com.example.demo.service.utils.PageUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PurchaseOrderController {

  private final PurchaseOrderService purchaseOrderService;
  private final PurchaseOrderMapper purchaseOrderMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/purchase_orders")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getPurchaseOrders(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "job_id", required = false) String jobId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    var result = purchaseOrderService.findByCompanyId(companyId, jobId, pageable);
    var list = result.stream().map(purchaseOrderMapper::toRest).toList();
    return new PaginatedResponse(list, (int) result.getTotalElements());
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
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deletePurchaseOrderById(@PathVariable String id) {
    purchaseOrderService.deleteById(id);
  }
}
