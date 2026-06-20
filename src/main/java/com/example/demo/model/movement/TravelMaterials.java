package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.money.TravelExpense;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.List;
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

  @Id private String id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "travel_id")
  @JsonBackReference
  private TravelExpense travel;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "material")
  private Material material;

  @NotNull
  @Min(0)
  private Integer quantity;

  private Integer quantityReceived;

  private Integer quantityLost;

  @ManyToOne
  @JoinColumn(name = "container_id")
  @JsonBackReference
  private TravelContainer container;

  @Transient
  public int getQuantityInTransit() {
    int received = quantityReceived != null ? quantityReceived : 0;
    int lost = quantityLost != null ? quantityLost : 0;
    return quantity - received - lost;
  }

  @Transient
  public boolean isInTransit() {
    return getQuantityInTransit() > 0;
  }

  @OneToMany(mappedBy = "travelMaterials", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  @ToString.Exclude
  private List<TravelMaterialsArrivalLog> arrivalLogs;

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
