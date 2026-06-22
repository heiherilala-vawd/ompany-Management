package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.money.TravelExpense;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
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
@Table(name = "travel_container")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TravelContainer extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "travel_id")
  @JsonBackReference
  private TravelExpense travel;

  @NotBlank private String name;

  private String description;

  @OneToMany(mappedBy = "container", cascade = CascadeType.ALL)
  @JsonManagedReference
  @ToString.Exclude
  private List<TravelEquipment> travelEquipment = new ArrayList<>();

  @OneToMany(mappedBy = "container", cascade = CascadeType.ALL)
  @JsonManagedReference
  @ToString.Exclude
  private List<TravelMaterials> travelMaterials = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    TravelContainer that = (TravelContainer) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
