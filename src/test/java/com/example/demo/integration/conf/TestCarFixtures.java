package com.example.demo.integration.conf;

import com.example.demo.client.model.CarStatus;
import com.example.demo.client.model.CrupdateCar;
import com.example.demo.client.model.FuelType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

final class TestCarFixtures {

  private TestCarFixtures() {}

  static CrupdateCar car1() {
    return new CrupdateCar()
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
        .mileage(50000);
  }

  static CrupdateCar car2() {
    return new CrupdateCar()
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
        .mileage(30000);
  }

  static CrupdateCar someCreatableCar() {
    return new CrupdateCar()
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
        .mileage(0);
  }
}
