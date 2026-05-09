package com.example.demo.model.money;

import com.example.demo.model.Job;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
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
@Table(name = "loan")
@Getter
@Setter
@ToString(exclude = "repayments")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Loan extends MonetaryMovement implements Serializable {

  @Id private String id;

  private String lender;

  private Integer interestRate;

  private LocalDate startDate;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private LoanStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  private Job job;

  @OneToMany(mappedBy = "loan")
  private List<LoanRepayment> repayments;

  public enum LoanStatus {
    ACTIVE,
    PAID,
    DEFAULTED
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Loan loan = (Loan) o;
    return id != null && Objects.equals(id, loan.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
