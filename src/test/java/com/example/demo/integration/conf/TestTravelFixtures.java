package com.example.demo.integration.conf;

import com.example.demo.client.model.CrupdateTravelEquipment;
import com.example.demo.client.model.CrupdateTravelExpense;
import com.example.demo.client.model.CrupdateTravelMaterials;
import com.example.demo.client.model.CrupdateTravelPeople;
import com.example.demo.client.model.TransportStatus;
import com.example.demo.client.model.TravelEquipment;
import com.example.demo.client.model.TravelExpense;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.client.model.TravelPeople;
import java.time.Instant;
import java.util.UUID;

final class TestTravelFixtures {

  private TestTravelFixtures() {}

  static TravelExpense travelExpense1() {
    TravelExpense travelExpense = new TravelExpense();
    travelExpense.setId(TestUtils.TRAVEL_EXPENSE1_ID);
    travelExpense.setExpenseId(TestUtils.EXPENSE1_ID);
    travelExpense.setDepartureLocation("Antananarivo");
    travelExpense.setArrivalLocation("Toamasina");
    travelExpense.setDepartureDate(Instant.parse("2024-03-01T06:00:00Z"));
    travelExpense.setArrivalDate(Instant.parse("2024-03-01T12:00:00Z"));
    return travelExpense;
  }

  static TravelExpense travelExpense2() {
    TravelExpense travelExpense = new TravelExpense();
    travelExpense.setId(TestUtils.TRAVEL_EXPENSE2_ID);
    travelExpense.setExpenseId(TestUtils.EXPENSE2_ID);
    travelExpense.setDepartureLocation("Fianarantsoa");
    travelExpense.setArrivalLocation("Antsirabe");
    travelExpense.setDepartureDate(Instant.parse("2024-03-05T07:30:00Z"));
    travelExpense.setArrivalDate(Instant.parse("2024-03-05T15:00:00Z"));
    return travelExpense;
  }

  static CrupdateTravelExpense travelExpenseToCrupdateTravelExpense(TravelExpense travelExpense) {
    CrupdateTravelExpense crupdateTravelExpense = new CrupdateTravelExpense();
    crupdateTravelExpense.setId(travelExpense.getId());
    crupdateTravelExpense.setExpenseId(travelExpense.getExpenseId());
    crupdateTravelExpense.setDepartureLocation(travelExpense.getDepartureLocation());
    crupdateTravelExpense.setArrivalLocation(travelExpense.getArrivalLocation());
    crupdateTravelExpense.setDepartureDate(travelExpense.getDepartureDate());
    crupdateTravelExpense.setArrivalDate(travelExpense.getArrivalDate());
    return crupdateTravelExpense;
  }

  static CrupdateTravelExpense someCreatableTravelExpense() {
    CrupdateTravelExpense travelExpense = new CrupdateTravelExpense();
    travelExpense.setId(UUID.randomUUID().toString());
    travelExpense.setExpenseId(TestUtils.EXPENSE1_ID);
    travelExpense.setDepartureLocation("Mahajanga");
    travelExpense.setArrivalLocation("Antananarivo");
    travelExpense.setDepartureDate(Instant.parse("2024-04-01T08:00:00Z"));
    travelExpense.setArrivalDate(Instant.parse("2024-04-01T18:00:00Z"));
    return travelExpense;
  }

  static TravelPeople travelPeople1() {
    TravelPeople tp = new TravelPeople();
    tp.setId(TestUtils.TRAVEL_PEOPLE1_ID);
    tp.setTravelId(TestUtils.TRAVEL_EXPENSE1_ID);
    tp.setPersonName("Alice Martin");
    return tp;
  }

  static TravelPeople travelPeople2() {
    TravelPeople tp = new TravelPeople();
    tp.setId(TestUtils.TRAVEL_PEOPLE2_ID);
    tp.setTravelId(TestUtils.TRAVEL_EXPENSE1_ID);
    tp.setPersonName("Bob Dupont");
    return tp;
  }

  static CrupdateTravelPeople travelPeopleToCrupdateTravelPeople(TravelPeople travelPeople) {
    CrupdateTravelPeople crupdateTravelPeople = new CrupdateTravelPeople();
    crupdateTravelPeople.setId(travelPeople.getId());
    crupdateTravelPeople.setTravelId(travelPeople.getTravelId());
    crupdateTravelPeople.setPersonName(travelPeople.getPersonName());
    crupdateTravelPeople.setComment(travelPeople.getComment());
    return crupdateTravelPeople;
  }

  static CrupdateTravelPeople someCreatableTravelPeople() {
    CrupdateTravelPeople crupdateTravelPeople = new CrupdateTravelPeople();
    crupdateTravelPeople.setId(UUID.randomUUID().toString());
    crupdateTravelPeople.setTravelId(TestUtils.TRAVEL_EXPENSE1_ID);
    crupdateTravelPeople.setPersonName("Nouveau passager");
    return crupdateTravelPeople;
  }

