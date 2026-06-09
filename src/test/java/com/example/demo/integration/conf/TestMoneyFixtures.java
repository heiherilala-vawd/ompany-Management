package com.example.demo.integration.conf;

import static com.example.demo.integration.conf.TestOrganizationFixtures.warehouse1;

import com.example.demo.client.model.BankFee;
import com.example.demo.client.model.BudgetLine;
import com.example.demo.client.model.CashAccount;
import com.example.demo.client.model.CashTransaction;
import com.example.demo.client.model.CashTransactionType;
import com.example.demo.client.model.CompanyFixedCost;
import com.example.demo.client.model.CrupdateBankFee;
import com.example.demo.client.model.CrupdateBudgetLine;
import com.example.demo.client.model.CrupdateCashAccount;
import com.example.demo.client.model.CrupdateCashTransaction;
import com.example.demo.client.model.CrupdateCompanyFixedCost;
import com.example.demo.client.model.CrupdateEmployeePayment;
import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.CrupdateIncomeReceipt;
import com.example.demo.client.model.CrupdateIncomeType;
import com.example.demo.client.model.CrupdateLoan;
import com.example.demo.client.model.CrupdateLoanRepayment;
import com.example.demo.client.model.CrupdateMaintenance;
import com.example.demo.client.model.CrupdateOtherExpense;
import com.example.demo.client.model.CrupdateOtherExpenseType;
import com.example.demo.client.model.CrupdatePurchase;
import com.example.demo.client.model.CrupdatePurchaseOrder;
import com.example.demo.client.model.CrupdatePurchaseOrderLine;
import com.example.demo.client.model.CrupdateSupplier;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.EmployeePayment;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.client.model.IncomeReceipt;
import com.example.demo.client.model.IncomeType;
import com.example.demo.client.model.Loan;
import com.example.demo.client.model.LoanRepayment;
import com.example.demo.client.model.LoanStatus;
import com.example.demo.client.model.Maintenance;
import com.example.demo.client.model.OtherExpense;
import com.example.demo.client.model.OtherExpenseType;
import com.example.demo.client.model.PaymentType;
import com.example.demo.client.model.Purchase;
import com.example.demo.client.model.PurchaseOrder;
import com.example.demo.client.model.PurchaseOrderLine;
import com.example.demo.client.model.PurchaseOrderStatus;
import com.example.demo.client.model.Supplier;
import com.example.demo.client.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

final class TestMoneyFixtures {

  private TestMoneyFixtures() {}

