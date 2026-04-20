package com.example.demo.model.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelMaterialsCriteria {
  private String travelId;
  private String materialId;
  private Integer quantity;
  private Integer quantityReceived;
}
