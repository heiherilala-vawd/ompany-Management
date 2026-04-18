package com.example.demo.model.money;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Table(name = "expense_money")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseMoney implements Serializable {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;

  @OneToOne
  @JoinColumn(name = "monetary_id")
  private MonetaryMovement monetaryMovement;

  @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
  @ToString.Exclude
  private List<EmployeePayment> employeePayments = new ArrayList<>();

  @OneToOne(mappedBy = "expense")
  private TravelExpense travelExpense;

  @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
  @ToString.Exclude
  private List<Purchase> purchases = new ArrayList<>();

  @OneToOne(mappedBy = "expense")
  private BankFee bankFee;

  @OneToOne(mappedBy = "expense")
  private OtherExpense otherExpense;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    ExpenseMoney that = (ExpenseMoney) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
