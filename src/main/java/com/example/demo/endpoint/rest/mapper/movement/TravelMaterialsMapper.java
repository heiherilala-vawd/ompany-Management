package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateTravelMaterials;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.service.money.TravelExpenseService;
import com.example.demo.service.movement.MaterialService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TravelMaterialsMapper {

  private final TravelExpenseService travelExpenseService;
  private final MaterialService materialService;

  public com.example.demo.model.movement.TravelMaterials toDomain(
      TravelMaterials restTravelMaterials) {
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
        .build();
  }

  public TravelMaterials toRestTravelMaterials(
      com.example.demo.model.movement.TravelMaterials domainTravelMaterials) {
    if (domainTravelMaterials == null) return null;

    TravelMaterials restTravelMaterials = new TravelMaterials();
    restTravelMaterials.setId(domainTravelMaterials.getId());
    restTravelMaterials.setTravelId(
        domainTravelMaterials.getTravel() != null
            ? domainTravelMaterials.getTravel().getId()
            : null);
    restTravelMaterials.setMaterial(
        domainTravelMaterials.getMaterial() != null
            ? domainTravelMaterials.getMaterial().getId()
            : null);
    restTravelMaterials.setQuantity(domainTravelMaterials.getQuantity());
    restTravelMaterials.setQuantityReceived(domainTravelMaterials.getQuantityReceived());
    restTravelMaterials.setCreatedAt(domainTravelMaterials.getCreatedAt());
    restTravelMaterials.setUpdatedAt(domainTravelMaterials.getUpdatedAt());
    restTravelMaterials.setComment(domainTravelMaterials.getComment());

    if (domainTravelMaterials.getCreatedBy() != null) {
      restTravelMaterials.setCreatedBy(domainTravelMaterials.getCreatedBy().getId());
    }
    if (domainTravelMaterials.getUpdatedBy() != null) {
      restTravelMaterials.setUpdatedBy(domainTravelMaterials.getUpdatedBy().getId());
    }

    return restTravelMaterials;
  }

  public List<TravelMaterials> toRestTravelMaterialsList(
      List<com.example.demo.model.movement.TravelMaterials> domainTravelMaterials) {
    return domainTravelMaterials.stream().map(this::toRestTravelMaterials).toList();
  }
}
