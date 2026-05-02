package com.example.demo.model.criteria;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelMaterialsCriteria {
  private String travelId;
  private String materialId;
  private Integer quantity;
  private Integer quantityReceived;
  private String arrivalLocation;
  private Instant arrivalDateMin;
  private Instant arrivalDateMax;
  private Boolean notArrived;
}
