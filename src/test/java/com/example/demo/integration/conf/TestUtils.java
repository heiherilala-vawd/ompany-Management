package com.example.demo.integration.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.invoker.ApiException;
import com.example.demo.client.model.Company;
import com.example.demo.client.model.CompanyType;
import com.example.demo.client.model.CrupdateCompany;
import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.CrupdateJob;
import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.Equipment;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.client.model.Job;
import com.example.demo.client.model.JobStatus;
import com.example.demo.client.model.Material;
import com.example.demo.client.model.Role;
import com.example.demo.client.model.Sex;
import com.example.demo.client.model.User;
import com.example.demo.client.model.Warehouse;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.Instant;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.function.Executable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestUtils {

  // IDs de test
  public static final String ADMIN_ID = "admin1_id";
  public static final String WAREHOUSE_ID = "warehouse1_id";
  public static final String EMPLOYEE_ID = "employee1_id";
  public static final String ADMINISTRATION_ID = "admin2_id";
  public static final String USER1_ID = "user1_id";
  public static final String USER2_ID = "user2_id";
  public static final String USER3_ID = "user3_id";
  public static final String COMPANY1_ID = "company1_id";
  public static final String COMPANY2_ID = "company2_id";
  public static final String JOB1_ID = "job1_id";
  public static final String JOB2_ID = "job2_id";
  public static final String WAREHOUSE1_ID = "warehouse1_id";
  public static final String WAREHOUSE2_ID = "warehouse2_id";
  public static final String EQUIPMENT1_ID = "equipment1_id";
  public static final String EQUIPMENT2_ID = "equipment2_id";
  public static final String EQUIPMENT3_ID = "equipment3_id";
  public static final String MATERIAL1_ID = "material1_id";
  public static final String MATERIAL2_ID = "material2_id";
  public static final String MATERIAL3_ID = "material3_id";
  public static final String INCOME1_ID = "income1_id";
  public static final String INCOME2_ID = "income2_id";
  public static final String EXPENSE1_ID = "expense1_id";
  public static final String EXPENSE2_ID = "expense2_id";

  // Emails de test
  public static final String ADMIN_EMAIL = "admin@hei.school";
  public static final String WAREHOUSE_EMAIL = "warehouse@hei.school";
  public static final String EMPLOYEE_EMAIL = "employee@hei.school";
  public static final String ADMINISTRATION_EMAIL = "admin2@hei.school";
  public static final String USER1_EMAIL = "alice@hei.school";
  public static final String USER2_EMAIL = "bob@hei.school";
  public static final String RANDOM_EMAIL = "RANDOM_EMAIL@hei.school";

  public static final String PASSWORD = "password";

  public static final Authentication ADMIN_AUTH = mock(Authentication.class);
  public static final Authentication WAREHOUSE_AUTH = mock(Authentication.class);
  public static final Authentication EMPLOYEE_AUTH = mock(Authentication.class);
  public static final Authentication ADMINISTRATION_AUTH = mock(Authentication.class);
  public static final Authentication USER1_AUTH = mock(Authentication.class);
  public static final Authentication USER2_AUTH = mock(Authentication.class);
  public static final Authentication RANDOM_AUTH = mock(Authentication.class);

  // Tokens de test (ce sont juste des identifiants, pas de vrais JWT)
  public static final String BAD_TOKEN = "bad_token";
  public static final String ADMIN_TOKEN = "admin_token";
  public static final String WAREHOUSE_TOKEN = "warehouse_token";
  public static final String EMPLOYEE_TOKEN = "employee_token";
  public static final String ADMINISTRATION_TOKEN = "administration_token";
  public static final String USER1_TOKEN = "user1_token";
  public static final String USER2_TOKEN = "user2_token";
  public static final String RANDOM_TOKEN = "random2_token";

  public static final String FORBIDDEN_ERROR =
      "{\"type\":\"403 FORBIDDEN\",\"message\":\"Access Denied\"}";

  public static final String NOT_AUTHORIZED_ERROR =
      "{\"type\":\"Full authentication is required to access this resource\",\"message\":\"NotAuthorizedException\"}";

  private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public static void setUpJwtService(JwtUtils jwtServiceMock) {

    // =========================
    // TOKEN -> EMAIL
    // =========================
    when(jwtServiceMock.getUserEmailFromJwtToken(anyString()))
        .thenAnswer(
            invocation -> {
              String token = invocation.getArgument(0);

              if (ADMIN_TOKEN.equals(token)) return ADMIN_EMAIL;
              if (WAREHOUSE_TOKEN.equals(token)) return WAREHOUSE_EMAIL;
              if (EMPLOYEE_TOKEN.equals(token)) return EMPLOYEE_EMAIL;
              if (ADMINISTRATION_TOKEN.equals(token)) return ADMINISTRATION_EMAIL;
              if (USER1_TOKEN.equals(token)) return USER1_EMAIL;
              if (USER2_TOKEN.equals(token)) return USER2_EMAIL;
              if (RANDOM_TOKEN.equals(token)) return RANDOM_EMAIL;

              if (BAD_TOKEN.equals(token)) return null;

              return null; // token inconnu
            });

    // =========================
    // AUTH -> TOKEN
    // =========================
    when(jwtServiceMock.generateJwtToken(any()))
        .thenAnswer(
            invocation -> {
              Object auth = invocation.getArgument(0);

              if (ADMIN_AUTH.equals(auth)) return ADMIN_TOKEN;
              if (WAREHOUSE_AUTH.equals(auth)) return WAREHOUSE_TOKEN;
              if (EMPLOYEE_AUTH.equals(auth)) return EMPLOYEE_TOKEN;
              if (ADMINISTRATION_AUTH.equals(auth)) return ADMINISTRATION_TOKEN;
              if (USER1_AUTH.equals(auth)) return USER1_TOKEN;
              if (USER2_AUTH.equals(auth)) return USER2_TOKEN;
              if (RANDOM_AUTH.equals(auth)) return RANDOM_TOKEN;

              return null;
            });

    // =========================
    // VALIDATION TOKEN
    // =========================
    when(jwtServiceMock.validateJwtToken(anyString()))
        .thenAnswer(
            invocation -> {
              String token = invocation.getArgument(0);

              return ADMIN_TOKEN.equals(token)
                  || WAREHOUSE_TOKEN.equals(token)
                  || EMPLOYEE_TOKEN.equals(token)
                  || ADMINISTRATION_TOKEN.equals(token)
                  || USER1_TOKEN.equals(token)
                  || USER2_TOKEN.equals(token)
                  || RANDOM_TOKEN.equals(token);
            });
  }

  public static void setUpAuthenticationManager(AuthenticationManager authenticationManagerMock) {

    when(authenticationManagerMock.authenticate(any()))
        .thenAnswer(
            invocation -> {
              UsernamePasswordAuthenticationToken auth = invocation.getArgument(0);

              String email = auth.getName();
              String password = (String) auth.getCredentials();

              // =========================
              // CHECK PASSWORD
              // =========================
              if (!PASSWORD.equals(password)) {
                throw new BadCredentialsException("Les identifications sont erronée");
              }

              // =========================
              // EMAIL -> AUTH
              // =========================
              if (ADMIN_EMAIL.equals(email)) return ADMIN_AUTH;
              if (EMPLOYEE_EMAIL.equals(email)) return EMPLOYEE_AUTH;
              if (ADMINISTRATION_EMAIL.equals(email)) return ADMINISTRATION_AUTH;
              if (USER1_EMAIL.equals(email)) return USER1_AUTH;
              if (USER2_EMAIL.equals(email)) return USER2_AUTH;
              if (WAREHOUSE_EMAIL.equals(email)) return WAREHOUSE_AUTH;
              if (RANDOM_EMAIL.equals(email)) return RANDOM_AUTH;

              throw new BadCredentialsException("Les identifications sont erronée");
            });
  }

  public static ApiClient anApiClient(String token, int serverPort) {
    ApiClient client = new ApiClient();
    client.setScheme("http");
    client.setHost("localhost");
    client.setPort(serverPort);
    client.setRequestInterceptor(
        httpRequestBuilder -> httpRequestBuilder.header("Authorization", "Bearer " + token));
    return client;
  }

  public static User admin1() {
    User user = new User();
    user.setId(ADMIN_ID);
    user.setRole(Role.ADMIN);
    user.setFirstName("Admin");
    user.setLastName("System");
    user.setSex(Sex.M);
    user.setEmail(ADMIN_EMAIL);
    user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    user.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    return user;
  }

  public static User warehouseWorker1() {
    User user = new User();
    user.setId(WAREHOUSE_ID);
    user.setRole(Role.WAREHOUSE_WORKER);
    user.setFirstName("Warehouse");
    user.setLastName("Worker");
    user.setSex(Sex.M);
    user.setEmail(WAREHOUSE_EMAIL);
    user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    user.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    return user;
  }

  public static User employee1() {
    User user = new User();
    user.setId(EMPLOYEE_ID);
    user.setRole(Role.EMPLOYEE);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setSex(Sex.M);
    user.setEmail(EMPLOYEE_EMAIL);
    user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    user.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    return user;
  }

  public static User administration1() {
    User user = new User();
    user.setId(ADMINISTRATION_ID);
    user.setRole(Role.ADMINISTRATION);
    user.setFirstName("Admin");
    user.setLastName("Staff");
    user.setSex(Sex.F);
    user.setEmail(ADMINISTRATION_EMAIL);
    user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    user.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    return user;
  }

  public static User user1() {
    User user = new User();
    user.setId(USER1_ID);
    user.setRole(Role.EMPLOYEE);
    user.setFirstName("Alice");
    user.setLastName("Martin");
    user.setSex(Sex.F);
    user.setEmail("alice@hei.school");
    user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    user.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    return user;
  }

  public static User user2() {
    User user = new User();
    user.setId(USER2_ID);
    user.setRole(Role.EMPLOYEE);
    user.setFirstName("Bob");
    user.setLastName("Bernard");
    user.setSex(Sex.M);
    user.setEmail("bob@hei.school");
    user.setCreatedAt(Instant.parse(""));
    user.setUpdatedAt(Instant.parse(""));
    return user;
  }

  public static CrupdateUser someCreatableUser() {
    double random = Math.random();
    int number = (int) (random * 1000);
    String stringRandom = String.format("%03d", number);
    CrupdateUser user = new CrupdateUser();
    user.setFirstName("Test");
    user.setLastName("User");
    user.setEmail(RANDOM_EMAIL);
    user.setPassword(PASSWORD);
    user.setSex(Sex.M);
    user.setRole(Role.EMPLOYEE);
    return user;
  }

  public static CrupdateUser userToCrupdateUser(User user) {
    CrupdateUser crupdateUser = new CrupdateUser();
    crupdateUser.setFirstName(user.getFirstName());
    crupdateUser.setLastName(user.getLastName());
    crupdateUser.setEmail(user.getEmail());
    crupdateUser.setPassword(PASSWORD);
    crupdateUser.setSex(user.getSex());
    crupdateUser.setRole(user.getRole());
    return crupdateUser;
  }
  ;

  public static Company company1() {
    Company company = new Company();
    company.setId(COMPANY1_ID);
    company.setName("BTP Construction SARL");
    company.setRib("FR7612345678901234567890123");
    company.setDescription("Entreprise de construction");
    company.setCompanyType(CompanyType.BTP);
    return company;
  }

  public static Company company2() {
    Company company = new Company();
    company.setId(COMPANY2_ID);
    company.setName("Hotel Palace");
    company.setRib("FR7698765432109876543210987");
    company.setDescription("Hotel de luxe");
    company.setCompanyType(CompanyType.HOTEL);
    return company;
  }

  public static CrupdateCompany companyToCrupdateCompany(Company company) {
    CrupdateCompany crupdateCompany = new CrupdateCompany();
    crupdateCompany.setId(company.getId());
    crupdateCompany.setName(company.getName());
    crupdateCompany.setRib(company.getRib());
    crupdateCompany.setDescription(company.getDescription());
    crupdateCompany.setCompanyType(company.getCompanyType());
    crupdateCompany.setComment(company.getComment());
    return crupdateCompany;
  }

  public static CrupdateCompany someCreatableCompany() {
    CrupdateCompany company = new CrupdateCompany();
    company.setName("New Company");
    company.setRib("FR0012345678901234567890123");
    company.setDescription("Nouvelle entreprise");
    company.setCompanyType(CompanyType.BTP);
    return company;
  }

  public static Job job1() {
    Job job = new Job();
    job.setId(JOB1_ID);
    job.setCompanyId(COMPANY1_ID);
    job.setDescription("Construction du bâtiment A");
    job.setContractSignatureDate(java.time.LocalDate.parse("2024-01-15"));
    job.setStartDate(java.time.LocalDate.parse("2024-02-01"));
    job.setEndDate(java.time.LocalDate.parse("2024-12-31"));
    job.setStatus(JobStatus.IN_PROGRESS);
    return job;
  }

  public static Job job2() {
    Job job = new Job();
    job.setId(JOB2_ID);
    job.setCompanyId(COMPANY2_ID);
    job.setDescription("Rénovation des chambres");
    job.setContractSignatureDate(java.time.LocalDate.parse("2024-01-20"));
    job.setStartDate(java.time.LocalDate.parse("2024-03-01"));
    job.setEndDate(java.time.LocalDate.parse("2024-06-30"));
    job.setStatus(JobStatus.PENDING_SIGNATURE);
    return job;
  }

  public static CrupdateJob jobToCrupdateJob(Job job) {
    CrupdateJob crupdateJob = new CrupdateJob();
    crupdateJob.setId(job.getId());
    crupdateJob.setCompanyId(job.getCompanyId());
    crupdateJob.setDescription(job.getDescription());
    crupdateJob.setContractSignatureDate(job.getContractSignatureDate());
    crupdateJob.setStartDate(job.getStartDate());
    crupdateJob.setEndDate(job.getEndDate());
    crupdateJob.setStatus(job.getStatus());
    crupdateJob.setComment(job.getComment());
    return crupdateJob;
  }

  public static CrupdateJob someCreatableJob() {
    CrupdateJob job = new CrupdateJob();
    job.setCompanyId(COMPANY1_ID);
    job.setDescription("Nouveau chantier");
    job.setContractSignatureDate(java.time.LocalDate.parse("2024-04-01"));
    job.setStartDate(java.time.LocalDate.parse("2024-04-15"));
    job.setEndDate(java.time.LocalDate.parse("2024-12-15"));
    job.setStatus(JobStatus.IN_PROGRESS);
    return job;
  }

  public static Warehouse warehouse1() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId(WAREHOUSE1_ID);
    warehouse.setName("Entrepôt Nord");
    warehouse.setDescription("Stockage matériaux lourds");
    warehouse.setJobId(JOB1_ID);
    return warehouse;
  }

  public static Warehouse warehouse2() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId(WAREHOUSE2_ID);
    warehouse.setName("Entrepôt Sud");
    warehouse.setDescription("Stockage équipements");
    warehouse.setJobId(JOB2_ID);
    return warehouse;
  }

  public static CrupdateWarehouse warehouseToCrupdateWarehouse(Warehouse warehouse) {
    CrupdateWarehouse crupdateWarehouse = new CrupdateWarehouse();
    crupdateWarehouse.setId(warehouse.getId());
    crupdateWarehouse.setName(warehouse.getName());
    crupdateWarehouse.setDescription(warehouse.getDescription());
    crupdateWarehouse.setJobId(warehouse.getJobId());
    crupdateWarehouse.setComment(warehouse.getComment());
    return crupdateWarehouse;
  }

  public static CrupdateWarehouse someCreatableWarehouse() {
    CrupdateWarehouse warehouse = new CrupdateWarehouse();
    warehouse.setName("Entrepôt Est");
    warehouse.setDescription("Stockage temporaire");
    warehouse.setJobId(JOB1_ID);
    return warehouse;
  }

  public static Equipment equipment1() {
    Equipment equipment = new Equipment();
    equipment.setId(EQUIPMENT1_ID);
    equipment.setName("Pelle mécanique");
    equipment.setDescription("Pelle Caterpillar 320");
    equipment.setWarehouseId(WAREHOUSE1_ID);
    equipment.setFloorNumber(1);
    equipment.setStorageNumber(10);
    return equipment;
  }

  public static Equipment equipment2() {
    Equipment equipment = new Equipment();
    equipment.setId(EQUIPMENT2_ID);
    equipment.setName("Bétonnière");
    equipment.setDescription("Bétonnière électrique");
    equipment.setWarehouseId(WAREHOUSE1_ID);
    equipment.setFloorNumber(1);
    equipment.setStorageNumber(15);
    return equipment;
  }

  public static Equipment equipment3() {
    Equipment equipment = new Equipment();
    equipment.setId(EQUIPMENT3_ID);
    equipment.setName("Climatisation");
    equipment.setDescription("Unité extérieure");
    equipment.setWarehouseId(WAREHOUSE2_ID);
    equipment.setFloorNumber(2);
    equipment.setStorageNumber(5);
    return equipment;
  }

  public static CrupdateEquipment equipmentToCrupdateEquipment(Equipment equipment) {
    CrupdateEquipment crupdateEquipment = new CrupdateEquipment();
    crupdateEquipment.setId(equipment.getId());
    crupdateEquipment.setName(equipment.getName());
    crupdateEquipment.setDescription(equipment.getDescription());
    crupdateEquipment.setWarehouseId(equipment.getWarehouseId());
    crupdateEquipment.setFloorNumber(equipment.getFloorNumber());
    crupdateEquipment.setStorageNumber(equipment.getStorageNumber());
    crupdateEquipment.setComment(equipment.getComment());
    return crupdateEquipment;
  }

  public static CrupdateEquipment someCreatableEquipment() {
    CrupdateEquipment equipment = new CrupdateEquipment();
    equipment.setName("Marteau-piqueur");
    equipment.setDescription("Outil de demolition");
    equipment.setWarehouseId(WAREHOUSE1_ID);
    equipment.setFloorNumber(1);
    equipment.setStorageNumber(20);
    return equipment;
  }

  public static Material material1() {
    Material material = new Material();
    material.setId(MATERIAL1_ID);
    material.setName("Ciment");
    material.setDescription("Ciment Portland 35kg");
    material.setWarehouseId(WAREHOUSE1_ID);
    material.setFloorNumber(1);
    material.setStorageNumber(100);
    return material;
  }

  public static Material material2() {
    Material material = new Material();
    material.setId(MATERIAL2_ID);
    material.setName("Brique");
    material.setDescription("Brique rouge 20x10x5");
    material.setWarehouseId(WAREHOUSE1_ID);
    material.setFloorNumber(1);
    material.setStorageNumber(500);
    return material;
  }

  public static Material material3() {
    Material material = new Material();
    material.setId(MATERIAL3_ID);
    material.setName("Peinture");
    material.setDescription("Peinture blanche mate");
    material.setWarehouseId(WAREHOUSE2_ID);
    material.setFloorNumber(2);
    material.setStorageNumber(50);
    return material;
  }

  public static CrupdateMaterial materialToCrupdateMaterial(Material material) {
    CrupdateMaterial crupdateMaterial = new CrupdateMaterial();
    crupdateMaterial.setId(material.getId());
    crupdateMaterial.setName(material.getName());
    crupdateMaterial.setDescription(material.getDescription());
    crupdateMaterial.setWarehouseId(material.getWarehouseId());
    crupdateMaterial.setFloorNumber(material.getFloorNumber());
    crupdateMaterial.setStorageNumber(material.getStorageNumber());
    crupdateMaterial.setComment(material.getComment());
    return crupdateMaterial;
  }

  public static CrupdateMaterial someCreatableMaterial() {
    CrupdateMaterial material = new CrupdateMaterial();
    material.setName("Sable");
    material.setDescription("Sable fin");
    material.setWarehouseId(WAREHOUSE1_ID);
    material.setFloorNumber(1);
    material.setStorageNumber(200);
    return material;
  }

  public static IncomeMoney income1() {
    IncomeMoney income = new IncomeMoney();
    income.setId(INCOME1_ID);
    income.setSourceOrganization("Client Alpha");
    income.setInvoiceReference("INV-2024-001");
    income.setAmount(150000);
    income.setDescription("Paiement initial chantier A");
    return income;
  }

  public static IncomeMoney income2() {
    IncomeMoney income = new IncomeMoney();
    income.setId(INCOME2_ID);
    income.setSourceOrganization("Client Beta");
    income.setInvoiceReference("INV-2024-002");
    income.setAmount(275000);
    income.setDescription("Paiement avance renovation hotel");
    return income;
  }

  public static CrupdateIncomeMoney incomeToCrupdateIncome(IncomeMoney income) {
    CrupdateIncomeMoney crupdateIncome = new CrupdateIncomeMoney();
    crupdateIncome.setId(income.getId());
    crupdateIncome.setSourceOrganization(income.getSourceOrganization());
    crupdateIncome.setInvoiceReference(income.getInvoiceReference());
    crupdateIncome.setAmount(income.getAmount());
    crupdateIncome.setDescription(income.getDescription());
    crupdateIncome.setComment(income.getComment());
    return crupdateIncome;
  }

  public static CrupdateIncomeMoney someCreatableIncome() {
    CrupdateIncomeMoney income = new CrupdateIncomeMoney();
    income.setSourceOrganization("Client Gamma");
    income.setInvoiceReference("INV-2024-003");
    income.setAmount(99000);
    income.setDescription("Paiement complementaire");
    return income;
  }

  public static ExpenseMoney expense1() {
    ExpenseMoney expense = new ExpenseMoney();
    expense.setId(EXPENSE1_ID);
    expense.setAmount(45000);
    expense.setDescription("Achat materiaux chantier A");
    return expense;
  }

  public static ExpenseMoney expense2() {
    ExpenseMoney expense = new ExpenseMoney();
    expense.setId(EXPENSE2_ID);
    expense.setAmount(80000);
    expense.setDescription("Paiement sous-traitant renovation");
    return expense;
  }

  public static CrupdateExpenseMoney expenseToCrupdateExpense(ExpenseMoney expense) {
    CrupdateExpenseMoney crupdateExpense = new CrupdateExpenseMoney();
    crupdateExpense.setId(expense.getId());
    crupdateExpense.setAmount(expense.getAmount());
    crupdateExpense.setDescription(expense.getDescription());
    crupdateExpense.setComment(expense.getComment());
    return crupdateExpense;
  }

  public static CrupdateExpenseMoney someCreatableExpense() {
    CrupdateExpenseMoney expense = new CrupdateExpenseMoney();
    expense.setAmount(32000);
    expense.setDescription("Frais logistiques");
    return expense;
  }

  public static int anAvailableRandomPort() {
    try {
      return new ServerSocket(0).getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SneakyThrows
  public static void assertThrowsApiException(String expectedMessage, Executable executable) {
    ApiException exception = assertThrows(ApiException.class, executable);
    assertEquals(expectedMessage, exception.getResponseBody());
  }

  public static void assertThrowsForbiddenException(Executable executable) {
    ApiException exception = assertThrows(ApiException.class, executable);
    assertEquals(FORBIDDEN_ERROR, exception.getResponseBody());
  }

  public static void assertThrowsNotAuthorizedException(Executable executable) {
    ApiException exception = assertThrows(ApiException.class, executable);
    assertEquals(NOT_AUTHORIZED_ERROR, exception.getResponseBody());
  }

  @FunctionalInterface
  public interface ThrowingRunnable {
    void run() throws Exception;
  }

  public static boolean isValidUUID(String candidate) {
    try {
      UUID.fromString(candidate);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
