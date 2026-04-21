package com.example.demo.model.criteria;

import com.example.demo.model.History.EntityType;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryCriteria {
  private String userId;
  private EntityType entityType;
  private String entityId;
  private Instant dateFrom;
  private Instant dateTo;
}
