package com.example.demo.model.criteria;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseCriteria {
  private String supplierId;
  private Boolean isEquipment;
  private LocalDate invoiceDateFrom;
  private LocalDate invoiceDateTo;
  private Boolean paid;
}
