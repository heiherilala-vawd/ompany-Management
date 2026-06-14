package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "car")
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Car extends CreatAndUpdateEntity implements Serializable {

  @EmbeddedId private CarId id;

  @NotNull
  @ManyToOne
  @MapsId("equipmentId")
  @JoinColumn(name = "equipment_id")
  private Equipment equipment;

  @NotNull
  @ManyToOne
  @MapsId("warehouseId")
  @JoinColumn(name = "warehouse_id")
  private Warehouse warehouse;

  @Size(max = 50)
  @Column(name = "license_plate")
  private String licensePlate;

  @Enumerated(EnumType.STRING)
  @Column(name = "fuel_type")
  private FuelType fuelType;

  @Size(max = 100)
  private String brand;

  @Size(max = 100)
  private String model;

  private Integer year;

  @Size(max = 50)
  private String color;

  private Integer mileage;

  @Enumerated(EnumType.STRING)
  private CarStatus status;
}
