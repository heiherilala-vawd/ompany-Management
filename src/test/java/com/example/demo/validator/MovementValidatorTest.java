package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.*;
import com.example.demo.repository.movement.CarRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovementValidatorTest {

  @Mock private CarRepository carRepository;

  private MovementValidator validator;

  @BeforeEach
  void setUp() {
    validator = new MovementValidator(carRepository);
  }

  @Test
  void validateMaterial_valid_doesNotThrow() {
    Material m = Material.builder().id("mat1").name("Wood").unit(Material.Unit.M3).build();
    assertThatCode(() -> validator.validateMaterial(m)).doesNotThrowAnyException();
  }

  @Test
  void validateMaterial_null_throws() {
    assertThatThrownBy(() -> validator.validateMaterial(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material cannot be null");
  }

  @Test
  void validateMaterial_allFieldsInvalid_throws() {
    Material m = Material.builder().id("mat1").name(null).unit(null).build();
    assertThatThrownBy(() -> validator.validateMaterial(m))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material name is mandatory")
        .hasMessageContaining("Material unit is mandatory");
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
  void validateEquipment_valid_doesNotThrow() {
    Equipment e =
        Equipment.builder()
            .id("eq1")
            .name("Drill")
            .warehouse(Warehouse.builder().id("wh1").build())
            .build();
    assertThatCode(() -> validator.validateEquipment(e)).doesNotThrowAnyException();
  }

  @Test
  void validateEquipment_null_throws() {
    assertThatThrownBy(() -> validator.validateEquipment(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment cannot be null");
  }

  @Test
  void validateEquipment_allFieldsInvalid_throws() {
    Equipment e = Equipment.builder().id("eq1").name(null).warehouse(null).build();
    assertThatThrownBy(() -> validator.validateEquipment(e))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment name is mandatory")
        .hasMessageContaining("Equipment must be associated with a warehouse");
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
  void validateWarehouse_valid_doesNotThrow() {
    Warehouse w = Warehouse.builder().id("wh1").name("Main Warehouse").build();
    assertThatCode(() -> validator.validateWarehouse(w)).doesNotThrowAnyException();
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
  void validateMaintenance_valid_doesNotThrow() {
    Maintenance m =
        Maintenance.builder()
            .id("m1")
            .expense(com.example.demo.model.money.ExpenseMoney.builder().id("exp1").build())
            .description("Fix engine")
            .build();
    assertThatCode(() -> validator.validateMaintenance(m)).doesNotThrowAnyException();
  }

  @Test
  void validateMaintenance_allFieldsInvalid_throws() {
    Maintenance m = Maintenance.builder().id("m1").expense(null).description(null).build();
    assertThatThrownBy(() -> validator.validateMaintenance(m))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Maintenance must be linked to an expense")
        .hasMessageContaining("Maintenance description is mandatory");
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
  void validateTravelEquipment_allFieldsInvalid_throws() {
    TravelEquipment te =
        TravelEquipment.builder().travel(null).equipment(null).quantity(null).status(null).build();
    assertThatThrownBy(() -> validator.validateTravelEquipment(te))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel equipment must be linked to a travel expense")
        .hasMessageContaining("Equipment is mandatory for travel equipment")
        .hasMessageContaining("Quantity must be positive")
        .hasMessageContaining("Transport status is mandatory");
  }

  @Test
  void validateTravelMaterials_valid_doesNotThrow() {
    TravelMaterials tm =
        TravelMaterials.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .material(Material.builder().id("mat1").build())
            .quantity(10)
            .build();
    assertThatCode(() -> validator.validateTravelMaterials(tm)).doesNotThrowAnyException();
  }

  @Test
  void validateTravelMaterials_allFieldsInvalid_throws() {
    TravelMaterials tm =
        TravelMaterials.builder().travel(null).material(null).quantity(null).build();
    assertThatThrownBy(() -> validator.validateTravelMaterials(tm))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel materials must be linked to a travel expense")
        .hasMessageContaining("Material is mandatory for travel materials")
        .hasMessageContaining("Quantity must be positive");
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

  @Test
  void validateMaterialWarehouse_allFieldsInvalid_throws() {
    MaterialWarehouse mw =
        MaterialWarehouse.builder().material(null).warehouse(null).quantity(null).build();
    assertThatThrownBy(() -> validator.validateMaterialWarehouse(mw))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material is mandatory for material warehouse")
        .hasMessageContaining("Warehouse is mandatory for material warehouse")
        .hasMessageContaining("Quantity must be non-negative");
  }

  @Test
  void validateMaintenance_null_throws() {
    assertThatThrownBy(() -> validator.validateMaintenance(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Maintenance cannot be null");
  }

  @Test
  void validateMaintenance_expenseNull_throws() {
    Maintenance m = Maintenance.builder().id("m1").expense(null).description("Fix engine").build();
    assertThatThrownBy(() -> validator.validateMaintenance(m))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Maintenance must be linked to an expense");
  }

  @Test
  void validateMaintenance_expenseWithoutId_throws() {
    Maintenance m =
        Maintenance.builder()
            .id("m1")
            .expense(ExpenseMoney.builder().build())
            .description("Fix engine")
            .build();
    assertThatThrownBy(() -> validator.validateMaintenance(m))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Maintenance must be linked to an expense");
  }

  @Test
  void validateMaintenance_expenseAmountNegative_throws() {
    Maintenance m =
        Maintenance.builder()
            .id("m1")
            .expense(ExpenseMoney.builder().id("exp1").amount(new BigDecimal("-1")).build())
            .description("Fix engine")
            .build();
    assertThatThrownBy(() -> validator.validateMaintenance(m))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Maintenance expense amount must be non-negative");
  }

  @Test
  void validateMaintenance_descriptionNull_throws() {
    Maintenance m =
        Maintenance.builder()
            .id("m1")
            .expense(ExpenseMoney.builder().id("exp1").build())
            .description(null)
            .build();
    assertThatThrownBy(() -> validator.validateMaintenance(m))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Maintenance description is mandatory");
  }

  @Test
  void validateMaintenance_descriptionBlank_throws() {
    Maintenance m =
        Maintenance.builder()
            .id("m1")
            .expense(ExpenseMoney.builder().id("exp1").build())
            .description("   ")
            .build();
    assertThatThrownBy(() -> validator.validateMaintenance(m))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Maintenance description is mandatory");
  }

  @Test
  void validateMaintenance_descriptionEmpty_throws() {
    Maintenance m =
        Maintenance.builder()
            .id("m1")
            .expense(ExpenseMoney.builder().id("exp1").build())
            .description("")
            .build();
    assertThatThrownBy(() -> validator.validateMaintenance(m))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Maintenance description is mandatory");
  }

  @Test
  void validateMaintenances_null_throws() {
    assertThatThrownBy(() -> validator.validateMaintenances(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Maintenance list cannot be null or empty");
  }

  @Test
  void validateMaintenances_empty_throws() {
    assertThatThrownBy(() -> validator.validateMaintenances(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Maintenance list cannot be null or empty");
  }

  @Test
  void validateWarehouse_null_throws() {
    assertThatThrownBy(() -> validator.validateWarehouse(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Warehouse cannot be null");
  }

  @Test
  void validateWarehouse_nameNull_throws() {
    Warehouse w = Warehouse.builder().id("wh1").name(null).build();
    assertThatThrownBy(() -> validator.validateWarehouse(w))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Warehouse name is mandatory");
  }

  @Test
  void validateWarehouse_nameBlank_throws() {
    Warehouse w = Warehouse.builder().id("wh1").name("   ").build();
    assertThatThrownBy(() -> validator.validateWarehouse(w))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Warehouse name is mandatory");
  }

  @Test
  void validateTravelEquipment_null_throws() {
    assertThatThrownBy(() -> validator.validateTravelEquipment(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel equipment cannot be null");
  }

  @Test
  void validateTravelEquipment_travelNull_throws() {
    TravelEquipment te =
        TravelEquipment.builder()
            .travel(null)
            .equipment(Equipment.builder().id("eq1").build())
            .quantity(1)
            .status(TravelEquipment.TransportStatus.IN_PROGRESS)
            .build();
    assertThatThrownBy(() -> validator.validateTravelEquipment(te))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel equipment must be linked to a travel expense");
  }

  @Test
  void validateTravelEquipment_travelWithoutId_throws() {
    TravelEquipment te =
        TravelEquipment.builder()
            .travel(TravelExpense.builder().build())
            .equipment(Equipment.builder().id("eq1").build())
            .quantity(1)
            .status(TravelEquipment.TransportStatus.IN_PROGRESS)
            .build();
    assertThatThrownBy(() -> validator.validateTravelEquipment(te))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel equipment must be linked to a travel expense");
  }

  @Test
  void validateTravelEquipment_equipmentNull_throws() {
    TravelEquipment te =
        TravelEquipment.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .equipment(null)
            .quantity(1)
            .status(TravelEquipment.TransportStatus.IN_PROGRESS)
            .build();
    assertThatThrownBy(() -> validator.validateTravelEquipment(te))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment is mandatory for travel equipment");
  }

  @Test
  void validateTravelEquipment_equipmentWithoutId_throws() {
    TravelEquipment te =
        TravelEquipment.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .equipment(Equipment.builder().build())
            .quantity(1)
            .status(TravelEquipment.TransportStatus.IN_PROGRESS)
            .build();
    assertThatThrownBy(() -> validator.validateTravelEquipment(te))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment is mandatory for travel equipment");
  }

  @Test
  void validateTravelEquipment_valid_doesNotThrow() {
    TravelEquipment te =
        TravelEquipment.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .equipment(Equipment.builder().id("eq1").build())
            .quantity(1)
            .status(TravelEquipment.TransportStatus.IN_PROGRESS)
            .build();
    assertThatCode(() -> validator.validateTravelEquipment(te)).doesNotThrowAnyException();
  }

  @Test
  void validateTravelMaterials_null_throws() {
    assertThatThrownBy(() -> validator.validateTravelMaterials(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel materials cannot be null");
  }

  @Test
  void validateTravelMaterials_travelNull_throws() {
    TravelMaterials tm =
        TravelMaterials.builder()
            .travel(null)
            .material(Material.builder().id("mat1").build())
            .quantity(1)
            .build();
    assertThatThrownBy(() -> validator.validateTravelMaterials(tm))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel materials must be linked to a travel expense");
  }

  @Test
  void validateTravelMaterials_travelWithoutId_throws() {
    TravelMaterials tm =
        TravelMaterials.builder()
            .travel(TravelExpense.builder().build())
            .material(Material.builder().id("mat1").build())
            .quantity(1)
            .build();
    assertThatThrownBy(() -> validator.validateTravelMaterials(tm))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel materials must be linked to a travel expense");
  }

  @Test
  void validateTravelMaterials_materialNull_throws() {
    TravelMaterials tm =
        TravelMaterials.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .material(null)
            .quantity(1)
            .build();
    assertThatThrownBy(() -> validator.validateTravelMaterials(tm))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material is mandatory for travel materials");
  }

  @Test
  void validateTravelMaterials_materialWithoutId_throws() {
    TravelMaterials tm =
        TravelMaterials.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .material(Material.builder().build())
            .quantity(1)
            .build();
    assertThatThrownBy(() -> validator.validateTravelMaterials(tm))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material is mandatory for travel materials");
  }

  @Test
  void validateMaterialWarehouse_null_throws() {
    assertThatThrownBy(() -> validator.validateMaterialWarehouse(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material warehouse cannot be null");
  }

  @Test
  void validateMaterialWarehouse_materialNull_throws() {
    MaterialWarehouse mw =
        MaterialWarehouse.builder()
            .material(null)
            .warehouse(Warehouse.builder().id("wh1").build())
            .quantity(1)
            .build();
    assertThatThrownBy(() -> validator.validateMaterialWarehouse(mw))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material is mandatory for material warehouse");
  }

  @Test
  void validateMaterialWarehouse_materialWithoutId_throws() {
    MaterialWarehouse mw =
        MaterialWarehouse.builder()
            .material(Material.builder().build())
            .warehouse(Warehouse.builder().id("wh1").build())
            .quantity(1)
            .build();
    assertThatThrownBy(() -> validator.validateMaterialWarehouse(mw))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material is mandatory for material warehouse");
  }

  @Test
  void validateMaterialWarehouse_warehouseNull_throws() {
    MaterialWarehouse mw =
        MaterialWarehouse.builder()
            .material(Material.builder().id("mat1").build())
            .warehouse(null)
            .quantity(1)
            .build();
    assertThatThrownBy(() -> validator.validateMaterialWarehouse(mw))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Warehouse is mandatory for material warehouse");
  }

  @Test
  void validateMaterialWarehouse_warehouseWithoutId_throws() {
    MaterialWarehouse mw =
        MaterialWarehouse.builder()
            .material(Material.builder().id("mat1").build())
            .warehouse(Warehouse.builder().build())
            .quantity(1)
            .build();
    assertThatThrownBy(() -> validator.validateMaterialWarehouse(mw))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Warehouse is mandatory for material warehouse");
  }

  @Test
  void validateMaterialWarehouse_valid_doesNotThrow() {
    MaterialWarehouse mw =
        MaterialWarehouse.builder()
            .material(Material.builder().id("mat1").build())
            .warehouse(Warehouse.builder().id("wh1").build())
            .quantity(1)
            .build();
    assertThatCode(() -> validator.validateMaterialWarehouse(mw)).doesNotThrowAnyException();
  }

  @Test
  void validateTravelPeople_null_throws() {
    assertThatThrownBy(() -> validator.validateTravelPeople(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel people cannot be null");
  }

  @Test
  void validateTravelPeople_travelNull_throws() {
    TravelPeople tp =
        TravelPeople.builder()
            .travel(null)
            .user(User.builder().id("user1").build())
            .arrivalLocation(Warehouse.builder().id("wh1").build())
            .build();
    assertThatThrownBy(() -> validator.validateTravelPeople(tp))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel people must be linked to a travel expense");
  }

  @Test
  void validateTravelPeople_travelWithoutId_throws() {
    TravelPeople tp =
        TravelPeople.builder()
            .travel(TravelExpense.builder().build())
            .user(User.builder().id("user1").build())
            .arrivalLocation(Warehouse.builder().id("wh1").build())
            .build();
    assertThatThrownBy(() -> validator.validateTravelPeople(tp))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel people must be linked to a travel expense");
  }

  @Test
  void validateTravelPeople_userNull_throws() {
    TravelPeople tp =
        TravelPeople.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .user(null)
            .arrivalLocation(Warehouse.builder().id("wh1").build())
            .build();
    assertThatThrownBy(() -> validator.validateTravelPeople(tp))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("User is mandatory for travel people");
  }

  @Test
  void validateTravelPeople_userWithoutId_throws() {
    TravelPeople tp =
        TravelPeople.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .user(User.builder().build())
            .arrivalLocation(Warehouse.builder().id("wh1").build())
            .build();
    assertThatThrownBy(() -> validator.validateTravelPeople(tp))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("User is mandatory for travel people");
  }

  @Test
  void validateTravelPeople_valid_doesNotThrow() {
    TravelPeople tp =
        TravelPeople.builder()
            .travel(TravelExpense.builder().id("travel1").build())
            .user(User.builder().id("user1").build())
            .arrivalLocation(Warehouse.builder().id("wh1").build())
            .build();
    assertThatCode(() -> validator.validateTravelPeople(tp)).doesNotThrowAnyException();
  }

  @Test
  void validateTravelPeople_allFieldsInvalid_throws() {
    TravelPeople tp =
        TravelPeople.builder()
            .travel(null)
            .user(null)
            .arrivalLocation(Warehouse.builder().id("wh1").build())
            .build();
    assertThatThrownBy(() -> validator.validateTravelPeople(tp))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Travel people must be linked to a travel expense")
        .hasMessageContaining("User is mandatory for travel people");
  }

  @Test
  void validateMaterialConsumption_null_throws() {
    assertThatThrownBy(() -> validator.validateMaterialConsumption(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material consumption cannot be null");
  }

  @Test
  void validateMaterialConsumption_materialNull_throws() {
    MaterialConsumption mc =
        MaterialConsumption.builder()
            .material(null)
            .warehouse(Warehouse.builder().id("wh1").build())
            .quantity(1)
            .consumptionDate(LocalDate.now())
            .job(Job.builder().id("job1").build())
            .consumptionStatus(MaterialConsumption.ConsumptionStatus.COMPLETED)
            .build();
    assertThatThrownBy(() -> validator.validateMaterialConsumption(mc))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material is mandatory");
  }

  @Test
  void validateMaterialConsumption_materialWithoutId_throws() {
    MaterialConsumption mc =
        MaterialConsumption.builder()
            .material(Material.builder().build())
            .warehouse(Warehouse.builder().id("wh1").build())
            .quantity(1)
            .consumptionDate(LocalDate.now())
            .job(Job.builder().id("job1").build())
            .consumptionStatus(MaterialConsumption.ConsumptionStatus.COMPLETED)
            .build();
    assertThatThrownBy(() -> validator.validateMaterialConsumption(mc))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material is mandatory");
  }

  @Test
  void validateMaterialConsumption_quantityNull_throws() {
    MaterialConsumption mc =
        MaterialConsumption.builder()
            .material(Material.builder().id("mat1").build())
            .warehouse(Warehouse.builder().id("wh1").build())
            .quantity(null)
            .consumptionDate(LocalDate.now())
            .job(Job.builder().id("job1").build())
            .consumptionStatus(MaterialConsumption.ConsumptionStatus.COMPLETED)
            .build();
    assertThatThrownBy(() -> validator.validateMaterialConsumption(mc))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Quantity is mandatory");
  }

  @Test
  void validateMaterialConsumption_valid_doesNotThrow() {
    MaterialConsumption mc =
        MaterialConsumption.builder()
            .material(Material.builder().id("mat1").build())
            .warehouse(Warehouse.builder().id("wh1").build())
            .quantity(5)
            .consumptionDate(LocalDate.now())
            .job(Job.builder().id("job1").build())
            .consumptionStatus(MaterialConsumption.ConsumptionStatus.COMPLETED)
            .build();
    assertThatCode(() -> validator.validateMaterialConsumption(mc)).doesNotThrowAnyException();
  }

  @Test
  void validateMaterialConsumption_allFieldsInvalid_throws() {
    MaterialConsumption mc =
        MaterialConsumption.builder()
            .material(null)
            .warehouse(Warehouse.builder().id("wh1").build())
            .quantity(null)
            .consumptionDate(LocalDate.now())
            .job(Job.builder().id("job1").build())
            .consumptionStatus(MaterialConsumption.ConsumptionStatus.COMPLETED)
            .build();
    assertThatThrownBy(() -> validator.validateMaterialConsumption(mc))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material is mandatory")
        .hasMessageContaining("Quantity is mandatory");
  }

  @Test
  void validateMaterialConsumptions_null_throws() {
    assertThatThrownBy(() -> validator.validateMaterialConsumptions(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material consumption list cannot be null or empty");
  }

  @Test
  void validateMaterialConsumptions_empty_throws() {
    assertThatThrownBy(() -> validator.validateMaterialConsumptions(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Material consumption list cannot be null or empty");
  }

  @Test
  void validateEquipmentUsage_null_throws() {
    assertThatThrownBy(() -> validator.validateEquipmentUsage(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment usage cannot be null");
  }

  @Test
  void validateEquipmentUsage_equipmentNull_throws() {
    EquipmentUsage eu =
        EquipmentUsage.builder()
            .equipment(null)
            .job(Job.builder().id("job1").build())
            .startTime(Instant.now())
            .sourceLocation(Warehouse.builder().id("wh1").build())
            .usageStatus(EquipmentUsage.UsageStatus.IN_USE)
            .usedBy(User.builder().id("user1").build())
            .build();
    assertThatThrownBy(() -> validator.validateEquipmentUsage(eu))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment is mandatory");
  }

  @Test
  void validateEquipmentUsage_equipmentWithoutId_throws() {
    EquipmentUsage eu =
        EquipmentUsage.builder()
            .equipment(Equipment.builder().build())
            .job(Job.builder().id("job1").build())
            .startTime(Instant.now())
            .sourceLocation(Warehouse.builder().id("wh1").build())
            .usageStatus(EquipmentUsage.UsageStatus.IN_USE)
            .usedBy(User.builder().id("user1").build())
            .build();
    assertThatThrownBy(() -> validator.validateEquipmentUsage(eu))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment is mandatory");
  }

  @Test
  void validateEquipmentUsage_valid_doesNotThrow() {
    EquipmentUsage eu =
        EquipmentUsage.builder()
            .equipment(Equipment.builder().id("eq1").build())
            .job(Job.builder().id("job1").build())
            .startTime(Instant.now())
            .sourceLocation(Warehouse.builder().id("wh1").build())
            .usageStatus(EquipmentUsage.UsageStatus.IN_USE)
            .usedBy(User.builder().id("user1").build())
            .build();
    assertThatCode(() -> validator.validateEquipmentUsage(eu)).doesNotThrowAnyException();
  }

  @Test
  void validateEquipmentUsage_allFieldsInvalid_throws() {
    EquipmentUsage eu =
        EquipmentUsage.builder()
            .equipment(null)
            .job(Job.builder().id("job1").build())
            .startTime(Instant.now())
            .sourceLocation(Warehouse.builder().id("wh1").build())
            .usageStatus(EquipmentUsage.UsageStatus.IN_USE)
            .usedBy(User.builder().id("user1").build())
            .build();
    assertThatThrownBy(() -> validator.validateEquipmentUsage(eu))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment is mandatory");
  }

  @Test
  void validateEquipmentUsages_null_throws() {
    assertThatThrownBy(() -> validator.validateEquipmentUsages(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment usage list cannot be null or empty");
  }

  @Test
  void validateEquipmentUsages_empty_throws() {
    assertThatThrownBy(() -> validator.validateEquipmentUsages(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Equipment usage list cannot be null or empty");
  }
}
