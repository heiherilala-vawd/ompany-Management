package com.example.demo.model.movement;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.example.demo.model.Job;
import com.example.demo.model.User;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "warehouse")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse implements Serializable {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;

  private String name;

  private String description;

  @ManyToOne
  @JoinColumn(name = "job_id")
  private Job job;

  @CreationTimestamp private Instant createdAt;

  @UpdateTimestamp private Instant updatedAt;

  @ManyToOne
  @JoinColumn(name = "created_by")
  private User createdBy;

  @ManyToOne
  @JoinColumn(name = "updated_by")
  private User updatedBy;

  private String comment;

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
