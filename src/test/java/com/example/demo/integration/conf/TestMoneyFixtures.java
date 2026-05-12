package com.example.demo.integration.conf;

import static com.example.demo.integration.conf.TestOrganizationFixtures.warehouse1;

import com.example.demo.client.model.BankFee;
import com.example.demo.client.model.CrupdateBankFee;
import com.example.demo.client.model.CrupdateEmployeePayment;
import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.CrupdateIncomeReceipt;
import com.example.demo.client.model.CrupdateIncomeType;
import com.example.demo.client.model.CrupdateLoan;
import com.example.demo.client.model.CrupdateLoanRepayment;
import com.example.demo.client.model.CrupdateOtherExpense;
import com.example.demo.client.model.CrupdatePurchase;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.EmployeePayment;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.client.model.IncomeReceipt;
import com.example.demo.client.model.IncomeType;
import com.example.demo.client.model.Loan;
import com.example.demo.client.model.LoanRepayment;
import com.example.demo.client.model.LoanStatus;
import com.example.demo.client.model.OtherExpense;
import com.example.demo.client.model.PaymentType;
import com.example.demo.client.model.Purchase;
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
    income.setIncomeType(incomeType1());
    income.setAmount(150000);
    income.setDescription("Paiement initial chantier A");
    IncomeReceipt receipt = new IncomeReceipt();
    receipt.setId(TestUtils.RECEIPT1_ID);
    receipt.setPaymentDate(LocalDate.of(2024, 2, 1));
    receipt.setAmount(150000);
    income.setReceipts(List.of(receipt));
    income.setRemainingAmount(0);
    return income;
  }

  static IncomeMoney income2() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME2_ID);
    income.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    income.setSourceOrganization("Client Beta");
    income.setInvoiceReference("INV-2024-002");
    income.setBillingStartDate(LocalDate.of(2024, 2, 10));
    income.setIncomeType(incomeType1());
    income.setAmount(275000);
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
    income.setIncomeTypeId(TestUtils.INCOME_TYPE2_ID);
    income.setAmount(99000);
    income.setDescription("Paiement complementaire");
    return income;
  }

  static ExpenseMoney expense1() {
    ExpenseMoney expense = new ExpenseMoney();
    expense.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    expense.setId(TestUtils.EXPENSE1_ID);
    expense.setAmount(45000);
    expense.setDescription("Achat materiaux chantier A");
    return expense;
  }

  static ExpenseMoney expense2() {
    ExpenseMoney expense = new ExpenseMoney();
    expense.setId(TestUtils.EXPENSE2_ID);
    expense.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    expense.setAmount(80000);
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
    expense.setAmount(32000);
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
    crupdateBankFee.setExpense(bankFee.getExpense());
    crupdateBankFee.setBankName(bankFee.getBankName());
    crupdateBankFee.setDescription(bankFee.getDescription());
    return crupdateBankFee;
  }

  static CrupdateBankFee someCreatableBankFee() {
    CrupdateBankFee bankFee = new CrupdateBankFee();
    bankFee.setId(UUID.randomUUID().toString());
    CrupdateExpenseMoney expense = new CrupdateExpenseMoney();
    expense.setId(TestUtils.EXPENSE1_ID);
    expense.setAmount(5000);
    expense.setDescription("Test bank fee expense");
    expense.setJobId(TestUtils.JOB1_ID);
    bankFee.setExpense(expense);
    bankFee.setBankName("MCB Madagascar");
    bankFee.setDescription("Frais tenue compte");
    return bankFee;
  }

  static EmployeePayment employeePayment1() {
    EmployeePayment employeePayment = new EmployeePayment();
    employeePayment.setId(TestUtils.EMPLOYEE_PAYMENT1_ID);
    employeePayment.setExpense(expenseToCrupdateExpense(expense1()));
    employeePayment.setEmployee(TestUserFixtures.employee1());
    employeePayment.setPaymentDescription("Avance salaire chantier A");
    employeePayment.setPaymentType(PaymentType.ADVANCE);
    return employeePayment;
  }

  static EmployeePayment employeePayment2() {
    EmployeePayment employeePayment = new EmployeePayment();
    employeePayment.setId(TestUtils.EMPLOYEE_PAYMENT2_ID);
    employeePayment.setExpense(expenseToCrupdateExpense(expense2()));
    employeePayment.setEmployee(TestUserFixtures.user1());
    employeePayment.setPaymentDescription("Paiement mensuel renovation");
    employeePayment.setPaymentType(PaymentType.MONTHLY);
    return employeePayment;
  }

  static CrupdateEmployeePayment employeePaymentToCrupdateEmployeePayment(
      EmployeePayment employeePayment) {
    CrupdateEmployeePayment crupdateEmployeePayment = new CrupdateEmployeePayment();
    crupdateEmployeePayment.setId(employeePayment.getId());
    crupdateEmployeePayment.setExpense(employeePayment.getExpense());
    crupdateEmployeePayment.setEmployeeId(
        employeePayment.getEmployee() != null ? employeePayment.getEmployee().getId() : null);
    crupdateEmployeePayment.setPaymentDescription(employeePayment.getPaymentDescription());
    crupdateEmployeePayment.setPaymentType(employeePayment.getPaymentType());
    return crupdateEmployeePayment;
  }

  static CrupdateEmployeePayment someCreatableEmployeePayment() {
    CrupdateEmployeePayment employeePayment = new CrupdateEmployeePayment();
    employeePayment.setId(UUID.randomUUID().toString());
    CrupdateExpenseMoney expense = new CrupdateExpenseMoney();
    expense.setId(TestUtils.EXPENSE1_ID);
    expense.setAmount(2000);
    expense.setDescription("Test employee payment expense");
    expense.setJobId(TestUtils.JOB1_ID);
    employeePayment.setExpense(expense);
    employeePayment.setEmployeeId(TestUtils.EMPLOYEE_ID);
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
    crupdateOtherExpense.setExpense(otherExpense.getExpense());
    crupdateOtherExpense.setDescription(otherExpense.getDescription());
    return crupdateOtherExpense;
  }

  static CrupdateOtherExpense someCreatableOtherExpense() {
    CrupdateOtherExpense otherExpense = new CrupdateOtherExpense();
    otherExpense.setId(UUID.randomUUID().toString());
    CrupdateExpenseMoney expense = new CrupdateExpenseMoney();
    expense.setId(TestUtils.EXPENSE1_ID);
    expense.setAmount(1500);
    expense.setDescription("Test other expense");
    expense.setJobId(TestUtils.JOB1_ID);
    otherExpense.setExpense(expense);
    otherExpense.setDescription("Frais divers chantier");
    return otherExpense;
  }

  static Purchase purchase1() {
    Purchase purchase = new Purchase();
    purchase.setId(TestUtils.PURCHASE1_ID);
    purchase.setExpense(expenseToCrupdateExpense(expense1()));
    purchase.setSupplier(
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
    return purchase;
  }

  static Purchase purchase2() {
    Purchase purchase = new Purchase();
    purchase.setId(TestUtils.PURCHASE2_ID);
    purchase.setExpense(expenseToCrupdateExpense(expense2()));
    purchase.setSupplier(new CrupdateWarehouse().id(TestUtils.WAREHOUSE2_ID));
    purchase.setEquipment(null);
    purchase.setMaterial(
        TestOrganizationFixtures.materialToCrupdateMaterial(TestOrganizationFixtures.material2()));
    purchase.setQuantity(25);
    purchase.setIsEquipment(false);
    return purchase;
  }

  static CrupdatePurchase purchaseToCrupdatePurchase(Purchase purchase) {
    CrupdatePurchase crupdatePurchase = new CrupdatePurchase();
    crupdatePurchase.setId(purchase.getId());
    crupdatePurchase.setExpense(purchase.getExpense());
    crupdatePurchase.setSupplier(purchase.getSupplier() != null ? purchase.getSupplier() : null);
    crupdatePurchase.setEquipment(
        purchase.getEquipment() != null ? purchase.getEquipment().getId() : null);
    crupdatePurchase.setMaterial(
        purchase.getMaterial() != null ? purchase.getMaterial().getId() : null);
    crupdatePurchase.setQuantity(purchase.getQuantity());
    crupdatePurchase.setIsEquipment(purchase.getIsEquipment());
    return crupdatePurchase;
  }

  static CrupdatePurchase someCreatablePurchase() {
    CrupdatePurchase purchase = new CrupdatePurchase();
    purchase.setId(UUID.randomUUID().toString());
    CrupdateExpenseMoney expense = new CrupdateExpenseMoney();
    expense.setId(TestUtils.EXPENSE1_ID);
    expense.setAmount(5000);
    expense.setDescription("Test purchase expense");
    expense.setJobId(TestUtils.JOB1_ID);
    purchase.setExpense(expense);
    purchase.setSupplier(new CrupdateWarehouse().id(TestUtils.WAREHOUSE1_ID));
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
    loan.setAmount(5000000);
    loan.setDescription("Emprunt construction entrepot");
    LoanRepayment r1 = new LoanRepayment();
    r1.setId(TestUtils.REPAYMENT1_ID);
    r1.setPaymentDate(LocalDate.of(2024, 3, 1));
    r1.setAmount(600000);
    r1.setPrincipalPortion(500000);
    r1.setInterestPortion(100000);
    LoanRepayment r2 = new LoanRepayment();
    r2.setId(TestUtils.REPAYMENT2_ID);
    r2.setPaymentDate(LocalDate.of(2024, 4, 1));
    r2.setAmount(600000);
    r2.setPrincipalPortion(510000);
    r2.setInterestPortion(90000);
    loan.setRepayments(List.of(r1, r2));
    loan.setRemainingAmount(3800000);
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
    loan.setAmount(3000000);
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
    loan.setAmount(2000000);
    loan.setDescription("Nouvel emprunt tresorerie");
    return loan;
  }

  static LoanRepayment repayment1() {
    LoanRepayment repayment = new LoanRepayment();
    repayment.setId(TestUtils.REPAYMENT1_ID);
    repayment.setLoan(loanToCrupdateLoan(loan1()));
    repayment.setPaymentDate(LocalDate.of(2024, 3, 1));
    repayment.setAmount(600000);
    repayment.setPrincipalPortion(500000);
    repayment.setInterestPortion(100000);
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
    repayment.setAmount(600000);
    return repayment;
  }

  static IncomeReceipt receipt1() {
    IncomeReceipt receipt = new IncomeReceipt();
    receipt.setId(TestUtils.RECEIPT1_ID);
    receipt.setIncome(income1());
    receipt.setPaymentDate(LocalDate.of(2024, 2, 1));
    receipt.setAmount(150000);
    return receipt;
  }

  static IncomeReceipt receipt2() {
    IncomeReceipt receipt = new IncomeReceipt();
    receipt.setId(TestUtils.RECEIPT2_ID);
    receipt.setIncome(income2());
    receipt.setPaymentDate(LocalDate.of(2024, 2, 15));
    receipt.setAmount(275000);
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
    receipt.setAmount(50000);
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
    loan.setAmount(2000000);
    loan.setDescription("Emprunt rembourse");
    LoanRepayment r3 = new LoanRepayment();
    r3.setId(TestUtils.REPAYMENT3_ID);
    r3.setPaymentDate(LocalDate.of(2024, 6, 1));
    r3.setAmount(2000000);
    r3.setPrincipalPortion(1800000);
    r3.setInterestPortion(200000);
    loan.setRepayments(List.of(r3));
    loan.setRemainingAmount(0);
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
    loan.setAmount(2000000);
    loan.setDescription("Emprunt en defaut");
    loan.setRemainingAmount(2000000);
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
    loan.setAmount(2000000);
    loan.setDescription("Emprunt rembourse avant echeance");
    LoanRepayment r5 = new LoanRepayment();
    r5.setId(TestUtils.REPAYMENT5_ID);
    r5.setPaymentDate(LocalDate.of(2024, 5, 1));
    r5.setAmount(2000000);
    r5.setPrincipalPortion(1800000);
    r5.setInterestPortion(200000);
    loan.setRepayments(List.of(r5));
    loan.setRemainingAmount(0);
    return loan;
  }

  static IncomeMoney income5() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME5_ID);
    income.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    income.setSourceOrganization("Client Delta");
    income.setInvoiceReference("INV-2024-005");
    income.setBillingStartDate(LocalDate.of(2024, 4, 1));
    income.setIncomeType(incomeType1());
    income.setAmount(100000);
    income.setDescription("Paiement partiel");
    IncomeReceipt receipt = new IncomeReceipt();
    receipt.setId(TestUtils.RECEIPT5_ID);
    receipt.setPaymentDate(LocalDate.of(2024, 4, 10));
    receipt.setAmount(60000);
    income.setReceipts(List.of(receipt));
    income.setRemainingAmount(40000);
    return income;
  }

  static IncomeMoney income6() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME6_ID);
    income.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    income.setSourceOrganization("Client Epsilon");
    income.setInvoiceReference("INV-2024-006");
    income.setBillingStartDate(LocalDate.of(2024, 4, 15));
    income.setIncomeType(incomeType1());
    income.setAmount(100000);
    income.setDescription("Paiement en exces");
    IncomeReceipt r1 = new IncomeReceipt();
    r1.setId(TestUtils.RECEIPT6A_ID);
    r1.setPaymentDate(LocalDate.of(2024, 4, 20));
    r1.setAmount(60000);
    IncomeReceipt r2 = new IncomeReceipt();
    r2.setId(TestUtils.RECEIPT6B_ID);
    r2.setPaymentDate(LocalDate.of(2024, 4, 25));
    r2.setAmount(60000);
    income.setReceipts(List.of(r1, r2));
    income.setRemainingAmount(-20000);
    return income;
  }

  static IncomeMoney income7() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME7_ID);
    income.setJob(TestOrganizationFixtures.jobToCrupdateJob(TestOrganizationFixtures.job1()));
    income.setSourceOrganization("Client Zeta");
    income.setInvoiceReference("INV-2024-007");
    income.setBillingStartDate(LocalDate.of(2024, 5, 1));
    income.setIncomeType(incomeType1());
    income.setAmount(100000);
    income.setDescription("Paiement total multiple recus");
    IncomeReceipt r1 = new IncomeReceipt();
    r1.setId(TestUtils.RECEIPT7A_ID);
    r1.setPaymentDate(LocalDate.of(2024, 5, 10));
    r1.setAmount(60000);
    IncomeReceipt r2 = new IncomeReceipt();
    r2.setId(TestUtils.RECEIPT7B_ID);
    r2.setPaymentDate(LocalDate.of(2024, 5, 15));
    r2.setAmount(40000);
    income.setReceipts(List.of(r1, r2));
    income.setRemainingAmount(0);
    return income;
  }
}
