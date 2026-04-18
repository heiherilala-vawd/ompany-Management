package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateMonetaryMovement;
import com.example.demo.client.model.MonetaryMovement;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MonetaryMovementMapper {

  public com.example.demo.model.money.MonetaryMovement toDomain(MonetaryMovement restMovement) {
    if (restMovement == null) return null;

    return com.example.demo.model.money.MonetaryMovement.builder()
        .id(restMovement.getId())
        .amount(restMovement.getAmount())
        .description(restMovement.getDescription())
        .build();
  }

  public com.example.demo.model.money.MonetaryMovement toDomain(
      CrupdateMonetaryMovement restMovement) {
    if (restMovement == null) return null;

    return com.example.demo.model.money.MonetaryMovement.builder()
        .id(restMovement.getId())
        .amount(restMovement.getAmount())
        .description(restMovement.getDescription())
        .build();
  }

  public MonetaryMovement toRestMovement(
      com.example.demo.model.money.MonetaryMovement domainMovement) {
    if (domainMovement == null) return null;

    MonetaryMovement restMovement = new MonetaryMovement();
    restMovement.setId(domainMovement.getId());
    restMovement.setAmount(domainMovement.getAmount());
    restMovement.setDescription(domainMovement.getDescription());
    restMovement.setCreatedAt(domainMovement.getCreatedAt());
    restMovement.setUpdatedAt(domainMovement.getUpdatedAt());
    restMovement.setComment(domainMovement.getComment());

    if (domainMovement.getCreatedBy() != null) {
      restMovement.setCreatedBy(domainMovement.getCreatedBy().getId());
    }
    if (domainMovement.getUpdatedBy() != null) {
      restMovement.setUpdatedBy(domainMovement.getUpdatedBy().getId());
    }

    return restMovement;
  }

  public List<MonetaryMovement> toRestMovements(
      List<com.example.demo.model.money.MonetaryMovement> domainMovements) {
    return domainMovements.stream().map(this::toRestMovement).toList();
  }

  public List<com.example.demo.model.money.MonetaryMovement> toDomain(
      List<MonetaryMovement> restMovements) {
    return restMovements.stream().map(this::toDomain).toList();
  }
}
