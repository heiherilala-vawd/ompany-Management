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
@Table(name = "equipment")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Equipment extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  private String name;

  private String description;

  @ManyToOne
  @JoinColumn(name = "warehouse_id")
  private Warehouse warehouse;

  private Integer floorNumber;

  private Integer storageNumber;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Equipment equipment = (Equipment) o;
    return id != null && Objects.equals(id, equipment.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
