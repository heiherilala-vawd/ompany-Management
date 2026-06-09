package com.example.demo.model.hr;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

@Entity
@Table(name = "leave_accrued_by_month")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveAccruedByMonth extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @NotNull
  @Min(2000)
  @Max(2099)
  private Integer year;

  @NotNull
  @Min(1)
  @Max(12)
  private Integer month;

  @NotNull
  @PositiveOrZero
  @Column(name = "accrued_days")
  private BigDecimal accruedDays;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    LeaveAccruedByMonth that = (LeaveAccruedByMonth) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
