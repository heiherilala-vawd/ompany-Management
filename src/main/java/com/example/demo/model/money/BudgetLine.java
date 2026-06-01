package com.example.demo.model.money;

import com.example.demo.model.Company;
import com.example.demo.model.CreatAndUpdateEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
@Table(name = "budget_line")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetLine extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  @NotNull
  private Company company;

  @NotBlank
  @Size(max = 255)
  private String category;

  @Column(name = "planned_amount")
  @NotNull
  @PositiveOrZero
  private BigDecimal plannedAmount;

  @Column(name = "actual_amount")
  @NotNull
  @PositiveOrZero
  private BigDecimal actualAmount;

  @Column(name = "period_start")
  @NotNull
  private LocalDate periodStart;

  @Column(name = "period_end")
  @NotNull
  private LocalDate periodEnd;

  private String description;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    BudgetLine that = (BudgetLine) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
