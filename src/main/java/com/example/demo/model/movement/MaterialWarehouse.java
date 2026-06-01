package com.example.demo.model.movement;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.Job;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "material_warehouse")
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MaterialWarehouse extends CreatAndUpdateEntity implements Serializable {

  @EmbeddedId private MaterialWarehouseId id;

  @NotNull
  @ManyToOne
  @MapsId("materialId")
  @JoinColumn(name = "material_id")
  private Material material;

  @NotNull
  @ManyToOne
  @MapsId("warehouseId")
  @JoinColumn(name = "warehouse_id")
  private Warehouse warehouse;

  @NotNull
  @Min(0)
  private Integer quantity;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  private Job job;

  @Column(name = "min_stock")
  private Integer minStock;

  @Column(name = "max_stock")
  private Integer maxStock;
}
