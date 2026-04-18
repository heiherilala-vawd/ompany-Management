package com.example.demo.model.money;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
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

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;

  @ManyToOne
  @JoinColumn(name = "expense_id")
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
}
