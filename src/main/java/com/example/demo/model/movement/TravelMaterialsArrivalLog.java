package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "travel_materials_arrival_log")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TravelMaterialsArrivalLog extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "travel_materials_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonBackReference
  private TravelMaterials travelMaterials;

  @NotNull
  @Min(0)
  private Integer quantityReceived;

  @NotNull
  @Min(0)
  private Integer quantityLost;

  @NotNull private Instant arrivalDate;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "arrival_location")
  private Warehouse arrivalLocation;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    TravelMaterialsArrivalLog that = (TravelMaterialsArrivalLog) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
