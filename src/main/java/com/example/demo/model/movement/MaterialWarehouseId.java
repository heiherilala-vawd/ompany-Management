package com.example.demo.model.movement;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialWarehouseId implements Serializable {

  @Column(name = "material_id")
  private String materialId;

  @Column(name = "warehouse_id")
  private String warehouseId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MaterialWarehouseId that)) return false;
    return Objects.equals(materialId, that.materialId)
        && Objects.equals(warehouseId, that.warehouseId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(materialId, warehouseId);
  }
}
