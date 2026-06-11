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
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MoneyValidator {

  public void validateMonetaryMovement(MonetaryMovement movement) {
    List<String> errors = new ArrayList<>();
    if (movement == null) {
      errors.add("Monetary movement cannot be null");
    }
    if (movement != null
        && movement.getAmount() != null
        && movement.getAmount().compareTo(BigDecimal.ZERO) < 0) {
      errors.add("Amount must be non-negative");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateExpenseMoney(ExpenseMoney expense) {
    List<String> errors = new ArrayList<>();
    if (expense == null) {
      errors.add("Expense cannot be null");
    }
    if (expense != null && (expense.getJob() == null || expense.getJob().getId() == null)) {
      errors.add("Expense must be associated with a job");
    }
    if (expense != null
        && expense.getAmount() != null
        && expense.getAmount().compareTo(BigDecimal.ZERO) < 0) {
      errors.add("Amount must be non-negative");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateExpenseMonies(List<ExpenseMoney> expenses) {
    List<String> errors = new ArrayList<>();
    if (expenses == null || expenses.isEmpty()) {
      errors.add("Expense list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    expenses.forEach(this::validateExpenseMoney);
  }

  public void validateIncomeMoney(IncomeMoney income) {
    List<String> errors = new ArrayList<>();
    if (income == null) {
      errors.add("Income cannot be null");
    }
    if (income != null) {
      if (income.getAmount() != null && income.getAmount().compareTo(BigDecimal.ZERO) < 0) {
        errors.add("Amount must be non-negative");
      }
      if (income.getJob() == null || income.getJob().getId() == null) {
        errors.add("Income must be associated with a job");
      }
      if (income.getOrganization() == null || income.getOrganization().getId() == null) {
        errors.add("Organization is mandatory for income");
      }
      if (income.getIncomeType() == null || income.getIncomeType().getId() == null) {
        errors.add("Income type is mandatory for income");
      }
      if (income.getDueDate() != null
          && income.getFacturationDate() != null
          && income
              .getDueDate()
              .isBefore(income.getFacturationDate().atZone(ZoneId.systemDefault()).toLocalDate())) {
        errors.add("Due date cannot be before facturation date");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateIncomeMonies(List<IncomeMoney> incomes) {
    List<String> errors = new ArrayList<>();
    if (incomes == null || incomes.isEmpty()) {
      errors.add("Income list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    incomes.forEach(this::validateIncomeMoney);
  }

  public void validateIncomeType(IncomeType incomeType) {
    List<String> errors = new ArrayList<>();
    if (incomeType == null) {
      errors.add("Income type cannot be null");
    }
    if (incomeType != null && (incomeType.getName() == null || incomeType.getName().isBlank())) {
      errors.add("Income type name is mandatory");
    }
    if (incomeType != null
        && (incomeType.getCompany() == null || incomeType.getCompany().getId() == null)) {
      errors.add("Income type must be associated with a company");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateIncomeTypes(List<IncomeType> incomeTypes) {
    List<String> errors = new ArrayList<>();
    if (incomeTypes == null || incomeTypes.isEmpty()) {
      errors.add("Income type list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    incomeTypes.forEach(this::validateIncomeType);
  }

  public void validateEmployeePayment(EmployeePayment payment) {
    List<String> errors = new ArrayList<>();
    if (payment == null) {
      errors.add("Employee payment cannot be null");
    }
    if (payment != null) {
      if (payment.getExpense() == null || payment.getExpense().getId() == null) {
        errors.add("Employee payment must be linked to an expense");
      }
      if (Boolean.TRUE.equals(payment.getIsForTeam())) {
        if (payment.getTeam() == null || payment.getTeam().getId() == null) {
          errors.add("Team is required when payment is for a team");
        }
        if (payment.getUsers() != null && !payment.getUsers().isEmpty()) {
          errors.add("Users list must be empty when payment is for a team");
        }
      } else {
        if (payment.getUsers() == null || payment.getUsers().isEmpty()) {
          errors.add("Employee payment must be linked to at least one user");
        }
        if (payment.getUsers() != null
            && payment.getUsers().stream().anyMatch(u -> u == null || u.getId() == null)) {
          errors.add("All users must have a valid ID");
        }
        if (payment.getTeam() != null) {
          errors.add("Team must be null when payment is not for a team");
        }
      }
      if (payment.getPaymentType() == null) {
        errors.add("Payment type is mandatory");
      }
      if (payment.getPaymentDescription() == null || payment.getPaymentDescription().isBlank()) {
        errors.add("Payment description is mandatory");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateEmployeePayments(List<EmployeePayment> payments) {
    List<String> errors = new ArrayList<>();
    if (payments == null || payments.isEmpty()) {
      errors.add("Employee payment list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    payments.forEach(this::validateEmployeePayment);
  }

  public void validateTravelExpense(TravelExpense travelExpense) {
    List<String> errors = new ArrayList<>();
    if (travelExpense == null) {
      errors.add("Travel expense cannot be null");
    }
    if (travelExpense != null) {
      if (travelExpense.getExpense() == null || travelExpense.getExpense().getId() == null) {
        errors.add("Travel expense must be linked to an expense");
      }
      if (travelExpense.getDepartureLocation() == null
          || travelExpense.getDepartureLocation().getId() == null) {
        errors.add("Departure location is mandatory");
      }
      if (travelExpense.getArrivalLocation() == null
          || travelExpense.getArrivalLocation().getId() == null) {
        errors.add("Arrival location is mandatory");
      }
      if (travelExpense.getDepartureDate() == null) {
        errors.add("Departure date is mandatory");
      }
      if (travelExpense.getArrivalDate() == null) {
        errors.add("Arrival date is mandatory");
      }
      if (travelExpense.getDepartureDate() != null
          && travelExpense.getArrivalDate() != null
          && travelExpense.getDepartureDate().isAfter(travelExpense.getArrivalDate())) {
        errors.add("Departure date cannot be after arrival date");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateTravelExpenses(List<TravelExpense> travelExpenses) {
    List<String> errors = new ArrayList<>();
    if (travelExpenses == null || travelExpenses.isEmpty()) {
      errors.add("Travel expense list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    travelExpenses.forEach(this::validateTravelExpense);
  }

  public void validatePurchase(Purchase purchase) {
    List<String> errors = new ArrayList<>();
    if (purchase == null) {
      errors.add("Purchase cannot be null");
    }
    if (purchase != null) {
      if (purchase.getExpense() == null || purchase.getExpense().getId() == null) {
        errors.add("Purchase must be linked to an expense");
      }
      if (purchase.getSourceWarehouse() == null || purchase.getSourceWarehouse().getId() == null) {
        errors.add("Source warehouse is mandatory for purchase");
      }
      if (purchase.getIsEquipment() == null) {
        errors.add("IsEquipment flag is mandatory");
      }
      if (purchase.getQuantity() == null || purchase.getQuantity() <= 0) {
        errors.add("Quantity must be positive");
      }
      if (Boolean.TRUE.equals(purchase.getIsEquipment())) {
        if (purchase.getEquipment() == null || purchase.getEquipment().getId() == null) {
          errors.add("Equipment is mandatory when isEquipment is true");
        }
      } else if (purchase.getIsEquipment() != null) {
        if (purchase.getMaterial() == null || purchase.getMaterial().getId() == null) {
          errors.add("Material is mandatory when isEquipment is false");
        }
      }
      if (purchase.getInvoiceDate() == null) {
        errors.add("Invoice date is mandatory for purchase");
      }
      if (purchase.getPaidAt() != null
          && purchase.getInvoiceDate() != null
          && purchase.getPaidAt().isBefore(purchase.getInvoiceDate())) {
        errors.add("Payment date cannot be before invoice date");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validatePurchases(List<Purchase> purchases) {
    List<String> errors = new ArrayList<>();
    if (purchases == null || purchases.isEmpty()) {
      errors.add("Purchase list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    purchases.forEach(this::validatePurchase);
  }

  public void validateBankFee(BankFee bankFee) {
    List<String> errors = new ArrayList<>();
    if (bankFee == null) {
      errors.add("Bank fee cannot be null");
    }
    if (bankFee != null) {
      if (bankFee.getExpense() == null || bankFee.getExpense().getId() == null) {
        errors.add("Bank fee must be linked to an expense");
      }
      if (bankFee.getExpense() != null
          && bankFee.getExpense().getAmount() != null
          && bankFee.getExpense().getAmount().compareTo(BigDecimal.ZERO) < 0) {
        errors.add("Bank fee amount must be non-negative");
      }
      if (bankFee.getBankName() == null || bankFee.getBankName().isBlank()) {
        errors.add("Bank name is mandatory");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateBankFees(List<BankFee> bankFees) {
    List<String> errors = new ArrayList<>();
    if (bankFees == null || bankFees.isEmpty()) {
      errors.add("Bank fee list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    bankFees.forEach(this::validateBankFee);
  }

  public void validateOtherExpenseType(OtherExpenseType otherExpenseType) {
    List<String> errors = new ArrayList<>();
    if (otherExpenseType == null) {
      errors.add("Other expense type cannot be null");
    }
    if (otherExpenseType != null) {
      if (otherExpenseType.getName() == null || otherExpenseType.getName().isBlank()) {
        errors.add("Other expense type name is mandatory");
      }
      if (otherExpenseType.getCompany() == null || otherExpenseType.getCompany().getId() == null) {
        errors.add("Other expense type must be associated with a company");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateCompanyFixedCost(CompanyFixedCost fixedCost) {
    List<String> errors = new ArrayList<>();
    if (fixedCost == null) {
      errors.add("Company fixed cost cannot be null");
    }
    if (fixedCost != null) {
      if (fixedCost.getName() == null || fixedCost.getName().isBlank()) {
        errors.add("Company fixed cost name is mandatory");
      }
      if (fixedCost.getAmount() == null
          || fixedCost.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
        errors.add("Company fixed cost amount must be non-negative");
      }
      if (fixedCost.getCompany() == null || fixedCost.getCompany().getId() == null) {
        errors.add("Company fixed cost must be associated with a company");
      }
      if (fixedCost.getStartDate() == null) {
        errors.add("Company fixed cost start date is mandatory");
      }
      if (fixedCost.getStartDate() != null
          && fixedCost.getEndDate() != null
          && fixedCost.getEndDate().isBefore(fixedCost.getStartDate())) {
        errors.add("Company fixed cost end date cannot be before start date");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateCompanyFixedCosts(List<CompanyFixedCost> fixedCosts) {
    List<String> errors = new ArrayList<>();
    if (fixedCosts == null || fixedCosts.isEmpty()) {
      errors.add("Company fixed cost list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    fixedCosts.forEach(this::validateCompanyFixedCost);
  }

  public void validateOtherExpenseTypes(List<OtherExpenseType> otherExpenseTypes) {
    List<String> errors = new ArrayList<>();
    if (otherExpenseTypes == null || otherExpenseTypes.isEmpty()) {
      errors.add("Other expense type list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    otherExpenseTypes.forEach(this::validateOtherExpenseType);
  }

  public void validateOtherExpense(OtherExpense otherExpense) {
    List<String> errors = new ArrayList<>();
    if (otherExpense == null) {
      errors.add("Other expense cannot be null");
    }
    if (otherExpense != null) {
      if (otherExpense.getExpense() == null || otherExpense.getExpense().getId() == null) {
        errors.add("Other expense must be linked to an expense");
      }
      if (otherExpense.getDescription() == null || otherExpense.getDescription().isBlank()) {
        errors.add("Description is mandatory for other expense");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateOtherExpenses(List<OtherExpense> otherExpenses) {
    List<String> errors = new ArrayList<>();
    if (otherExpenses == null || otherExpenses.isEmpty()) {
      errors.add("Other expense list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    otherExpenses.forEach(this::validateOtherExpense);
  }

  public void validateLoan(Loan loan) {
    List<String> errors = new ArrayList<>();
    if (loan == null) {
      errors.add("Loan cannot be null");
    }
    if (loan != null) {
      if (loan.getAmount() == null || loan.getAmount().compareTo(BigDecimal.ZERO) < 0) {
        errors.add("Loan amount must be non-negative");
      }
      if (loan.getOrganization() == null || loan.getOrganization().getId() == null) {
        errors.add("Organization is mandatory for loan");
      }
      if (loan.getInterestRate() == null || loan.getInterestRate() < 0) {
        errors.add("Interest rate must be non-negative");
      }
      if (loan.getStartDate() == null) {
        errors.add("Start date is mandatory for loan");
      }
      if (loan.getJob() == null || loan.getJob().getId() == null) {
        errors.add("Loan must be associated with a job");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateLoans(List<Loan> loans) {
    List<String> errors = new ArrayList<>();
    if (loans == null || loans.isEmpty()) {
      errors.add("Loan list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    loans.forEach(this::validateLoan);
  }

  public void validateLoanRepayment(LoanRepayment repayment) {
    List<String> errors = new ArrayList<>();
    if (repayment == null) {
      errors.add("Loan repayment cannot be null");
    }
    if (repayment != null) {
      if (repayment.getLoan() == null || repayment.getLoan().getId() == null) {
        errors.add("Loan repayment must be linked to a loan");
      }
      if (repayment.getPaymentDate() == null) {
        errors.add("Payment date is mandatory for loan repayment");
      }
      if (repayment.getAmount() == null || repayment.getAmount().compareTo(BigDecimal.ZERO) < 0) {
        errors.add("Repayment amount must be non-negative");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateLoanRepayments(List<LoanRepayment> repayments) {
    List<String> errors = new ArrayList<>();
    if (repayments == null || repayments.isEmpty()) {
      errors.add("Loan repayment list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    repayments.forEach(this::validateLoanRepayment);
  }

  public void validateIncomeReceipt(IncomeReceipt receipt) {
    List<String> errors = new ArrayList<>();
    if (receipt == null) {
      errors.add("Income receipt cannot be null");
    }
    if (receipt != null) {
      if (receipt.getIncome() == null || receipt.getIncome().getId() == null) {
        errors.add("Income receipt must be linked to an income");
      }
      if (receipt.getPaymentDate() == null) {
        errors.add("Payment date is mandatory for income receipt");
      }
      if (receipt.getAmount() == null || receipt.getAmount().compareTo(BigDecimal.ZERO) < 0) {
        errors.add("Receipt amount must be non-negative");
      }
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateIncomeReceipts(List<IncomeReceipt> receipts) {
    List<String> errors = new ArrayList<>();
    if (receipts == null || receipts.isEmpty()) {
      errors.add("Income receipt list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    receipts.forEach(this::validateIncomeReceipt);
  }
}
