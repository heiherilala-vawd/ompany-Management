package com.example.demo.model.money;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.User;
import com.example.demo.model.core.Team;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "employee_payment")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePayment extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @OneToOne
  @JoinColumn(name = "expense_id")
  @JsonManagedReference
  private ExpenseMoney expense;

  @ManyToMany
  @JoinTable(
      name = "employee_payment_users",
      joinColumns = @JoinColumn(name = "employee_payment_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  @OrderBy("id ASC")
  @Builder.Default
  private List<User> users = new ArrayList<>();

  @Column(name = "is_for_team")
  @Builder.Default
  private Boolean isForTeam = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  private Team team;

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
        + ", isForTeam="
        + isForTeam
        + ", team="
        + (team != null ? team.getId() : null)
        + ", users="
        + (users != null ? users.stream().map(u -> u.getId() + ":" + u.getEmail()).toList() : null)
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
