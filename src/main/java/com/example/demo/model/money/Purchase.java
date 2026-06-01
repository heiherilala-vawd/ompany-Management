package com.example.demo.model.money;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.Warehouse;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

@Entity
@Table(name = "purchase")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Purchase extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @OneToOne
  @JoinColumn(name = "expense_id")
  @JsonManagedReference
  @NotNull
  private ExpenseMoney expense;

  @ManyToOne
  @JoinColumn(name = "source_warehouse_id")
  @NotNull
  private Warehouse sourceWarehouse;

  @ManyToOne
  @JoinColumn(name = "supplier_id")
  private Supplier supplier;

  @ManyToOne
  @JoinColumn(name = "equipment")
  private Equipment equipment;

  @ManyToOne
  @JoinColumn(name = "material")
  private Material material;

  @NotNull
  @Min(0)
  private Integer quantity;

  @NotNull private Boolean isEquipment;

  @NotNull private LocalDate invoiceDate;

  @NotNull private LocalDate dueDate;

  private LocalDate paidAt;

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
        + ", sourceWarehouse="
        + (sourceWarehouse != null ? sourceWarehouse.getId() : null)
        + ", supplier="
        + (supplier != null ? supplier.getId() : null)
        + ", equipment="
        + (equipment != null ? equipment.getId() + ":" + equipment.getName() : null)
        + ", material="
        + (material != null ? material.getId() + ":" + material.getName() : null)
        + ", quantity="
        + quantity
        + ", isEquipment="
        + isEquipment
        + ", invoiceDate="
        + invoiceDate
        + ", dueDate="
        + dueDate
        + ", paidAt="
        + paidAt
        + '}';
  }
}
