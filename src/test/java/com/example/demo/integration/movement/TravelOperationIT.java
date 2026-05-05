package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.TravelEquipmentApi;
import com.example.demo.client.api.TravelExpenseApi;
import com.example.demo.client.api.TravelMaterialsApi;
import com.example.demo.client.api.TravelOperationApi;
import com.example.demo.client.api.TravelPeopleApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.TravelEquipment;
import com.example.demo.client.model.TravelExpense;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.client.model.TravelOperationEquipmentLine;
import com.example.demo.client.model.TravelOperationMaterialLine;
import com.example.demo.client.model.TravelOperationPeopleLine;
import com.example.demo.client.model.TravelOperationRequest;
import com.example.demo.client.model.TravelOperationTravel;
import com.example.demo.client.model.TravelPeople;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
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
@ContextConfiguration(initializers = TravelOperationIT.ContextInitializer.class)
class TravelOperationIT {
  @Autowired private DataSource dataSource;

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
  void employee_can_create_travel_operation_with_mixed_lines() throws Exception {
    TravelOperationApi api = new TravelOperationApi(anApiClient(EMPLOYEE_TOKEN));

    String travelId = "travel_operation_travel_1";
    String travelExpenseId = "travel_operation_travel_expense_1";
    String travelEquipmentId = "travel_operation_travel_equipment_1";
    String travelMaterialId = "travel_operation_travel_material_1";
    String travelPeopleId = "travel_operation_travel_people_1";
    String departureWarehouseId = WAREHOUSE1_ID;
    String arrivalWarehouseId = "travel_operation_arrival_warehouse_1";

    TravelOperationRequest request = new TravelOperationRequest();
    request.setComment("Integration test for travel operation");
    request.setTravel(
        new TravelOperationTravel()
            .id(travelId)
            .expenseId(travelExpenseId)
            .departureLocation(new CrupdateWarehouse().id(departureWarehouseId).name("Warehouse 1"))
            .arrivalLocation(new CrupdateWarehouse().id(arrivalWarehouseId).name("Test Arrival"))
            .departureDate(Instant.parse("2024-05-01T08:00:00Z"))
            .arrivalDate(Instant.parse("2024-05-02T10:00:00Z"))
            .fee(1500));
    request.setEquipmentLines(
        List.of(
            new TravelOperationEquipmentLine()
                .id(travelEquipmentId)
                .equipment(equipmentRef(EQUIPMENT1_ID))
                .comment("Moving equipment 1")));
    request.setMaterialLines(
        List.of(
            new TravelOperationMaterialLine()
                .id(travelMaterialId)
                .material(materialRef(MATERIAL1_ID))
                .quantity(50)
                .comment("Moving 50 units of material 1")));
    request.setPeopleLines(
        List.of(
            new TravelOperationPeopleLine()
                .id(travelPeopleId)
                .userId(USER1_ID)
                .comment("Moving user 1")));

    api.createTravelOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request);

    // Verify TravelExpense
    TravelExpenseApi travelExpenseApi = new TravelExpenseApi(anApiClient(ADMIN_TOKEN));
    List<TravelExpense> createdTravelExpenses =
        travelExpenseApi.getTravelExpenses(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, departureWarehouseId, arrivalWarehouseId);
    assertEquals(1, createdTravelExpenses.size());
    TravelExpense createdTravel = createdTravelExpenses.get(0);
    assertEquals(travelId, createdTravel.getId());
    assertEquals(departureWarehouseId, createdTravel.getDepartureLocation().getId());
    assertEquals(arrivalWarehouseId, createdTravel.getArrivalLocation().getId());
    assertEquals(Instant.parse("2024-05-01T08:00:00Z"), createdTravel.getDepartureDate());
    assertEquals(Instant.parse("2024-05-02T10:00:00Z"), createdTravel.getArrivalDate());

