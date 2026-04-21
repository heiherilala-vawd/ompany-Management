package com.example.demo.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "history")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History implements Serializable {

  @Id private String id;

  @Column(columnDefinition = "TEXT")
  private String previousValue;

  @Column(columnDefinition = "TEXT")
  private String newValue;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "modified_at", nullable = false)
  private Instant modifiedAt;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "entity_type", nullable = false)
  private EntityType entityType;

  @Column(name = "entity_id", nullable = false)
  private String entityId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    History history = (History) o;
    return id != null && Objects.equals(id, history.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public enum EntityType {
    USER,
    COMPANY,
    JOB,
    WAREHOUSE,
    EQUIPMENT,
    MATERIAL,
    INCOMEMONEY,
    EXPENSEMONEY,
    EMPLOYEE_PAYMENT,
    TRAVEL_EXPENSE,
    TRAVELPEOPLE,
    TRAVELMATERIALS,
    TRAVELEQUIPMENT,
    PURCHASE,
    BANK_FEE,
    OTHER_EXPENSE
  }
}
