package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.PurchaseOperationRequest;
import com.example.demo.endpoint.rest.mapper.money.PurchaseOperationMapper;
import com.example.demo.service.money.PurchaseOperationService;
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

  @PostMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/purchase_operations")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #user_id == authentication.principal.id")
  public void createPurchaseOperation(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestBody PurchaseOperationRequest request) {
    purchaseOperationService.create(purchaseOperationMapper.toAggregate(job_id, user_id, request));
  }
}
