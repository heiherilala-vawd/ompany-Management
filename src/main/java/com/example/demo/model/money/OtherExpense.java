package com.example.demo.model.money;

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

@Entity
@Table(name = "other_expense")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtherExpense implements Serializable {

  @Id private String id;

  @OneToOne
  @JoinColumn(name = "expense_id")
  @JsonManagedReference
  private ExpenseMoney expense;

  private String description;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    OtherExpense that = (OtherExpense) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "OtherExpense{"
        + "id='"
        + id
        + '\''
        + ", expense="
        + (expense != null ? expense.getId() : null)
        + ", description='"
        + description
        + '\''
        + '}';
  }
}
