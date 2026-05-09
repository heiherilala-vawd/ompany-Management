package com.example.demo.integration.conf;

import com.example.demo.client.model.Company;
import com.example.demo.client.model.CompanyType;
import com.example.demo.client.model.CrupdateCompany;
import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.CrupdateJob;
import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.Equipment;
import com.example.demo.client.model.Job;
import com.example.demo.client.model.JobStatus;
import com.example.demo.client.model.Material;
import com.example.demo.client.model.MaterialWarehouseInfo;
import com.example.demo.client.model.Warehouse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class TestOrganizationFixtures {

  private TestOrganizationFixtures() {}

  static Company company1() {
    Company company = new Company();
    company.setId(TestUtils.COMPANY1_ID);
    company.setName("BTP Construction SARL");
    company.setRib("FR7612345678901234567890123");
    company.setDescription("Entreprise de construction");
    company.setCompanyType(CompanyType.BTP);
    return company;
  }

  static Company company2() {
    Company company = new Company();
    company.setId(TestUtils.COMPANY2_ID);
    company.setName("Hotel Palace");
    company.setRib("FR7698765432109876543210987");
    company.setDescription("Hotel de luxe");
    company.setCompanyType(CompanyType.HOTEL);
    return company;
  }

  static CrupdateCompany companyToCrupdateCompany(Company company) {
    CrupdateCompany crupdateCompany = new CrupdateCompany();
    crupdateCompany.setId(company.getId());
    crupdateCompany.setName(company.getName());
    crupdateCompany.setRib(company.getRib());
    crupdateCompany.setDescription(company.getDescription());
    crupdateCompany.setCompanyType(company.getCompanyType());
    crupdateCompany.setComment(company.getComment());
    return crupdateCompany;
  }

  static CrupdateCompany someCreatableCompany() {
    CrupdateCompany company = new CrupdateCompany();
    company.setId(UUID.randomUUID().toString());
    company.setName("New Company");
    company.setRib("FR0012345678901234567890123");
    company.setDescription("Nouvelle entreprise");
    company.setCompanyType(CompanyType.BTP);
    return company;
  }

  static Job job1() {
    Job job = new Job();
    job.setId(TestUtils.JOB1_ID);
    job.setCompany(companyToCrupdateCompany(company1()));
    job.setDescription("Construction du bâtiment A");
    job.setContractSignatureDate(LocalDate.parse("2024-01-15"));
    job.setStartDate(LocalDate.parse("2024-02-01"));
    job.setEndDate(LocalDate.parse("2024-12-31"));
    job.setStatus(JobStatus.IN_PROGRESS);
    return job;
  }

  static Job job2() {
    Job job = new Job();
    job.setId(TestUtils.JOB2_ID);
    job.setCompany(companyToCrupdateCompany(company2()));
    job.setDescription("Rénovation des chambres");
    job.setContractSignatureDate(LocalDate.parse("2024-01-20"));
    job.setStartDate(LocalDate.parse("2024-03-01"));
    job.setEndDate(LocalDate.parse("2024-06-30"));
    job.setStatus(JobStatus.PENDING_SIGNATURE);
    return job;
  }

  static CrupdateJob jobToCrupdateJob(Job job) {
    CrupdateJob crupdateJob = new CrupdateJob();
    crupdateJob.setId(job.getId());
    crupdateJob.setCompanyId(job.getCompany() != null ? job.getCompany().getId() : null);
    crupdateJob.setDescription(job.getDescription());
    crupdateJob.setContractSignatureDate(job.getContractSignatureDate());
    crupdateJob.setStartDate(job.getStartDate());
    crupdateJob.setEndDate(job.getEndDate());
    crupdateJob.setStatus(job.getStatus());
    crupdateJob.setComment(job.getComment());
    return crupdateJob;
  }

  static CrupdateJob someCreatableJob() {
    CrupdateJob job = new CrupdateJob();
    job.setId(UUID.randomUUID().toString());
    job.setCompanyId(TestUtils.COMPANY1_ID);
    job.setDescription("Nouveau chantier");
    job.setContractSignatureDate(LocalDate.parse("2024-04-01"));
    job.setStartDate(LocalDate.parse("2024-04-15"));
    job.setEndDate(LocalDate.parse("2024-12-15"));
    job.setStatus(JobStatus.IN_PROGRESS);
    return job;
  }

  static Warehouse warehouse1() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId(TestUtils.WAREHOUSE1_ID);
    warehouse.setName("Entrepôt Nord");
    warehouse.setDescription("Stockage matériaux lourds");
    warehouse.setJob(jobToCrupdateJob(job1()));
    return warehouse;
  }

  static Warehouse warehouse2() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId(TestUtils.WAREHOUSE2_ID);
    warehouse.setName("Entrepôt Sud");
    warehouse.setDescription("Stockage équipements");
    warehouse.setJob(jobToCrupdateJob(job2()));
    return warehouse;
  }

  static CrupdateWarehouse warehouseToCrupdateWarehouse(Warehouse warehouse) {
    CrupdateWarehouse crupdateWarehouse = new CrupdateWarehouse();
    crupdateWarehouse.setId(warehouse.getId());
    crupdateWarehouse.setName(warehouse.getName());
    crupdateWarehouse.setDescription(warehouse.getDescription());
    crupdateWarehouse.setJobId(warehouse.getJob() != null ? warehouse.getJob().getId() : null);
    crupdateWarehouse.setComment(warehouse.getComment());
    return crupdateWarehouse;
  }

  static CrupdateWarehouse someCreatableWarehouse() {
    CrupdateWarehouse warehouse = new CrupdateWarehouse();
    warehouse.setId(UUID.randomUUID().toString());
    warehouse.setName("Entrepôt Est");
    warehouse.setDescription("Stockage temporaire");
    warehouse.setJobId(TestUtils.JOB1_ID);
    return warehouse;
  }

  static Equipment equipment1() {
    Equipment equipment = new Equipment();
    equipment.setId(TestUtils.EQUIPMENT1_ID);
    equipment.setName("Pelle mécanique");
    equipment.setDescription("Pelle Caterpillar 320");
    equipment.setWarehouse(warehouseToCrupdateWarehouse(warehouse1()));
    equipment.setFloorNumber(1);
    equipment.setStorageNumber(10);
    return equipment;
  }

  static Equipment equipment2() {
    Equipment equipment = new Equipment();
    equipment.setId(TestUtils.EQUIPMENT2_ID);
    equipment.setName("Bétonnière");
    equipment.setDescription("Bétonnière électrique");
    equipment.setWarehouse(warehouseToCrupdateWarehouse(warehouse1()));
    equipment.setFloorNumber(1);
    equipment.setStorageNumber(15);
    return equipment;
  }

  static Equipment equipment3() {
    Equipment equipment = new Equipment();
    equipment.setId(TestUtils.EQUIPMENT3_ID);
    equipment.setName("Climatisation");
    equipment.setDescription("Unité extérieure");
    equipment.setWarehouse(warehouseToCrupdateWarehouse(warehouse2()));
    equipment.setFloorNumber(2);
    equipment.setStorageNumber(5);
    return equipment;
  }

  static CrupdateEquipment equipmentToCrupdateEquipment(Equipment equipment) {
    CrupdateEquipment crupdateEquipment = new CrupdateEquipment();
    crupdateEquipment.setId(equipment.getId());
    crupdateEquipment.setName(equipment.getName());
    crupdateEquipment.setDescription(equipment.getDescription());
    crupdateEquipment.setWarehouseId(
        equipment.getWarehouse() != null ? equipment.getWarehouse().getId() : null);
    crupdateEquipment.setFloorNumber(equipment.getFloorNumber());
    crupdateEquipment.setStorageNumber(equipment.getStorageNumber());
    crupdateEquipment.setComment(equipment.getComment());
    return crupdateEquipment;
  }

  static CrupdateEquipment someCreatableEquipment() {
    CrupdateEquipment equipment = new CrupdateEquipment();
    equipment.setId(UUID.randomUUID().toString());
    equipment.setName("Marteau-piqueur");
    equipment.setDescription("Outil de demolition");
    equipment.setWarehouseId(TestUtils.WAREHOUSE1_ID);
    equipment.setFloorNumber(1);
    equipment.setStorageNumber(20);
    return equipment;
  }

  static Warehouse routeWarehouse() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId("warehouse_route_id");
    warehouse.setName("En route");
    warehouse.setDescription("Emplacement virtuel pour les équipements en déplacement");
    return warehouse;
  }

  static Warehouse atSellerWarehouse() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId("warehouse_at_seller_id");
    warehouse.setName("Chez le vendeur");
    warehouse.setDescription("Emplacement virtuel pour les équipements encore chez le vendeur");
    return warehouse;
  }

  static Material material1() {
    Material material = new Material();
    material.setId(TestUtils.MATERIAL1_ID);
    material.setName("Ciment");
    material.setDescription("Ciment Portland 35kg");
    material.setUnit(com.example.demo.client.model.MaterialUnit.SAC);
    List<MaterialWarehouseInfo> mws = new ArrayList<>();
    MaterialWarehouseInfo mw1 = new MaterialWarehouseInfo();
    mw1.setWarehouse(warehouse1());
    mw1.setQuantity(100);
    mws.add(mw1);
    MaterialWarehouseInfo mw2 = new MaterialWarehouseInfo();
    mw2.setWarehouse(routeWarehouse());
    mw2.setQuantity(50);
    mws.add(mw2);
    material.setMaterialWarehouses(mws);
    return material;
  }

  static Material material2() {
    Material material = new Material();
    material.setId(TestUtils.MATERIAL2_ID);
    material.setName("Brique");
    material.setDescription("Brique rouge 20x10x5");
    material.setUnit(com.example.demo.client.model.MaterialUnit.U);
    List<MaterialWarehouseInfo> mws = new ArrayList<>();
    MaterialWarehouseInfo mw = new MaterialWarehouseInfo();
    mw.setWarehouse(atSellerWarehouse());
    mw.setQuantity(30);
    mws.add(mw);
    material.setMaterialWarehouses(mws);
    return material;
  }

  static Material material3() {
    Material material = new Material();
    material.setId(TestUtils.MATERIAL3_ID);
    material.setName("Peinture");
    material.setDescription("Peinture blanche mate");
    material.setUnit(com.example.demo.client.model.MaterialUnit.L);
    List<MaterialWarehouseInfo> mws = new ArrayList<>();
    MaterialWarehouseInfo mw = new MaterialWarehouseInfo();
    mw.setWarehouse(routeWarehouse());
    mw.setQuantity(0);
    mws.add(mw);
    material.setMaterialWarehouses(mws);
    return material;
  }

  static CrupdateMaterial materialToCrupdateMaterial(Material material) {
    CrupdateMaterial crupdateMaterial = new CrupdateMaterial();
    crupdateMaterial.setId(material.getId());
    crupdateMaterial.setName(material.getName());
    crupdateMaterial.setDescription(material.getDescription());
    crupdateMaterial.setUnit(material.getUnit());
    crupdateMaterial.setComment(material.getComment());
    return crupdateMaterial;
  }

  static CrupdateMaterial someCreatableMaterial() {
    CrupdateMaterial material = new CrupdateMaterial();
    material.setId(UUID.randomUUID().toString());
    material.setName("Sable");
    material.setDescription("Sable fin");
    material.setUnit(com.example.demo.client.model.MaterialUnit.KG);
    return material;
  }
}
