package com.example.demo.model.task;

import com.example.demo.model.Company;
import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.movement.MaintenanceSchedule;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
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
@Table(name = "task")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Task extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @NotBlank
  @Size(max = 255)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @NotNull
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private TaskPriority priority;

  @NotNull private Boolean completed;

  @Column(name = "completed_at")
  private Instant completedAt;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  @Column(name = "feature_name")
  private String featureName;

  @Column(name = "feature_id")
  private String featureId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "maintenance_schedule_id")
  private MaintenanceSchedule maintenanceSchedule;

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
