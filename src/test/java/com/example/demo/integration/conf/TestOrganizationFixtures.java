package com.example.demo.integration.conf;

import com.example.demo.client.model.Company;
import com.example.demo.client.model.CompanyType;
import com.example.demo.client.model.CrupdateCompany;
import com.example.demo.client.model.CrupdateDepartment;
import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.CrupdateJob;
import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.CrupdateOrganization;
import com.example.demo.client.model.CrupdateTeam;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.Department;
import com.example.demo.client.model.Equipment;
import com.example.demo.client.model.Job;
import com.example.demo.client.model.JobStatus;
import com.example.demo.client.model.Material;
import com.example.demo.client.model.MaterialWarehouseInfo;
import com.example.demo.client.model.Organization;
import com.example.demo.client.model.Warehouse;
import java.math.BigDecimal;
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
    equipment.setIsDamaged(false);
    equipment.setIsLost(false);
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
    equipment.setIsDamaged(false);
    equipment.setIsLost(false);
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
    equipment.setIsDamaged(false);
    equipment.setIsLost(false);
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
    crupdateEquipment.setIsDamaged(equipment.getIsDamaged());
    crupdateEquipment.setIsLost(equipment.getIsLost());
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
    equipment.setIsDamaged(false);
    equipment.setIsLost(false);
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
    material.setCompany(companyToCrupdateCompany(company1()));
    material.setUnitPrice(new BigDecimal("5000.00"));
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
    material.setCompany(companyToCrupdateCompany(company1()));
    material.setUnitPrice(new BigDecimal("200.00"));
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
    material.setCompany(companyToCrupdateCompany(company1()));
    material.setUnitPrice(new BigDecimal("15000.00"));
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

  static com.example.demo.client.model.Team team1() {
    com.example.demo.client.model.Team team = new com.example.demo.client.model.Team();
    team.setId(TestUtils.TEAM1_ID);
    team.setName("Équipe chantier A");
    team.setLeader(TestUserFixtures.employee1());
    team.setJob(jobToCrupdateJob(job1()));
    team.setMembers(
        List.of(TestUserFixtures.employee1(), TestUserFixtures.user1(), TestUserFixtures.user2()));
    return team;
  }

  static com.example.demo.client.model.Team team2() {
    com.example.demo.client.model.Team team = new com.example.demo.client.model.Team();
    team.setId(TestUtils.TEAM2_ID);
    team.setName("Équipe rénovation hôtel");
    team.setLeader(TestUserFixtures.user1());
    team.setJob(null);
    team.setMembers(List.of(TestUserFixtures.user1(), TestUserFixtures.employee1()));
    return team;
  }

  static CrupdateTeam teamToCrupdateTeam(com.example.demo.client.model.Team team) {
    CrupdateTeam crupdate = new CrupdateTeam();
    crupdate.setId(team.getId());
    crupdate.setName(team.getName());
    if (team.getLeader() != null) {
      crupdate.setLeaderId(team.getLeader().getId());
    }
    if (team.getJob() != null) {
      crupdate.setJobId(team.getJob().getId());
    }
    if (team.getMembers() != null) {
      crupdate.setMemberIds(
          team.getMembers().stream().map(com.example.demo.client.model.User::getId).toList());
    }
    crupdate.setComment(team.getComment());
    return crupdate;
  }

  static CrupdateTeam someCreatableTeam() {
    CrupdateTeam team = new CrupdateTeam();
    team.setId(UUID.randomUUID().toString());
    team.setName("Nouvelle équipe");
    team.setLeaderId(TestUtils.EMPLOYEE_ID);
    team.setMemberIds(List.of(TestUtils.EMPLOYEE_ID, TestUtils.USER1_ID));
    return team;
  }

  static CrupdateTeam teamWithJob(String jobId) {
    CrupdateTeam team = new CrupdateTeam();
    team.setId(UUID.randomUUID().toString());
    team.setName("Équipe avec job");
    team.setLeaderId(TestUtils.EMPLOYEE_ID);
    team.setJobId(jobId);
    team.setMemberIds(List.of(TestUtils.EMPLOYEE_ID));
    return team;
  }

  static Department department1() {
    Department department = new Department();
    department.setId(TestUtils.DEPARTMENT1_ID);
    department.setName("Génie Civil");
    department.setDescription("Département en charge des travaux de génie civil");
    department.setCompany(companyToCrupdateCompany(company1()));
    return department;
  }

  static Department department2() {
    Department department = new Department();
    department.setId(TestUtils.DEPARTMENT2_ID);
    department.setName("Électricité");
    department.setDescription("Département en charge des installations électriques");
    department.setCompany(companyToCrupdateCompany(company1()));
    return department;
  }

  static CrupdateDepartment departmentToCrupdateDepartment(Department department) {
    CrupdateDepartment crupdate = new CrupdateDepartment();
    crupdate.setId(department.getId());
    crupdate.setName(department.getName());
    crupdate.setDescription(department.getDescription());
    crupdate.setCompanyId(department.getCompany() != null ? department.getCompany().getId() : null);
    crupdate.setComment(department.getComment());
    return crupdate;
  }

  static CrupdateDepartment someCreatableDepartment() {
    CrupdateDepartment department = new CrupdateDepartment();
    department.setId(UUID.randomUUID().toString());
    department.setName("Nouveau departement");
    department.setDescription("Description du nouveau departement");
    department.setCompanyId(TestUtils.COMPANY1_ID);
    return department;
  }

  static Organization organization1() {
    Organization org = new Organization();
    org.setId(TestUtils.ORGANIZATION1_ID);
    org.setName("BNI Madagascar");
    org.setAddress("123 Avenue de l'Independance, Antananarivo");
    org.setEmail("contact@bni.mg");
    org.setPhone("+261202212345");
    org.setContactName("Rakotoarisoa Jean");
    org.setCompany(companyToCrupdateCompany(company1()));
    return org;
  }

  static Organization organization2() {
    Organization org = new Organization();
    org.setId(TestUtils.ORGANIZATION2_ID);
    org.setName("Client Alpha");
    org.setAddress("456 Rue Principale, Toamasina");
    org.setEmail("client.alpha@email.com");
    org.setPhone("+261320011223");
    org.setContactName("Marie Claire");
    org.setCompany(companyToCrupdateCompany(company1()));
    return org;
  }

  static CrupdateOrganization organizationToCrupdateOrganization(Organization organization) {
    CrupdateOrganization crupdate = new CrupdateOrganization();
    crupdate.setId(organization.getId());
    crupdate.setName(organization.getName());
    crupdate.setAddress(organization.getAddress());
    crupdate.setEmail(organization.getEmail());
    crupdate.setPhone(organization.getPhone());
    crupdate.setContactName(organization.getContactName());
    crupdate.setCompanyId(
        organization.getCompany() != null ? organization.getCompany().getId() : null);
    crupdate.setComment(organization.getComment());
    return crupdate;
  }

  static CrupdateOrganization someCreatableOrganization() {
    CrupdateOrganization org = new CrupdateOrganization();
    org.setId(java.util.UUID.randomUUID().toString());
    org.setName("Nouvelle organisation");
    org.setAddress("789 Rue Test");
    org.setEmail("test@org.mg");
    org.setPhone("+261330000000");
    org.setContactName("Contact Test");
    org.setCompanyId(TestUtils.COMPANY1_ID);
    return org;
  }
}
