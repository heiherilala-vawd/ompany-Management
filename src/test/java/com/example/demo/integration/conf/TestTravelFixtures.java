package com.example.demo.integration.conf;

import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.CrupdateTravelEquipment;
import com.example.demo.client.model.CrupdateTravelExpense;
import com.example.demo.client.model.CrupdateTravelMaterials;
import com.example.demo.client.model.CrupdateTravelPeople;
import com.example.demo.client.model.TransportStatus;
import com.example.demo.client.model.TravelEquipment;
import com.example.demo.client.model.TravelExpense;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.client.model.TravelPeople;
import com.example.demo.client.model.User;
import java.time.Instant;
import java.util.UUID;

final class TestTravelFixtures {

  private TestTravelFixtures() {}

  static TravelExpense travelExpense1() {
    TravelExpense travelExpense = new TravelExpense();
    travelExpense.setId(TestUtils.TRAVEL_EXPENSE1_ID);
    travelExpense.setExpense(
        TestMoneyFixtures.expenseToCrupdateExpense(TestMoneyFixtures.expense1()));
    travelExpense.setDepartureLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(
            TestOrganizationFixtures.warehouse1()));
    travelExpense.setArrivalLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(routeWarehouse()));
    travelExpense.setDepartureDate(Instant.parse("2024-03-01T06:00:00Z"));
    travelExpense.setArrivalDate(Instant.parse("2024-03-01T12:00:00Z"));
    return travelExpense;
  }

  static TravelExpense travelExpense2() {
    TravelExpense travelExpense = new TravelExpense();
    travelExpense.setId(TestUtils.TRAVEL_EXPENSE2_ID);
    travelExpense.setExpense(
        TestMoneyFixtures.expenseToCrupdateExpense(TestMoneyFixtures.expense2()));
    travelExpense.setDepartureLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(
            TestOrganizationFixtures.warehouse2()));
    travelExpense.setArrivalLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(
            TestOrganizationFixtures.warehouse1()));
    travelExpense.setDepartureDate(Instant.parse("2024-03-05T07:30:00Z"));
    travelExpense.setArrivalDate(Instant.parse("2024-03-05T15:00:00Z"));
    return travelExpense;
  }

  static CrupdateTravelExpense travelExpenseToCrupdateTravelExpense(TravelExpense travelExpense) {
    CrupdateTravelExpense crupdateTravelExpense = new CrupdateTravelExpense();
    crupdateTravelExpense.setId(travelExpense.getId());
    crupdateTravelExpense.setExpense(travelExpense.getExpense());
    crupdateTravelExpense.setDepartureLocation(travelExpense.getDepartureLocation());
    crupdateTravelExpense.setArrivalLocation(travelExpense.getArrivalLocation());
    crupdateTravelExpense.setDepartureDate(travelExpense.getDepartureDate());
    crupdateTravelExpense.setArrivalDate(travelExpense.getArrivalDate());
    return crupdateTravelExpense;
  }

  static CrupdateTravelExpense someCreatableTravelExpense() {
    CrupdateTravelExpense travelExpense = new CrupdateTravelExpense();
    travelExpense.setId(UUID.randomUUID().toString());
    CrupdateExpenseMoney expense = new CrupdateExpenseMoney();
    expense.setId(TestUtils.EXPENSE1_ID);
    expense.setAmount(1000);
    expense.setDescription("Test travel expense");
    expense.setJobId(TestUtils.JOB1_ID);
    travelExpense.setExpense(expense);
    travelExpense.setDepartureLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(routeWarehouse()));
    travelExpense.setArrivalLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(
            TestOrganizationFixtures.warehouse1()));
    travelExpense.setDepartureDate(Instant.parse("2024-04-01T08:00:00Z"));
    travelExpense.setArrivalDate(Instant.parse("2024-04-01T18:00:00Z"));
    return travelExpense;
  }

  private static com.example.demo.client.model.Warehouse routeWarehouse() {
    com.example.demo.client.model.Warehouse warehouse =
        new com.example.demo.client.model.Warehouse();
    warehouse.setId(TestUtils.ROUTE_WAREHOUSE_ID);
    warehouse.setName("En route");
    warehouse.setDescription("Emplacement virtuel pour les équipements en déplacement");
    return warehouse;
  }

  static TravelPeople travelPeople1() {
    TravelPeople tp = new TravelPeople();
    tp.setId(TestUtils.TRAVEL_PEOPLE1_ID);
    tp.setTravel(travelExpenseToCrupdateTravelExpense(travelExpense1()));
    User user = new User();
    user.setId(TestUtils.EMPLOYEE_ID);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("employee@hei.school");
    tp.setUser(user);
    tp.setArrivalLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(
            TestOrganizationFixtures.warehouse1()));
    tp.setArrivalDate(Instant.parse("2024-03-01T12:00:00Z"));
    return tp;
  }

  static TravelPeople travelPeople2() {
    TravelPeople tp = new TravelPeople();
    tp.setId(TestUtils.TRAVEL_PEOPLE2_ID);
    tp.setTravel(travelExpenseToCrupdateTravelExpense(travelExpense1()));
    User user = new User();
    user.setId(TestUtils.EMPLOYEE_ID);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("employee@hei.school");
    tp.setUser(user);
    tp.setArrivalLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(
            TestOrganizationFixtures.warehouse2()));
    tp.setArrivalDate(Instant.parse("2024-03-05T15:00:00Z"));
    return tp;
  }

  static CrupdateTravelPeople travelPeopleToCrupdateTravelPeople(TravelPeople travelPeople) {
    CrupdateTravelPeople crupdateTravelPeople = new CrupdateTravelPeople();
    crupdateTravelPeople.setId(travelPeople.getId());
    crupdateTravelPeople.setTravelId(
        travelPeople.getTravel() != null ? travelPeople.getTravel().getId() : null);
    crupdateTravelPeople.setUserId(
        travelPeople.getUser() != null ? travelPeople.getUser().getId() : null);
    crupdateTravelPeople.setComment(travelPeople.getComment());
    crupdateTravelPeople.setArrivalLocation(
        travelPeople.getArrivalLocation() != null
            ? travelPeople.getArrivalLocation().getId()
            : null);
    crupdateTravelPeople.setArrivalDate(travelPeople.getArrivalDate());
    return crupdateTravelPeople;
  }

  static CrupdateTravelPeople someCreatableTravelPeople() {
    CrupdateTravelPeople crupdateTravelPeople = new CrupdateTravelPeople();
    crupdateTravelPeople.setId(UUID.randomUUID().toString());
    crupdateTravelPeople.setTravelId(TestUtils.TRAVEL_EXPENSE1_ID);
    crupdateTravelPeople.setUserId(TestUtils.EMPLOYEE_ID);
    crupdateTravelPeople.setArrivalLocation(TestUtils.WAREHOUSE1_ID);
    crupdateTravelPeople.setArrivalDate(Instant.parse("2024-04-01T18:00:00Z"));
    return crupdateTravelPeople;
  }

  static TravelMaterials travelMaterials1() {
    TravelMaterials travelMaterials = new TravelMaterials();
    travelMaterials.setId(TestUtils.TRAVEL_MATERIALS1_ID);
    travelMaterials.setTravel(travelExpenseToCrupdateTravelExpense(travelExpense1()));
    travelMaterials.setMaterial(
        TestOrganizationFixtures.materialToCrupdateMaterial(TestOrganizationFixtures.material1()));
    travelMaterials.setQuantity(10);
    travelMaterials.setQuantityReceived(5);
    travelMaterials.setArrivalLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(
            TestOrganizationFixtures.warehouse1()));
    travelMaterials.setArrivalDate(Instant.parse("2024-03-01T12:00:00Z"));
    return travelMaterials;
  }

  static TravelMaterials travelMaterials2() {
    TravelMaterials travelMaterials = new TravelMaterials();
    travelMaterials.setId(TestUtils.TRAVEL_MATERIALS2_ID);
    travelMaterials.setTravel(travelExpenseToCrupdateTravelExpense(travelExpense2()));
    travelMaterials.setMaterial(
        TestOrganizationFixtures.materialToCrupdateMaterial(TestOrganizationFixtures.material2()));
    travelMaterials.setQuantity(20);
    travelMaterials.setQuantityReceived(null);
    travelMaterials.setArrivalLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(
            TestOrganizationFixtures.warehouse2()));
    travelMaterials.setArrivalDate(Instant.parse("2024-03-05T15:00:00Z"));
    return travelMaterials;
  }

  static CrupdateTravelMaterials travelMaterialsToCrupdateTravelMaterials(
      TravelMaterials travelMaterials) {
    CrupdateTravelMaterials crupdateTravelMaterials = new CrupdateTravelMaterials();
    crupdateTravelMaterials.setId(travelMaterials.getId());
    crupdateTravelMaterials.setTravelId(
        travelMaterials.getTravel() != null ? travelMaterials.getTravel().getId() : null);
    crupdateTravelMaterials.setMaterial(
        travelMaterials.getMaterial() != null ? travelMaterials.getMaterial().getId() : null);
    crupdateTravelMaterials.setQuantity(travelMaterials.getQuantity());
    crupdateTravelMaterials.setQuantityReceived(travelMaterials.getQuantityReceived());
    crupdateTravelMaterials.setComment(travelMaterials.getComment());
    crupdateTravelMaterials.setArrivalLocation(
        travelMaterials.getArrivalLocation() != null
            ? travelMaterials.getArrivalLocation().getId()
            : null);
    crupdateTravelMaterials.setArrivalDate(travelMaterials.getArrivalDate());
    return crupdateTravelMaterials;
  }

  static CrupdateTravelMaterials someCreatableTravelMaterials() {
    CrupdateTravelMaterials crupdateTravelMaterials = new CrupdateTravelMaterials();
    crupdateTravelMaterials.setId(UUID.randomUUID().toString());
    crupdateTravelMaterials.setTravelId(TestUtils.TRAVEL_EXPENSE1_ID);
    crupdateTravelMaterials.setMaterial(TestUtils.MATERIAL3_ID);
    crupdateTravelMaterials.setQuantity(3);
    crupdateTravelMaterials.setQuantityReceived(0);
    crupdateTravelMaterials.setArrivalLocation(TestUtils.WAREHOUSE1_ID);
    crupdateTravelMaterials.setArrivalDate(Instant.parse("2024-04-01T18:00:00Z"));
    return crupdateTravelMaterials;
  }

  static TravelEquipment travelEquipment1() {
    TravelEquipment travelEquipment = new TravelEquipment();
    travelEquipment.setId(TestUtils.TRAVEL_EQUIPMENT1_ID);
    travelEquipment.setTravel(travelExpenseToCrupdateTravelExpense(travelExpense1()));
    travelEquipment.setEquipment(
        TestOrganizationFixtures.equipmentToCrupdateEquipment(
            TestOrganizationFixtures.equipment1()));
    travelEquipment.setQuantity(2);
    travelEquipment.setStatus(TransportStatus.IN_PROGRESS);
    travelEquipment.setArrivalLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(
            TestOrganizationFixtures.warehouse1()));
    travelEquipment.setArrivalDate(Instant.parse("2024-03-01T12:00:00Z"));
    return travelEquipment;
  }

  static TravelEquipment travelEquipment2() {
    TravelEquipment travelEquipment = new TravelEquipment();
    travelEquipment.setId(TestUtils.TRAVEL_EQUIPMENT2_ID);
    travelEquipment.setTravel(travelExpenseToCrupdateTravelExpense(travelExpense2()));
    travelEquipment.setEquipment(
        TestOrganizationFixtures.equipmentToCrupdateEquipment(
            TestOrganizationFixtures.equipment2()));
    travelEquipment.setQuantity(1);
    travelEquipment.setStatus(TransportStatus.ARRIVED);
    travelEquipment.setArrivalLocation(
        TestOrganizationFixtures.warehouseToCrupdateWarehouse(
            TestOrganizationFixtures.warehouse2()));
    travelEquipment.setArrivalDate(Instant.parse("2024-03-05T15:00:00Z"));
    return travelEquipment;
  }

  static CrupdateTravelEquipment travelEquipmentToCrupdateTravelEquipment(
      TravelEquipment travelEquipment) {
    CrupdateTravelEquipment crupdateTravelEquipment = new CrupdateTravelEquipment();
    crupdateTravelEquipment.setId(travelEquipment.getId());
    crupdateTravelEquipment.setTravelId(
        travelEquipment.getTravel() != null ? travelEquipment.getTravel().getId() : null);
    crupdateTravelEquipment.setEquipment(
        travelEquipment.getEquipment() != null ? travelEquipment.getEquipment().getId() : null);
    crupdateTravelEquipment.setQuantity(travelEquipment.getQuantity());
    crupdateTravelEquipment.setStatus(travelEquipment.getStatus());
    crupdateTravelEquipment.setComment(travelEquipment.getComment());
    crupdateTravelEquipment.setArrivalLocation(
        travelEquipment.getArrivalLocation() != null
            ? travelEquipment.getArrivalLocation().getId()
            : null);
    crupdateTravelEquipment.setArrivalDate(travelEquipment.getArrivalDate());
    return crupdateTravelEquipment;
  }

  static CrupdateTravelEquipment someCreatableTravelEquipment() {
    CrupdateTravelEquipment crupdateTravelEquipment = new CrupdateTravelEquipment();
    crupdateTravelEquipment.setId(UUID.randomUUID().toString());
    crupdateTravelEquipment.setTravelId(TestUtils.TRAVEL_EXPENSE1_ID);
    crupdateTravelEquipment.setEquipment(TestUtils.EQUIPMENT3_ID);
    crupdateTravelEquipment.setQuantity(1);
    crupdateTravelEquipment.setStatus(TransportStatus.LOST);
    crupdateTravelEquipment.setArrivalLocation(TestUtils.WAREHOUSE1_ID);
    crupdateTravelEquipment.setArrivalDate(Instant.parse("2024-04-01T18:00:00Z"));
    return crupdateTravelEquipment;
  }
}
