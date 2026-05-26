package com.example.demo.model.money;

import com.example.demo.model.Company;
import com.example.demo.model.CreatAndUpdateEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "supplier")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Supplier extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  private String name;

  private String siret;

  @Column(columnDefinition = "TEXT")
  private String address;

  private String email;

  private String phone;

  @Column(name = "contact_name")
  private String contactName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  @JsonProperty("company_id")
  public String getCompanyId() {
    return company != null ? company.getId() : null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Supplier supplier = (Supplier) o;
    return id != null && Objects.equals(id, supplier.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
