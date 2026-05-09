package com.example.demo.model.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialWarehouseCriteria {
  private String materialId;
  private String warehouseId;
  private Boolean notArrived;
}
