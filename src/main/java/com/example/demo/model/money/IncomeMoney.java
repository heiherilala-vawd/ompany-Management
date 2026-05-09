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

@Entity
@Table(name = "income_money")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class IncomeMoney extends MonetaryMovement implements Serializable {

  @Id private String id;

  private String sourceOrganization;

  private String invoiceReference;

  private LocalDate billingStartDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  private Job job;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "income_type_id")
  private IncomeType incomeType;

  @OneToMany(mappedBy = "income")
  @ToString.Exclude
  private List<IncomeReceipt> receipts;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    IncomeMoney that = (IncomeMoney) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
