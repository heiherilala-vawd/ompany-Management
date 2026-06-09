package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.Car;
import com.example.demo.client.model.CrupdateCar;
import com.example.demo.client.model.StatutVoiture;
import com.example.demo.client.model.TypeCarburant;
import com.example.demo.endpoint.rest.mapper.EnumMapper;
import com.example.demo.endpoint.rest.mapper.movement.CarMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.CarCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.CarDomainParts;
import com.example.demo.service.movement.CarService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CarController {

  private final CarService carService;
  private final CarMapper carMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/voitures/{equipmentId}/{warehouseId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public Car getCarById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String equipmentId,
      @PathVariable String warehouseId) {
    return carMapper.toRestCar(
        carService
            .findByEquipmentIdAndWarehouseId(equipmentId, warehouseId)
            .orElseThrow(() -> new NotFoundException(
                "Car not found for equipment " + equipmentId + " and warehouse " + warehouseId)));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/voitures")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Car> getCars(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "equipment_id", required = false) String equipmentId,
      @RequestParam(name = "warehouse_id", required = false) String warehouseId,
      @RequestParam(name = "immatriculation", required = false) String immatriculation,
      @RequestParam(name = "type_carburant", required = false) TypeCarburant typeCarburant,
      @RequestParam(name = "marque", required = false) String marque,
      @RequestParam(name = "modele", required = false) String modele,
      @RequestParam(name = "annee", required = false) Integer annee,
      @RequestParam(name = "couleur", required = false) String couleur,
      @RequestParam(name = "kilometrage_min", required = false) Integer kilometrageMin,
      @RequestParam(name = "kilometrage_max", required = false) Integer kilometrageMax,
      @RequestParam(name = "statut", required = false) StatutVoiture statut) {
    CarCriteria criteria = new CarCriteria();
    criteria.setEquipmentId(equipmentId);
    criteria.setWarehouseId(warehouseId);
    criteria.setImmatriculation(immatriculation);
    criteria.setTypeCarburant(
        EnumMapper.mapEnum(typeCarburant, com.example.demo.model.movement.Car.TypeCarburant.class));
    criteria.setMarque(marque);
    criteria.setModele(modele);
    criteria.setAnnee(annee);
    criteria.setCouleur(couleur);
    criteria.setKilometrageMin(kilometrageMin);
    criteria.setKilometrageMax(kilometrageMax);
    criteria.setStatut(
        EnumMapper.mapEnum(statut, com.example.demo.model.movement.Car.StatutVoiture.class));

    return carService.findAll(page, pageSize, criteria).stream()
        .map(carMapper::toRestCar)
        .toList();
  }

  @PutMapping("/users/{userId}/companies/{companyId}/voitures")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Car> crupdateCars(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateCar> toWrite) {
    List<CarDomainParts> partsList = toWrite.stream().map(carMapper::toDomain).toList();
    List<com.example.demo.model.movement.Car> saved = carService.createOrUpdateAll(partsList);
    return saved.stream().map(carMapper::toRestCar).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/voitures/{equipmentId}/{warehouseId}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteCarById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String equipmentId,
      @PathVariable String warehouseId) {
    carService.deleteByEquipmentIdAndWarehouseId(equipmentId, warehouseId);
  }
}
