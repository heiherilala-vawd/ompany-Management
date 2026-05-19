package com.example.demo.model.hr;

import com.example.demo.model.Company;
import com.example.demo.model.CreatAndUpdateEntity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

@Entity
@Table(name = "leave_type")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveType extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  private Boolean paid;

  private Boolean deductFromBalance;

  @Column(length = 7)
  private String color;

  private Integer daysPerYear;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    LeaveType leaveType = (LeaveType) o;
    return id != null && Objects.equals(id, leaveType.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
