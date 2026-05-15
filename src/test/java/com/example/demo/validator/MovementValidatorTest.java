package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.*;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovementValidatorTest {

  private MovementValidator validator;

  @BeforeEach
  void setUp() {
    validator = new MovementValidator();
  }

  @Test
  void validateMaterials_null_throws() {
    assertThatThrownBy(() -> validator.validateMaterials(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material list cannot be null or empty");
  }

  @Test
  void validateMaterials_empty_throws() {
    assertThatThrownBy(() -> validator.validateMaterials(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material list cannot be null or empty");
  }

  @Test
  void validateEquipments_null_throws() {
    assertThatThrownBy(() -> validator.validateEquipments(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment list cannot be null or empty");
  }

  @Test
  void validateEquipments_empty_throws() {
    assertThatThrownBy(() -> validator.validateEquipments(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment list cannot be null or empty");
  }

  @Test
  void validateWarehouses_null_throws() {
    assertThatThrownBy(() -> validator.validateWarehouses(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Warehouse list cannot be null or empty");
  }

  @Test
  void validateWarehouses_empty_throws() {
    assertThatThrownBy(() -> validator.validateWarehouses(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Warehouse list cannot be null or empty");
  }

  @Test
  void validateTravelEquipments_null_throws() {
    assertThatThrownBy(() -> validator.validateTravelEquipments(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel equipment list cannot be null or empty");
  }

  @Test
  void validateTravelEquipments_empty_throws() {
    assertThatThrownBy(() -> validator.validateTravelEquipments(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel equipment list cannot be null or empty");
  }

  @Test
  void validateTravelMaterialsList_null_throws() {
    assertThatThrownBy(() -> validator.validateTravelMaterialsList(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel materials list cannot be null or empty");
  }

  @Test
  void validateTravelMaterialsList_empty_throws() {
    assertThatThrownBy(() -> validator.validateTravelMaterialsList(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel materials list cannot be null or empty");
  }

  @Test
  void validateTravelPeoples_null_throws() {
    assertThatThrownBy(() -> validator.validateTravelPeoples(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel people list cannot be null or empty");
  }

  @Test
  void validateTravelPeoples_empty_throws() {
    assertThatThrownBy(() -> validator.validateTravelPeoples(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel people list cannot be null or empty");
  }

  @Test
  void validateTravelEquipment_nullQuantity_throws() {
    TravelEquipment te =
        TravelEquipment.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .equipment(Equipment.builder().id("eq1").build())
            .quantity(null)
            .status(TravelEquipment.TransportStatus.IN_PROGRESS)
            .build();
    assertThatThrownBy(() -> validator.validateTravelEquipment(te))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Quantity must be positive");
  }

  @Test
  void validateTravelEquipment_zeroQuantity_throws() {
    TravelEquipment te =
        TravelEquipment.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .equipment(Equipment.builder().id("eq1").build())
            .quantity(0)
            .status(TravelEquipment.TransportStatus.IN_PROGRESS)
            .build();
    assertThatThrownBy(() -> validator.validateTravelEquipment(te))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Quantity must be positive");
  }

  @Test
  void validateTravelEquipment_nullStatus_throws() {
    TravelEquipment te =
        TravelEquipment.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .equipment(Equipment.builder().id("eq1").build())
            .quantity(1)
            .status(null)
            .build();
    assertThatThrownBy(() -> validator.validateTravelEquipment(te))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Transport status is mandatory");
  }

  @Test
  void validateMaterialWarehouse_nullQuantity_throws() {
    MaterialWarehouse mw =
        MaterialWarehouse.builder()
            .material(Material.builder().id("mat1").build())
            .warehouse(Warehouse.builder().id("wh1").build())
            .quantity(null)
            .build();
    assertThatThrownBy(() -> validator.validateMaterialWarehouse(mw))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Quantity must be non-negative");
  }

  @Test
  void validateMaterialWarehouse_negativeQuantity_throws() {
    MaterialWarehouse mw =
        MaterialWarehouse.builder()
            .material(Material.builder().id("mat1").build())
            .warehouse(Warehouse.builder().id("wh1").build())
            .quantity(-1)
            .build();
    assertThatThrownBy(() -> validator.validateMaterialWarehouse(mw))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Quantity must be non-negative");
  }
}
