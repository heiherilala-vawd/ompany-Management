package com.example.demo.model.task;

import com.example.demo.model.Company;
import com.example.demo.model.CreatAndUpdateEntity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

@Entity
@Table(name = "task")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Task extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "due_date")
  private LocalDate dueDate;

  private String priority;

  private String frequency;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Task task = (Task) o;
    return id != null && Objects.equals(id, task.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
