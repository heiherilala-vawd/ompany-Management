package com.example.demo.model.money;

import com.example.demo.model.Job;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

  @NotBlank
  @Size(max = 255)
  private String lender;

  @NotNull
  @Min(0)
  private Integer interestRate;

  @NotNull private LocalDate startDate;

  @NotNull private LocalDate dueDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  @NotNull
  private Job job;

  @OneToMany(mappedBy = "loan")
  private List<LoanRepayment> repayments;

  @Enumerated(EnumType.STRING)
  @NotNull
  private LoanStatus status;

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
