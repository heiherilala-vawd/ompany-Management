package com.example.demo.model.criteria;

import com.example.demo.model.movement.IncidentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentIncidentCriteria {
  private String equipmentId;
  private IncidentType incidentType;
  private String userId;
  private String travelId;
}
