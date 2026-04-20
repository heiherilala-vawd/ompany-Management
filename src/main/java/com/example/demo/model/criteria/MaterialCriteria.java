package com.example.demo.model.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialCriteria {
  private String warehouseId;
  private String name;
  private String description;
  private Integer floorNumber;
  private Integer storageNumber;
}
