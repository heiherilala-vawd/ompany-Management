package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.money.TravelExpense;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
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
@Table(name = "equipment_incident")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentIncident extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "incident_type")
  private IncidentType incidentType;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "equipment_id")
  private Equipment equipment;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private com.example.demo.model.User user;

  @ManyToOne
  @JoinColumn(name = "travel_id")
  private TravelExpense travel;

  private String location;

  private String comment;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    EquipmentIncident that = (EquipmentIncident) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
