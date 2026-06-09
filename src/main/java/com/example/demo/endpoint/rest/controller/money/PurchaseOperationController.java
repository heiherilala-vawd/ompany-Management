package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.Purchase;
import com.example.demo.client.model.PurchaseOperationRequest;
import com.example.demo.endpoint.rest.mapper.money.PurchaseMapper;
import com.example.demo.endpoint.rest.mapper.money.PurchaseOperationMapper;
import com.example.demo.service.money.PurchaseOperationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PurchaseOperationController {

  private final PurchaseOperationMapper purchaseOperationMapper;
  private final PurchaseOperationService purchaseOperationService;
  private final PurchaseMapper purchaseMapper;

  @PostMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/purchase_operations")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #userId == authentication.principal.id")
  public List<Purchase> createPurchaseOperation(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String jobId,
      @Valid @RequestBody PurchaseOperationRequest request) {
    return purchaseMapper.toRestPurchases(
        purchaseOperationService.create(
            purchaseOperationMapper.toAggregate(jobId, userId, request)));
  }
}
