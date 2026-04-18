package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateTravelPeople;
import com.example.demo.client.model.TravelPeople;
import com.example.demo.service.money.TravelExpenseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TravelPeopleMapper {

  private final TravelExpenseService travelExpenseService;

  public com.example.demo.model.movement.TravelPeople toDomain(TravelPeople restTravelPeople) {
    if (restTravelPeople == null) return null;

    return com.example.demo.model.movement.TravelPeople.builder()
        .id(restTravelPeople.getId())
        .travel(
            restTravelPeople.getTravelId() != null
                ? travelExpenseService.findById(restTravelPeople.getTravelId()).orElse(null)
                : null)
        .personName(restTravelPeople.getPersonName())
        .build();
  }

  public com.example.demo.model.movement.TravelPeople toDomain(
      CrupdateTravelPeople restTravelPeople) {
    if (restTravelPeople == null) return null;

    return com.example.demo.model.movement.TravelPeople.builder()
        .id(restTravelPeople.getId())
        .travel(
            restTravelPeople.getTravelId() != null
                ? travelExpenseService.findById(restTravelPeople.getTravelId()).orElse(null)
                : null)
        .personName(restTravelPeople.getPersonName())
        .build();
  }

  public TravelPeople toRestTravelPeople(
      com.example.demo.model.movement.TravelPeople domainTravelPeople) {
    if (domainTravelPeople == null) return null;

    TravelPeople restTravelPeople = new TravelPeople();
    restTravelPeople.setId(domainTravelPeople.getId());
    restTravelPeople.setTravelId(
        domainTravelPeople.getTravel() != null ? domainTravelPeople.getTravel().getId() : null);
    restTravelPeople.setPersonName(domainTravelPeople.getPersonName());
    restTravelPeople.setCreatedAt(domainTravelPeople.getCreatedAt());
    restTravelPeople.setUpdatedAt(domainTravelPeople.getUpdatedAt());
    restTravelPeople.setComment(domainTravelPeople.getComment());

    if (domainTravelPeople.getCreatedBy() != null) {
      restTravelPeople.setCreatedBy(domainTravelPeople.getCreatedBy().getId());
    }
    if (domainTravelPeople.getUpdatedBy() != null) {
      restTravelPeople.setUpdatedBy(domainTravelPeople.getUpdatedBy().getId());
    }

    return restTravelPeople;
  }

  public List<TravelPeople> toRestTravelPeopleList(
      List<com.example.demo.model.movement.TravelPeople> domainTravelPeople) {
    return domainTravelPeople.stream().map(this::toRestTravelPeople).toList();
  }
}
