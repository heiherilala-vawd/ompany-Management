package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.money.TravelExpense;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "travel_equipment")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TravelEquipment extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "travel_id")
  @JsonBackReference
  private TravelExpense travel;

  @ManyToOne
  @JoinColumn(name = "equipment")
  private Equipment equipment;

  private Integer quantity;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private TransportStatus status;

  @ManyToOne
  @JoinColumn(name = "arrival_location")
  private Warehouse arrivalLocation;

  private Instant arrivalDate;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    TravelEquipment that = (TravelEquipment) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public enum TransportStatus {
    IN_PROGRESS,
    LOST,
    ARRIVED
  }
}
