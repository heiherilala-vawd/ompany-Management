package com.example.demo.model.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialCriteria {
  private String companyId;
  private String name;
  private String description;
  private com.example.demo.model.movement.Material.Unit unit;
  private Boolean notArrived;
}
