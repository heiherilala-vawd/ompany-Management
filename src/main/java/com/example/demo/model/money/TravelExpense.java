package com.example.demo.model.money;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.TravelPeople;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Table(name = "travel_expense")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TravelExpense implements Serializable {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;

  @OneToOne
  @JoinColumn(name = "expense_id")
  private ExpenseMoney expense;

  private String departureLocation;

  private String arrivalLocation;

  private Instant departureDate;

  private Instant arrivalDate;

  @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
  @ToString.Exclude
  private List<TravelPeople> travelPeople = new ArrayList<>();

  @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
  @ToString.Exclude
  private List<TravelMaterials> travelMaterials = new ArrayList<>();

  @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
  @ToString.Exclude
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
}
