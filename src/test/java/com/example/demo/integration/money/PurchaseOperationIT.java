package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.EquipmentApi;
import com.example.demo.client.api.ExpenseApi;
import com.example.demo.client.api.PurchaseApi;
import com.example.demo.client.api.PurchaseOperationApi;
import com.example.demo.client.api.TravelEquipmentApi;
import com.example.demo.client.api.TravelExpenseApi;
import com.example.demo.client.api.TravelMaterialsApi;
import com.example.demo.client.api.TravelPeopleApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.Equipment;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.client.model.Purchase;
import com.example.demo.client.model.PurchaseOperationEquipmentLine;
import com.example.demo.client.model.PurchaseOperationMaterialLine;
import com.example.demo.client.model.PurchaseOperationRequest;
import com.example.demo.client.model.PurchaseOperationTravel;
import com.example.demo.client.model.TransportStatus;
import com.example.demo.client.model.TravelEquipment;
import com.example.demo.client.model.TravelExpense;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.client.model.TravelPeople;
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
    PurchaseOperationApi api = new PurchaseOperationApi(anApiClient(EMPLOYEE_TOKEN));

    PurchaseOperationRequest request = new PurchaseOperationRequest();
    request.setSupplierId(USER1_ID);
    request.setComment("bulk purchase test");
    request.setEquipmentLines(
        List.of(new PurchaseOperationEquipmentLine().equipmentId(EQUIPMENT1_ID).unitPrice(7000)));
    request.setMaterialLines(
        List.of(
            new PurchaseOperationMaterialLine()
                .materialId(MATERIAL1_ID)
                .quantity(4)
                .unitPrice(1200)));
    request.setTravel(
        new PurchaseOperationTravel()
            .departureLocationId(AT_SELLER_WAREHOUSE_ID)
            .arrivalLocationId(WAREHOUSE1_ID)
            .departureDate(Instant.parse("2024-04-01T08:00:00Z"))
            .arrivalDate(Instant.parse("2024-04-01T12:00:00Z"))
            .fee(3500));

    api.createPurchaseOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request);

    PurchaseApi purchaseApi = new PurchaseApi(anApiClient(ADMIN_TOKEN));
    List<Purchase> purchases =
        purchaseApi.getPurchases(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, 1, 100, null, USER1_ID, null);
    assertEquals(3, purchases.size());
    assertTrue(
        purchases.stream()
            .anyMatch(
                purchase ->
                    Boolean.TRUE.equals(purchase.getIsEquipment())
                        && purchase.getEquipment() != null
                        && EQUIPMENT1_ID.equals(purchase.getEquipment().getId())
                        && purchase.getMaterial() == null
                        && USER1_ID.equals(purchase.getSupplierId())
                        && purchase.getQuantity() == 1
                        && purchase.getExpense() != null
                        && purchase.getExpense().getAmount() == 7000));
    assertTrue(
        purchases.stream()
            .anyMatch(
                purchase ->
                    Boolean.FALSE.equals(purchase.getIsEquipment())
                        && purchase.getMaterial() != null
                        && MATERIAL1_ID.equals(purchase.getMaterial().getId())
                        && purchase.getEquipment() == null
                        && USER1_ID.equals(purchase.getSupplierId())
                        && purchase.getQuantity() == 4
                        && purchase.getExpense() != null
                        && purchase.getExpense().getAmount() == 4800));

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

    TravelExpenseApi travelExpenseApi = new TravelExpenseApi(anApiClient(ADMIN_TOKEN));
    List<TravelExpense> createdTravelExpenses =
        travelExpenseApi.getTravelExpenses(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            1,
            100,
            null,
            AT_SELLER_WAREHOUSE_ID,
            WAREHOUSE1_ID);
    assertEquals(1, createdTravelExpenses.size());
    String createdTravelId = createdTravelExpenses.get(0).getId();

    TravelPeopleApi travelPeopleApi = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));
    List<TravelPeople> createdTravelPeople =
        travelPeopleApi.getTravelPeople(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            createdTravelId,
            1,
            100,
            createdTravelId,
            EMPLOYEE_ID);
    assertEquals(1, createdTravelPeople.size());

    TravelEquipmentApi travelEquipmentApi = new TravelEquipmentApi(anApiClient(ADMIN_TOKEN));
    List<TravelEquipment> createdTravelEquipment =
        travelEquipmentApi.getTravelEquipment(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            createdTravelId,
            1,
            100,
            createdTravelId,
            EQUIPMENT1_ID,
            1,
            TransportStatus.IN_PROGRESS);
    assertEquals(1, createdTravelEquipment.size());

    TravelMaterialsApi travelMaterialsApi = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));
    List<TravelMaterials> createdTravelMaterials =
        travelMaterialsApi.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            createdTravelId,
            1,
            100,
            createdTravelId,
            MATERIAL1_ID,
            4,
            0);
    assertEquals(1, createdTravelMaterials.size());

    EquipmentApi equipmentApi = new EquipmentApi(anApiClient(EMPLOYEE_TOKEN));
    Equipment updatedEquipment = equipmentApi.getEquipmentById(COMPANY1_ID, EQUIPMENT1_ID);
    assertNotNull(updatedEquipment.getWarehouse());
    assertEquals(AT_SELLER_WAREHOUSE_ID, updatedEquipment.getWarehouse().getId());
    assertEquals(
        4,
        materialWarehouseRepository
            .findByMaterial_IdAndWarehouse_Id(MATERIAL1_ID, AT_SELLER_WAREHOUSE_ID)
            .orElseThrow()
            .getQuantity());
  }

  @Test
  @DirtiesContext
  void employee_can_create_purchase_operation_without_equipment() throws Exception {
    PurchaseOperationApi api = new PurchaseOperationApi(anApiClient(EMPLOYEE_TOKEN));

    PurchaseOperationRequest request = new PurchaseOperationRequest();
    request.setSupplierId(USER2_ID);
    request.setMaterialLines(
        List.of(
            new PurchaseOperationMaterialLine()
                .materialId(MATERIAL2_ID)
                .quantity(6)
                .unitPrice(1100)));

    api.createPurchaseOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request);

    assertEquals(
        6,
        materialWarehouseRepository
            .findByMaterial_IdAndWarehouse_Id(MATERIAL2_ID, AT_SELLER_WAREHOUSE_ID)
            .orElseThrow()
            .getQuantity());
  }

  @Test
  @DirtiesContext
  void employee_can_create_purchase_operation_with_multiple_equipments() throws Exception {
    PurchaseOperationApi api = new PurchaseOperationApi(anApiClient(EMPLOYEE_TOKEN));

    PurchaseOperationRequest request = new PurchaseOperationRequest();
    request.setSupplierId(USER1_ID);
    request.setEquipmentLines(
        List.of(
            new PurchaseOperationEquipmentLine().equipmentId(EQUIPMENT1_ID).unitPrice(7000),
            new PurchaseOperationEquipmentLine().equipmentId(EQUIPMENT2_ID).unitPrice(9000)));
    request.setTravel(
        new PurchaseOperationTravel()
            .departureLocationId(WAREHOUSE1_ID)
            .arrivalLocationId(WAREHOUSE2_ID)
            .departureDate(Instant.parse("2024-04-02T08:00:00Z"))
            .arrivalDate(Instant.parse("2024-04-02T18:00:00Z"))
            .fee(2500));

    api.createPurchaseOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request);

    EquipmentApi equipmentApi = new EquipmentApi(anApiClient(EMPLOYEE_TOKEN));
    assertEquals(
        ROUTE_WAREHOUSE_ID,
        equipmentApi.getEquipmentById(COMPANY1_ID, EQUIPMENT1_ID).getWarehouse().getId());
    assertEquals(
        ROUTE_WAREHOUSE_ID,
        equipmentApi.getEquipmentById(COMPANY1_ID, EQUIPMENT2_ID).getWarehouse().getId());
  }

  @Test
  void employee_cannot_create_purchase_operation_for_another_user() {
    PurchaseOperationApi api = new PurchaseOperationApi(anApiClient(EMPLOYEE_TOKEN));

    PurchaseOperationRequest request = new PurchaseOperationRequest();
    request.setSupplierId(USER1_ID);
    request.setEquipmentLines(
        List.of(new PurchaseOperationEquipmentLine().equipmentId(EQUIPMENT1_ID).unitPrice(5000)));

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
}
