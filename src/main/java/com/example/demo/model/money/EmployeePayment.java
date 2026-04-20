package com.example.demo.model.money;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.example.demo.model.User;
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

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;

  @ManyToOne
  @JoinColumn(name = "expense_id")
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

  public enum PaymentType {
    ADVANCE,
    MONTHLY,
    OTHER
  }
}
