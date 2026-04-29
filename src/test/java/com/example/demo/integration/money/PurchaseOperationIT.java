package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.EquipmentApi;
import com.example.demo.client.api.ExpenseApi;
import com.example.demo.client.api.MaterialApi;
import com.example.demo.client.api.PurchaseApi;
import com.example.demo.client.api.PurchaseOperationApi;
import com.example.demo.client.api.TravelEquipmentApi;
import com.example.demo.client.api.TravelExpenseApi;
import com.example.demo.client.api.TravelMaterialsApi;
import com.example.demo.client.api.WarehouseApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.Equipment;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.client.model.Material;
import com.example.demo.client.model.Purchase;
import com.example.demo.client.model.PurchaseOperationEquipmentLine;
import com.example.demo.client.model.PurchaseOperationMaterialLine;
import com.example.demo.client.model.PurchaseOperationRequest;
import com.example.demo.client.model.PurchaseOperationTravel;
import com.example.demo.client.model.TransportStatus;
import com.example.demo.client.model.TravelEquipment;
import com.example.demo.client.model.TravelExpense;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import com.example.demo.repository.movement.MaterialWarehouseRepository;
import java.time.Instant;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = PurchaseOperationIT.ContextInitializer.class)
class PurchaseOperationIT {
  @Autowired private DataSource dataSource;
  @Autowired private MaterialWarehouseRepository materialWarehouseRepository;

  @MockitoBean private SentryConf sentryConf;
  @MockitoBean private AuthenticationManager authenticationManagerMock;
  @MockitoBean private JwtUtils jwtServiceMock;

  private static ApiClient anApiClient(String token) {
    return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
  }

  @BeforeEach
  void setUp() throws Exception {
    TestUtils.setUpJwtService(jwtServiceMock);
    TestUtils.setUpAuthenticationManager(authenticationManagerMock);
    TestDataSqlLoader.executeAllSqlScripts(dataSource);
  }

  @Test
  @DirtiesContext
  void employee_can_create_purchase_operation_with_mixed_lines() throws Exception {
    createPurchasableEquipment("purchase_operation_equipment_1", "Perforateur");

    PurchaseOperationApi api = new PurchaseOperationApi(anApiClient(EMPLOYEE_TOKEN));

    PurchaseOperationRequest request = new PurchaseOperationRequest();
    request.setComment("bulk purchase test");
    request.setEquipmentLines(
        List.of(
            new PurchaseOperationEquipmentLine()
                .equipment(equipmentRef("purchase_operation_equipment_1"))
                .expenseId("purchase_operation_equipment_expense_1")
                .purchaseId("purchase_operation_equipment_purchase_1")
                .travelEquipmentId("purchase_operation_travel_equipment_1")
                .unitPrice(7000)));
    request.setMaterialLines(
        List.of(
            new PurchaseOperationMaterialLine()
                .material(materialRef(MATERIAL1_ID))
                .expenseId("purchase_operation_material_expense_1")
                .purchaseId("purchase_operation_material_purchase_1")
                .travelMaterialId("purchase_operation_travel_material_1")
                .quantity(4)
                .unitPrice(1200)));
    request.setTravel(
        new PurchaseOperationTravel()
            .id("purchase_operation_travel_1")
            .expenseId("purchase_operation_travel_expense_1")
            .travelPeopleId("purchase_operation_travel_people_1")
            .departureLocation(
                new CrupdateWarehouse().id(AT_SELLER_WAREHOUSE_ID).name("At Seller Warehouse"))
            .arrivalLocation(
                new CrupdateWarehouse()
                    .id("purchase_operation_arrival_warehouse_1")
                    .name("Arrival Warehouse"))
            .departureDate(Instant.parse("2024-04-01T08:00:00Z"))
            .arrivalDate(Instant.parse("2024-04-01T12:00:00Z"))
            .fee(3500));

    api.createPurchaseOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request);

    PurchaseApi purchaseApi = new PurchaseApi(anApiClient(ADMIN_TOKEN));
    List<Purchase> equipmentPurchases =
        purchaseApi.getPurchases(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            "purchase_operation_equipment_expense_1",
            1,
            100,
            null,
            "warehouse_at_seller_id",
            true);

