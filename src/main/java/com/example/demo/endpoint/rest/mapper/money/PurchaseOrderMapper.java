package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.model.Company;
import com.example.demo.model.Job;
import com.example.demo.model.money.PurchaseOrder;
import com.example.demo.model.money.PurchaseOrderLine;
import com.example.demo.model.money.Supplier;
import com.example.demo.model.movement.Material;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderMapper {

  public PurchaseOrder toDomain(
      com.example.demo.client.model.CrupdatePurchaseOrder rest, String companyId) {
    if (rest == null) return null;
    PurchaseOrder order =
        PurchaseOrder.builder()
            .id(rest.getId())
            .supplier(
                rest.getSupplierId() != null
                    ? Supplier.builder().id(rest.getSupplierId()).build()
                    : null)
            .orderDate(rest.getOrderDate())
            .status(
                rest.getStatus() != null
                    ? PurchaseOrder.PurchaseOrderStatus.valueOf(rest.getStatus().name())
                    : null)
            .totalAmount(rest.getTotalAmount())
            .company(companyId != null ? Company.builder().id(companyId).build() : null)
            .job(rest.getJobId() != null ? Job.builder().id(rest.getJobId()).build() : null)
            .lines(new ArrayList<>())
            .build();

    if (rest.getLines() != null) {
      order.setLines(
          rest.getLines().stream()
              .map(
                  l ->
                      PurchaseOrderLine.builder()
                          .id(l.getId())
                          .purchaseOrder(order)
                          .material(
                              l.getMaterialId() != null
                                  ? Material.builder().id(l.getMaterialId()).build()
                                  : null)
                          .quantity(l.getQuantity())
                          .unitPrice(l.getUnitPrice())
                          .build())
              .collect(Collectors.toList()));
    }
    return order;
  }

  public com.example.demo.client.model.PurchaseOrder toRest(PurchaseOrder domain) {
    if (domain == null) return null;
    var rest = new com.example.demo.client.model.PurchaseOrder();
    rest.setId(domain.getId());
    if (domain.getSupplier() != null) {
      rest.setSupplierId(domain.getSupplier().getId());
    }
    rest.setOrderDate(domain.getOrderDate());
    if (domain.getStatus() != null) {
      rest.setStatus(
          com.example.demo.client.model.PurchaseOrderStatus.fromValue(domain.getStatus().name()));
    }
    rest.setTotalAmount(domain.getTotalAmount());
    if (domain.getJob() != null) {
      rest.setJobId(domain.getJob().getId());
    }
    if (domain.getCompany() != null) {
      rest.setCompanyId(domain.getCompany().getId());
    }
    if (domain.getLines() != null) {
      rest.setLines(
          domain.getLines().stream()
              .map(
                  l -> {
                    var line = new com.example.demo.client.model.PurchaseOrderLine();
                    line.setId(l.getId());
                    if (l.getMaterial() != null) {
                      line.setMaterialId(l.getMaterial().getId());
                    }
                    line.setQuantity(l.getQuantity());
                    if (l.getUnitPrice() != null) {
                      line.setUnitPrice(new BigDecimal(l.getUnitPrice().toString()));
                    }
                    return line;
                  })
              .collect(Collectors.toList()));
    }
    return rest;
  }

  public com.example.demo.client.model.CrupdatePurchaseOrder toCrupdate(PurchaseOrder domain) {
    if (domain == null) return null;
    var rest = new com.example.demo.client.model.CrupdatePurchaseOrder();
    rest.setId(domain.getId());
    if (domain.getSupplier() != null) {
      rest.setSupplierId(domain.getSupplier().getId());
    }
    rest.setOrderDate(domain.getOrderDate());
    if (domain.getStatus() != null) {
      rest.setStatus(
          com.example.demo.client.model.PurchaseOrderStatus.fromValue(domain.getStatus().name()));
    }
    rest.setTotalAmount(domain.getTotalAmount());
    if (domain.getJob() != null) {
      rest.setJobId(domain.getJob().getId());
    }
    return rest;
  }
}
