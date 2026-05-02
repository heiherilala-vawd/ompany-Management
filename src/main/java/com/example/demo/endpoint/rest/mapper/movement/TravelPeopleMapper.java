package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateTravelPeople;
import com.example.demo.client.model.TravelPeople;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.endpoint.rest.mapper.money.TravelExpenseMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.money.TravelExpenseService;
import com.example.demo.service.movement.WarehouseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TravelPeopleMapper {

  private final TravelExpenseService travelExpenseService;
  private final TravelExpenseMapper travelExpenseMapper;
  private final UserRepository userRepository;
  private final WarehouseService warehouseService;
  private final WarehouseMapper warehouseMapper;

  public com.example.demo.model.movement.TravelPeople toDomain(TravelPeople restTravelPeople) {
    if (restTravelPeople == null) return null;

    return com.example.demo.model.movement.TravelPeople.builder()
        .id(restTravelPeople.getId())
        .travel(
            restTravelPeople.getTravel() != null && restTravelPeople.getTravel().getId() != null
                ? travelExpenseService.findById(restTravelPeople.getTravel().getId()).orElse(null)
                : null)
        .user(
            restTravelPeople.getUser() != null && restTravelPeople.getUser().getId() != null
                ? userRepository.findById(restTravelPeople.getUser().getId()).orElse(null)
                : null)
        .comment(restTravelPeople.getComment())
        .arrivalLocation(
            restTravelPeople.getArrivalLocation() != null
                ? warehouseService
                    .findById(restTravelPeople.getArrivalLocation().getId())
                    .orElse(null)
                : null)
        .arrivalDate(restTravelPeople.getArrivalDate())
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
        .user(
            restTravelPeople.getUserId() != null
                ? userRepository.findById(restTravelPeople.getUserId()).orElse(null)
                : null)
        .comment(restTravelPeople.getComment())
        .arrivalLocation(
            restTravelPeople.getArrivalLocation() != null
                ? warehouseService.findById(restTravelPeople.getArrivalLocation()).orElse(null)
                : null)
        .arrivalDate(restTravelPeople.getArrivalDate())
        .build();
  }

  public TravelPeople toRestTravelPeople(
      com.example.demo.model.movement.TravelPeople domainTravelPeople) {
    if (domainTravelPeople == null) return null;

    TravelPeople restTravelPeople = new TravelPeople();
    restTravelPeople.setId(domainTravelPeople.getId());
    restTravelPeople.setTravel(
        travelExpenseMapper.toRestCrupdateTravelExpense(domainTravelPeople.getTravel()));
    restTravelPeople.setUser(
        domainTravelPeople.getUser() != null ? mapUserToRest(domainTravelPeople.getUser()) : null);
    restTravelPeople.setArrivalDate(domainTravelPeople.getArrivalDate());
    restTravelPeople.setArrivalLocation(
        warehouseMapper.toRestCrupdateWarehouse(domainTravelPeople.getArrivalLocation()));
    RestAuditMapperUtils.mapAuditFields(
        domainTravelPeople,
        restTravelPeople::setCreatedAt,
        restTravelPeople::setUpdatedAt,
        restTravelPeople::setComment,
        restTravelPeople::setCreatedBy,
        restTravelPeople::setUpdatedBy);

    return restTravelPeople;
  }

  private com.example.demo.client.model.User mapUserToRest(User user) {
    com.example.demo.client.model.User restUser = new com.example.demo.client.model.User();
    restUser.setId(user.getId());
    restUser.setFirstName(user.getFirstName());
    restUser.setLastName(user.getLastName());
    restUser.setEmail(user.getEmail());
    return restUser;
  }

  public List<TravelPeople> toRestTravelPeopleList(
      List<com.example.demo.model.movement.TravelPeople> domainTravelPeople) {
    return domainTravelPeople.stream().map(this::toRestTravelPeople).toList();
  }
}
