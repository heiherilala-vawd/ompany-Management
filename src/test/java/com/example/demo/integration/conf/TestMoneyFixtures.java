package com.example.demo.integration.conf;

import com.example.demo.client.model.BankFee;
import com.example.demo.client.model.CrupdateBankFee;
import com.example.demo.client.model.CrupdateEmployeePayment;
import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.CrupdateOtherExpense;
import com.example.demo.client.model.CrupdatePurchase;
import com.example.demo.client.model.EmployeePayment;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.client.model.OtherExpense;
import com.example.demo.client.model.PaymentType;
import com.example.demo.client.model.Purchase;
import java.util.UUID;

final class TestMoneyFixtures {

  private TestMoneyFixtures() {}

  static IncomeMoney income1() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME1_ID);
    income.setJobId(TestUtils.JOB1_ID);
    income.setSourceOrganization("Client Alpha");
    income.setInvoiceReference("INV-2024-001");
    income.setAmount(150000);
    income.setDescription("Paiement initial chantier A");
    return income;
  }

  static IncomeMoney income2() {
    IncomeMoney income = new IncomeMoney();
    income.setId(TestUtils.INCOME2_ID);
    income.setJobId(TestUtils.JOB1_ID);
    income.setSourceOrganization("Client Beta");
    income.setInvoiceReference("INV-2024-002");
    income.setAmount(275000);
    income.setDescription("Paiement avance renovation hotel");
    return income;
  }

  static CrupdateIncomeMoney incomeToCrupdateIncome(IncomeMoney income) {
    CrupdateIncomeMoney crupdateIncome = new CrupdateIncomeMoney();
    crupdateIncome.setId(income.getId());
    crupdateIncome.setSourceOrganization(income.getSourceOrganization());
    crupdateIncome.setInvoiceReference(income.getInvoiceReference());
    crupdateIncome.setAmount(income.getAmount());
    crupdateIncome.setDescription(income.getDescription());
    crupdateIncome.setComment(income.getComment());
    return crupdateIncome;
  }

  static CrupdateIncomeMoney someCreatableIncome() {
    CrupdateIncomeMoney income = new CrupdateIncomeMoney();
    income.setId(UUID.randomUUID().toString());
    income.setSourceOrganization("Client Gamma");
    income.setInvoiceReference("INV-2024-003");
    income.setAmount(99000);
    income.setDescription("Paiement complementaire");
    return income;
  }

  static ExpenseMoney expense1() {
    ExpenseMoney expense = new ExpenseMoney();
    expense.setJobId(TestUtils.JOB1_ID);
    expense.setId(TestUtils.EXPENSE1_ID);
    expense.setAmount(45000);
    expense.setDescription("Achat materiaux chantier A");
    return expense;
  }

  static ExpenseMoney expense2() {
    ExpenseMoney expense = new ExpenseMoney();
    expense.setId(TestUtils.EXPENSE2_ID);
    expense.setJobId(TestUtils.JOB1_ID);
    expense.setAmount(80000);
    expense.setDescription("Paiement sous-traitant renovation");
    return expense;
  }

  static CrupdateExpenseMoney expenseToCrupdateExpense(ExpenseMoney expense) {
    CrupdateExpenseMoney crupdateExpense = new CrupdateExpenseMoney();
    crupdateExpense.setId(expense.getId());
    crupdateExpense.setAmount(expense.getAmount());
    crupdateExpense.setDescription(expense.getDescription());
    crupdateExpense.setComment(expense.getComment());
    return crupdateExpense;
  }

  static CrupdateExpenseMoney someCreatableExpense() {
    CrupdateExpenseMoney expense = new CrupdateExpenseMoney();
    expense.setId(UUID.randomUUID().toString());
    expense.setAmount(32000);
    expense.setDescription("Frais logistiques");
    return expense;
  }

  static BankFee bankFee1() {
    BankFee bankFee = new BankFee();
    bankFee.setId(TestUtils.BANK_FEE1_ID);
    bankFee.setExpenseId(TestUtils.EXPENSE1_ID);
    bankFee.setBankName("BNI Madagascar");
    bankFee.setDescription("Frais virement fournisseur");
    return bankFee;
  }

  static BankFee bankFee2() {
    BankFee bankFee = new BankFee();
    bankFee.setId(TestUtils.BANK_FEE2_ID);
    bankFee.setExpenseId(TestUtils.EXPENSE2_ID);
    bankFee.setBankName("BOA Madagascar");
    bankFee.setDescription("Commission paiement sous-traitant");
    return bankFee;
  }

  static CrupdateBankFee bankFeeToCrupdateBankFee(BankFee bankFee) {
    CrupdateBankFee crupdateBankFee = new CrupdateBankFee();
    crupdateBankFee.setId(bankFee.getId());
    crupdateBankFee.setExpenseId(bankFee.getExpenseId());
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
    employeePayment.setExpenseId(TestUtils.EXPENSE1_ID);
    employeePayment.setEmployeeId(TestUtils.EMPLOYEE_ID);
    employeePayment.setPaymentDescription("Avance salaire chantier A");
    employeePayment.setPaymentType(PaymentType.ADVANCE);
    return employeePayment;
  }

  static EmployeePayment employeePayment2() {
    EmployeePayment employeePayment = new EmployeePayment();
    employeePayment.setId(TestUtils.EMPLOYEE_PAYMENT2_ID);
    employeePayment.setExpenseId(TestUtils.EXPENSE2_ID);
    employeePayment.setEmployeeId(TestUtils.USER1_ID);
    employeePayment.setPaymentDescription("Paiement mensuel renovation");
    employeePayment.setPaymentType(PaymentType.MONTHLY);
    return employeePayment;
  }

  static CrupdateEmployeePayment employeePaymentToCrupdateEmployeePayment(
      EmployeePayment employeePayment) {
    CrupdateEmployeePayment crupdateEmployeePayment = new CrupdateEmployeePayment();
    crupdateEmployeePayment.setId(employeePayment.getId());
    crupdateEmployeePayment.setExpenseId(employeePayment.getExpenseId());
    crupdateEmployeePayment.setEmployeeId(employeePayment.getEmployeeId());
    crupdateEmployeePayment.setPaymentDescription(employeePayment.getPaymentDescription());
    crupdateEmployeePayment.setPaymentType(employeePayment.getPaymentType());
    return crupdateEmployeePayment;
  }

  static CrupdateEmployeePayment someCreatableEmployeePayment() {
    CrupdateEmployeePayment employeePayment = new CrupdateEmployeePayment();
    employeePayment.setId(UUID.randomUUID().toString());
    employeePayment.setExpenseId(TestUtils.EXPENSE1_ID);
    employeePayment.setEmployeeId(TestUtils.EMPLOYEE_ID);
    employeePayment.setPaymentDescription("Prime exceptionnelle");
    employeePayment.setPaymentType(PaymentType.OTHER);
    return employeePayment;
  }

  static OtherExpense otherExpense1() {
    OtherExpense otherExpense = new OtherExpense();
    otherExpense.setId(TestUtils.OTHER_EXPENSE1_ID);
    otherExpense.setExpenseId(TestUtils.EXPENSE1_ID);
    otherExpense.setDescription("Frais administratifs chantier A");
    return otherExpense;
  }

  static OtherExpense otherExpense2() {
    OtherExpense otherExpense = new OtherExpense();
    otherExpense.setId(TestUtils.OTHER_EXPENSE2_ID);
    otherExpense.setExpenseId(TestUtils.EXPENSE2_ID);
    otherExpense.setDescription("Imprevus renovation hotel");
    return otherExpense;
  }

  static CrupdateOtherExpense otherExpenseToCrupdateOtherExpense(OtherExpense otherExpense) {
    CrupdateOtherExpense crupdateOtherExpense = new CrupdateOtherExpense();
    crupdateOtherExpense.setId(otherExpense.getId());
    crupdateOtherExpense.setExpenseId(otherExpense.getExpenseId());
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

  static Purchase purchase1() {
    Purchase purchase = new Purchase();
    purchase.setId(TestUtils.PURCHASE1_ID);
    purchase.setExpenseId(TestUtils.EXPENSE1_ID);
    purchase.setSupplier("Fournisseur Beton SA");
    purchase.setEquipment(TestUtils.EQUIPMENT1_ID);
    purchase.setMaterial(TestUtils.MATERIAL1_ID);
    purchase.setQuantity(3);
    purchase.setIsEquipment(true);
    return purchase;
  }

  static Purchase purchase2() {
    Purchase purchase = new Purchase();
    purchase.setId(TestUtils.PURCHASE2_ID);
    purchase.setExpenseId(TestUtils.EXPENSE2_ID);
    purchase.setSupplier("Materiaux Plus");
    purchase.setEquipment(TestUtils.EQUIPMENT2_ID);
    purchase.setMaterial(TestUtils.MATERIAL2_ID);
    purchase.setQuantity(25);
    purchase.setIsEquipment(false);
    return purchase;
  }

  static CrupdatePurchase purchaseToCrupdatePurchase(Purchase purchase) {
    CrupdatePurchase crupdatePurchase = new CrupdatePurchase();
    crupdatePurchase.setId(purchase.getId());
    crupdatePurchase.setExpenseId(purchase.getExpenseId());
    crupdatePurchase.setSupplier(purchase.getSupplier());
    crupdatePurchase.setEquipment(purchase.getEquipment());
    crupdatePurchase.setMaterial(purchase.getMaterial());
    crupdatePurchase.setQuantity(purchase.getQuantity());
    crupdatePurchase.setIsEquipment(purchase.getIsEquipment());
    return crupdatePurchase;
  }

  static CrupdatePurchase someCreatablePurchase() {
    CrupdatePurchase purchase = new CrupdatePurchase();
    purchase.setId(UUID.randomUUID().toString());
    purchase.setExpenseId(TestUtils.EXPENSE1_ID);
    purchase.setSupplier("Quincaillerie Centrale");
    purchase.setEquipment(TestUtils.EQUIPMENT1_ID);
    purchase.setMaterial(TestUtils.MATERIAL1_ID);
    purchase.setQuantity(4);
    purchase.setIsEquipment(true);
    return purchase;
  }
}
