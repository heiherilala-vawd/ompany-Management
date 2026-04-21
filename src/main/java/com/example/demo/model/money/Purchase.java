package com.example.demo.model.money;

import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
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
@Table(name = "purchase")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Purchase implements Serializable {

  @Id private String id;

  @OneToOne
  @JoinColumn(name = "expense_id")
  @JsonManagedReference
  private ExpenseMoney expense;

  private String supplier;

  @ManyToOne
  @JoinColumn(name = "equipment")
  private Equipment equipment;

  @ManyToOne
  @JoinColumn(name = "material")
  private Material material;

  private Integer quantity;

  private Boolean isEquipment;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Purchase purchase = (Purchase) o;
    return id != null && Objects.equals(id, purchase.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Purchase{"
        + "id='"
        + id
        + '\''
        + ", expense="
        + (expense != null ? expense.getId() : null)
        + ", supplier='"
        + supplier
        + '\''
        + ", equipment="
        + (equipment != null ? equipment.getId() + ":" + equipment.getName() : null)
        + ", material="
        + (material != null ? material.getId() + ":" + material.getName() : null)
        + ", quantity="
        + quantity
        + ", isEquipment="
        + isEquipment
        + '}';
  }
}
