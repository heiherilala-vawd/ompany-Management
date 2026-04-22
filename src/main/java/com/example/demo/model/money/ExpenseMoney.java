package com.example.demo.model.money;

import com.example.demo.model.Job;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

@Entity
@Table(name = "expense_money")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseMoney extends MonetaryMovement implements Serializable {

  @Id private String id;

  @OneToOne(mappedBy = "expense")
  @JsonBackReference
  private EmployeePayment employeePayment;

  @OneToOne(mappedBy = "expense")
  @JsonBackReference
  private TravelExpense travelExpense;

  @OneToOne(mappedBy = "expense")
  @JsonBackReference
  private Purchase purchase;

  @OneToOne(mappedBy = "expense")
  @JsonBackReference
  private BankFee bankFee;

  @OneToOne(mappedBy = "expense")
  @JsonBackReference
  private OtherExpense otherExpense;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  private Job job;

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

  @Override
  public String toString() {
    return "ExpenseMoney{"
        + "id='"
        + id
        + '\''
        + ", employeePayment="
        + (employeePayment != null ? employeePayment.getId() : null)
        + ", travelExpense="
        + (travelExpense != null ? travelExpense.getId() : null)
        + ", purchase="
        + (purchase != null ? purchase.getId() : null)
        + ", bankFee="
        + (bankFee != null ? bankFee.getId() : null)
        + ", otherExpense="
        + (otherExpense != null ? otherExpense.getId() : null)
        + ", job="
        + (job != null ? job.getId() : null)
        + '}';
  }
}
