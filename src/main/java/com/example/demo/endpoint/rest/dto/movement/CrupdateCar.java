package com.example.demo.endpoint.rest.dto.movement;

import com.example.demo.model.movement.CarStatus;
import com.example.demo.model.movement.FuelType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrupdateCar {

  private String equipmentId;
  private String warehouseId;
  private String equipmentName;
  private String equipmentCategory;
  private Boolean estEnPanne;
  private BigDecimal purchasePrice;
  private LocalDate purchaseDate;
  private String warehouseName;
  private String warehouseDescription;
  private String licensePlate;
  private FuelType fuelType;
  private CarStatus status;
  private String brand;
  private String model;
  private Integer year;
  private String color;
  private Integer mileage;
  private String comment;
}
