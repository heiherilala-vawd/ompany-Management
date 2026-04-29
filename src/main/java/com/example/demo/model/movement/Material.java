package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
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
@Table(name = "material")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Material extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  private String name;

  private String description;

  @Enumerated(EnumType.STRING)
  @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
  private Unit unit;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Material material = (Material) o;
    return id != null && Objects.equals(id, material.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public enum Unit {
    SAC,
    L,
    KG,
    M2,
    M3,
    KIT,
    POT,
    PNL,
    FEU,
    BAR,
    T,
    M,
    FFT,
    U
  }
}
