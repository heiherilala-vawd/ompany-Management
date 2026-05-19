package com.example.demo.model.hr;

import com.example.demo.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveBalance {

  @JsonProperty("user")
  private User user;

  @JsonProperty("year")
  private Integer year;

  @JsonProperty("accrued_days")
  private BigDecimal accruedDays;

  @JsonProperty("taken_days")
  private BigDecimal takenDays;

  @JsonProperty("remaining_days")
  private BigDecimal remainingDays;
}
