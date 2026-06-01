package com.example.demo.model.money;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.TravelPeople;
import com.example.demo.model.movement.Warehouse;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
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
@Table(name = "travel_expense")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TravelExpense extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @OneToOne
  @JoinColumn(name = "expense_id")
  @JsonManagedReference
  @NotNull
  private ExpenseMoney expense;

  @ManyToOne
  @JoinColumn(name = "departure_location")
  @NotNull
  private Warehouse departureLocation;

  @ManyToOne
  @JoinColumn(name = "arrival_location")
  @NotNull
  private Warehouse arrivalLocation;

  @NotNull private Instant departureDate;

  @NotNull private Instant arrivalDate;

  @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<TravelPeople> travelPeople = new ArrayList<>();

  @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<TravelMaterials> travelMaterials = new ArrayList<>();

  @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<TravelEquipment> travelEquipment = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    TravelExpense that = (TravelExpense) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "TravelExpense{"
        + "id='"
        + id
        + '\''
        + ", expense="
        + (expense != null ? expense.getId() : null)
        + ", departureLocation="
        + (departureLocation != null ? departureLocation.getId() : null)
        + ", arrivalLocation="
        + (arrivalLocation != null ? arrivalLocation.getId() : null)
        + ", departureDate="
        + departureDate
        + ", arrivalDate="
        + arrivalDate
        + ", travelPeople="
        + (travelPeople != null ? travelPeople.size() + " items" : null)
        + ", travelMaterials="
        + (travelMaterials != null ? travelMaterials.size() + " items" : null)
        + ", travelEquipment="
        + (travelEquipment != null ? travelEquipment.size() + " items" : null)
        + '}';
  }
}
