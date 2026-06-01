package com.example.demo.model.money;

import com.example.demo.model.Company;
import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.Job;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
@Table(name = "purchase_order")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrder extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supplier_id")
  @NotNull
  private Supplier supplier;

  @Column(name = "order_date")
  @NotNull
  private LocalDate orderDate;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @NotNull
  private PurchaseOrderStatus status;

  @Column(name = "total_amount")
  @NotNull
  @PositiveOrZero
  private BigDecimal totalAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  @NotNull
  private Company company;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  @NotNull
  private Job job;

  @JsonProperty("supplier_id")
  public String getSupplierId() {
    return supplier != null ? supplier.getId() : null;
  }

  @JsonProperty("company_id")
  public String getCompanyId() {
    return company != null ? company.getId() : null;
  }

  @JsonProperty("job_id")
  public String getJobId() {
    return job != null ? job.getId() : null;
  }

  @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private List<PurchaseOrderLine> lines = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    PurchaseOrder that = (PurchaseOrder) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public enum PurchaseOrderStatus {
    PENDING,
    VALIDATED,
    DELIVERED,
    INVOICED
  }
}
