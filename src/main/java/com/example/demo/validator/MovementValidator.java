package com.example.demo.validator;

import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.TravelPeople;
import com.example.demo.model.movement.Warehouse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MovementValidator {

  public void validateMaterial(Material material) {
    if (material == null) {
      throw new BadRequestException("Material cannot be null");
    }
    if (material.getName() == null || material.getName().isBlank()) {
      throw new BadRequestException("Material name is mandatory");
    }
    if (material.getUnit() == null) {
      throw new BadRequestException("Material unit is mandatory");
    }
  }

  public void validateMaterials(List<Material> materials) {
    if (materials == null || materials.isEmpty()) {
      throw new BadRequestException("Material list cannot be null or empty");
    }
    materials.forEach(this::validateMaterial);
  }

  public void validateEquipment(Equipment equipment) {
    if (equipment == null) {
      throw new BadRequestException("Equipment cannot be null");
    }
    if (equipment.getName() == null || equipment.getName().isBlank()) {
      throw new BadRequestException("Equipment name is mandatory");
    }
    if (equipment.getWarehouse() == null || equipment.getWarehouse().getId() == null) {
      throw new BadRequestException("Equipment must be associated with a warehouse");
    }
  }

  public void validateEquipments(List<Equipment> equipments) {
    if (equipments == null || equipments.isEmpty()) {
      throw new BadRequestException("Equipment list cannot be null or empty");
    }
    equipments.forEach(this::validateEquipment);
  }

  public void validateWarehouse(Warehouse warehouse) {
    if (warehouse == null) {
      throw new BadRequestException("Warehouse cannot be null");
    }
    if (warehouse.getName() == null || warehouse.getName().isBlank()) {
      throw new BadRequestException("Warehouse name is mandatory");
    }
  }

  public void validateWarehouses(List<Warehouse> warehouses) {
    if (warehouses == null || warehouses.isEmpty()) {
      throw new BadRequestException("Warehouse list cannot be null or empty");
    }
    warehouses.forEach(this::validateWarehouse);
  }

  public void validateTravelEquipment(TravelEquipment travelEquipment) {
    if (travelEquipment == null) {
      throw new BadRequestException("Travel equipment cannot be null");
    }
    if (travelEquipment.getTravel() == null || travelEquipment.getTravel().getId() == null) {
      throw new BadRequestException("Travel equipment must be linked to a travel expense");
    }
    if (travelEquipment.getEquipment() == null || travelEquipment.getEquipment().getId() == null) {
      throw new BadRequestException("Equipment is mandatory for travel equipment");
    }
    if (travelEquipment.getQuantity() == null || travelEquipment.getQuantity() <= 0) {
      throw new BadRequestException("Quantity must be positive");
    }
    if (travelEquipment.getStatus() == null) {
      throw new BadRequestException("Transport status is mandatory");
    }
  }

  public void validateTravelEquipments(List<TravelEquipment> travelEquipments) {
    if (travelEquipments == null || travelEquipments.isEmpty()) {
      throw new BadRequestException("Travel equipment list cannot be null or empty");
    }
    travelEquipments.forEach(this::validateTravelEquipment);
  }

  public void validateTravelMaterials(TravelMaterials travelMaterials) {
    if (travelMaterials == null) {
      throw new BadRequestException("Travel materials cannot be null");
    }
    if (travelMaterials.getTravel() == null || travelMaterials.getTravel().getId() == null) {
      throw new BadRequestException("Travel materials must be linked to a travel expense");
    }
    if (travelMaterials.getMaterial() == null || travelMaterials.getMaterial().getId() == null) {
      throw new BadRequestException("Material is mandatory for travel materials");
    }
    if (travelMaterials.getQuantity() == null || travelMaterials.getQuantity() <= 0) {
      throw new BadRequestException("Quantity must be positive");
    }
  }

  public void validateTravelMaterialsList(List<TravelMaterials> travelMaterialsList) {
    if (travelMaterialsList == null || travelMaterialsList.isEmpty()) {
      throw new BadRequestException("Travel materials list cannot be null or empty");
    }
    travelMaterialsList.forEach(this::validateTravelMaterials);
  }

  public void validateTravelPeople(TravelPeople travelPeople) {
    if (travelPeople == null) {
      throw new BadRequestException("Travel people cannot be null");
    }
    if (travelPeople.getTravel() == null || travelPeople.getTravel().getId() == null) {
      throw new BadRequestException("Travel people must be linked to a travel expense");
    }
    if (travelPeople.getUser() == null || travelPeople.getUser().getId() == null) {
      throw new BadRequestException("User is mandatory for travel people");
    }
  }

  public void validateTravelPeoples(List<TravelPeople> travelPeoples) {
    if (travelPeoples == null || travelPeoples.isEmpty()) {
      throw new BadRequestException("Travel people list cannot be null or empty");
    }
    travelPeoples.forEach(this::validateTravelPeople);
  }

  public void validateMaterialWarehouse(MaterialWarehouse materialWarehouse) {
    if (materialWarehouse == null) {
      throw new BadRequestException("Material warehouse cannot be null");
    }
    if (materialWarehouse.getMaterial() == null
        || materialWarehouse.getMaterial().getId() == null) {
      throw new BadRequestException("Material is mandatory for material warehouse");
    }
    if (materialWarehouse.getWarehouse() == null
        || materialWarehouse.getWarehouse().getId() == null) {
      throw new BadRequestException("Warehouse is mandatory for material warehouse");
    }
    if (materialWarehouse.getQuantity() == null || materialWarehouse.getQuantity() < 0) {
      throw new BadRequestException("Quantity must be non-negative");
    }
  }
}