    assertTrue(
        equipmentPurchases.stream()
            .anyMatch(
                purchase ->
                    "purchase_operation_equipment_purchase_1".equals(purchase.getId())
                        && purchase.getEquipment() != null
                        && "purchase_operation_equipment_1".equals(purchase.getEquipment().getId())
                        && purchase.getExpense() != null
                        && purchase.getExpense().getAmount() == 7000));

    List<Purchase> materialPurchases =
        purchaseApi.getPurchases(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            "purchase_operation_material_expense_1",
            1,
            100,
            null,
            "warehouse_at_seller_id",
            false);

    assertEquals(1, materialPurchases.size());
    assertEquals("purchase_operation_material_purchase_1", materialPurchases.get(0).getId());
    assertEquals(MATERIAL1_ID, materialPurchases.get(0).getMaterial().getId());
    assertEquals(4800, materialPurchases.get(0).getExpense().getAmount());

    ExpenseApi expenseApi = new ExpenseApi(anApiClient(ADMIN_TOKEN));
    List<ExpenseMoney> travelExpensesAsMoney =
        expenseApi.getExpenses(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            1,
            100,
            "Travel expense for purchase operation",
            3500);
    assertEquals(1, travelExpensesAsMoney.size());
    assertEquals("purchase_operation_travel_expense_1", travelExpensesAsMoney.get(0).getId());

    TravelExpenseApi travelExpenseApi = new TravelExpenseApi(anApiClient(ADMIN_TOKEN));
    List<TravelExpense> createdTravelExpenses =
        travelExpenseApi.getTravelExpenses(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            1,
            100,
            "purchase_operation_travel_expense_1",
            AT_SELLER_WAREHOUSE_ID,
            "purchase_operation_arrival_warehouse_1");
    assertTrue(
        createdTravelExpenses.stream()
            .anyMatch(
                travelExpense ->
                    "purchase_operation_travel_1".equals(travelExpense.getId())
                        && "purchase_operation_travel_expense_1"
                            .equals(travelExpense.getExpense().getId())));
    String createdTravelId = "purchase_operation_travel_1";

    TravelEquipmentApi travelEquipmentApi = new TravelEquipmentApi(anApiClient(ADMIN_TOKEN));
    List<TravelEquipment> createdTravelEquipment =
        travelEquipmentApi.getTravelEquipment(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            "purchase_operation_travel_expense_1",
            createdTravelId,
            1,
            100,
            createdTravelId,
            "purchase_operation_equipment_1",
            1,
            TransportStatus.IN_PROGRESS);
    assertEquals(1, createdTravelEquipment.size());
    assertEquals("purchase_operation_travel_equipment_1", createdTravelEquipment.get(0).getId());

