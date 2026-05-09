package com.example.demo.model.money;

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
@Table(name = "loan_repayment")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoanRepayment extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  private LocalDate paymentDate;

  private Integer amount;

  private Integer principalPortion;

  private Integer interestPortion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loan_id")
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
