package com.example.demo.model.criteria;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelExpenseCriteria {
  private String expenseId;
  private String departureLocation;
  private String arrivalLocation;
  private Instant arrivalDate;
}
