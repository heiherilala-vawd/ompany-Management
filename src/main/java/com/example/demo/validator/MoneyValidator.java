package com.example.demo.validator;

import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.money.BankFee;
import com.example.demo.model.money.CompanyFixedCost;
import com.example.demo.model.money.EmployeePayment;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.IncomeMoney;
import com.example.demo.model.money.IncomeReceipt;
import com.example.demo.model.money.IncomeType;
import com.example.demo.model.money.Loan;
import com.example.demo.model.money.LoanRepayment;
import com.example.demo.model.money.MonetaryMovement;
import com.example.demo.model.money.OtherExpense;
import com.example.demo.model.money.OtherExpenseType;
import com.example.demo.model.money.Purchase;
import com.example.demo.model.money.TravelExpense;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MoneyValidator {

  public void validateMonetaryMovement(MonetaryMovement movement) {
    if (movement == null) {
      throw new BadRequestException("Monetary movement cannot be null");
    }
    if (movement.getAmount() == null || movement.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Amount must be positive");
    }
  }

  public void validateExpenseMoney(ExpenseMoney expense) {
    if (expense == null) {
      throw new BadRequestException("Expense cannot be null");
    }
    if (expense.getJob() == null || expense.getJob().getId() == null) {
      throw new BadRequestException("Expense must be associated with a job");
    }
    if (expense.getAmount() != null && expense.getAmount().compareTo(BigDecimal.ZERO) < 0) {
      throw new BadRequestException("Amount must be positive");
    }
  }

  public void validateExpenseMonies(List<ExpenseMoney> expenses) {
    if (expenses == null || expenses.isEmpty()) {
      throw new BadRequestException("Expense list cannot be null or empty");
    }
    expenses.forEach(this::validateExpenseMoney);
  }

  public void validateIncomeMoney(IncomeMoney income) {
    validateMonetaryMovement(income);
    if (income.getJob() == null || income.getJob().getId() == null) {
      throw new BadRequestException("Income must be associated with a job");
    }
    if (income.getSourceOrganization() == null || income.getSourceOrganization().isBlank()) {
      throw new BadRequestException("Source organization is mandatory for income");
    }
    if (income.getIncomeType() == null || income.getIncomeType().getId() == null) {
      throw new BadRequestException("Income type is mandatory for income");
    }
    if (income.getDueDate() != null
        && income.getFacturationDate() != null
        && income
            .getDueDate()
            .isBefore(income.getFacturationDate().atZone(ZoneId.systemDefault()).toLocalDate())) {
      throw new BadRequestException("Due date cannot be before facturation date");
    }
  }

  public void validateIncomeMonies(List<IncomeMoney> incomes) {
    if (incomes == null || incomes.isEmpty()) {
      throw new BadRequestException("Income list cannot be null or empty");
    }
    incomes.forEach(this::validateIncomeMoney);
  }

  public void validateIncomeType(IncomeType incomeType) {
    if (incomeType == null) {
      throw new BadRequestException("Income type cannot be null");
    }
    if (incomeType.getName() == null || incomeType.getName().isBlank()) {
      throw new BadRequestException("Income type name is mandatory");
    }
    if (incomeType.getCompany() == null || incomeType.getCompany().getId() == null) {
      throw new BadRequestException("Income type must be associated with a company");
    }
  }

  public void validateIncomeTypes(List<IncomeType> incomeTypes) {
    if (incomeTypes == null || incomeTypes.isEmpty()) {
      throw new BadRequestException("Income type list cannot be null or empty");
    }
    incomeTypes.forEach(this::validateIncomeType);
  }

  public void validateEmployeePayment(EmployeePayment payment) {
    if (payment == null) {
      throw new BadRequestException("Employee payment cannot be null");
    }
    if (payment.getExpense() == null || payment.getExpense().getId() == null) {
      throw new BadRequestException("Employee payment must be linked to an expense");
    }
    if (Boolean.TRUE.equals(payment.getIsForTeam())) {
      if (payment.getTeam() == null || payment.getTeam().getId() == null) {
        throw new BadRequestException("Team is required when payment is for a team");
      }
      if (payment.getUsers() != null && !payment.getUsers().isEmpty()) {
        throw new BadRequestException("Users list must be empty when payment is for a team");
      }
    } else {
      if (payment.getUsers() == null || payment.getUsers().isEmpty()) {
        throw new BadRequestException("Employee payment must be linked to at least one user");
      }
      if (payment.getUsers().stream().anyMatch(u -> u == null || u.getId() == null)) {
        throw new BadRequestException("All users must have a valid ID");
      }
      if (payment.getTeam() != null) {
        throw new BadRequestException("Team must be null when payment is not for a team");
      }
    }
    if (payment.getPaymentType() == null) {
      throw new BadRequestException("Payment type is mandatory");
    }
    if (payment.getPaymentDescription() == null || payment.getPaymentDescription().isBlank()) {
      throw new BadRequestException("Payment description is mandatory");
    }
  }

  public void validateEmployeePayments(List<EmployeePayment> payments) {
    if (payments == null || payments.isEmpty()) {
      throw new BadRequestException("Employee payment list cannot be null or empty");
    }
    payments.forEach(this::validateEmployeePayment);
  }

  public void validateTravelExpense(TravelExpense travelExpense) {
    if (travelExpense == null) {
      throw new BadRequestException("Travel expense cannot be null");
    }
    if (travelExpense.getExpense() == null || travelExpense.getExpense().getId() == null) {
      throw new BadRequestException("Travel expense must be linked to an expense");
    }
    if (travelExpense.getDepartureLocation() == null
        || travelExpense.getDepartureLocation().getId() == null) {
      throw new BadRequestException("Departure location is mandatory");
    }
    if (travelExpense.getArrivalLocation() == null
        || travelExpense.getArrivalLocation().getId() == null) {
      throw new BadRequestException("Arrival location is mandatory");
    }
    if (travelExpense.getDepartureDate() == null) {
      throw new BadRequestException("Departure date is mandatory");
    }
    if (travelExpense.getArrivalDate() == null) {
      throw new BadRequestException("Arrival date is mandatory");
    }
    if (travelExpense.getDepartureDate().isAfter(travelExpense.getArrivalDate())) {
      throw new BadRequestException("Departure date cannot be after arrival date");
    }
  }

  public void validateTravelExpenses(List<TravelExpense> travelExpenses) {
    if (travelExpenses == null || travelExpenses.isEmpty()) {
      throw new BadRequestException("Travel expense list cannot be null or empty");
    }
    travelExpenses.forEach(this::validateTravelExpense);
  }

  public void validatePurchase(Purchase purchase) {
    if (purchase == null) {
      throw new BadRequestException("Purchase cannot be null");
    }
    if (purchase.getExpense() == null || purchase.getExpense().getId() == null) {
      throw new BadRequestException("Purchase must be linked to an expense");
    }
    if (purchase.getSupplier() == null || purchase.getSupplier().getId() == null) {
      throw new BadRequestException("Supplier is mandatory for purchase");
    }
    if (purchase.getIsEquipment() == null) {
      throw new BadRequestException("IsEquipment flag is mandatory");
    }
    if (purchase.getQuantity() == null || purchase.getQuantity() <= 0) {
      throw new BadRequestException("Quantity must be positive");
    }
    if (Boolean.TRUE.equals(purchase.getIsEquipment())) {
      if (purchase.getEquipment() == null || purchase.getEquipment().getId() == null) {
        throw new BadRequestException("Equipment is mandatory when isEquipment is true");
      }
    } else {
      if (purchase.getMaterial() == null || purchase.getMaterial().getId() == null) {
        throw new BadRequestException("Material is mandatory when isEquipment is false");
      }
    }
    if (purchase.getInvoiceDate() == null) {
      throw new BadRequestException("Invoice date is mandatory for purchase");
    }
    if (purchase.getPaidAt() != null && purchase.getPaidAt().isBefore(purchase.getInvoiceDate())) {
      throw new BadRequestException("Payment date cannot be before invoice date");
    }
  }

  public void validatePurchases(List<Purchase> purchases) {
    if (purchases == null || purchases.isEmpty()) {
      throw new BadRequestException("Purchase list cannot be null or empty");
    }
    purchases.forEach(this::validatePurchase);
  }

  public void validateBankFee(BankFee bankFee) {
    if (bankFee == null) {
      throw new BadRequestException("Bank fee cannot be null");
    }
    if (bankFee.getExpense() == null || bankFee.getExpense().getId() == null) {
      throw new BadRequestException("Bank fee must be linked to an expense");
    }
    if (bankFee.getExpense().getAmount() == null
        || bankFee.getExpense().getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Bank fee amount must be positive");
    }
    if (bankFee.getBankName() == null || bankFee.getBankName().isBlank()) {
      throw new BadRequestException("Bank name is mandatory");
    }
  }

  public void validateBankFees(List<BankFee> bankFees) {
    if (bankFees == null || bankFees.isEmpty()) {
      throw new BadRequestException("Bank fee list cannot be null or empty");
    }
    bankFees.forEach(this::validateBankFee);
  }

  public void validateOtherExpenseType(OtherExpenseType otherExpenseType) {
    if (otherExpenseType == null) {
      throw new BadRequestException("Other expense type cannot be null");
    }
    if (otherExpenseType.getName() == null || otherExpenseType.getName().isBlank()) {
      throw new BadRequestException("Other expense type name is mandatory");
    }
    if (otherExpenseType.getCompany() == null || otherExpenseType.getCompany().getId() == null) {
      throw new BadRequestException("Other expense type must be associated with a company");
    }
  }

  public void validateCompanyFixedCost(CompanyFixedCost fixedCost) {
    if (fixedCost == null) {
      throw new BadRequestException("Company fixed cost cannot be null");
    }
    if (fixedCost.getName() == null || fixedCost.getName().isBlank()) {
      throw new BadRequestException("Company fixed cost name is mandatory");
    }
    if (fixedCost.getAmount() == null
        || fixedCost.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Company fixed cost amount must be positive");
    }
    if (fixedCost.getCompany() == null || fixedCost.getCompany().getId() == null) {
      throw new BadRequestException("Company fixed cost must be associated with a company");
    }
    if (fixedCost.getStartDate() == null) {
      throw new BadRequestException("Company fixed cost start date is mandatory");
    }
    if (fixedCost.getEndDate() != null
        && fixedCost.getEndDate().isBefore(fixedCost.getStartDate())) {
      throw new BadRequestException("Company fixed cost end date cannot be before start date");
    }
  }

  public void validateCompanyFixedCosts(List<CompanyFixedCost> fixedCosts) {
    if (fixedCosts == null || fixedCosts.isEmpty()) {
      throw new BadRequestException("Company fixed cost list cannot be null or empty");
    }
    fixedCosts.forEach(this::validateCompanyFixedCost);
  }

  public void validateOtherExpenseTypes(List<OtherExpenseType> otherExpenseTypes) {
    if (otherExpenseTypes == null || otherExpenseTypes.isEmpty()) {
      throw new BadRequestException("Other expense type list cannot be null or empty");
    }
    otherExpenseTypes.forEach(this::validateOtherExpenseType);
  }

  public void validateOtherExpense(OtherExpense otherExpense) {
    if (otherExpense == null) {
      throw new BadRequestException("Other expense cannot be null");
    }
    if (otherExpense.getExpense() == null || otherExpense.getExpense().getId() == null) {
      throw new BadRequestException("Other expense must be linked to an expense");
    }
    if (otherExpense.getDescription() == null || otherExpense.getDescription().isBlank()) {
      throw new BadRequestException("Description is mandatory for other expense");
    }
  }

  public void validateOtherExpenses(List<OtherExpense> otherExpenses) {
    if (otherExpenses == null || otherExpenses.isEmpty()) {
      throw new BadRequestException("Other expense list cannot be null or empty");
    }
    otherExpenses.forEach(this::validateOtherExpense);
  }

  public void validateLoan(Loan loan) {
    if (loan == null) {
      throw new BadRequestException("Loan cannot be null");
    }
    if (loan.getAmount() == null || loan.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Loan amount must be positive");
    }
    if (loan.getLender() == null || loan.getLender().isBlank()) {
      throw new BadRequestException("Lender is mandatory for loan");
    }
    if (loan.getInterestRate() == null || loan.getInterestRate() < 0) {
      throw new BadRequestException("Interest rate must be non-negative");
    }
    if (loan.getStartDate() == null) {
      throw new BadRequestException("Start date is mandatory for loan");
    }
    if (loan.getJob() == null || loan.getJob().getId() == null) {
      throw new BadRequestException("Loan must be associated with a job");
    }
  }

  public void validateLoans(List<Loan> loans) {
    if (loans == null || loans.isEmpty()) {
      throw new BadRequestException("Loan list cannot be null or empty");
    }
    loans.forEach(this::validateLoan);
  }

  public void validateLoanRepayment(LoanRepayment repayment) {
    if (repayment == null) {
      throw new BadRequestException("Loan repayment cannot be null");
    }
    if (repayment.getLoan() == null || repayment.getLoan().getId() == null) {
      throw new BadRequestException("Loan repayment must be linked to a loan");
    }
    if (repayment.getPaymentDate() == null) {
      throw new BadRequestException("Payment date is mandatory for loan repayment");
    }
    if (repayment.getAmount() == null || repayment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Repayment amount must be positive");
    }
  }

  public void validateLoanRepayments(List<LoanRepayment> repayments) {
    if (repayments == null || repayments.isEmpty()) {
      throw new BadRequestException("Loan repayment list cannot be null or empty");
    }
    repayments.forEach(this::validateLoanRepayment);
  }

  public void validateIncomeReceipt(IncomeReceipt receipt) {
    if (receipt == null) {
      throw new BadRequestException("Income receipt cannot be null");
    }
    if (receipt.getIncome() == null || receipt.getIncome().getId() == null) {
      throw new BadRequestException("Income receipt must be linked to an income");
    }
    if (receipt.getPaymentDate() == null) {
      throw new BadRequestException("Payment date is mandatory for income receipt");
    }
    if (receipt.getAmount() == null || receipt.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Receipt amount must be positive");
    }
  }

  public void validateIncomeReceipts(List<IncomeReceipt> receipts) {
    if (receipts == null || receipts.isEmpty()) {
      throw new BadRequestException("Income receipt list cannot be null or empty");
    }
    receipts.forEach(this::validateIncomeReceipt);
  }
}
