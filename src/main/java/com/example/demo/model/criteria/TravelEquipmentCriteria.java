package com.example.demo.model.criteria;

import com.example.demo.model.movement.TravelEquipment;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelEquipmentCriteria {
  private String travelId;
  private String equipmentId;
  private Integer quantity;
  private TravelEquipment.TransportStatus status;
  private String arrivalLocation;
  private Instant arrivalDateMin;
  private Instant arrivalDateMax;
  private Boolean notArrived;
}
