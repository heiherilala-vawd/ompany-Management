package com.example.demo.model.movement;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "material_warehouse")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MaterialWarehouse implements Serializable {

  @EmbeddedId private MaterialWarehouseId id;

  @ManyToOne
  @MapsId("materialId")
  @JoinColumn(name = "material_id")
  private Material material;

  @ManyToOne
  @MapsId("warehouseId")
  @JoinColumn(name = "warehouse_id")
  private Warehouse warehouse;

  private Integer quantity;
}
