package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.model.Company;
import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.money.*;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.Warehouse;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MoneyValidatorTest {

  private MoneyValidator validator;

  @BeforeEach
  void setUp() {
    validator = new MoneyValidator();
  }

  @Nested
  class ValidateMonetaryMovement {

    private MonetaryMovement validMovement;

    @BeforeEach
    void setUp() {
      validMovement = MonetaryMovement.builder().amount(1000).build();
    }

    @Test
    void should_pass_with_valid_amount() {
      assertThatCode(() -> validator.validateMonetaryMovement(validMovement))
          .doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateMonetaryMovement(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Monetary movement cannot be null");
    }

    @Test
    void should_throw_when_amount_null() {
      MonetaryMovement m = MonetaryMovement.builder().amount(null).build();
      assertThatThrownBy(() -> validator.validateMonetaryMovement(m))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Amount must be positive");
    }

    @Test
    void should_throw_when_amount_zero() {
      MonetaryMovement m = MonetaryMovement.builder().amount(0).build();
      assertThatThrownBy(() -> validator.validateMonetaryMovement(m))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Amount must be positive");
    }

    @Test
    void should_throw_when_amount_negative() {
      MonetaryMovement m = MonetaryMovement.builder().amount(-1).build();
      assertThatThrownBy(() -> validator.validateMonetaryMovement(m))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Amount must be positive");
    }
  }

  @Nested
  class ValidateExpenseMoney {

    private ExpenseMoney validExpense;

    @BeforeEach
    void setUp() {
      validExpense =
          ExpenseMoney.builder()
              .id("exp1")
              .job(Job.builder().id("job1").build())
              .amount(100)
              .build();
    }

    @Test
    void should_pass_with_valid_expense() {
      assertThatCode(() -> validator.validateExpenseMoney(validExpense)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateExpenseMoney(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Expense cannot be null");
    }

    @Test
    void should_throw_when_job_null() {
      ExpenseMoney e = ExpenseMoney.builder().id("exp1").job(null).build();
      assertThatThrownBy(() -> validator.validateExpenseMoney(e))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Expense must be associated with a job");
    }

    @Test
    void should_throw_when_job_without_id() {
      ExpenseMoney e = ExpenseMoney.builder().id("exp1").job(new Job()).build();
      assertThatThrownBy(() -> validator.validateExpenseMoney(e))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Expense must be associated with a job");
    }

    @Test
    void should_pass_when_amount_null() {
      ExpenseMoney e =
          ExpenseMoney.builder()
              .id("exp1")
              .job(Job.builder().id("job1").build())
              .amount(null)
              .build();
      assertThatCode(() -> validator.validateExpenseMoney(e)).doesNotThrowAnyException();
    }

    @Test
    void should_pass_when_amount_zero() {
      ExpenseMoney e =
          ExpenseMoney.builder().id("exp1").job(Job.builder().id("job1").build()).amount(0).build();
      assertThatCode(() -> validator.validateExpenseMoney(e)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_amount_negative() {
      ExpenseMoney e =
          ExpenseMoney.builder()
              .id("exp1")
              .job(Job.builder().id("job1").build())
              .amount(-1)
              .build();
      assertThatThrownBy(() -> validator.validateExpenseMoney(e))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Amount must be positive");
    }

    @Test
    void should_pass_when_amount_positive() {
      assertThatCode(() -> validator.validateExpenseMoney(validExpense)).doesNotThrowAnyException();
    }
  }

  @Nested
  class ValidateExpenseMonies {

    @Test
    void should_pass_with_non_empty_list() {
      List<ExpenseMoney> list =
          List.of(ExpenseMoney.builder().id("exp1").job(Job.builder().id("job1").build()).build());
      assertThatCode(() -> validator.validateExpenseMonies(list)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateExpenseMonies(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Expense list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validateExpenseMonies(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Expense list cannot be null or empty");
    }
  }

  @Nested
  class ValidateIncomeMoney {

    private IncomeMoney validIncome;

    @BeforeEach
    void setUp() {
      validIncome =
          IncomeMoney.builder()
              .id("inc1")
              .amount(2000)
              .sourceOrganization("Client Corp")
              .job(Job.builder().id("job1").build())
              .incomeType(IncomeType.builder().id("it1").build())
              .build();
    }

    @Test
    void should_pass_with_valid_income() {
      assertThatCode(() -> validator.validateIncomeMoney(validIncome)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateIncomeMoney(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Monetary movement cannot be null");
    }

    @Test
    void should_throw_when_amount_null() {
      IncomeMoney i =
          IncomeMoney.builder()
              .id("inc1")
              .amount(null)
              .sourceOrganization("Org")
              .job(Job.builder().id("job1").build())
              .incomeType(IncomeType.builder().id("it1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeMoney(i))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Amount must be positive");
    }

    @Test
    void should_throw_when_amount_zero() {
      IncomeMoney i =
          IncomeMoney.builder()
              .id("inc1")
              .amount(0)
              .sourceOrganization("Org")
              .job(Job.builder().id("job1").build())
              .incomeType(IncomeType.builder().id("it1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeMoney(i))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Amount must be positive");
    }

    @Test
    void should_throw_when_amount_negative() {
      IncomeMoney i =
          IncomeMoney.builder()
              .id("inc1")
              .amount(-1)
              .sourceOrganization("Org")
              .job(Job.builder().id("job1").build())
              .incomeType(IncomeType.builder().id("it1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeMoney(i))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Amount must be positive");
    }

    @Test
    void should_throw_when_job_null() {
      IncomeMoney i =
          IncomeMoney.builder()
              .id("inc1")
              .amount(2000)
              .sourceOrganization("Org")
              .job(null)
              .incomeType(IncomeType.builder().id("it1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeMoney(i))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income must be associated with a job");
    }

    @Test
    void should_throw_when_job_without_id() {
      IncomeMoney i =
          IncomeMoney.builder()
              .id("inc1")
              .amount(2000)
              .sourceOrganization("Org")
              .job(new Job())
              .incomeType(IncomeType.builder().id("it1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeMoney(i))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income must be associated with a job");
    }

    @Test
    void should_throw_when_sourceOrganization_null() {
      IncomeMoney i =
          IncomeMoney.builder()
              .id("inc1")
              .amount(2000)
              .sourceOrganization(null)
              .job(Job.builder().id("job1").build())
              .incomeType(IncomeType.builder().id("it1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeMoney(i))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Source organization is mandatory for income");
    }

    @Test
    void should_throw_when_sourceOrganization_empty() {
      IncomeMoney i =
          IncomeMoney.builder()
              .id("inc1")
              .amount(2000)
              .sourceOrganization("")
              .job(Job.builder().id("job1").build())
              .incomeType(IncomeType.builder().id("it1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeMoney(i))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Source organization is mandatory for income");
    }

    @Test
    void should_throw_when_sourceOrganization_blank() {
      IncomeMoney i =
          IncomeMoney.builder()
              .id("inc1")
              .amount(2000)
              .sourceOrganization("   ")
              .job(Job.builder().id("job1").build())
              .incomeType(IncomeType.builder().id("it1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeMoney(i))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Source organization is mandatory for income");
    }

    @Test
    void should_throw_when_incomeType_null() {
      IncomeMoney i =
          IncomeMoney.builder()
              .id("inc1")
              .amount(2000)
              .sourceOrganization("Org")
              .job(Job.builder().id("job1").build())
              .incomeType(null)
              .build();
      assertThatThrownBy(() -> validator.validateIncomeMoney(i))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income type is mandatory for income");
    }

    @Test
    void should_throw_when_incomeType_without_id() {
      IncomeMoney i =
          IncomeMoney.builder()
              .id("inc1")
              .amount(2000)
              .sourceOrganization("Org")
              .job(Job.builder().id("job1").build())
              .incomeType(new IncomeType())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeMoney(i))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income type is mandatory for income");
    }
  }

  @Nested
  class ValidateIncomeMonies {

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateIncomeMonies(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validateIncomeMonies(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income list cannot be null or empty");
    }
  }

  @Nested
  class ValidateIncomeType {

    private IncomeType validIncomeType;

    @BeforeEach
    void setUp() {
      validIncomeType =
          IncomeType.builder()
              .id("it1")
              .name("Consulting")
              .company(Company.builder().id("c1").build())
              .build();
    }

    @Test
    void should_pass_with_valid_income_type() {
      assertThatCode(() -> validator.validateIncomeType(validIncomeType))
          .doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateIncomeType(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income type cannot be null");
    }

    @Test
    void should_throw_when_name_null() {
      IncomeType it =
          IncomeType.builder()
              .id("it1")
              .name(null)
              .company(Company.builder().id("c1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeType(it))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income type name is mandatory");
    }

    @Test
    void should_throw_when_name_empty() {
      IncomeType it =
          IncomeType.builder()
              .id("it1")
              .name("")
              .company(Company.builder().id("c1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeType(it))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income type name is mandatory");
    }

    @Test
    void should_throw_when_name_blank() {
      IncomeType it =
          IncomeType.builder()
              .id("it1")
              .name("   ")
              .company(Company.builder().id("c1").build())
              .build();
      assertThatThrownBy(() -> validator.validateIncomeType(it))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income type name is mandatory");
    }

    @Test
    void should_throw_when_company_null() {
      IncomeType it = IncomeType.builder().id("it1").name("Consulting").company(null).build();
      assertThatThrownBy(() -> validator.validateIncomeType(it))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income type must be associated with a company");
    }

    @Test
    void should_throw_when_company_without_id() {
      IncomeType it =
          IncomeType.builder().id("it1").name("Consulting").company(new Company()).build();
      assertThatThrownBy(() -> validator.validateIncomeType(it))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income type must be associated with a company");
    }
  }

  @Nested
  class ValidateIncomeTypes {

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateIncomeTypes(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income type list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validateIncomeTypes(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income type list cannot be null or empty");
    }
  }

  @Nested
  class ValidateEmployeePayment {

    @Test
    void should_pass_with_valid_payment() {
      EmployeePayment p =
          EmployeePayment.builder()
              .id("ep1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .employee(User.builder().id("user1").build())
              .paymentType(EmployeePayment.PaymentType.MONTHLY)
              .paymentDescription("Monthly salary")
              .build();
      assertThatCode(() -> validator.validateEmployeePayment(p)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateEmployeePayment(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Employee payment cannot be null");
    }

    @Test
    void should_throw_when_expense_null() {
      EmployeePayment p =
          EmployeePayment.builder()
              .id("ep1")
              .expense(null)
              .employee(User.builder().id("user1").build())
              .paymentType(EmployeePayment.PaymentType.MONTHLY)
              .paymentDescription("Monthly salary")
              .build();
      assertThatThrownBy(() -> validator.validateEmployeePayment(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Employee payment must be linked to an expense");
    }

    @Test
    void should_throw_when_expense_without_id() {
      EmployeePayment p =
          EmployeePayment.builder()
              .id("ep1")
              .expense(new ExpenseMoney())
              .employee(User.builder().id("user1").build())
              .paymentType(EmployeePayment.PaymentType.MONTHLY)
              .paymentDescription("Monthly salary")
              .build();
      assertThatThrownBy(() -> validator.validateEmployeePayment(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Employee payment must be linked to an expense");
    }

    @Test
    void should_throw_when_employee_null() {
      EmployeePayment p =
          EmployeePayment.builder()
              .id("ep1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .employee(null)
              .paymentType(EmployeePayment.PaymentType.MONTHLY)
              .paymentDescription("Monthly salary")
              .build();
      assertThatThrownBy(() -> validator.validateEmployeePayment(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Employee payment must be linked to an employee");
    }

    @Test
    void should_throw_when_employee_without_id() {
      EmployeePayment p =
          EmployeePayment.builder()
              .id("ep1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .employee(new User())
              .paymentType(EmployeePayment.PaymentType.MONTHLY)
              .paymentDescription("Monthly salary")
              .build();
      assertThatThrownBy(() -> validator.validateEmployeePayment(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Employee payment must be linked to an employee");
    }

    @Test
    void should_throw_when_paymentType_null() {
      EmployeePayment p =
          EmployeePayment.builder()
              .id("ep1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .employee(User.builder().id("user1").build())
              .paymentType(null)
              .paymentDescription("Monthly salary")
              .build();
      assertThatThrownBy(() -> validator.validateEmployeePayment(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Payment type is mandatory");
    }

    @Test
    void should_throw_when_paymentDescription_null() {
      EmployeePayment p =
          EmployeePayment.builder()
              .id("ep1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .employee(User.builder().id("user1").build())
              .paymentType(EmployeePayment.PaymentType.MONTHLY)
              .paymentDescription(null)
              .build();
      assertThatThrownBy(() -> validator.validateEmployeePayment(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Payment description is mandatory");
    }

    @Test
    void should_throw_when_paymentDescription_empty() {
      EmployeePayment p =
          EmployeePayment.builder()
              .id("ep1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .employee(User.builder().id("user1").build())
              .paymentType(EmployeePayment.PaymentType.MONTHLY)
              .paymentDescription("")
              .build();
      assertThatThrownBy(() -> validator.validateEmployeePayment(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Payment description is mandatory");
    }

    @Test
    void should_throw_when_paymentDescription_blank() {
      EmployeePayment p =
          EmployeePayment.builder()
              .id("ep1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .employee(User.builder().id("user1").build())
              .paymentType(EmployeePayment.PaymentType.MONTHLY)
              .paymentDescription("   ")
              .build();
      assertThatThrownBy(() -> validator.validateEmployeePayment(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Payment description is mandatory");
    }
  }

  @Nested
  class ValidateEmployeePayments {

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateEmployeePayments(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Employee payment list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validateEmployeePayments(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Employee payment list cannot be null or empty");
    }
  }

  @Nested
  class ValidateTravelExpense {

    @Test
    void should_pass_with_valid_travel_expense() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .departureLocation(Warehouse.builder().id("wh1").build())
              .arrivalLocation(Warehouse.builder().id("wh2").build())
              .departureDate(Instant.parse("2024-01-01T08:00:00Z"))
              .arrivalDate(Instant.parse("2024-01-01T18:00:00Z"))
              .build();
      assertThatCode(() -> validator.validateTravelExpense(te)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateTravelExpense(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Travel expense cannot be null");
    }

    @Test
    void should_throw_when_expense_null() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(null)
              .departureLocation(Warehouse.builder().id("wh1").build())
              .arrivalLocation(Warehouse.builder().id("wh2").build())
              .departureDate(Instant.parse("2024-01-01T08:00:00Z"))
              .arrivalDate(Instant.parse("2024-01-01T18:00:00Z"))
              .build();
      assertThatThrownBy(() -> validator.validateTravelExpense(te))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Travel expense must be linked to an expense");
    }

    @Test
    void should_throw_when_expense_without_id() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(new ExpenseMoney())
              .departureLocation(Warehouse.builder().id("wh1").build())
              .arrivalLocation(Warehouse.builder().id("wh2").build())
              .departureDate(Instant.parse("2024-01-01T08:00:00Z"))
              .arrivalDate(Instant.parse("2024-01-01T18:00:00Z"))
              .build();
      assertThatThrownBy(() -> validator.validateTravelExpense(te))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Travel expense must be linked to an expense");
    }

    @Test
    void should_throw_when_departureLocation_null() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .departureLocation(null)
              .arrivalLocation(Warehouse.builder().id("wh2").build())
              .departureDate(Instant.parse("2024-01-01T08:00:00Z"))
              .arrivalDate(Instant.parse("2024-01-01T18:00:00Z"))
              .build();
      assertThatThrownBy(() -> validator.validateTravelExpense(te))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Departure location is mandatory");
    }

    @Test
    void should_throw_when_departureLocation_without_id() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .departureLocation(new Warehouse())
              .arrivalLocation(Warehouse.builder().id("wh2").build())
              .departureDate(Instant.parse("2024-01-01T08:00:00Z"))
              .arrivalDate(Instant.parse("2024-01-01T18:00:00Z"))
              .build();
      assertThatThrownBy(() -> validator.validateTravelExpense(te))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Departure location is mandatory");
    }

    @Test
    void should_throw_when_arrivalLocation_null() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .departureLocation(Warehouse.builder().id("wh1").build())
              .arrivalLocation(null)
              .departureDate(Instant.parse("2024-01-01T08:00:00Z"))
              .arrivalDate(Instant.parse("2024-01-01T18:00:00Z"))
              .build();
      assertThatThrownBy(() -> validator.validateTravelExpense(te))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Arrival location is mandatory");
    }

    @Test
    void should_throw_when_arrivalLocation_without_id() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .departureLocation(Warehouse.builder().id("wh1").build())
              .arrivalLocation(new Warehouse())
              .departureDate(Instant.parse("2024-01-01T08:00:00Z"))
              .arrivalDate(Instant.parse("2024-01-01T18:00:00Z"))
              .build();
      assertThatThrownBy(() -> validator.validateTravelExpense(te))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Arrival location is mandatory");
    }

    @Test
    void should_throw_when_departureDate_null() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .departureLocation(Warehouse.builder().id("wh1").build())
              .arrivalLocation(Warehouse.builder().id("wh2").build())
              .departureDate(null)
              .arrivalDate(Instant.parse("2024-01-01T18:00:00Z"))
              .build();
      assertThatThrownBy(() -> validator.validateTravelExpense(te))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Departure date is mandatory");
    }

    @Test
    void should_throw_when_arrivalDate_null() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .departureLocation(Warehouse.builder().id("wh1").build())
              .arrivalLocation(Warehouse.builder().id("wh2").build())
              .departureDate(Instant.parse("2024-01-01T08:00:00Z"))
              .arrivalDate(null)
              .build();
      assertThatThrownBy(() -> validator.validateTravelExpense(te))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Arrival date is mandatory");
    }

    @Test
    void should_throw_when_departure_after_arrival() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .departureLocation(Warehouse.builder().id("wh1").build())
              .arrivalLocation(Warehouse.builder().id("wh2").build())
              .departureDate(Instant.parse("2024-01-02T08:00:00Z"))
              .arrivalDate(Instant.parse("2024-01-01T18:00:00Z"))
              .build();
      assertThatThrownBy(() -> validator.validateTravelExpense(te))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Departure date cannot be after arrival date");
    }

    @Test
    void should_pass_when_departure_before_arrival() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .departureLocation(Warehouse.builder().id("wh1").build())
              .arrivalLocation(Warehouse.builder().id("wh2").build())
              .departureDate(Instant.parse("2024-01-01T08:00:00Z"))
              .arrivalDate(Instant.parse("2024-01-02T08:00:00Z"))
              .build();
      assertThatCode(() -> validator.validateTravelExpense(te)).doesNotThrowAnyException();
    }

    @Test
    void should_pass_when_departure_equals_arrival() {
      TravelExpense te =
          TravelExpense.builder()
              .id("te1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .departureLocation(Warehouse.builder().id("wh1").build())
              .arrivalLocation(Warehouse.builder().id("wh2").build())
              .departureDate(Instant.parse("2024-01-01T08:00:00Z"))
              .arrivalDate(Instant.parse("2024-01-01T08:00:00Z"))
              .build();
      assertThatCode(() -> validator.validateTravelExpense(te)).doesNotThrowAnyException();
    }
  }

  @Nested
  class ValidateTravelExpenses {

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateTravelExpenses(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Travel expense list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validateTravelExpenses(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Travel expense list cannot be null or empty");
    }
  }

  @Nested
  class ValidatePurchase {

    @Test
    void should_pass_with_valid_purchase_isEquipment_true() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(true)
              .quantity(5)
              .equipment(Equipment.builder().id("eq1").build())
              .build();
      assertThatCode(() -> validator.validatePurchase(p)).doesNotThrowAnyException();
    }

    @Test
    void should_pass_with_valid_purchase_isEquipment_false() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(false)
              .quantity(5)
              .material(Material.builder().id("mat1").build())
              .build();
      assertThatCode(() -> validator.validatePurchase(p)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validatePurchase(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Purchase cannot be null");
    }

    @Test
    void should_throw_when_expense_null() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(null)
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(true)
              .quantity(5)
              .equipment(Equipment.builder().id("eq1").build())
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Purchase must be linked to an expense");
    }

    @Test
    void should_throw_when_expense_without_id() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(new ExpenseMoney())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(true)
              .quantity(5)
              .equipment(Equipment.builder().id("eq1").build())
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Purchase must be linked to an expense");
    }

    @Test
    void should_throw_when_supplier_null() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(null)
              .isEquipment(true)
              .quantity(5)
              .equipment(Equipment.builder().id("eq1").build())
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Supplier is mandatory for purchase");
    }

    @Test
    void should_throw_when_supplier_without_id() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(new Warehouse())
              .isEquipment(true)
              .quantity(5)
              .equipment(Equipment.builder().id("eq1").build())
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Supplier is mandatory for purchase");
    }

    @Test
    void should_throw_when_isEquipment_null() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(null)
              .quantity(5)
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("IsEquipment flag is mandatory");
    }

    @Test
    void should_throw_when_quantity_null() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(true)
              .quantity(null)
              .equipment(Equipment.builder().id("eq1").build())
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Quantity must be positive");
    }

    @Test
    void should_throw_when_quantity_zero() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(true)
              .quantity(0)
              .equipment(Equipment.builder().id("eq1").build())
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Quantity must be positive");
    }

    @Test
    void should_throw_when_quantity_negative() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(true)
              .quantity(-1)
              .equipment(Equipment.builder().id("eq1").build())
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Quantity must be positive");
    }

    @Test
    void should_throw_when_isEquipment_true_and_equipment_null() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(true)
              .quantity(5)
              .equipment(null)
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Equipment is mandatory when isEquipment is true");
    }

    @Test
    void should_throw_when_isEquipment_true_and_equipment_without_id() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(true)
              .quantity(5)
              .equipment(new Equipment())
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Equipment is mandatory when isEquipment is true");
    }

    @Test
    void should_throw_when_isEquipment_false_and_material_null() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(false)
              .quantity(5)
              .material(null)
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Material is mandatory when isEquipment is false");
    }

    @Test
    void should_throw_when_isEquipment_false_and_material_without_id() {
      Purchase p =
          Purchase.builder()
              .id("p1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .supplier(Warehouse.builder().id("wh1").build())
              .isEquipment(false)
              .quantity(5)
              .material(new Material())
              .build();
      assertThatThrownBy(() -> validator.validatePurchase(p))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Material is mandatory when isEquipment is false");
    }
  }

  @Nested
  class ValidatePurchases {

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validatePurchases(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Purchase list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validatePurchases(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Purchase list cannot be null or empty");
    }
  }

  @Nested
  class ValidateBankFee {

    @Test
    void should_pass_with_valid_bank_fee() {
      BankFee bf =
          BankFee.builder()
              .id("bf1")
              .expense(ExpenseMoney.builder().id("exp1").amount(500).build())
              .bankName("BNI")
              .build();
      assertThatCode(() -> validator.validateBankFee(bf)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateBankFee(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank fee cannot be null");
    }

    @Test
    void should_throw_when_expense_null() {
      BankFee bf = BankFee.builder().id("bf1").expense(null).bankName("BNI").build();
      assertThatThrownBy(() -> validator.validateBankFee(bf))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank fee must be linked to an expense");
    }

    @Test
    void should_throw_when_expense_without_id() {
      BankFee bf = BankFee.builder().id("bf1").expense(new ExpenseMoney()).bankName("BNI").build();
      assertThatThrownBy(() -> validator.validateBankFee(bf))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank fee must be linked to an expense");
    }

    @Test
    void should_throw_when_expense_amount_null() {
      BankFee bf =
          BankFee.builder()
              .id("bf1")
              .expense(ExpenseMoney.builder().id("exp1").amount(null).build())
              .bankName("BNI")
              .build();
      assertThatThrownBy(() -> validator.validateBankFee(bf))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank fee amount must be positive");
    }

    @Test
    void should_throw_when_expense_amount_zero() {
      BankFee bf =
          BankFee.builder()
              .id("bf1")
              .expense(ExpenseMoney.builder().id("exp1").amount(0).build())
              .bankName("BNI")
              .build();
      assertThatThrownBy(() -> validator.validateBankFee(bf))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank fee amount must be positive");
    }

    @Test
    void should_throw_when_expense_amount_negative() {
      BankFee bf =
          BankFee.builder()
              .id("bf1")
              .expense(ExpenseMoney.builder().id("exp1").amount(-1).build())
              .bankName("BNI")
              .build();
      assertThatThrownBy(() -> validator.validateBankFee(bf))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank fee amount must be positive");
    }

    @Test
    void should_pass_when_expense_amount_positive() {
      BankFee bf =
          BankFee.builder()
              .id("bf1")
              .expense(ExpenseMoney.builder().id("exp1").amount(500).build())
              .bankName("BNI")
              .build();
      assertThatCode(() -> validator.validateBankFee(bf)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_bankName_null() {
      BankFee bf =
          BankFee.builder()
              .id("bf1")
              .expense(ExpenseMoney.builder().id("exp1").amount(500).build())
              .bankName(null)
              .build();
      assertThatThrownBy(() -> validator.validateBankFee(bf))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank name is mandatory");
    }

    @Test
    void should_throw_when_bankName_empty() {
      BankFee bf =
          BankFee.builder()
              .id("bf1")
              .expense(ExpenseMoney.builder().id("exp1").amount(500).build())
              .bankName("")
              .build();
      assertThatThrownBy(() -> validator.validateBankFee(bf))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank name is mandatory");
    }

    @Test
    void should_throw_when_bankName_blank() {
      BankFee bf =
          BankFee.builder()
              .id("bf1")
              .expense(ExpenseMoney.builder().id("exp1").amount(500).build())
              .bankName("   ")
              .build();
      assertThatThrownBy(() -> validator.validateBankFee(bf))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank name is mandatory");
    }
  }

  @Nested
  class ValidateBankFees {

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateBankFees(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank fee list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validateBankFees(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Bank fee list cannot be null or empty");
    }
  }

  @Nested
  class ValidateOtherExpense {

    @Test
    void should_pass_with_valid_other_expense() {
      OtherExpense oe =
          OtherExpense.builder()
              .id("oe1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .description("Office supplies")
              .build();
      assertThatCode(() -> validator.validateOtherExpense(oe)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateOtherExpense(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Other expense cannot be null");
    }

    @Test
    void should_throw_when_expense_null() {
      OtherExpense oe =
          OtherExpense.builder().id("oe1").expense(null).description("Office supplies").build();
      assertThatThrownBy(() -> validator.validateOtherExpense(oe))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Other expense must be linked to an expense");
    }

    @Test
    void should_throw_when_expense_without_id() {
      OtherExpense oe =
          OtherExpense.builder()
              .id("oe1")
              .expense(new ExpenseMoney())
              .description("Office supplies")
              .build();
      assertThatThrownBy(() -> validator.validateOtherExpense(oe))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Other expense must be linked to an expense");
    }

    @Test
    void should_throw_when_description_null() {
      OtherExpense oe =
          OtherExpense.builder()
              .id("oe1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .description(null)
              .build();
      assertThatThrownBy(() -> validator.validateOtherExpense(oe))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Description is mandatory for other expense");
    }

    @Test
    void should_throw_when_description_empty() {
      OtherExpense oe =
          OtherExpense.builder()
              .id("oe1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .description("")
              .build();
      assertThatThrownBy(() -> validator.validateOtherExpense(oe))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Description is mandatory for other expense");
    }

    @Test
    void should_throw_when_description_blank() {
      OtherExpense oe =
          OtherExpense.builder()
              .id("oe1")
              .expense(ExpenseMoney.builder().id("exp1").build())
              .description("   ")
              .build();
      assertThatThrownBy(() -> validator.validateOtherExpense(oe))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Description is mandatory for other expense");
    }
  }

  @Nested
  class ValidateOtherExpenses {

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateOtherExpenses(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Other expense list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validateOtherExpenses(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Other expense list cannot be null or empty");
    }
  }

  @Nested
  class ValidateLoan {

    @Test
    void should_pass_with_valid_loan() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(10000)
              .lender("Bank ABC")
              .interestRate(5)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(Job.builder().id("job1").build())
              .build();
      assertThatCode(() -> validator.validateLoan(loan)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateLoan(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan cannot be null");
    }

    @Test
    void should_throw_when_amount_null() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(null)
              .lender("Bank ABC")
              .interestRate(5)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(Job.builder().id("job1").build())
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan amount must be positive");
    }

    @Test
    void should_throw_when_amount_zero() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(0)
              .lender("Bank ABC")
              .interestRate(5)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(Job.builder().id("job1").build())
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan amount must be positive");
    }

    @Test
    void should_throw_when_amount_negative() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(-1)
              .lender("Bank ABC")
              .interestRate(5)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(Job.builder().id("job1").build())
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan amount must be positive");
    }

    @Test
    void should_throw_when_lender_null() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(10000)
              .lender(null)
              .interestRate(5)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(Job.builder().id("job1").build())
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Lender is mandatory for loan");
    }

    @Test
    void should_throw_when_lender_empty() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(10000)
              .lender("")
              .interestRate(5)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(Job.builder().id("job1").build())
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Lender is mandatory for loan");
    }

    @Test
    void should_throw_when_lender_blank() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(10000)
              .lender("   ")
              .interestRate(5)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(Job.builder().id("job1").build())
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Lender is mandatory for loan");
    }

    @Test
    void should_throw_when_interestRate_null() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(10000)
              .lender("Bank ABC")
              .interestRate(null)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(Job.builder().id("job1").build())
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Interest rate must be non-negative");
    }

    @Test
    void should_throw_when_interestRate_negative() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(10000)
              .lender("Bank ABC")
              .interestRate(-1)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(Job.builder().id("job1").build())
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Interest rate must be non-negative");
    }

    @Test
    void should_pass_when_interestRate_zero() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(10000)
              .lender("Bank ABC")
              .interestRate(0)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(Job.builder().id("job1").build())
              .build();
      assertThatCode(() -> validator.validateLoan(loan)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_startDate_null() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(10000)
              .lender("Bank ABC")
              .interestRate(5)
              .startDate(null)
              .job(Job.builder().id("job1").build())
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Start date is mandatory for loan");
    }

    @Test
    void should_throw_when_job_null() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(10000)
              .lender("Bank ABC")
              .interestRate(5)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(null)
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan must be associated with a job");
    }

    @Test
    void should_throw_when_job_without_id() {
      Loan loan =
          Loan.builder()
              .id("l1")
              .amount(10000)
              .lender("Bank ABC")
              .interestRate(5)
              .startDate(LocalDate.of(2024, 1, 15))
              .job(new Job())
              .build();
      assertThatThrownBy(() -> validator.validateLoan(loan))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan must be associated with a job");
    }
  }

  @Nested
  class ValidateLoans {

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateLoans(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validateLoans(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan list cannot be null or empty");
    }
  }

  @Nested
  class ValidateLoanRepayment {

    @Test
    void should_pass_with_valid_repayment() {
      LoanRepayment r =
          LoanRepayment.builder()
              .id("r1")
              .loan(Loan.builder().id("l1").build())
              .paymentDate(LocalDate.of(2024, 2, 15))
              .amount(1000)
              .build();
      assertThatCode(() -> validator.validateLoanRepayment(r)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateLoanRepayment(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan repayment cannot be null");
    }

    @Test
    void should_throw_when_loan_null() {
      LoanRepayment r =
          LoanRepayment.builder()
              .id("r1")
              .loan(null)
              .paymentDate(LocalDate.of(2024, 2, 15))
              .amount(1000)
              .build();
      assertThatThrownBy(() -> validator.validateLoanRepayment(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan repayment must be linked to a loan");
    }

    @Test
    void should_throw_when_loan_without_id() {
      LoanRepayment r =
          LoanRepayment.builder()
              .id("r1")
              .loan(new Loan())
              .paymentDate(LocalDate.of(2024, 2, 15))
              .amount(1000)
              .build();
      assertThatThrownBy(() -> validator.validateLoanRepayment(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan repayment must be linked to a loan");
    }

    @Test
    void should_throw_when_paymentDate_null() {
      LoanRepayment r =
          LoanRepayment.builder()
              .id("r1")
              .loan(Loan.builder().id("l1").build())
              .paymentDate(null)
              .amount(1000)
              .build();
      assertThatThrownBy(() -> validator.validateLoanRepayment(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Payment date is mandatory for loan repayment");
    }

    @Test
    void should_throw_when_amount_null() {
      LoanRepayment r =
          LoanRepayment.builder()
              .id("r1")
              .loan(Loan.builder().id("l1").build())
              .paymentDate(LocalDate.of(2024, 2, 15))
              .amount(null)
              .build();
      assertThatThrownBy(() -> validator.validateLoanRepayment(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Repayment amount must be positive");
    }

    @Test
    void should_throw_when_amount_zero() {
      LoanRepayment r =
          LoanRepayment.builder()
              .id("r1")
              .loan(Loan.builder().id("l1").build())
              .paymentDate(LocalDate.of(2024, 2, 15))
              .amount(0)
              .build();
      assertThatThrownBy(() -> validator.validateLoanRepayment(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Repayment amount must be positive");
    }

    @Test
    void should_throw_when_amount_negative() {
      LoanRepayment r =
          LoanRepayment.builder()
              .id("r1")
              .loan(Loan.builder().id("l1").build())
              .paymentDate(LocalDate.of(2024, 2, 15))
              .amount(-1)
              .build();
      assertThatThrownBy(() -> validator.validateLoanRepayment(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Repayment amount must be positive");
    }
  }

  @Nested
  class ValidateLoanRepayments {

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateLoanRepayments(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan repayment list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validateLoanRepayments(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Loan repayment list cannot be null or empty");
    }
  }

  @Nested
  class ValidateIncomeReceipt {

    @Test
    void should_pass_with_valid_receipt() {
      IncomeReceipt r =
          IncomeReceipt.builder()
              .id("ir1")
              .income(IncomeMoney.builder().id("inc1").build())
              .paymentDate(LocalDate.of(2024, 1, 31))
              .amount(2000)
              .build();
      assertThatCode(() -> validator.validateIncomeReceipt(r)).doesNotThrowAnyException();
    }

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateIncomeReceipt(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income receipt cannot be null");
    }

    @Test
    void should_throw_when_income_null() {
      IncomeReceipt r =
          IncomeReceipt.builder()
              .id("ir1")
              .income(null)
              .paymentDate(LocalDate.of(2024, 1, 31))
              .amount(2000)
              .build();
      assertThatThrownBy(() -> validator.validateIncomeReceipt(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income receipt must be linked to an income");
    }

    @Test
    void should_throw_when_income_without_id() {
      IncomeReceipt r =
          IncomeReceipt.builder()
              .id("ir1")
              .income(new IncomeMoney())
              .paymentDate(LocalDate.of(2024, 1, 31))
              .amount(2000)
              .build();
      assertThatThrownBy(() -> validator.validateIncomeReceipt(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income receipt must be linked to an income");
    }

    @Test
    void should_throw_when_paymentDate_null() {
      IncomeReceipt r =
          IncomeReceipt.builder()
              .id("ir1")
              .income(IncomeMoney.builder().id("inc1").build())
              .paymentDate(null)
              .amount(2000)
              .build();
      assertThatThrownBy(() -> validator.validateIncomeReceipt(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Payment date is mandatory for income receipt");
    }

    @Test
    void should_throw_when_amount_null() {
      IncomeReceipt r =
          IncomeReceipt.builder()
              .id("ir1")
              .income(IncomeMoney.builder().id("inc1").build())
              .paymentDate(LocalDate.of(2024, 1, 31))
              .amount(null)
              .build();
      assertThatThrownBy(() -> validator.validateIncomeReceipt(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Receipt amount must be positive");
    }

    @Test
    void should_throw_when_amount_zero() {
      IncomeReceipt r =
          IncomeReceipt.builder()
              .id("ir1")
              .income(IncomeMoney.builder().id("inc1").build())
              .paymentDate(LocalDate.of(2024, 1, 31))
              .amount(0)
              .build();
      assertThatThrownBy(() -> validator.validateIncomeReceipt(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Receipt amount must be positive");
    }

    @Test
    void should_throw_when_amount_negative() {
      IncomeReceipt r =
          IncomeReceipt.builder()
              .id("ir1")
              .income(IncomeMoney.builder().id("inc1").build())
              .paymentDate(LocalDate.of(2024, 1, 31))
              .amount(-1)
              .build();
      assertThatThrownBy(() -> validator.validateIncomeReceipt(r))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Receipt amount must be positive");
    }
  }

  @Nested
  class ValidateIncomeReceipts {

    @Test
    void should_throw_when_null() {
      assertThatThrownBy(() -> validator.validateIncomeReceipts(null))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income receipt list cannot be null or empty");
    }

    @Test
    void should_throw_when_empty() {
      assertThatThrownBy(() -> validator.validateIncomeReceipts(Collections.emptyList()))
          .isInstanceOf(BadRequestException.class)
          .hasMessageContaining("Income receipt list cannot be null or empty");
    }
  }
}
