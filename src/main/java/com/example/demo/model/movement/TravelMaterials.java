package com.example.demo.model.movement;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.money.TravelExpense;
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
@Table(name = "travel_materials")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TravelMaterials extends CreatAndUpdateEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;

  @ManyToOne
  @JoinColumn(name = "travel_id")
  @JsonBackReference
  private TravelExpense travel;

  @ManyToOne
  @JoinColumn(name = "material")
  private Material material;

  private Integer quantity;

  private Integer quantityReceived;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    TravelMaterials that = (TravelMaterials) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
