package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.money.ExpenseMoney;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "maintenance")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Maintenance extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @OneToOne
  @JoinColumn(name = "expense_id")
  @JsonManagedReference
  private ExpenseMoney expense;

  @ManyToOne
  @JoinColumn(name = "equipment_id")
  private Equipment equipment;

  private String description;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Maintenance that = (Maintenance) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Maintenance{"
        + "id='"
        + id
        + '\''
        + ", expense="
        + (expense != null ? expense.getId() : null)
        + ", equipment="
        + (equipment != null ? equipment.getId() : null)
        + ", description='"
        + description
        + '\''
        + '}';
  }
}
