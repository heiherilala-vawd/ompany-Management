package com.example.demo.integration.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.client.invoker.ApiException;
import com.example.demo.client.model.BankFee;
import com.example.demo.client.model.Company;
import com.example.demo.client.model.CrupdateBankFee;
import com.example.demo.client.model.CrupdateCompany;
import com.example.demo.client.model.CrupdateEmployeePayment;
import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.CrupdateIncomeType;
import com.example.demo.client.model.CrupdateJob;
import com.example.demo.client.model.CrupdateLoan;
import com.example.demo.client.model.CrupdateLoanRepayment;
import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.CrupdateOtherExpense;
import com.example.demo.client.model.CrupdatePurchase;
import com.example.demo.client.model.CrupdateTravelEquipment;
import com.example.demo.client.model.CrupdateTravelExpense;
import com.example.demo.client.model.CrupdateTravelMaterials;
import com.example.demo.client.model.CrupdateTravelPeople;
import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.EmployeePayment;
import com.example.demo.client.model.Equipment;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.client.model.IncomeType;
import com.example.demo.client.model.Job;
import com.example.demo.client.model.Loan;
import com.example.demo.client.model.LoanRepayment;
import com.example.demo.client.model.Material;
import com.example.demo.client.model.OtherExpense;
import com.example.demo.client.model.Purchase;
import com.example.demo.client.model.TravelEquipment;
import com.example.demo.client.model.TravelExpense;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.client.model.TravelPeople;
import com.example.demo.client.model.User;
import com.example.demo.client.model.Warehouse;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.function.Executable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class TestUtils {

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
  public static final String ROUTE_WAREHOUSE_ID = "warehouse_route_id";
  public static final String AT_SELLER_WAREHOUSE_ID = "warehouse_at_seller_id";
  public static final String UNFINDABLE_WAREHOUSE_ID = "warehouse_unfindable_id";
  public static final String EQUIPMENT1_ID = "equipment1_id";
  public static final String EQUIPMENT2_ID = "equipment2_id";
  public static final String EQUIPMENT3_ID = "equipment3_id";
  public static final String EQUIPMENT4_ID = "equipment4_id";
  public static final String EQUIPMENT5_ID = "equipment5_id";
  public static final String MATERIAL1_ID = "material1_id";
  public static final String MATERIAL2_ID = "material2_id";
  public static final String MATERIAL3_ID = "material3_id";
  public static final String INCOME1_ID = "income1_id";
  public static final String INCOME2_ID = "income2_id";
  public static final String INCOME3_ID = "income3_id";
  public static final String INCOME4_ID = "income4_id";
  public static final String INCOME_TYPE1_ID = "income_type1_id";
  public static final String INCOME_TYPE2_ID = "income_type2_id";
  public static final String INCOME_TYPE3_ID = "income_type3_id";
  public static final String EXPENSE1_ID = "expense1_id";
  public static final String EXPENSE2_ID = "expense2_id";
  public static final String BANK_FEE1_ID = "bank_fee1_id";
  public static final String BANK_FEE2_ID = "bank_fee2_id";
  public static final String EMPLOYEE_PAYMENT1_ID = "employee_payment1_id";
  public static final String EMPLOYEE_PAYMENT2_ID = "employee_payment2_id";
  public static final String OTHER_EXPENSE1_ID = "other_expense1_id";
  public static final String OTHER_EXPENSE2_ID = "other_expense2_id";
  public static final String PURCHASE1_ID = "purchase1_id";
  public static final String PURCHASE2_ID = "purchase2_id";
  public static final String TRAVEL_EXPENSE1_ID = "travel_expense1_id";
  public static final String TRAVEL_EXPENSE2_ID = "travel_expense2_id";
  public static final String TRAVEL_PEOPLE1_ID = "travel_people1_id";
  public static final String TRAVEL_PEOPLE2_ID = "travel_people2_id";
  public static final String TRAVEL_MATERIALS1_ID = "travel_materials1_id";
  public static final String TRAVEL_MATERIALS2_ID = "travel_materials2_id";
  public static final String TRAVEL_EQUIPMENT1_ID = "travel_equipment1_id";
  public static final String TRAVEL_EQUIPMENT2_ID = "travel_equipment2_id";
  public static final String LOAN1_ID = "loan1_id";
  public static final String LOAN2_ID = "loan2_id";
  public static final String REPAYMENT1_ID = "repayment1_id";
  public static final String REPAYMENT2_ID = "repayment2_id";

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

  static {
    when(ADMIN_AUTH.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    when(WAREHOUSE_AUTH.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_WAREHOUSE_WORKER")));
    when(EMPLOYEE_AUTH.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    when(ADMINISTRATION_AUTH.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATION")));
    when(USER1_AUTH.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    when(USER2_AUTH.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    when(RANDOM_AUTH.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
  }

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

  public static void setUpJwtService(JwtUtils jwtServiceMock) {
    TestAuthSupport.setUpJwtService(jwtServiceMock);
  }

  public static void setUpAuthenticationManager(AuthenticationManager authenticationManagerMock) {
    TestAuthSupport.setUpAuthenticationManager(authenticationManagerMock);
  }

  public static com.example.demo.client.invoker.ApiClient anApiClient(
      String token, int serverPort) {
    return TestAuthSupport.anApiClient(token, serverPort);
  }

  public static User admin1() {
    return TestUserFixtures.admin1();
  }

  public static User warehouseWorker1() {
    return TestUserFixtures.warehouseWorker1();
  }

  public static User employee1() {
    return TestUserFixtures.employee1();
  }

  public static User administration1() {
    return TestUserFixtures.administration1();
  }

  public static User user1() {
    return TestUserFixtures.user1();
  }

  public static User user2() {
    return TestUserFixtures.user2();
  }

  public static CrupdateUser someCreatableUser() {
    return TestUserFixtures.someCreatableUser();
  }

  public static CrupdateUser userToCrupdateUser(User user) {
    return TestUserFixtures.userToCrupdateUser(user);
  }

  public static Company company1() {
    return TestOrganizationFixtures.company1();
  }

  public static Company company2() {
    return TestOrganizationFixtures.company2();
  }

  public static CrupdateCompany companyToCrupdateCompany(Company company) {
    return TestOrganizationFixtures.companyToCrupdateCompany(company);
  }

  public static CrupdateCompany someCreatableCompany() {
    return TestOrganizationFixtures.someCreatableCompany();
  }

  public static Job job1() {
    return TestOrganizationFixtures.job1();
  }

  public static Job job2() {
    return TestOrganizationFixtures.job2();
  }

  public static CrupdateJob jobToCrupdateJob(Job job) {
    return TestOrganizationFixtures.jobToCrupdateJob(job);
  }

  public static CrupdateJob someCreatableJob() {
    return TestOrganizationFixtures.someCreatableJob();
  }

  public static Warehouse warehouse1() {
    return TestOrganizationFixtures.warehouse1();
  }

  public static Warehouse warehouse2() {
    return TestOrganizationFixtures.warehouse2();
  }

  public static CrupdateWarehouse warehouseToCrupdateWarehouse(Warehouse warehouse) {
    return TestOrganizationFixtures.warehouseToCrupdateWarehouse(warehouse);
  }

  public static CrupdateWarehouse someCreatableWarehouse() {
    return TestOrganizationFixtures.someCreatableWarehouse();
  }

  public static Equipment equipment1() {
    return TestOrganizationFixtures.equipment1();
  }

  public static Equipment equipment2() {
    return TestOrganizationFixtures.equipment2();
  }

  public static Equipment equipment3() {
    return TestOrganizationFixtures.equipment3();
  }

  public static CrupdateEquipment equipmentToCrupdateEquipment(Equipment equipment) {
    return TestOrganizationFixtures.equipmentToCrupdateEquipment(equipment);
  }

  public static CrupdateEquipment someCreatableEquipment() {
    return TestOrganizationFixtures.someCreatableEquipment();
  }

  public static Material material1() {
    return TestOrganizationFixtures.material1();
  }

  public static Material material2() {
    return TestOrganizationFixtures.material2();
  }

  public static Material material3() {
    return TestOrganizationFixtures.material3();
  }

  public static CrupdateMaterial materialToCrupdateMaterial(Material material) {
    return TestOrganizationFixtures.materialToCrupdateMaterial(material);
  }

  public static CrupdateMaterial someCreatableMaterial() {
    return TestOrganizationFixtures.someCreatableMaterial();
  }

  public static IncomeMoney income1() {
    return TestMoneyFixtures.income1();
  }

  public static IncomeMoney income2() {
    return TestMoneyFixtures.income2();
  }

  public static IncomeType incomeType1() {
    return TestMoneyFixtures.incomeType1();
  }

  public static IncomeType incomeType2() {
    return TestMoneyFixtures.incomeType2();
  }

  public static CrupdateIncomeType incomeTypeToCrupdateIncomeType(IncomeType incomeType) {
    return TestMoneyFixtures.incomeTypeToCrupdateIncomeType(incomeType);
  }

  public static CrupdateIncomeType someCreatableIncomeType() {
    return TestMoneyFixtures.someCreatableIncomeType();
  }

  public static CrupdateIncomeMoney incomeToCrupdateIncome(IncomeMoney income) {
    return TestMoneyFixtures.incomeToCrupdateIncome(income);
  }

  public static CrupdateIncomeMoney someCreatableIncome() {
    return TestMoneyFixtures.someCreatableIncome();
  }

  public static ExpenseMoney expense1() {
    return TestMoneyFixtures.expense1();
  }

  public static ExpenseMoney expense2() {
    return TestMoneyFixtures.expense2();
  }

  public static CrupdateExpenseMoney expenseToCrupdateExpense(ExpenseMoney expense) {
    return TestMoneyFixtures.expenseToCrupdateExpense(expense);
  }

  public static CrupdateExpenseMoney someCreatableExpense() {
    return TestMoneyFixtures.someCreatableExpense();
  }

  public static BankFee bankFee1() {
    return TestMoneyFixtures.bankFee1();
  }

  public static BankFee bankFee2() {
    return TestMoneyFixtures.bankFee2();
  }

  public static CrupdateBankFee bankFeeToCrupdateBankFee(BankFee bankFee) {
    return TestMoneyFixtures.bankFeeToCrupdateBankFee(bankFee);
  }

  public static CrupdateBankFee someCreatableBankFee() {
    return TestMoneyFixtures.someCreatableBankFee();
  }

  public static EmployeePayment employeePayment1() {
    return TestMoneyFixtures.employeePayment1();
  }

  public static EmployeePayment employeePayment2() {
    return TestMoneyFixtures.employeePayment2();
  }

  public static CrupdateEmployeePayment employeePaymentToCrupdateEmployeePayment(
      EmployeePayment employeePayment) {
    return TestMoneyFixtures.employeePaymentToCrupdateEmployeePayment(employeePayment);
  }

  public static CrupdateEmployeePayment someCreatableEmployeePayment() {
    return TestMoneyFixtures.someCreatableEmployeePayment();
  }

  public static OtherExpense otherExpense1() {
    return TestMoneyFixtures.otherExpense1();
  }

  public static OtherExpense otherExpense2() {
    return TestMoneyFixtures.otherExpense2();
  }

  public static CrupdateOtherExpense otherExpenseToCrupdateOtherExpense(OtherExpense otherExpense) {
    return TestMoneyFixtures.otherExpenseToCrupdateOtherExpense(otherExpense);
  }

  public static CrupdateOtherExpense someCreatableOtherExpense() {
    return TestMoneyFixtures.someCreatableOtherExpense();
  }

  public static Loan loan1() {
    return TestMoneyFixtures.loan1();
  }

  public static Loan loan2() {
    return TestMoneyFixtures.loan2();
  }

  public static CrupdateLoan loanToCrupdateLoan(Loan loan) {
    return TestMoneyFixtures.loanToCrupdateLoan(loan);
  }

  public static CrupdateLoan someCreatableLoan() {
    return TestMoneyFixtures.someCreatableLoan();
  }

  public static LoanRepayment repayment1() {
    return TestMoneyFixtures.repayment1();
  }

  public static CrupdateLoanRepayment repaymentToCrupdateRepayment(LoanRepayment repayment) {
    return TestMoneyFixtures.repaymentToCrupdateRepayment(repayment);
  }

  public static CrupdateLoanRepayment someCreatableRepayment() {
    return TestMoneyFixtures.someCreatableRepayment();
  }

  public static Purchase purchase1() {
    return TestMoneyFixtures.purchase1();
  }

  public static Purchase purchase2() {
    return TestMoneyFixtures.purchase2();
  }

  public static CrupdatePurchase purchaseToCrupdatePurchase(Purchase purchase) {
    return TestMoneyFixtures.purchaseToCrupdatePurchase(purchase);
  }

  public static CrupdatePurchase someCreatablePurchase() {
    return TestMoneyFixtures.someCreatablePurchase();
  }

  public static TravelExpense travelExpense1() {
    return TestTravelFixtures.travelExpense1();
  }

  public static TravelExpense travelExpense2() {
    return TestTravelFixtures.travelExpense2();
  }

  public static CrupdateTravelExpense travelExpenseToCrupdateTravelExpense(
      TravelExpense travelExpense) {
    return TestTravelFixtures.travelExpenseToCrupdateTravelExpense(travelExpense);
  }

  public static CrupdateTravelExpense someCreatableTravelExpense() {
    return TestTravelFixtures.someCreatableTravelExpense();
  }

  public static TravelPeople travelPeople1() {
    return TestTravelFixtures.travelPeople1();
  }

  public static TravelPeople travelPeople2() {
    return TestTravelFixtures.travelPeople2();
  }

  public static CrupdateTravelPeople travelPeopleToCrupdateTravelPeople(TravelPeople travelPeople) {
    return TestTravelFixtures.travelPeopleToCrupdateTravelPeople(travelPeople);
  }

  public static CrupdateTravelPeople someCreatableTravelPeople() {
    return TestTravelFixtures.someCreatableTravelPeople();
  }

  public static TravelMaterials travelMaterials1() {
    return TestTravelFixtures.travelMaterials1();
  }

  public static TravelMaterials travelMaterials2() {
    return TestTravelFixtures.travelMaterials2();
  }

  public static CrupdateTravelMaterials travelMaterialsToCrupdateTravelMaterials(
      TravelMaterials travelMaterials) {
    return TestTravelFixtures.travelMaterialsToCrupdateTravelMaterials(travelMaterials);
  }

  public static CrupdateTravelMaterials someCreatableTravelMaterials() {
    return TestTravelFixtures.someCreatableTravelMaterials();
  }

  public static TravelEquipment travelEquipment1() {
    return TestTravelFixtures.travelEquipment1();
  }

  public static TravelEquipment travelEquipment2() {
    return TestTravelFixtures.travelEquipment2();
  }

  public static CrupdateTravelEquipment travelEquipmentToCrupdateTravelEquipment(
      TravelEquipment travelEquipment) {
    return TestTravelFixtures.travelEquipmentToCrupdateTravelEquipment(travelEquipment);
  }

  public static CrupdateTravelEquipment someCreatableTravelEquipment() {
    return TestTravelFixtures.someCreatableTravelEquipment();
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