  static TravelMaterials travelMaterials1() {
    TravelMaterials travelMaterials = new TravelMaterials();
    travelMaterials.setId(TestUtils.TRAVEL_MATERIALS1_ID);
    travelMaterials.setTravelId(TestUtils.TRAVEL_EXPENSE1_ID);
    travelMaterials.setMaterial(TestUtils.MATERIAL1_ID);
    travelMaterials.setQuantity(10);
    travelMaterials.setQuantityReceived(5);
    return travelMaterials;
  }

  static TravelMaterials travelMaterials2() {
    TravelMaterials travelMaterials = new TravelMaterials();
    travelMaterials.setId(TestUtils.TRAVEL_MATERIALS2_ID);
    travelMaterials.setTravelId(TestUtils.TRAVEL_EXPENSE2_ID);
    travelMaterials.setMaterial(TestUtils.MATERIAL2_ID);
    travelMaterials.setQuantity(20);
    travelMaterials.setQuantityReceived(null);
    return travelMaterials;
  }

  static CrupdateTravelMaterials travelMaterialsToCrupdateTravelMaterials(
      TravelMaterials travelMaterials) {
    CrupdateTravelMaterials crupdateTravelMaterials = new CrupdateTravelMaterials();
    crupdateTravelMaterials.setId(travelMaterials.getId());
    crupdateTravelMaterials.setTravelId(travelMaterials.getTravelId());
    crupdateTravelMaterials.setMaterial(travelMaterials.getMaterial());
    crupdateTravelMaterials.setQuantity(travelMaterials.getQuantity());
    crupdateTravelMaterials.setQuantityReceived(travelMaterials.getQuantityReceived());
    crupdateTravelMaterials.setComment(travelMaterials.getComment());
    return crupdateTravelMaterials;
  }

  static CrupdateTravelMaterials someCreatableTravelMaterials() {
    CrupdateTravelMaterials crupdateTravelMaterials = new CrupdateTravelMaterials();
    crupdateTravelMaterials.setId(UUID.randomUUID().toString());
    crupdateTravelMaterials.setTravelId(TestUtils.TRAVEL_EXPENSE1_ID);
    crupdateTravelMaterials.setMaterial(TestUtils.MATERIAL3_ID);
    crupdateTravelMaterials.setQuantity(3);
    crupdateTravelMaterials.setQuantityReceived(0);
    return crupdateTravelMaterials;
  }

  static TravelEquipment travelEquipment1() {
    TravelEquipment travelEquipment = new TravelEquipment();
    travelEquipment.setId(TestUtils.TRAVEL_EQUIPMENT1_ID);
    travelEquipment.setTravelId(TestUtils.TRAVEL_EXPENSE1_ID);
    travelEquipment.setEquipment(TestUtils.EQUIPMENT1_ID);
    travelEquipment.setQuantity(2);
    travelEquipment.setStatus(TransportStatus.IN_PROGRESS);
    return travelEquipment;
  }

  static TravelEquipment travelEquipment2() {
    TravelEquipment travelEquipment = new TravelEquipment();
    travelEquipment.setId(TestUtils.TRAVEL_EQUIPMENT2_ID);
    travelEquipment.setTravelId(TestUtils.TRAVEL_EXPENSE2_ID);
    travelEquipment.setEquipment(TestUtils.EQUIPMENT2_ID);
    travelEquipment.setQuantity(1);
    travelEquipment.setStatus(TransportStatus.ARRIVED);
    return travelEquipment;
  }

  static CrupdateTravelEquipment travelEquipmentToCrupdateTravelEquipment(
      TravelEquipment travelEquipment) {
    CrupdateTravelEquipment crupdateTravelEquipment = new CrupdateTravelEquipment();
    crupdateTravelEquipment.setId(travelEquipment.getId());
    crupdateTravelEquipment.setTravelId(travelEquipment.getTravelId());
    crupdateTravelEquipment.setEquipment(travelEquipment.getEquipment());
    crupdateTravelEquipment.setQuantity(travelEquipment.getQuantity());
    crupdateTravelEquipment.setStatus(travelEquipment.getStatus());
    crupdateTravelEquipment.setComment(travelEquipment.getComment());
    return crupdateTravelEquipment;
  }

  static CrupdateTravelEquipment someCreatableTravelEquipment() {
    CrupdateTravelEquipment crupdateTravelEquipment = new CrupdateTravelEquipment();
    crupdateTravelEquipment.setId(UUID.randomUUID().toString());
    crupdateTravelEquipment.setTravelId(TestUtils.TRAVEL_EXPENSE1_ID);
    crupdateTravelEquipment.setEquipment(TestUtils.EQUIPMENT3_ID);
    crupdateTravelEquipment.setQuantity(1);
    crupdateTravelEquipment.setStatus(TransportStatus.LOST);
    return crupdateTravelEquipment;
  }
}
