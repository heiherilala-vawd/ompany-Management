package com.example.demo.model.money;

import static jakarta.persistence.GenerationType.IDENTITY;

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
@Table(name = "income_money")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class IncomeMoney extends MonetaryMovement implements Serializable {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;

  private String sourceOrganization;

  private String invoiceReference;

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
