package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Equipment extends CreatAndUpdateEntity implements Serializable {

  @Id private String id;

  @NotBlank
  @Size(max = 255)
  private String name;

  private String description;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "warehouse_id")
  private Warehouse warehouse;

  private Integer floorNumber;

  private Integer storageNumber;

  @NotNull
  @Column(name = "est_en_panne")
  @Builder.Default
  private Boolean estEnPanne = false;

  @NotNull
  @PositiveOrZero
  @Column(name = "purchase_price")
  private BigDecimal purchasePrice;

  @Column(name = "purchase_date")
  private LocalDate purchaseDate;

  @NotNull
  @Column(name = "is_damaged")
  @Builder.Default
  private Boolean isDamaged = false;

  @NotNull
  @Column(name = "is_lost")
  @Builder.Default
  private Boolean isLost = false;

  @NotNull
  @Column(name = "is_leased")
  @Builder.Default
  private Boolean isLeased = false;

  @Column(name = "lease_end_date")
  private LocalDate leaseEndDate;

  @Column(name = "document_url")
  private String documentUrl;

  private String category;

  @OneToMany(mappedBy = "equipment")
  @ToString.Exclude
  @Builder.Default
  private List<Maintenance> maintenances = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Equipment equipment = (Equipment) o;
    return id != null && Objects.equals(id, equipment.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
