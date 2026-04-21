package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.Job;
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
@Table(name = "warehouse")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  private String name;

  private String description;

  @ManyToOne
  @JoinColumn(name = "job_id")
  private Job job;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Warehouse warehouse = (Warehouse) o;
    return id != null && Objects.equals(id, warehouse.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
