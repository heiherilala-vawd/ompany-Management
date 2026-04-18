package com.example.demo.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
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
@Table(name = "company")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Company extends CreatAndUpdateEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;

  @NotBlank(message = "Name is mandatory")
  private String name;

  private String rib;

  private String description;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private CompanyType companyType;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Company company = (Company) o;
    return id != null && Objects.equals(id, company.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public enum CompanyType {
    BTP,
    HOTEL
  }
}
