package com.example.demo.model.hr;

import com.example.demo.model.Company;
import com.example.demo.model.CreatAndUpdateEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "employee_leave_config")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLeaveConfig extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  @NotBlank
  @Size(max = 255)
  private String contractType;

  @NotNull
  @PositiveOrZero
  @Column(name = "vacation_days_per_month")
  private BigDecimal vacationDaysPerMonth;

  @Transient private LocalDate hireDate;

  @Transient private LocalDate endDate;

  @NotNull
  @Min(0)
  @Column(name = "weekly_hours")
  private Integer weeklyHours;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    EmployeeLeaveConfig that = (EmployeeLeaveConfig) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
