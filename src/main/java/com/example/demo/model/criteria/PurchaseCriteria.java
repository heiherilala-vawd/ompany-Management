package com.example.demo.model.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseCriteria {
  private String expenseId;
  private String supplier;
  private Boolean isEquipment;
}