  static IncomeMoney income1() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME1_ID);
    income.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    income.setSourceOrganization("Client Alpha");
    income.setInvoiceReference("INV-2024-001");
    income.setBillingStartDate(LocalDate.of(2024, 1, 15));
    income.setDueDate(LocalDate.of(2024, 2, 15));
    income.setPaymentTerms("NET-30");
    income.setIncomeType(incomeType1());
    income.setAmount(new BigDecimal("150000.00"));
    income.setDescription("Paiement initial chantier A");
    IncomeReceipt receipt = new IncomeReceipt();
    receipt.setId(TestUtils.RECEIPT1_ID);
    receipt.setPaymentDate(LocalDate.of(2024, 2, 1));
    receipt.setAmount(new BigDecimal("150000.00"));
    income.setReceipts(List.of(receipt));
    income.setRemainingAmount(new BigDecimal("0.00"));
    return income;
  }

  static IncomeMoney income2() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME2_ID);
    income.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    income.setSourceOrganization("Client Beta");
    income.setInvoiceReference("INV-2024-002");
    income.setBillingStartDate(LocalDate.of(2024, 2, 10));
    income.setDueDate(LocalDate.of(2024, 3, 12));
    income.setPaymentTerms("NET-30");
    income.setIncomeType(incomeType1());
    income.setAmount(new BigDecimal("275000.00"));
    income.setDescription("Paiement avance renovation hotel");
    return income;
  }

  static IncomeType incomeType1() {
    IncomeType incomeType = new IncomeType();
    incomeType.setId(TestUtils.INCOME_TYPE1_ID);
    incomeType.setName("Facturation client");
    incomeType.setDescription("Revenus issus de la facturation client");
    incomeType.setCompanyId(TestUtils.COMPANY1_ID);
    return incomeType;
  }

  static IncomeType incomeType2() {
    IncomeType incomeType = new IncomeType();
    incomeType.setId(TestUtils.INCOME_TYPE2_ID);
    incomeType.setName("Subvention");
    incomeType.setDescription("Aides et subventions recues");
    incomeType.setCompanyId(TestUtils.COMPANY1_ID);
    return incomeType;
  }

  static CrupdateIncomeType incomeTypeToCrupdateIncomeType(IncomeType incomeType) {
    CrupdateIncomeType crupdateIncomeType = new CrupdateIncomeType();
    crupdateIncomeType.setId(incomeType.getId());
    crupdateIncomeType.setName(incomeType.getName());
    crupdateIncomeType.setDescription(incomeType.getDescription());
    crupdateIncomeType.setCompanyId(incomeType.getCompanyId());
    crupdateIncomeType.setComment(incomeType.getComment());
    return crupdateIncomeType;
  }

  static CrupdateIncomeType someCreatableIncomeType() {
    CrupdateIncomeType incomeType = new CrupdateIncomeType();
    incomeType.setId(UUID.randomUUID().toString());
    incomeType.setName("Don");
    incomeType.setDescription("Dons et apports exceptionnels");
    incomeType.setCompanyId(TestUtils.COMPANY1_ID);
    return incomeType;
  }

  static CrupdateIncomeMoney incomeToCrupdateIncome(IncomeMoney income) {
    CrupdateIncomeMoney crupdateIncome = new CrupdateIncomeMoney();
    crupdateIncome.setId(income.getId());
    crupdateIncome.setJobId(income.getJob() != null ? income.getJob().getId() : null);
    crupdateIncome.setSourceOrganization(income.getSourceOrganization());
    crupdateIncome.setInvoiceReference(income.getInvoiceReference());
    crupdateIncome.setBillingStartDate(income.getBillingStartDate());
    crupdateIncome.setDueDate(income.getDueDate());
    crupdateIncome.setPaymentTerms(income.getPaymentTerms());
    crupdateIncome.setIncomeTypeId(
        income.getIncomeType() != null ? income.getIncomeType().getId() : null);
    crupdateIncome.setAmount(income.getAmount());
    crupdateIncome.setDescription(income.getDescription());
    crupdateIncome.setComment(income.getComment());
    return crupdateIncome;
  }

  static CrupdateIncomeMoney someCreatableIncome() {
    CrupdateIncomeMoney income = new CrupdateIncomeMoney();
    income.setId(UUID.randomUUID().toString());
    income.setJobId(TestUtils.JOB1_ID);
    income.setSourceOrganization("Client Gamma");
    income.setInvoiceReference("INV-2024-003");
    income.setBillingStartDate(LocalDate.of(2024, 3, 1));
    income.setDueDate(LocalDate.of(2024, 3, 31));
    income.setPaymentTerms("NET-30");
    income.setIncomeTypeId(TestUtils.INCOME_TYPE2_ID);
    income.setAmount(new BigDecimal("99000.00"));
    income.setDescription("Paiement complementaire");
    return income;
  }

  static ExpenseMoney expense1() {
    ExpenseMoney expense = new ExpenseMoney();
    expense.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    expense.setId(TestUtils.EXPENSE1_ID);
    expense.setAmount(new BigDecimal("45000.00"));
    expense.setDescription("Achat materiaux chantier A");
    return expense;
  }

  static ExpenseMoney expense2() {
    ExpenseMoney expense = new ExpenseMoney();
    expense.setId(TestUtils.EXPENSE2_ID);
    expense.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    expense.setAmount(new BigDecimal("80000.00"));
    expense.setDescription("Paiement sous-traitant renovation");
    return expense;
  }

  static CrupdateExpenseMoney expenseToCrupdateExpense(ExpenseMoney expense) {
    CrupdateExpenseMoney crupdateExpense = new CrupdateExpenseMoney();
    crupdateExpense.setId(expense.getId());
    crupdateExpense.setJobId(expense.getJob() != null ? expense.getJob().getId() : null);
    crupdateExpense.setAmount(expense.getAmount());
    crupdateExpense.setDescription(expense.getDescription());
    crupdateExpense.setComment(expense.getComment());
    return crupdateExpense;
  }

  static CrupdateExpenseMoney someCreatableExpense() {
    CrupdateExpenseMoney expense = new CrupdateExpenseMoney();
    expense.setId(UUID.randomUUID().toString());
    expense.setJobId(TestUtils.JOB1_ID);
    expense.setAmount(new BigDecimal("32000.00"));
    expense.setDescription("Frais logistiques");
    return expense;
  }

  static BankFee bankFee1() {
    BankFee bankFee = new BankFee();
    bankFee.setId(TestUtils.BANK_FEE1_ID);
    bankFee.setExpense(expenseToCrupdateExpense(expense1()));
    bankFee.setBankName("BNI Madagascar");
    bankFee.setDescription("Frais virement fournisseur");
    return bankFee;
  }

  static BankFee bankFee2() {
    BankFee bankFee = new BankFee();
    bankFee.setId(TestUtils.BANK_FEE2_ID);
    bankFee.setExpense(expenseToCrupdateExpense(expense2()));
    bankFee.setBankName("BOA Madagascar");
    bankFee.setDescription("Commission paiement sous-traitant");
    return bankFee;
  }

  static CrupdateBankFee bankFeeToCrupdateBankFee(BankFee bankFee) {
    CrupdateBankFee crupdateBankFee = new CrupdateBankFee();
    crupdateBankFee.setId(bankFee.getId());
    crupdateBankFee.setExpenseId(
        bankFee.getExpense() != null ? bankFee.getExpense().getId() : null);
    crupdateBankFee.setBankName(bankFee.getBankName());
    crupdateBankFee.setDescription(bankFee.getDescription());
    return crupdateBankFee;
  }

  static CrupdateBankFee someCreatableBankFee() {
    CrupdateBankFee bankFee = new CrupdateBankFee();
    bankFee.setId(UUID.randomUUID().toString());
    bankFee.setExpenseId(TestUtils.EXPENSE1_ID);
    bankFee.setBankName("MCB Madagascar");
    bankFee.setDescription("Frais tenue compte");
    return bankFee;
  }

  static EmployeePayment employeePayment1() {
    EmployeePayment employeePayment = new EmployeePayment();
    employeePayment.setId(TestUtils.EMPLOYEE_PAYMENT1_ID);
    employeePayment.setExpense(expenseToCrupdateExpense(expense1()));
    employeePayment.setUsers(List.of(TestUserFixtures.employee1()));
    employeePayment.setIsForTeam(false);
    employeePayment.setPaymentDescription("Avance salaire chantier A");
    employeePayment.setPaymentType(PaymentType.ADVANCE);
    return employeePayment;
  }

  static EmployeePayment employeePayment2() {
    EmployeePayment employeePayment = new EmployeePayment();
    employeePayment.setId(TestUtils.EMPLOYEE_PAYMENT2_ID);
    employeePayment.setExpense(expenseToCrupdateExpense(expense2()));
    employeePayment.setUsers(List.of(TestUserFixtures.user1()));
    employeePayment.setIsForTeam(false);
    employeePayment.setPaymentDescription("Paiement mensuel renovation");
    employeePayment.setPaymentType(PaymentType.MONTHLY);
    return employeePayment;
  }

  static CrupdateEmployeePayment employeePaymentToCrupdateEmployeePayment(
      EmployeePayment employeePayment) {
    CrupdateEmployeePayment crupdateEmployeePayment = new CrupdateEmployeePayment();
    crupdateEmployeePayment.setId(employeePayment.getId());
    crupdateEmployeePayment.setExpenseId(
        employeePayment.getExpense() != null ? employeePayment.getExpense().getId() : null);
    crupdateEmployeePayment.setUserIds(
        employeePayment.getUsers() != null
            ? employeePayment.getUsers().stream().map(User::getId).toList()
            : null);
    crupdateEmployeePayment.setPaymentDescription(employeePayment.getPaymentDescription());
    crupdateEmployeePayment.setPaymentType(employeePayment.getPaymentType());
    return crupdateEmployeePayment;
  }

  static CrupdateEmployeePayment someCreatableEmployeePayment() {
    CrupdateEmployeePayment employeePayment = new CrupdateEmployeePayment();
    employeePayment.setId(UUID.randomUUID().toString());
    employeePayment.setExpenseId(TestUtils.EXPENSE1_ID);
    employeePayment.setUserIds(List.of(TestUtils.EMPLOYEE_ID));
    employeePayment.setPaymentDescription("Prime exceptionnelle");
    employeePayment.setPaymentType(PaymentType.OTHER);
    return employeePayment;
  }

  static OtherExpense otherExpense1() {
    OtherExpense otherExpense = new OtherExpense();
    otherExpense.setId(TestUtils.OTHER_EXPENSE1_ID);
    otherExpense.setExpense(expenseToCrupdateExpense(expense1()));
    otherExpense.setDescription("Frais administratifs chantier A");
    return otherExpense;
  }

  static OtherExpense otherExpense2() {
    OtherExpense otherExpense = new OtherExpense();
    otherExpense.setId(TestUtils.OTHER_EXPENSE2_ID);
    otherExpense.setExpense(expenseToCrupdateExpense(expense2()));
    otherExpense.setDescription("Imprevus renovation hotel");
    return otherExpense;
  }

  static CrupdateOtherExpense otherExpenseToCrupdateOtherExpense(OtherExpense otherExpense) {
    CrupdateOtherExpense crupdateOtherExpense = new CrupdateOtherExpense();
    crupdateOtherExpense.setId(otherExpense.getId());
    crupdateOtherExpense.setExpenseId(
        otherExpense.getExpense() != null ? otherExpense.getExpense().getId() : null);
    crupdateOtherExpense.setDescription(otherExpense.getDescription());
    return crupdateOtherExpense;
  }

  static CrupdateOtherExpense someCreatableOtherExpense() {
    CrupdateOtherExpense otherExpense = new CrupdateOtherExpense();
    otherExpense.setId(UUID.randomUUID().toString());
    otherExpense.setExpenseId(TestUtils.EXPENSE1_ID);
    otherExpense.setDescription("Frais divers chantier");
    return otherExpense;
  }

  static OtherExpenseType otherExpenseType1() {
    OtherExpenseType otherExpenseType = new OtherExpenseType();
    otherExpenseType.setId(TestUtils.OTHER_EXPENSE_TYPE1_ID);
    otherExpenseType.setName("Logistique");
    otherExpenseType.setDescription("Frais logistiques et transport");
    otherExpenseType.setCompanyId(TestUtils.COMPANY1_ID);
    return otherExpenseType;
  }

  static OtherExpenseType otherExpenseType2() {
    OtherExpenseType otherExpenseType = new OtherExpenseType();
    otherExpenseType.setId(TestUtils.OTHER_EXPENSE_TYPE2_ID);
    otherExpenseType.setName("Administratif");
    otherExpenseType.setDescription("Frais administratifs et bureau");
    otherExpenseType.setCompanyId(TestUtils.COMPANY1_ID);
    return otherExpenseType;
  }

  static CrupdateOtherExpenseType otherExpenseTypeToCrupdateOtherExpenseType(
      OtherExpenseType otherExpenseType) {
    CrupdateOtherExpenseType crupdate = new CrupdateOtherExpenseType();
    crupdate.setId(otherExpenseType.getId());
    crupdate.setName(otherExpenseType.getName());
    crupdate.setDescription(otherExpenseType.getDescription());
    crupdate.setCompanyId(otherExpenseType.getCompanyId());
    crupdate.setComment(otherExpenseType.getComment());
    return crupdate;
  }

  static CrupdateOtherExpenseType someCreatableOtherExpenseType() {
    CrupdateOtherExpenseType otherExpenseType = new CrupdateOtherExpenseType();
    otherExpenseType.setId(UUID.randomUUID().toString());
    otherExpenseType.setName("Maintenance");
    otherExpenseType.setDescription("Frais de maintenance et reparation");
    otherExpenseType.setCompanyId(TestUtils.COMPANY1_ID);
    return otherExpenseType;
  }

  static Maintenance maintenance1() {
    Maintenance maintenance = new Maintenance();
    maintenance.setId(TestUtils.MAINTENANCE1_ID);
    maintenance.setExpense(expenseToCrupdateExpense(expense1()));
    maintenance.setEquipmentId(TestUtils.EQUIPMENT1_ID);
    maintenance.setDescription("Revision moteur periodique");
    return maintenance;
  }

  static Maintenance maintenance2() {
    Maintenance maintenance = new Maintenance();
    maintenance.setId(TestUtils.MAINTENANCE2_ID);
    maintenance.setExpense(expenseToCrupdateExpense(expense2()));
    maintenance.setEquipmentId(TestUtils.EQUIPMENT2_ID);
    maintenance.setDescription("Remplacement pneus");
    return maintenance;
  }

  static CrupdateMaintenance maintenanceToCrupdateMaintenance(Maintenance maintenance) {
    CrupdateMaintenance crupdate = new CrupdateMaintenance();
    crupdate.setId(maintenance.getId());
    crupdate.setExpenseId(
        maintenance.getExpense() != null ? maintenance.getExpense().getId() : null);
    crupdate.setEquipmentId(maintenance.getEquipmentId());
    crupdate.setDescription(maintenance.getDescription());
    return crupdate;
  }

  static CrupdateMaintenance someCreatableMaintenance() {
    CrupdateMaintenance maintenance = new CrupdateMaintenance();
    maintenance.setId(UUID.randomUUID().toString());
    maintenance.setExpenseId(TestUtils.EXPENSE1_ID);
    maintenance.setEquipmentId(TestUtils.EQUIPMENT1_ID);
    maintenance.setDescription("Vidange et revision");
    return maintenance;
  }

  static Purchase purchase1() {
    Purchase purchase = new Purchase();
    purchase.setId(TestUtils.PURCHASE1_ID);
    purchase.setExpense(expenseToCrupdateExpense(expense1()));
    purchase.setSourceWarehouse(
        new CrupdateWarehouse()
            .id(TestUtils.WAREHOUSE1_ID)
            .name(warehouse1().getName())
            .description(warehouse1().getDescription())
            .jobId(warehouse1().getJob().getId())
            .comment(warehouse1().getComment()));
    purchase.setEquipment(
        TestOrganizationFixtures.equipmentToCrupdateEquipment(
            TestOrganizationFixtures.equipment1()));
    purchase.setMaterial(null);
    purchase.setQuantity(1);
    purchase.setIsEquipment(true);
    purchase.setInvoiceDate(LocalDate.of(2024, 1, 15));
    purchase.setDueDate(LocalDate.of(2024, 2, 15));
    purchase.setPaidAt(LocalDate.of(2024, 2, 10));
    return purchase;
  }

  static Purchase purchase2() {
    Purchase purchase = new Purchase();
    purchase.setId(TestUtils.PURCHASE2_ID);
    purchase.setExpense(expenseToCrupdateExpense(expense2()));
    purchase.setSourceWarehouse(new CrupdateWarehouse().id(TestUtils.WAREHOUSE2_ID));
    purchase.setEquipment(null);
    purchase.setMaterial(
        TestOrganizationFixtures.materialToCrupdateMaterial(TestOrganizationFixtures.material2()));
    purchase.setQuantity(25);
    purchase.setIsEquipment(false);
    purchase.setInvoiceDate(LocalDate.of(2024, 2, 1));
    purchase.setDueDate(LocalDate.of(2024, 3, 1));
    return purchase;
  }

  static CrupdatePurchase purchaseToCrupdatePurchase(Purchase purchase) {
    CrupdatePurchase crupdatePurchase = new CrupdatePurchase();
    crupdatePurchase.setId(purchase.getId());
    crupdatePurchase.setExpenseId(
        purchase.getExpense() != null ? purchase.getExpense().getId() : null);
    crupdatePurchase.setSourceWarehouseId(
        purchase.getSourceWarehouse() != null ? purchase.getSourceWarehouse().getId() : null);
    crupdatePurchase.setSupplierId(
        purchase.getSupplier() != null ? purchase.getSupplier().getId() : null);
    crupdatePurchase.setEquipment(
        purchase.getEquipment() != null ? purchase.getEquipment().getId() : null);
    crupdatePurchase.setMaterial(
        purchase.getMaterial() != null ? purchase.getMaterial().getId() : null);
    crupdatePurchase.setQuantity(purchase.getQuantity());
    crupdatePurchase.setIsEquipment(purchase.getIsEquipment());
    crupdatePurchase.setInvoiceDate(purchase.getInvoiceDate());
    crupdatePurchase.setDueDate(purchase.getDueDate());
    crupdatePurchase.setPaidAt(purchase.getPaidAt());
    return crupdatePurchase;
  }

  static CrupdatePurchase someCreatablePurchase() {
    CrupdatePurchase purchase = new CrupdatePurchase();
    purchase.setId(UUID.randomUUID().toString());
    purchase.setExpenseId(TestUtils.EXPENSE1_ID);
    purchase.setSourceWarehouseId(TestUtils.WAREHOUSE1_ID);
    purchase.setEquipment(TestUtils.EQUIPMENT1_ID);
    purchase.setMaterial(null);
    purchase.setQuantity(1);
    purchase.setIsEquipment(true);
    return purchase;
  }

  static Loan loan1() {
    Loan loan = new Loan();
    loan.setId(TestUtils.LOAN1_ID);
    loan.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    loan.setLender("BNI Madagascar");
    loan.setInterestRate(1200);
    loan.setStartDate(LocalDate.of(2024, 2, 1));
    loan.setDueDate(LocalDate.of(2026, 12, 31));
    loan.setStatus(LoanStatus.ACTIVE);
    loan.setAmount(new BigDecimal("5000000.00"));
    loan.setDescription("Emprunt construction entrepot");
    LoanRepayment r1 = new LoanRepayment();
    r1.setId(TestUtils.REPAYMENT1_ID);
    r1.setPaymentDate(LocalDate.of(2024, 3, 1));
    r1.setAmount(new BigDecimal("600000.00"));
    r1.setPrincipalPortion(new BigDecimal("500000.00"));
    r1.setInterestPortion(new BigDecimal("100000.00"));
    LoanRepayment r2 = new LoanRepayment();
    r2.setId(TestUtils.REPAYMENT2_ID);
    r2.setPaymentDate(LocalDate.of(2024, 4, 1));
    r2.setAmount(new BigDecimal("600000.00"));
    r2.setPrincipalPortion(new BigDecimal("510000.00"));
    r2.setInterestPortion(new BigDecimal("90000.00"));
    loan.setRepayments(List.of(r1, r2));
    loan.setRemainingAmount(new BigDecimal("3800000.00"));
    return loan;
  }

  static Loan loan2() {
    Loan loan = new Loan();
    loan.setId(TestUtils.LOAN2_ID);
    loan.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    loan.setLender("BOA Madagascar");
    loan.setInterestRate(1500);
    loan.setStartDate(LocalDate.of(2024, 3, 1));
    loan.setStatus(LoanStatus.ACTIVE);
    loan.setAmount(new BigDecimal("3000000.00"));
    loan.setDescription("Emprunt equipements");
    return loan;
  }

  static CrupdateLoan loanToCrupdateLoan(Loan loan) {
    CrupdateLoan crupdateLoan = new CrupdateLoan();
    crupdateLoan.setId(loan.getId());
    crupdateLoan.setJobId(loan.getJob() != null ? loan.getJob().getId() : null);
    crupdateLoan.setLender(loan.getLender());
    crupdateLoan.setInterestRate(loan.getInterestRate());
    crupdateLoan.setStartDate(loan.getStartDate());
    crupdateLoan.setDueDate(loan.getDueDate());
    crupdateLoan.setAmount(loan.getAmount());
    crupdateLoan.setDescription(loan.getDescription());
    crupdateLoan.setComment(loan.getComment());
    return crupdateLoan;
  }

  static CrupdateLoan someCreatableLoan() {
    CrupdateLoan loan = new CrupdateLoan();
    loan.setId(UUID.randomUUID().toString());
    loan.setJobId(TestUtils.JOB1_ID);
    loan.setLender("MCB Madagascar");
    loan.setInterestRate(1000);
    loan.setStartDate(LocalDate.of(2024, 6, 1));
    loan.setDueDate(LocalDate.of(2026, 12, 31));
    loan.setAmount(new BigDecimal("2000000.00"));
    loan.setDescription("Nouvel emprunt tresorerie");
    return loan;
  }

  static LoanRepayment repayment1() {
    LoanRepayment repayment = new LoanRepayment();
    repayment.setId(TestUtils.REPAYMENT1_ID);
    repayment.setLoan(loanToCrupdateLoan(loan1()));
    repayment.setPaymentDate(LocalDate.of(2024, 3, 1));
    repayment.setAmount(new BigDecimal("600000.00"));
    repayment.setPrincipalPortion(new BigDecimal("500000.00"));
    repayment.setInterestPortion(new BigDecimal("100000.00"));
    return repayment;
  }

  static CrupdateLoanRepayment repaymentToCrupdateRepayment(LoanRepayment repayment) {
    CrupdateLoanRepayment crupdate = new CrupdateLoanRepayment();
    crupdate.setId(repayment.getId());
    crupdate.setLoanId(repayment.getLoan() != null ? repayment.getLoan().getId() : null);
    crupdate.setPaymentDate(repayment.getPaymentDate());
    crupdate.setAmount(repayment.getAmount());
    crupdate.setComment(repayment.getComment());
    return crupdate;
  }

  static CrupdateLoanRepayment someCreatableRepayment() {
    CrupdateLoanRepayment repayment = new CrupdateLoanRepayment();
    repayment.setId(UUID.randomUUID().toString());
    repayment.setLoanId(TestUtils.LOAN1_ID);
    repayment.setPaymentDate(LocalDate.of(2024, 5, 1));
    repayment.setAmount(new BigDecimal("600000.00"));
    return repayment;
  }

  static IncomeReceipt receipt1() {
    IncomeReceipt receipt = new IncomeReceipt();
    receipt.setId(TestUtils.RECEIPT1_ID);
    receipt.setIncome(income1());
    receipt.setPaymentDate(LocalDate.of(2024, 2, 1));
    receipt.setAmount(new BigDecimal("150000.00"));
    return receipt;
  }

  static IncomeReceipt receipt2() {
    IncomeReceipt receipt = new IncomeReceipt();
    receipt.setId(TestUtils.RECEIPT2_ID);
    receipt.setIncome(income2());
    receipt.setPaymentDate(LocalDate.of(2024, 2, 15));
    receipt.setAmount(new BigDecimal("275000.00"));
    return receipt;
  }

  static CrupdateIncomeReceipt receiptToCrupdateReceipt(IncomeReceipt receipt) {
    CrupdateIncomeReceipt crupdate = new CrupdateIncomeReceipt();
    crupdate.setId(receipt.getId());
    crupdate.setIncomeId(receipt.getIncome() != null ? receipt.getIncome().getId() : null);
    crupdate.setPaymentDate(receipt.getPaymentDate());
    crupdate.setAmount(receipt.getAmount());
    crupdate.setComment(receipt.getComment());
    return crupdate;
  }

  static CrupdateIncomeReceipt someCreatableReceipt() {
    CrupdateIncomeReceipt receipt = new CrupdateIncomeReceipt();
    receipt.setId(UUID.randomUUID().toString());
    receipt.setIncomeId(TestUtils.INCOME1_ID);
    receipt.setPaymentDate(LocalDate.of(2024, 4, 1));
    receipt.setAmount(new BigDecimal("50000.00"));
    return receipt;
  }

  static Loan loan3() {
    Loan loan = new Loan();
    loan.setId(TestUtils.LOAN3_ID);
    loan.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    loan.setLender("Microcred");
    loan.setInterestRate(1000);
    loan.setStartDate(LocalDate.of(2024, 1, 15));
    loan.setStatus(LoanStatus.PAID);
    loan.setAmount(new BigDecimal("2000000.00"));
    loan.setDescription("Emprunt rembourse");
    LoanRepayment r3 = new LoanRepayment();
    r3.setId(TestUtils.REPAYMENT3_ID);
    r3.setPaymentDate(LocalDate.of(2024, 6, 1));
    r3.setAmount(new BigDecimal("2000000.00"));
    r3.setPrincipalPortion(new BigDecimal("1800000.00"));
    r3.setInterestPortion(new BigDecimal("200000.00"));
    loan.setRepayments(List.of(r3));
    loan.setRemainingAmount(new BigDecimal("0.00"));
    return loan;
  }

  static Loan loan4() {
    Loan loan = new Loan();
    loan.setId(TestUtils.LOAN4_ID);
    loan.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    loan.setLender("MBC Madagascar");
    loan.setInterestRate(1000);
    loan.setStartDate(LocalDate.of(2024, 3, 1));
    loan.setDueDate(LocalDate.of(2024, 6, 1));
    loan.setStatus(LoanStatus.DEFAULTED);
    loan.setAmount(new BigDecimal("2000000.00"));
    loan.setDescription("Emprunt en defaut");
    loan.setRemainingAmount(new BigDecimal("2000000.00"));
    return loan;
  }

  static Loan loan5() {
    Loan loan = new Loan();
    loan.setId(TestUtils.LOAN5_ID);
    loan.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    loan.setLender("SIPEM");
    loan.setInterestRate(1000);
    loan.setStartDate(LocalDate.of(2024, 1, 1));
    loan.setDueDate(LocalDate.of(2024, 6, 1));
    loan.setStatus(LoanStatus.PAID);
    loan.setAmount(new BigDecimal("2000000.00"));
    loan.setDescription("Emprunt rembourse avant echeance");
    LoanRepayment r5 = new LoanRepayment();
    r5.setId(TestUtils.REPAYMENT5_ID);
    r5.setPaymentDate(LocalDate.of(2024, 5, 1));
    r5.setAmount(new BigDecimal("2000000.00"));
    r5.setPrincipalPortion(new BigDecimal("1800000.00"));
    r5.setInterestPortion(new BigDecimal("200000.00"));
    loan.setRepayments(List.of(r5));
    loan.setRemainingAmount(new BigDecimal("0.00"));
    return loan;
  }

  static IncomeMoney income5() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME5_ID);
    income.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    income.setSourceOrganization("Client Delta");
    income.setInvoiceReference("INV-2024-005");
    income.setBillingStartDate(LocalDate.of(2024, 4, 1));
    income.setDueDate(LocalDate.of(2024, 5, 1));
    income.setPaymentTerms("NET-30");
    income.setIncomeType(incomeType1());
    income.setAmount(new BigDecimal("100000.00"));
    income.setDescription("Paiement partiel");
    IncomeReceipt receipt = new IncomeReceipt();
    receipt.setId(TestUtils.RECEIPT5_ID);
    receipt.setPaymentDate(LocalDate.of(2024, 4, 10));
    receipt.setAmount(new BigDecimal("60000.00"));
    income.setReceipts(List.of(receipt));
    income.setRemainingAmount(new BigDecimal("40000.00"));
    return income;
  }

  static IncomeMoney income6() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME6_ID);
    income.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    income.setSourceOrganization("Client Epsilon");
    income.setInvoiceReference("INV-2024-006");
    income.setBillingStartDate(LocalDate.of(2024, 4, 15));
    income.setDueDate(LocalDate.of(2024, 5, 15));
    income.setPaymentTerms("NET-30");
    income.setIncomeType(incomeType1());
    income.setAmount(new BigDecimal("100000.00"));
    income.setDescription("Paiement en exces");
    IncomeReceipt r1 = new IncomeReceipt();
    r1.setId(TestUtils.RECEIPT6A_ID);
    r1.setPaymentDate(LocalDate.of(2024, 4, 20));
    r1.setAmount(new BigDecimal("60000.00"));
    IncomeReceipt r2 = new IncomeReceipt();
    r2.setId(TestUtils.RECEIPT6B_ID);
    r2.setPaymentDate(LocalDate.of(2024, 4, 25));
    r2.setAmount(new BigDecimal("60000.00"));
    income.setReceipts(List.of(r1, r2));
    income.setRemainingAmount(new BigDecimal("-20000.00"));
    return income;
  }

  static IncomeMoney income7() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME7_ID);
    income.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    income.setSourceOrganization("Client Zeta");
    income.setInvoiceReference("INV-2024-007");
    income.setBillingStartDate(LocalDate.of(2024, 5, 1));
    income.setDueDate(LocalDate.of(2024, 5, 31));
    income.setPaymentTerms("NET-30");
    income.setIncomeType(incomeType1());
    income.setAmount(new BigDecimal("100000.00"));
    income.setDescription("Paiement total multiple recus");
    IncomeReceipt r1 = new IncomeReceipt();
    r1.setId(TestUtils.RECEIPT7A_ID);
    r1.setPaymentDate(LocalDate.of(2024, 5, 10));
    r1.setAmount(new BigDecimal("60000.00"));
    IncomeReceipt r2 = new IncomeReceipt();
    r2.setId(TestUtils.RECEIPT7B_ID);
    r2.setPaymentDate(LocalDate.of(2024, 5, 15));
    r2.setAmount(new BigDecimal("40000.00"));
    income.setReceipts(List.of(r1, r2));
    income.setRemainingAmount(new BigDecimal("0.00"));
    return income;
  }

  static CompanyFixedCost companyFixedCost1() {
    CompanyFixedCost cost = new CompanyFixedCost();
    cost.setId(TestUtils.FIXED_COST1_ID);
    cost.setName("Loyer bureau");
    cost.setAmount(new java.math.BigDecimal("2000.00"));
    cost.setDescription("Loyer mensuel des locaux principaux");
    cost.setCompanyId(TestUtils.COMPANY1_ID);
    cost.setStartDate(java.time.LocalDate.of(2024, 1, 1));
    cost.setEndDate(null);
    return cost;
  }

  static CompanyFixedCost companyFixedCost2() {
    CompanyFixedCost cost = new CompanyFixedCost();
    cost.setId(TestUtils.FIXED_COST2_ID);
    cost.setName("Assurance vehicule");
    cost.setAmount(new java.math.BigDecimal("500.00"));
    cost.setDescription("Assurance flotte automobile");
    cost.setCompanyId(TestUtils.COMPANY1_ID);
    cost.setStartDate(java.time.LocalDate.of(2024, 3, 1));
    cost.setEndDate(java.time.LocalDate.of(2025, 3, 1));
    return cost;
  }

  static Supplier supplier1() {
    Supplier supplier = new Supplier();
    supplier.setId(TestUtils.SUPPLIER1_ID);
    supplier.setName("Fournitures Pro");
    supplier.setSiret("12345678901234");
    supplier.setAddress("123 Rue du Commerce, Antananarivo");
    supplier.setEmail("contact@fourniturespro.mg");
    supplier.setPhone("+261341234567");
    supplier.setContactName("Jean Rajaonarison");
    supplier.setCompanyId(TestUtils.COMPANY1_ID);
    return supplier;
  }

  static Supplier supplier2() {
    Supplier supplier = new Supplier();
    supplier.setId(TestUtils.SUPPLIER2_ID);
    supplier.setName("Matériaux BTP");
    supplier.setSiret("98765432109876");
    supplier.setAddress("456 Avenue de l'Industrie, Toamasina");
    supplier.setEmail("info@materiauxbtp.mg");
    supplier.setPhone("+261337654321");
    supplier.setContactName("Marie Randrianarisoa");
    supplier.setCompanyId(TestUtils.COMPANY1_ID);
    return supplier;
  }

  static CrupdateSupplier supplierToCrupdateSupplier(Supplier supplier) {
    CrupdateSupplier crupdate = new CrupdateSupplier();
    crupdate.setId(supplier.getId());
    crupdate.setName(supplier.getName());
    crupdate.setSiret(supplier.getSiret());
    crupdate.setAddress(supplier.getAddress());
    crupdate.setEmail(supplier.getEmail());
    crupdate.setPhone(supplier.getPhone());
    crupdate.setContactName(supplier.getContactName());
    crupdate.setComment(supplier.getComment());
    return crupdate;
  }

  static CrupdateSupplier someCreatableSupplier() {
    CrupdateSupplier supplier = new CrupdateSupplier();
    supplier.setId(java.util.UUID.randomUUID().toString());
    supplier.setName("Nouveau Fournisseur");
    supplier.setSiret("11111111111111");
    supplier.setAddress("789 Rue Test");
    supplier.setEmail("test@fournisseur.mg");
    supplier.setPhone("+261330000000");
    supplier.setContactName("Contact Test");
    return supplier;
  }

  static PurchaseOrder purchaseOrder1() {
    PurchaseOrder po = new PurchaseOrder();
    po.setId(TestUtils.PO1_ID);
    po.setSupplierId(TestUtils.SUPPLIER1_ID);
    po.setOrderDate(java.time.LocalDate.of(2024, 6, 1));
    po.setStatus(PurchaseOrderStatus.VALIDATED);
    po.setTotalAmount(new java.math.BigDecimal("150000.00"));
    po.setCompanyId(TestUtils.COMPANY1_ID);
    po.setJobId(TestUtils.JOB1_ID);
    PurchaseOrderLine line1 = new PurchaseOrderLine();
    line1.setId(TestUtils.POL1_ID);
    line1.setMaterialId(TestUtils.MATERIAL1_ID);
    line1.setQuantity(50);
    line1.setUnitPrice(new java.math.BigDecimal("1500.00"));
    PurchaseOrderLine line2 = new PurchaseOrderLine();
    line2.setId(TestUtils.POL2_ID);
    line2.setMaterialId(TestUtils.MATERIAL2_ID);
    line2.setQuantity(200);
    line2.setUnitPrice(new java.math.BigDecimal("350.00"));
    po.setLines(java.util.List.of(line1, line2));
    return po;
  }

  static PurchaseOrder purchaseOrder2() {
    PurchaseOrder po = new PurchaseOrder();
    po.setId(TestUtils.PO2_ID);
    po.setSupplierId(TestUtils.SUPPLIER2_ID);
    po.setOrderDate(java.time.LocalDate.of(2024, 6, 15));
    po.setStatus(PurchaseOrderStatus.PENDING);
    po.setTotalAmount(new java.math.BigDecimal("85000.00"));
    po.setCompanyId(TestUtils.COMPANY1_ID);
    po.setJobId(TestUtils.JOB1_ID);
    PurchaseOrderLine line = new PurchaseOrderLine();
    line.setId(TestUtils.POL3_ID);
    line.setMaterialId(TestUtils.MATERIAL1_ID);
    line.setQuantity(20);
    line.setUnitPrice(new java.math.BigDecimal("1600.00"));
    po.setLines(java.util.List.of(line));
    return po;
  }

  static CrupdatePurchaseOrder purchaseOrderToCrupdatePurchaseOrder(PurchaseOrder po) {
    CrupdatePurchaseOrder crupdate = new CrupdatePurchaseOrder();
    crupdate.setId(po.getId());
    crupdate.setSupplierId(po.getSupplierId());
    crupdate.setOrderDate(po.getOrderDate());
    crupdate.setStatus(po.getStatus());
    crupdate.setTotalAmount(po.getTotalAmount());
    crupdate.setCompanyId(po.getCompanyId());
    crupdate.setJobId(po.getJobId());
    if (po.getLines() != null) {
      crupdate.setLines(
          po.getLines().stream()
              .map(
                  l -> {
                    CrupdatePurchaseOrderLine cl = new CrupdatePurchaseOrderLine();
                    cl.setId(l.getId());
                    cl.setMaterialId(l.getMaterialId());
                    cl.setQuantity(l.getQuantity());
                    cl.setUnitPrice(l.getUnitPrice());
                    return cl;
                  })
              .toList());
    }
    crupdate.setComment(po.getComment());
    return crupdate;
  }

  static CrupdatePurchaseOrder someCreatablePurchaseOrder() {
    CrupdatePurchaseOrder po = new CrupdatePurchaseOrder();
    po.setId(java.util.UUID.randomUUID().toString());
    po.setSupplierId(TestUtils.SUPPLIER1_ID);
    po.setOrderDate(java.time.LocalDate.of(2024, 7, 1));
    po.setStatus(PurchaseOrderStatus.PENDING);
    po.setTotalAmount(new java.math.BigDecimal("50000.00"));
    po.setCompanyId(TestUtils.COMPANY1_ID);
    po.setJobId(TestUtils.JOB1_ID);
    CrupdatePurchaseOrderLine line = new CrupdatePurchaseOrderLine();
    line.setId(java.util.UUID.randomUUID().toString());
    line.setMaterialId(TestUtils.MATERIAL1_ID);
    line.setQuantity(10);
    line.setUnitPrice(new java.math.BigDecimal("1500.00"));
    po.setLines(java.util.List.of(line));
    return po;
  }

  static CrupdateCompanyFixedCost companyFixedCostToCrupdateCompanyFixedCost(
      CompanyFixedCost fixedCost) {
    CrupdateCompanyFixedCost crupdate = new CrupdateCompanyFixedCost();
    crupdate.setId(fixedCost.getId());
    crupdate.setName(fixedCost.getName());
    crupdate.setAmount(fixedCost.getAmount());
    crupdate.setDescription(fixedCost.getDescription());
    crupdate.setCompanyId(fixedCost.getCompanyId());
    crupdate.setStartDate(fixedCost.getStartDate());
    crupdate.setEndDate(fixedCost.getEndDate());
    crupdate.setComment(fixedCost.getComment());
    return crupdate;
  }

  static CrupdateCompanyFixedCost someCreatableCompanyFixedCost() {
    CrupdateCompanyFixedCost cost = new CrupdateCompanyFixedCost();
    cost.setId(java.util.UUID.randomUUID().toString());
    cost.setName("Abonnement internet");
    cost.setAmount(new java.math.BigDecimal("150.00"));
    cost.setDescription("Fibre optique professionnelle");
    cost.setCompanyId(TestUtils.COMPANY1_ID);
    cost.setStartDate(java.time.LocalDate.of(2024, 6, 1));
    cost.setEndDate(null);
    return cost;
  }

  static CashAccount cashAccount1() {
    CashAccount cashAccount = new CashAccount();
    cashAccount.setId(TestUtils.CASH_ACCOUNT1_ID);
    cashAccount.setName("Compte bancaire principal");
    cashAccount.setBalance(new java.math.BigDecimal("100000.00"));
    cashAccount.setDescription("Compte principal BNI");
    cashAccount.setCompanyId(TestUtils.COMPANY1_ID);
    return cashAccount;
  }

  static CashAccount cashAccount2() {
    CashAccount cashAccount = new CashAccount();
    cashAccount.setId(TestUtils.CASH_ACCOUNT2_ID);
    cashAccount.setName("Caisse");
    cashAccount.setBalance(new java.math.BigDecimal("50000.00"));
    cashAccount.setDescription("Caisse especes bureau");
    cashAccount.setCompanyId(TestUtils.COMPANY1_ID);
    return cashAccount;
  }

  static CrupdateCashAccount cashAccountToCrupdateCashAccount(CashAccount cashAccount) {
    CrupdateCashAccount crupdate = new CrupdateCashAccount();
    crupdate.setId(cashAccount.getId());
    crupdate.setName(cashAccount.getName());
    crupdate.setBalance(cashAccount.getBalance());
    crupdate.setDescription(cashAccount.getDescription());
    crupdate.setCompanyId(cashAccount.getCompanyId());
    crupdate.setComment(cashAccount.getComment());
    return crupdate;
  }

  static CrupdateCashAccount someCreatableCashAccount() {
    CrupdateCashAccount cashAccount = new CrupdateCashAccount();
    cashAccount.setId(UUID.randomUUID().toString());
    cashAccount.setName("Nouveau compte");
    cashAccount.setBalance(new java.math.BigDecimal("10000.00"));
    cashAccount.setDescription("Nouveau compte bancaire");
    cashAccount.setCompanyId(TestUtils.COMPANY1_ID);
    return cashAccount;
  }

  static BudgetLine budgetLine1() {
    BudgetLine budgetLine = new BudgetLine();
    budgetLine.setId(TestUtils.BUDGET_LINE1_ID);
    budgetLine.setCompanyId(TestUtils.COMPANY1_ID);
    budgetLine.setCategory("Materiaux");
    budgetLine.setPlannedAmount(new java.math.BigDecimal("50000.00"));
    budgetLine.setActualAmount(new java.math.BigDecimal("45000.00"));
    budgetLine.setPeriodStart(java.time.LocalDate.of(2024, 1, 1));
    budgetLine.setPeriodEnd(java.time.LocalDate.of(2024, 12, 31));
    budgetLine.setDescription("Budget materiaux construction");
    return budgetLine;
  }

  static BudgetLine budgetLine2() {
    BudgetLine budgetLine = new BudgetLine();
    budgetLine.setId(TestUtils.BUDGET_LINE2_ID);
    budgetLine.setCompanyId(TestUtils.COMPANY1_ID);
    budgetLine.setCategory("Main-d'oeuvre");
    budgetLine.setPlannedAmount(new java.math.BigDecimal("80000.00"));
    budgetLine.setActualAmount(new java.math.BigDecimal("75000.00"));
    budgetLine.setPeriodStart(java.time.LocalDate.of(2024, 1, 1));
    budgetLine.setPeriodEnd(java.time.LocalDate.of(2024, 12, 31));
    budgetLine.setDescription("Budget main-d'oeuvre");
    return budgetLine;
  }

  static CrupdateBudgetLine budgetLineToCrupdateBudgetLine(BudgetLine budgetLine) {
    CrupdateBudgetLine crupdate = new CrupdateBudgetLine();
    crupdate.setId(budgetLine.getId());
    crupdate.setCompanyId(budgetLine.getCompanyId());
    crupdate.setCategory(budgetLine.getCategory());
    crupdate.setPlannedAmount(budgetLine.getPlannedAmount());
    crupdate.setActualAmount(budgetLine.getActualAmount());
    crupdate.setPeriodStart(budgetLine.getPeriodStart());
    crupdate.setPeriodEnd(budgetLine.getPeriodEnd());
    crupdate.setDescription(budgetLine.getDescription());
    crupdate.setComment(budgetLine.getComment());
    return crupdate;
  }

  static CrupdateBudgetLine someCreatableBudgetLine() {
    CrupdateBudgetLine budgetLine = new CrupdateBudgetLine();
    budgetLine.setId(UUID.randomUUID().toString());
    budgetLine.setCompanyId(TestUtils.COMPANY1_ID);
    budgetLine.setCategory("Transport");
    budgetLine.setPlannedAmount(new java.math.BigDecimal("30000.00"));
    budgetLine.setActualAmount(new java.math.BigDecimal("0.00"));
    budgetLine.setPeriodStart(java.time.LocalDate.of(2024, 1, 1));
    budgetLine.setPeriodEnd(java.time.LocalDate.of(2024, 12, 31));
    budgetLine.setDescription("Budget transport");
    return budgetLine;
  }

  static CashTransaction cashTransaction1() {
    CashTransaction cashTransaction = new CashTransaction();
    cashTransaction.setId(TestUtils.CASH_TXN1_ID);
    cashTransaction.setCashAccountId(TestUtils.CASH_ACCOUNT1_ID);
    cashTransaction.setAmount(new java.math.BigDecimal("1500.00"));
    cashTransaction.setTransactionDate(java.time.LocalDate.of(2024, 1, 15));
    cashTransaction.setDescription("Achat materiel bureau");
    cashTransaction.setType(CashTransactionType.DEBIT);
    return cashTransaction;
  }

  static CashTransaction cashTransaction2() {
    CashTransaction cashTransaction = new CashTransaction();
    cashTransaction.setId(TestUtils.CASH_TXN2_ID);
    cashTransaction.setCashAccountId(TestUtils.CASH_ACCOUNT1_ID);
    cashTransaction.setAmount(new java.math.BigDecimal("25000.00"));
    cashTransaction.setTransactionDate(java.time.LocalDate.of(2024, 2, 1));
    cashTransaction.setDescription("Paiement fournisseur");
    cashTransaction.setType(CashTransactionType.CREDIT);
    return cashTransaction;
  }

  static CrupdateCashTransaction cashTransactionToCrupdateCashTransaction(
      CashTransaction cashTransaction) {
    CrupdateCashTransaction crupdate = new CrupdateCashTransaction();
    crupdate.setId(cashTransaction.getId());
    crupdate.setCashAccountId(cashTransaction.getCashAccountId());
    crupdate.setAmount(cashTransaction.getAmount());
    crupdate.setTransactionDate(cashTransaction.getTransactionDate());
    crupdate.setDescription(cashTransaction.getDescription());
    crupdate.setType(cashTransaction.getType());
    crupdate.setComment(cashTransaction.getComment());
    return crupdate;
  }

  static CrupdateCashTransaction someCreatableCashTransaction() {
    CrupdateCashTransaction cashTransaction = new CrupdateCashTransaction();
    cashTransaction.setId(UUID.randomUUID().toString());
    cashTransaction.setCashAccountId(TestUtils.CASH_ACCOUNT1_ID);
    cashTransaction.setAmount(new java.math.BigDecimal("5000.00"));
    cashTransaction.setTransactionDate(java.time.LocalDate.of(2024, 3, 1));
    cashTransaction.setDescription("Achat fournitures");
    cashTransaction.setType(CashTransactionType.DEBIT);
    return cashTransaction;
  }
}
