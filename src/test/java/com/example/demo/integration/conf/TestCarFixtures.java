package com.example.demo.integration.conf;

import com.example.demo.endpoint.rest.dto.movement.CrupdateCar;
import com.example.demo.model.movement.CarStatus;
import com.example.demo.model.movement.FuelType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

final class TestCarFixtures {

  private TestCarFixtures() {}

  static CrupdateCar car1() {
    return CrupdateCar.builder()
        .equipmentId(TestUtils.EQUIPMENT1_ID)
        .warehouseId(TestUtils.WAREHOUSE1_ID)
        .equipmentName("Pelle mécanique")
        .equipmentCategory("Véhicule")
        .estEnPanne(false)
        .purchasePrice(new BigDecimal("50000"))
        .purchaseDate(LocalDate.of(2024, 1, 15))
        .warehouseName("Entrepôt Nord")
        .warehouseDescription("Stockage matériaux lourds")
        .licensePlate("AB-123-CD")
        .fuelType(FuelType.DIESEL)
        .status(CarStatus.AVAILABLE)
        .brand("Toyota")
        .model("Hilux")
        .year(2020)
        .color("Blanc")
        .mileage(50000)
        .build();
  }

  static CrupdateCar car2() {
    return CrupdateCar.builder()
        .equipmentId(TestUtils.EQUIPMENT2_ID)
        .warehouseId(TestUtils.WAREHOUSE2_ID)
        .equipmentName("Bétonnière")
        .equipmentCategory("Véhicule")
        .estEnPanne(false)
        .purchasePrice(new BigDecimal("30000"))
        .purchaseDate(LocalDate.of(2024, 3, 1))
        .warehouseName("Entrepôt Sud")
        .warehouseDescription("Stockage équipements")
        .licensePlate("EF-456-GH")
        .fuelType(FuelType.GASOLINE)
        .status(CarStatus.ON_MISSION)
        .brand("Renault")
        .model("Kangoo")
        .year(2021)
        .color("Rouge")
        .mileage(30000)
        .build();
  }

  static CrupdateCar someCreatableCar() {
    return CrupdateCar.builder()
        .equipmentId(UUID.randomUUID().toString())
        .warehouseId(UUID.randomUUID().toString())
        .equipmentName("Nouveau véhicule")
        .equipmentCategory("Véhicule")
        .estEnPanne(false)
        .purchasePrice(new BigDecimal("20000"))
        .purchaseDate(LocalDate.of(2024, 6, 1))
        .warehouseName("Nouvel entrepôt")
        .warehouseDescription("Description entrepôt")
        .licensePlate("NEW-001")
        .fuelType(FuelType.ELECTRIC)
        .status(CarStatus.AVAILABLE)
        .brand("Tesla")
        .model("Model 3")
        .year(2024)
        .color("Noir")
        .mileage(0)
        .build();
  }
}
