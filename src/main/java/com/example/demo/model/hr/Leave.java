package com.example.demo.model.hr;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.User;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
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
@Table(name = "\"leave\"")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Leave extends CreatAndUpdateEntity implements Serializable {

  public enum LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
  }

  @Id private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "leave_type_id")
  private LeaveType leaveType;

  private LocalDate startDate;

  private LocalDate endDate;

  @Column(name = "duration_days")
  private BigDecimal durationDays;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private LeaveStatus status;

  @Column(columnDefinition = "TEXT")
  private String reason;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approved_by")
  private User approvedBy;

  private Instant approvedAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Leave leave = (Leave) o;
    return id != null && Objects.equals(id, leave.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
