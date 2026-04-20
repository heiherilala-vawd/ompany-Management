package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateTravelPeople;
import com.example.demo.client.model.TravelPeople;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
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
        .comment(restTravelPeople.getComment())
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
        .comment(restTravelPeople.getComment())
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
    RestAuditMapperUtils.mapAuditFields(
        domainTravelPeople,
        restTravelPeople::setCreatedAt,
        restTravelPeople::setUpdatedAt,
        restTravelPeople::setComment,
        restTravelPeople::setCreatedBy,
        restTravelPeople::setUpdatedBy);

    return restTravelPeople;
  }

  public List<TravelPeople> toRestTravelPeopleList(
      List<com.example.demo.model.movement.TravelPeople> domainTravelPeople) {
    return domainTravelPeople.stream().map(this::toRestTravelPeople).toList();
  }
}