    // Verify TravelEquipment
    TravelEquipmentApi travelEquipmentApi = new TravelEquipmentApi(anApiClient(ADMIN_TOKEN));
    List<TravelEquipment> createdTravelEquipment =
        travelEquipmentApi.getTravelEquipment(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            travelExpenseId,
            1,
            100,
            travelId,
            EQUIPMENT1_ID,
            1,
            null,
            null,
            null,
            null,
            null);
    assertEquals(1, createdTravelEquipment.size());
    assertEquals(travelEquipmentId, createdTravelEquipment.get(0).getId());
    assertEquals(EQUIPMENT1_ID, createdTravelEquipment.get(0).getEquipment().getId());

    // Verify TravelMaterials
    TravelMaterialsApi travelMaterialsApi = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));
    List<TravelMaterials> createdTravelMaterials =
        travelMaterialsApi.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            travelExpenseId,
            1,
            100,
            travelId,
            MATERIAL1_ID,
            50,
            null,
            null,
            null,
            null,
            null);
    assertEquals(1, createdTravelMaterials.size());
    assertEquals(travelMaterialId, createdTravelMaterials.get(0).getId());
    assertEquals(MATERIAL1_ID, createdTravelMaterials.get(0).getMaterial().getId());

    // Verify TravelPeople
    TravelPeopleApi travelPeopleApi = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));
    List<TravelPeople> createdTravelPeople =
        travelPeopleApi.getTravelPeople(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            travelExpenseId,
            1,
            100,
            travelId,
            USER1_ID,
            null,
            null,
            null,
            null);
    assertEquals(1, createdTravelPeople.size());
    assertEquals(travelPeopleId, createdTravelPeople.get(0).getId());
    assertEquals(USER1_ID, createdTravelPeople.get(0).getUser().getId());
  }

  @Test
  @DirtiesContext
  void employee_can_create_travel_operation_without_equipment_and_materials() throws Exception {
    TravelOperationApi api = new TravelOperationApi(anApiClient(EMPLOYEE_TOKEN));

    String travelId = "travel_operation_people_only";
    String travelExpenseId = "travel_operation_people_only_expense";
    String travelPeopleId = "travel_operation_people_only_people";

    TravelOperationRequest request = new TravelOperationRequest();
    request.setTravel(
        new TravelOperationTravel()
            .id(travelId)
            .expenseId(travelExpenseId)
            .departureLocation(new CrupdateWarehouse().id(WAREHOUSE1_ID).name("Wh1"))
            .arrivalLocation(new CrupdateWarehouse().id(WAREHOUSE2_ID).name("Wh2"))
            .departureDate(Instant.parse("2024-06-01T09:00:00Z"))
            .arrivalDate(Instant.parse("2024-06-01T17:00:00Z")));
    request.setPeopleLines(
        List.of(
            new TravelOperationPeopleLine()
                .id(travelPeopleId)
                .userId(USER2_ID)
                .comment("Only moving this person")));

    api.createTravelOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request);

    TravelPeopleApi travelPeopleApi = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));
    List<TravelPeople> createdTravelPeople =
        travelPeopleApi.getTravelPeople(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            travelExpenseId,
            1,
            100,
            travelId,
            USER2_ID,
            null,
            null,
            null,
            null);
    assertEquals(1, createdTravelPeople.size());
    assertEquals(travelPeopleId, createdTravelPeople.get(0).getId());
    assertEquals(USER2_ID, createdTravelPeople.get(0).getUser().getId());

    // Verify no equipment or materials were created for this travel
    TravelEquipmentApi travelEquipmentApi = new TravelEquipmentApi(anApiClient(ADMIN_TOKEN));
    List<TravelEquipment> equipmentList =
        travelEquipmentApi.getTravelEquipment(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            travelExpenseId,
            1,
            100,
            travelId,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    assertTrue(equipmentList.isEmpty());

    TravelMaterialsApi travelMaterialsApi = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));
    List<TravelMaterials> materialsList =
        travelMaterialsApi.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            travelExpenseId,
            1,
            100,
            travelId,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    assertTrue(materialsList.isEmpty());
  }

  @Test
  @DirtiesContext
  void employee_can_create_travel_operation_with_existing_entities() throws Exception {
    TravelOperationApi api = new TravelOperationApi(anApiClient(EMPLOYEE_TOKEN));

    String travelId = "travel_operation_existing_entities";
    String travelExpenseId = "travel_operation_existing_entities_expense";
    String travelEquipmentId = "travel_operation_existing_equipment";
    String travelPeopleId = "travel_operation_existing_people";

    TravelOperationRequest request = new TravelOperationRequest();
    request.setTravel(
        new TravelOperationTravel()
            .id(travelId)
            .expenseId(travelExpenseId)
            .departureLocation(new CrupdateWarehouse().id(WAREHOUSE1_ID).name("Existing Wh1"))
            .arrivalLocation(new CrupdateWarehouse().id(WAREHOUSE2_ID).name("Existing Wh2"))
            .departureDate(Instant.parse("2024-07-01T08:00:00Z"))
            .arrivalDate(Instant.parse("2024-07-02T08:00:00Z")));
    request.setEquipmentLines(
        List.of(
            new TravelOperationEquipmentLine()
                .id(travelEquipmentId)
                .equipment(equipmentRef(EQUIPMENT2_ID))));
    request.setPeopleLines(
        List.of(new TravelOperationPeopleLine().id(travelPeopleId).userId(USER1_ID)));

    api.createTravelOperation(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, request);

    TravelEquipmentApi travelEquipmentApi = new TravelEquipmentApi(anApiClient(ADMIN_TOKEN));
    List<TravelEquipment> equipmentList =
        travelEquipmentApi.getTravelEquipment(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            travelExpenseId,
            1,
            100,
            travelId,
            EQUIPMENT2_ID,
            1,
            null,
            null,
            null,
            null,
            null);
    assertEquals(1, equipmentList.size());
    assertEquals(travelEquipmentId, equipmentList.get(0).getId());

    TravelPeopleApi travelPeopleApi = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));
    List<TravelPeople> peopleList =
        travelPeopleApi.getTravelPeople(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            travelExpenseId,
            1,
            100,
            travelId,
            USER1_ID,
            null,
            null,
            null,
            null);
    assertEquals(1, peopleList.size());
    assertEquals(travelPeopleId, peopleList.get(0).getId());
  }

  @Test
  void employee_cannot_create_travel_operation_for_another_user() throws Exception {
    TravelOperationApi api = new TravelOperationApi(anApiClient(EMPLOYEE_TOKEN));

    TravelOperationRequest request = new TravelOperationRequest();
    request.setTravel(
        new TravelOperationTravel()
            .id("travel_operation_forbidden")
            .expenseId("travel_operation_forbidden_expense")
            .departureLocation(new CrupdateWarehouse().id(WAREHOUSE1_ID).name("Wh1"))
            .arrivalLocation(new CrupdateWarehouse().id(WAREHOUSE2_ID).name("Wh2"))
            .departureDate(Instant.parse("2024-08-01T08:00:00Z"))
            .arrivalDate(Instant.parse("2024-08-02T08:00:00Z")));
    request.setPeopleLines(
        List.of(new TravelOperationPeopleLine().id("tp_forbidden").userId(USER2_ID)));

    // Attempting to create for USER1_ID (another user) should fail
    assertThrowsForbiddenException(
        () -> api.createTravelOperation(COMPANY1_ID, JOB1_ID, USER1_ID, request));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }

  private CrupdateEquipment equipmentRef(String equipmentId) {
    return new CrupdateEquipment().id(equipmentId);
  }

  private CrupdateMaterial materialRef(String materialId) {
    return new CrupdateMaterial().id(materialId);
  }
}
