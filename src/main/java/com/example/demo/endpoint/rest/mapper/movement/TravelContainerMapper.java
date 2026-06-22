package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateTravelContainer;
import com.example.demo.client.model.TravelContainer;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.endpoint.rest.mapper.money.TravelExpenseMapper;
import com.example.demo.service.money.TravelExpenseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TravelContainerMapper {

  private final TravelExpenseService travelExpenseService;
  private final TravelExpenseMapper travelExpenseMapper;

  public com.example.demo.model.movement.TravelContainer toDomain(
      CrupdateTravelContainer restContainer) {
    if (restContainer == null) return null;

    return com.example.demo.model.movement.TravelContainer.builder()
        .id(restContainer.getId())
        .travel(
            restContainer.getTravelId() != null
                ? travelExpenseService.findById(restContainer.getTravelId()).orElse(null)
                : null)
        .name(restContainer.getName())
        .description(restContainer.getDescription())
        .comment(restContainer.getComment())
        .build();
  }

  public TravelContainer toRestTravelContainer(
      com.example.demo.model.movement.TravelContainer domainContainer) {
    if (domainContainer == null) return null;

    TravelContainer restContainer = new TravelContainer();
    restContainer.setId(domainContainer.getId());
    restContainer.setTravel(
        travelExpenseMapper.toRestCrupdateTravelExpense(domainContainer.getTravel()));
    restContainer.setName(domainContainer.getName());
    restContainer.setDescription(domainContainer.getDescription());
    RestAuditMapperUtils.mapAuditFields(
        domainContainer,
        restContainer::setCreatedAt,
        restContainer::setUpdatedAt,
        restContainer::setComment,
        restContainer::setCreatedBy,
        restContainer::setUpdatedBy);

    return restContainer;
  }

  public List<TravelContainer> toRestTravelContainerList(
      List<com.example.demo.model.movement.TravelContainer> domainContainers) {
    return domainContainers.stream().map(this::toRestTravelContainer).toList();
  }
}
