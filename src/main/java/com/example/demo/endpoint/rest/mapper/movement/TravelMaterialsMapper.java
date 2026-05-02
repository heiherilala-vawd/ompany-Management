package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateTravelMaterials;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.endpoint.rest.mapper.money.TravelExpenseMapper;
import com.example.demo.service.money.TravelExpenseService;
import com.example.demo.service.movement.MaterialService;
import com.example.demo.service.movement.WarehouseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TravelMaterialsMapper {

  private final TravelExpenseService travelExpenseService;
  private final MaterialService materialService;
  private final WarehouseService warehouseService;
  private final TravelExpenseMapper travelExpenseMapper;
  private final MaterialMapper materialMapper;
  private final WarehouseMapper warehouseMapper;

  public com.example.demo.model.movement.TravelMaterials toDomain(
      TravelMaterials restTravelMaterials) {
    if (restTravelMaterials == null) return null;

    return com.example.demo.model.movement.TravelMaterials.builder()
        .id(restTravelMaterials.getId())
        .travel(
            restTravelMaterials.getTravel() != null
                    && restTravelMaterials.getTravel().getId() != null
                ? travelExpenseService
                    .findById(restTravelMaterials.getTravel().getId())
                    .orElse(null)
                : null)
        .material(
            restTravelMaterials.getMaterial() != null
                ? materialService.findById(restTravelMaterials.getMaterial().getId()).orElse(null)
                : null)
        .quantity(restTravelMaterials.getQuantity())
        .quantityReceived(restTravelMaterials.getQuantityReceived())
        .comment(restTravelMaterials.getComment())
        .arrivalLocation(
            restTravelMaterials.getArrivalLocation() != null
                ? warehouseService
                    .findById(restTravelMaterials.getArrivalLocation().getId())
                    .orElse(null)
                : null)
        .arrivalDate(restTravelMaterials.getArrivalDate())
        .build();
  }

  public com.example.demo.model.movement.TravelMaterials toDomain(
      CrupdateTravelMaterials restTravelMaterials) {
    if (restTravelMaterials == null) return null;

    return com.example.demo.model.movement.TravelMaterials.builder()
        .id(restTravelMaterials.getId())
        .travel(
            restTravelMaterials.getTravelId() != null
                ? travelExpenseService.findById(restTravelMaterials.getTravelId()).orElse(null)
                : null)
        .material(
            restTravelMaterials.getMaterial() != null
                ? materialService.findById(restTravelMaterials.getMaterial()).orElse(null)
                : null)
        .quantity(restTravelMaterials.getQuantity())
        .quantityReceived(restTravelMaterials.getQuantityReceived())
        .comment(restTravelMaterials.getComment())
        .arrivalLocation(
            restTravelMaterials.getArrivalLocation() != null
                ? warehouseService.findById(restTravelMaterials.getArrivalLocation()).orElse(null)
                : null)
        .arrivalDate(restTravelMaterials.getArrivalDate())
        .build();
  }

  public TravelMaterials toRestTravelMaterials(
      com.example.demo.model.movement.TravelMaterials domainTravelMaterials) {
    if (domainTravelMaterials == null) return null;

    TravelMaterials restTravelMaterials = new TravelMaterials();
    restTravelMaterials.setId(domainTravelMaterials.getId());
    restTravelMaterials.setTravel(
        travelExpenseMapper.toRestCrupdateTravelExpense(domainTravelMaterials.getTravel()));
    restTravelMaterials.setMaterial(
        materialMapper.toRestCrupdateMaterial(domainTravelMaterials.getMaterial()));
    restTravelMaterials.setQuantity(domainTravelMaterials.getQuantity());
    restTravelMaterials.setQuantityReceived(domainTravelMaterials.getQuantityReceived());
    restTravelMaterials.setArrivalDate(domainTravelMaterials.getArrivalDate());
    restTravelMaterials.setArrivalLocation(
        warehouseMapper.toRestCrupdateWarehouse(domainTravelMaterials.getArrivalLocation()));
    RestAuditMapperUtils.mapAuditFields(
        domainTravelMaterials,
        restTravelMaterials::setCreatedAt,
        restTravelMaterials::setUpdatedAt,
        restTravelMaterials::setComment,
        restTravelMaterials::setCreatedBy,
        restTravelMaterials::setUpdatedBy);

    return restTravelMaterials;
  }

  public List<TravelMaterials> toRestTravelMaterialsList(
      List<com.example.demo.model.movement.TravelMaterials> domainTravelMaterials) {
    return domainTravelMaterials.stream().map(this::toRestTravelMaterials).toList();
  }
}
