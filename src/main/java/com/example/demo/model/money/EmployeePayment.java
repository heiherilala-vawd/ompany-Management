package com.example.demo.model.money;

import com.example.demo.model.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "employee_payment")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePayment implements Serializable {

  @Id private String id;

  @OneToOne
  @JoinColumn(name = "expense_id")
  @JsonManagedReference
  private ExpenseMoney expense;

  @ManyToOne
  @JoinColumn(name = "employee_id")
  private User employee;

  private String paymentDescription;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private PaymentType paymentType;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    EmployeePayment that = (EmployeePayment) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "EmployeePayment{"
        + "id='"
        + id
        + '\''
        + ", expense="
        + (expense != null ? expense.getId() : null)
        + ", employee="
        + (employee != null ? employee.getId() + ":" + employee.getEmail() : null)
        + ", paymentDescription='"
        + paymentDescription
        + '\''
        + ", paymentType="
        + paymentType
        + '}';
  }

  public enum PaymentType {
    ADVANCE,
    MONTHLY,
    OTHER
  }
}
