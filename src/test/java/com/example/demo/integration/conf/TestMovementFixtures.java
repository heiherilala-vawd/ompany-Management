package com.example.demo.integration.conf;

import com.example.demo.client.model.CrupdateEquipmentUsage;
import com.example.demo.client.model.CrupdateMaterialConsumption;
import com.example.demo.client.model.EquipmentUsage;
import com.example.demo.client.model.MaterialConsumption;
import com.example.demo.client.model.UsageStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

final class TestMovementFixtures {

  private TestMovementFixtures() {}

  static MaterialConsumption materialConsumption1() {
    MaterialConsumption mc = new MaterialConsumption();
    mc.setId(TestUtils.MAT_CONSUMPTION1_ID);
    mc.setMaterialId(TestUtils.MATERIAL1_ID);
    mc.setWarehouseId(TestUtils.WAREHOUSE1_ID);
    mc.setQuantity(10);
    mc.setConsumptionDate(LocalDate.of(2024, 6, 1));
    mc.setJobId(TestUtils.JOB1_ID);
    mc.setReason("Utilisation pour fondation");
    mc.setConsumptionStatus("COMPLETED");
    return mc;
  }

  static MaterialConsumption materialConsumption2() {
    MaterialConsumption mc = new MaterialConsumption();
    mc.setId(TestUtils.MAT_CONSUMPTION2_ID);
    mc.setMaterialId(TestUtils.MATERIAL1_ID);
    mc.setWarehouseId(TestUtils.WAREHOUSE1_ID);
    mc.setQuantity(5);
    mc.setConsumptionDate(LocalDate.of(2024, 6, 15));
    mc.setJobId(TestUtils.JOB1_ID);
    mc.setReason("Utilisation pour r\u00e9paration");
    mc.setConsumptionStatus("COMPLETED");
    return mc;
  }

  static CrupdateMaterialConsumption materialConsumptionToCrupdateMaterialConsumption(
      MaterialConsumption mc) {
    CrupdateMaterialConsumption crupdate = new CrupdateMaterialConsumption();
    crupdate.setId(mc.getId());
    crupdate.setMaterialId(mc.getMaterialId());
    crupdate.setWarehouseId(mc.getWarehouseId());
    crupdate.setQuantity(mc.getQuantity());
    crupdate.setConsumptionDate(mc.getConsumptionDate());
    crupdate.setJobId(mc.getJobId());
    crupdate.setReason(mc.getReason());
    crupdate.setConsumptionStatus(mc.getConsumptionStatus());
    crupdate.setComment(mc.getComment());
    return crupdate;
  }

  static CrupdateMaterialConsumption someCreatableMaterialConsumption() {
    CrupdateMaterialConsumption crupdate = new CrupdateMaterialConsumption();
    crupdate.setId(UUID.randomUUID().toString());
    crupdate.setMaterialId(TestUtils.MATERIAL1_ID);
    crupdate.setWarehouseId(TestUtils.WAREHOUSE1_ID);
    crupdate.setQuantity(3);
    crupdate.setConsumptionDate(LocalDate.of(2024, 7, 1));
    crupdate.setJobId(TestUtils.JOB1_ID);
    crupdate.setReason("Test consumption");
    crupdate.setConsumptionStatus("IN_PROGRESS");
    return crupdate;
  }

  static EquipmentUsage equipmentUsage1() {
    EquipmentUsage eu = new EquipmentUsage();
    eu.setId(TestUtils.EQUIP_USAGE1_ID);
    eu.setEquipmentId(TestUtils.EQUIPMENT1_ID);
    eu.setJobId(TestUtils.JOB1_ID);
    eu.setStartTime(Instant.parse("2024-06-01T05:00:00Z"));
    eu.setEndTime(Instant.parse("2024-06-01T14:00:00Z"));
    eu.setSourceLocation(TestUtils.WAREHOUSE1_ID);
    eu.setUsageStatus(UsageStatus.RETURNED);
    eu.setUsedBy(TestUtils.ADMIN_ID);
    return eu;
  }

  static EquipmentUsage equipmentUsage2() {
    EquipmentUsage eu = new EquipmentUsage();
    eu.setId(TestUtils.EQUIP_USAGE2_ID);
    eu.setEquipmentId(TestUtils.EQUIPMENT1_ID);
    eu.setJobId(TestUtils.JOB1_ID);
    eu.setStartTime(Instant.parse("2024-06-02T05:00:00Z"));
    eu.setEndTime(Instant.parse("2024-06-02T14:00:00Z"));
    eu.setSourceLocation(TestUtils.WAREHOUSE1_ID);
    eu.setUsageStatus(UsageStatus.RETURNED);
    eu.setUsedBy(TestUtils.ADMIN_ID);
    return eu;
  }

  static CrupdateEquipmentUsage equipmentUsageToCrupdateEquipmentUsage(EquipmentUsage eu) {
    CrupdateEquipmentUsage crupdate = new CrupdateEquipmentUsage();
    crupdate.setId(eu.getId());
    crupdate.setEquipmentId(eu.getEquipmentId());
    crupdate.setJobId(eu.getJobId());
    crupdate.setStartTime(eu.getStartTime());
    crupdate.setEndTime(eu.getEndTime());
    crupdate.setSourceLocation(eu.getSourceLocation());
    crupdate.setUsageStatus(eu.getUsageStatus().getValue());
    crupdate.setUsedBy(eu.getUsedBy());
    crupdate.setComment(eu.getComment());
    return crupdate;
  }

  static CrupdateEquipmentUsage someCreatableEquipmentUsage() {
    CrupdateEquipmentUsage crupdate = new CrupdateEquipmentUsage();
    crupdate.setId(UUID.randomUUID().toString());
    crupdate.setEquipmentId(TestUtils.EQUIPMENT2_ID);
    crupdate.setJobId(TestUtils.JOB1_ID);
    crupdate.setStartTime(Instant.parse("2024-07-01T06:00:00Z"));
    crupdate.setEndTime(Instant.parse("2024-07-01T15:00:00Z"));
    crupdate.setSourceLocation(TestUtils.WAREHOUSE1_ID);
    crupdate.setUsageStatus("IN_USE");
    crupdate.setUsedBy(TestUtils.ADMIN_ID);
    return crupdate;
  }
}
