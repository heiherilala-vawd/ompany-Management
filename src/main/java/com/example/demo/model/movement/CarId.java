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
public class CarId implements Serializable {

  @Column(name = "equipment_id")
  private String equipmentId;

  @Column(name = "warehouse_id")
  private String warehouseId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CarId that)) return false;
    return Objects.equals(equipmentId, that.equipmentId)
        && Objects.equals(warehouseId, that.warehouseId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(equipmentId, warehouseId);
  }
}
