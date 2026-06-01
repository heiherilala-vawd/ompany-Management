package com.example.demo.model.money;

import com.example.demo.model.CreatAndUpdateEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
@Table(name = "loan_repayment")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoanRepayment extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @NotNull private LocalDate paymentDate;

  @NotNull @PositiveOrZero private BigDecimal amount;

  @NotNull @PositiveOrZero private BigDecimal principalPortion;

  @NotNull @PositiveOrZero private BigDecimal interestPortion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loan_id")
  @NotNull
  private Loan loan;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    LoanRepayment that = (LoanRepayment) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