    TravelMaterialsApi travelMaterialsApi = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));
    List<TravelMaterials> createdTravelMaterials =
        travelMaterialsApi.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            "purchase_operation_travel_expense_1",
            createdTravelId,
            1,
            100,
            createdTravelId,
            MATERIAL1_ID,
            4,
            0);
    assertEquals(1, createdTravelMaterials.size());
    assertEquals("purchase_operation_travel_material_1", createdTravelMaterials.get(0).getId());

    EquipmentApi equipmentApi = new EquipmentApi(anApiClient(EMPLOYEE_TOKEN));
    Equipment updatedEquipment =
        equipmentApi.getEquipmentById(COMPANY1_ID, "purchase_operation_equipment_1");
    assertNotNull(updatedEquipment.getWarehouse());
    assertEquals(AT_SELLER_WAREHOUSE_ID, updatedEquipment.getWarehouse().getId());
    assertEquals(
        4,
        materialWarehouseRepository
            .findByMaterial_IdAndWarehouse_Id(MATERIAL1_ID, AT_SELLER_WAREHOUSE_ID)
            .orElseThrow()
            .getQuantity());

    WarehouseApi warehouseApi = new WarehouseApi(anApiClient(ADMIN_TOKEN));
    assertEquals(
        "purchase_operation_arrival_warehouse_1",
        warehouseApi
            .getWarehouseById(COMPANY1_ID, "purchase_operation_arrival_warehouse_1")
            .getId());
  }

  @Test
  @DirtiesContext
  void employee_can_create_purchase_operation_without_equipment() throws Exception {
    PurchaseOperationApi api = new PurchaseOperationApi(anApiClient(EMPLOYEE_TOKEN));

    PurchaseOperationRequest request = new PurchaseOperationRequest();
    request.setMaterialLines(
        List.of(
            new PurchaseOperationMaterialLine()
                .material(materialRef("purchase_operation_material_new_1"))
                .expenseId("purchase_operation_material_expense_2")
                .purchaseId("purchase_operation_material_purchase_2")
                .quantity(6)
                .unitPrice(1100)));

    api.createPurchaseOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request);

    MaterialApi materialApi = new MaterialApi(anApiClient(WAREHOUSE_TOKEN));
    Material createdMaterial = materialApi.getMaterialById("purchase_operation_material_new_1");
    assertEquals("purchase_operation_material_new_1", createdMaterial.getId());

    assertEquals(
        6,
        materialWarehouseRepository
            .findByMaterial_IdAndWarehouse_Id(
                "purchase_operation_material_new_1", AT_SELLER_WAREHOUSE_ID)
            .orElseThrow()
            .getQuantity());
  }

  @Test
  @DirtiesContext
  void employee_can_create_purchase_operation_with_identifier_only_travel() throws Exception {
    createPurchasableEquipment("purchase_operation_equipment_2", "Coupe-bordure");

    PurchaseOperationApi api = new PurchaseOperationApi(anApiClient(EMPLOYEE_TOKEN));

    PurchaseOperationRequest request = new PurchaseOperationRequest();
    request.setComment("auto travel test");
    request.setEquipmentLines(
        List.of(
            new PurchaseOperationEquipmentLine()
                .equipment(equipmentRef("purchase_operation_equipment_2"))
                .expenseId("purchase_operation_equipment_expense_2")
                .purchaseId("purchase_operation_equipment_purchase_2")
                .travelEquipmentId("purchase_operation_travel_equipment_2")
                .unitPrice(7000)));
    request.setMaterialLines(
        List.of(
            new PurchaseOperationMaterialLine()
                .material(materialRef(MATERIAL1_ID))
                .expenseId("purchase_operation_material_expense_3")
                .purchaseId("purchase_operation_material_purchase_3")
                .travelMaterialId("purchase_operation_travel_material_2")
                .quantity(4)
                .unitPrice(1200)));
    request.setTravel(
        new PurchaseOperationTravel()
            .id("purchase_operation_travel_2")
            .expenseId("purchase_operation_travel_expense_2")
            .travelPeopleId("purchase_operation_travel_people_2")
            .departureLocation(
                new CrupdateWarehouse().id(AT_SELLER_WAREHOUSE_ID).name("At Seller Warehouse"))
            .arrivalLocation(new CrupdateWarehouse().id(WAREHOUSE1_ID).name("Warehouse 1"))
            .departureDate(Instant.parse("2024-04-03T08:00:00Z"))
            .arrivalDate(Instant.parse("2024-04-03T10:00:00Z")));

    api.createPurchaseOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request);

    TravelEquipmentApi travelEquipmentApi = new TravelEquipmentApi(anApiClient(ADMIN_TOKEN));
    List<TravelEquipment> createdTravelEquipment =
        travelEquipmentApi.getTravelEquipment(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            "purchase_operation_travel_expense_2",
            "purchase_operation_travel_2",
            1,
            100,
            "purchase_operation_travel_2",
            "purchase_operation_equipment_2",
            1,
            null);
    assertEquals(1, createdTravelEquipment.size());
    assertEquals("purchase_operation_travel_equipment_2", createdTravelEquipment.get(0).getId());

    TravelMaterialsApi travelMaterialsApi = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));
    List<TravelMaterials> createdTravelMaterials =
        travelMaterialsApi.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            "purchase_operation_travel_expense_2",
            "purchase_operation_travel_2",
            1,
            100,
            "purchase_operation_travel_2",
            MATERIAL1_ID,
            4,
            null);
    assertEquals(1, createdTravelMaterials.size());
    assertEquals("purchase_operation_travel_material_2", createdTravelMaterials.get(0).getId());
  }

  @Test
  @DirtiesContext
  void employee_can_create_purchase_operation_with_multiple_equipments() throws Exception {
    createPurchasableEquipment("purchase_operation_equipment_3", "Scie circulaire");
    createPurchasableEquipment("purchase_operation_equipment_4", "Laser de mesure");

    PurchaseOperationApi api = new PurchaseOperationApi(anApiClient(EMPLOYEE_TOKEN));

    PurchaseOperationRequest request = new PurchaseOperationRequest();
    request.setEquipmentLines(
        List.of(
            new PurchaseOperationEquipmentLine()
                .equipment(equipmentRef("purchase_operation_equipment_3"))
                .expenseId("purchase_operation_equipment_expense_3")
                .purchaseId("purchase_operation_equipment_purchase_3")
                .travelEquipmentId("purchase_operation_travel_equipment_3")
                .unitPrice(7000),
            new PurchaseOperationEquipmentLine()
                .equipment(equipmentRef("purchase_operation_equipment_4"))
                .expenseId("purchase_operation_equipment_expense_4")
                .purchaseId("purchase_operation_equipment_purchase_4")
                .travelEquipmentId("purchase_operation_travel_equipment_4")
                .unitPrice(9000)));
    request.setTravel(
        new PurchaseOperationTravel()
            .id("purchase_operation_travel_3")
            .expenseId("purchase_operation_travel_expense_3")
            .travelPeopleId("purchase_operation_travel_people_3")
            .departureLocation(new CrupdateWarehouse().id(WAREHOUSE1_ID).name("Warehouse 1"))
            .arrivalLocation(new CrupdateWarehouse().id(WAREHOUSE2_ID).name("Warehouse 2"))
            .departureDate(Instant.parse("2024-04-02T08:00:00Z"))
            .arrivalDate(Instant.parse("2024-04-02T18:00:00Z"))
            .fee(2500));

    api.createPurchaseOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request);

    EquipmentApi equipmentApi = new EquipmentApi(anApiClient(EMPLOYEE_TOKEN));
    assertEquals(
        ROUTE_WAREHOUSE_ID,
        equipmentApi
            .getEquipmentById(COMPANY1_ID, "purchase_operation_equipment_3")
            .getWarehouse()
            .getId());
    assertEquals(
        ROUTE_WAREHOUSE_ID,
        equipmentApi
            .getEquipmentById(COMPANY1_ID, "purchase_operation_equipment_4")
            .getWarehouse()
            .getId());
  }

  @Test
  void employee_cannot_purchase_equipment_that_is_already_in_stock() {
    PurchaseOperationApi api = new PurchaseOperationApi(anApiClient(EMPLOYEE_TOKEN));

    PurchaseOperationRequest request = new PurchaseOperationRequest();
    request.setEquipmentLines(
        List.of(
            new PurchaseOperationEquipmentLine()
                .equipment(equipmentRef(EQUIPMENT1_ID))
                .expenseId("purchase_operation_equipment_expense_conflict")
                .purchaseId("purchase_operation_equipment_purchase_conflict")
                .unitPrice(7000)));

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"equipment already has a warehouse assigned\"}",
        () -> api.createPurchaseOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request));
  }

  @Test
  @DirtiesContext
  void employee_cannot_create_purchase_operation_for_another_user() throws Exception {
    createPurchasableEquipment("purchase_operation_equipment_forbidden", "Niveau laser");

    PurchaseOperationApi api = new PurchaseOperationApi(anApiClient(EMPLOYEE_TOKEN));

    PurchaseOperationRequest request = new PurchaseOperationRequest();
    request.setEquipmentLines(
        List.of(
            new PurchaseOperationEquipmentLine()
                .equipment(equipmentRef("purchase_operation_equipment_forbidden"))
                .expenseId("purchase_operation_equipment_expense_forbidden")
                .purchaseId("purchase_operation_equipment_purchase_forbidden")
                .unitPrice(5000)));

    assertThrowsForbiddenException(
        () -> api.createPurchaseOperation(COMPANY1_ID, JOB1_ID, USER1_ID, request));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }

  private void createPurchasableEquipment(String equipmentId, String name) throws Exception {
    EquipmentApi equipmentApi = new EquipmentApi(anApiClient(ADMINISTRATION_TOKEN));
    CrupdateEquipment equipment = new CrupdateEquipment();
    equipment.setId(equipmentId);
    equipment.setName(name);
    equipment.setDescription(name + " a acheter");
    equipment.setWarehouseId(null);
    equipment.setFloorNumber(1);
    equipment.setStorageNumber(1);
    equipmentApi.crupdateEquipment(COMPANY1_ID, List.of(equipment));
  }

  private CrupdateEquipment equipmentRef(String equipmentId) {
    return new CrupdateEquipment().id(equipmentId);
  }

  private CrupdateMaterial materialRef(String materialId) {
    return new CrupdateMaterial().id(materialId);
  }
}
