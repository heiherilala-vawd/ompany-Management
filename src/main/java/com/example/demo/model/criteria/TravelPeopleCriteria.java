package com.example.demo.model.criteria;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelPeopleCriteria {
  private String travelId;
  private String userId;
  private String arrivalLocation;
  private Instant arrivalDateMin;
  private Instant arrivalDateMax;
  private Boolean notArrived;
}
