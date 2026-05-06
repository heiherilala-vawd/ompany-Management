package com.example.demo.validator;

import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.money.BankFee;
import com.example.demo.model.money.EmployeePayment;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.IncomeMoney;
import com.example.demo.model.money.IncomeType;
import com.example.demo.model.money.MonetaryMovement;
import com.example.demo.model.money.OtherExpense;
import com.example.demo.model.money.Purchase;
import com.example.demo.model.money.TravelExpense;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MoneyValidator {

  public void validateMonetaryMovement(MonetaryMovement movement) {
    if (movement == null) {
      throw new BadRequestException("Monetary movement cannot be null");
    }
    if (movement.getAmount() == null || movement.getAmount() <= 0) {
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
    if (expense.getAmount() != null && expense.getAmount() < 0) {
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
    if (payment.getEmployee() == null || payment.getEmployee().getId() == null) {
      throw new BadRequestException("Employee payment must be linked to an employee");
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
}
