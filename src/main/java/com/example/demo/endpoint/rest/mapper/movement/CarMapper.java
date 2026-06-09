package com.example.demo.endpoint.rest.mapper.movement;

import static com.example.demo.endpoint.rest.mapper.EnumMapper.mapEnum;

import com.example.demo.client.model.Car;
import com.example.demo.client.model.CrupdateCar;
import com.example.demo.client.model.StatutVoiture;
import com.example.demo.client.model.TypeCarburant;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Job;
import com.example.demo.model.movement.CarId;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.service.movement.CarDomainParts;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CarMapper {

  private final WarehouseMapper warehouseMapper;
  private final EquipmentMapper equipmentMapper;

  public CarDomainParts toDomain(CrupdateCar dto) {
    if (dto == null) return null;

    Warehouse warehouse = Warehouse.builder()
        .id(dto.getWarehouseId())
        .name(dto.getWarehouseName())
        .description(dto.getWarehouseDescription())
        .job(dto.getJobId() != null ? Job.builder().id(dto.getJobId()).build() : null)
        .comment(dto.getComment())
        .build();

    Equipment equipment = Equipment.builder()
        .id(dto.getEquipmentId())
        .name(dto.getEquipmentName())
        .category(dto.getEquipmentCategory())
        .estEnPanne(dto.getEstEnPanne() != null ? dto.getEstEnPanne() : false)
        .purchasePrice(dto.getPurchasePrice())
        .purchaseDate(dto.getPurchaseDate())
        .comment(dto.getComment())
        .build();

    CarId carId = new CarId(dto.getEquipmentId(), dto.getWarehouseId());

    com.example.demo.model.movement.Car car =
        com.example.demo.model.movement.Car.builder()
            .id(carId)
            .immatriculation(dto.getImmatriculation())
            .typeCarburant(mapEnum(dto.getTypeCarburant(), com.example.demo.model.movement.Car.TypeCarburant.class))
            .marque(dto.getMarque())
            .modele(dto.getModele())
            .annee(dto.getAnnee())
            .couleur(dto.getCouleur())
            .kilometrage(dto.getKilometrage())
            .statut(mapEnum(dto.getStatut(), com.example.demo.model.movement.Car.StatutVoiture.class))
            .warehouse(warehouse)
            .equipment(equipment)
            .comment(dto.getComment())
            .build();

    return new CarDomainParts(car, warehouse, equipment);
  }

  public Car toRestCar(com.example.demo.model.movement.Car domainCar) {
    if (domainCar == null) return null;

    Car restCar = new Car();
    restCar.setEquipmentId(domainCar.getId().getEquipmentId());
    restCar.setWarehouseId(domainCar.getId().getWarehouseId());
    restCar.setImmatriculation(domainCar.getImmatriculation());
    restCar.setTypeCarburant(mapEnum(domainCar.getTypeCarburant(), TypeCarburant.class));
    restCar.setMarque(domainCar.getMarque());
    restCar.setModele(domainCar.getModele());
    restCar.setAnnee(domainCar.getAnnee());
    restCar.setCouleur(domainCar.getCouleur());
    restCar.setKilometrage(domainCar.getKilometrage());
    restCar.setStatut(mapEnum(domainCar.getStatut(), StatutVoiture.class));
    restCar.setWarehouse(warehouseMapper.toRestCrupdateWarehouse(domainCar.getWarehouse()));
    restCar.setEquipment(equipmentMapper.toRestCrupdateEquipment(domainCar.getEquipment()));
    RestAuditMapperUtils.mapAuditFields(
        domainCar,
        restCar::setCreatedAt,
        restCar::setUpdatedAt,
        restCar::setComment,
        restCar::setCreatedBy,
        restCar::setUpdatedBy);

    return restCar;
  }

  public List<Car> toRestCars(List<com.example.demo.model.movement.Car> domainCars) {
    return domainCars.stream().map(this::toRestCar).toList();
  }
}
