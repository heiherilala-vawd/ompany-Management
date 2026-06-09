package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
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
@Table(name = "voiture")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Car extends CreatAndUpdateEntity implements Serializable {

  @EmbeddedId
  private CarId id;

  @ManyToOne
  @MapsId("equipmentId")
  @JoinColumn(name = "equipment_id")
  private Equipment equipment;

  @ManyToOne
  @MapsId("warehouseId")
  @JoinColumn(name = "warehouse_id")
  private Warehouse warehouse;

  private String immatriculation;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private TypeCarburant typeCarburant;

  private String marque;

  private String modele;

  private Integer annee;

  private String couleur;

  private Integer kilometrage;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private StatutVoiture statut;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Car car = (Car) o;
    return id != null && Objects.equals(id, car.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public enum TypeCarburant {
    DIESEL, ESSENCE, ELECTRIQUE, HYBRIDE, GPL
  }

  public enum StatutVoiture {
    EN_MISSION, DISPONIBLE, EN_MAINTENANCE, HORS_SERVICE
  }
}
