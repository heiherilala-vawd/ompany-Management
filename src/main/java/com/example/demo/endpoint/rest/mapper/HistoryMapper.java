package com.example.demo.endpoint.rest.mapper;

import com.example.demo.client.model.EntityType;
import com.example.demo.client.model.History;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class HistoryMapper {

  public History toRestHistory(com.example.demo.model.History domainHistory) {
    if (domainHistory == null) return null;

    History restHistory = new History();
    restHistory.setId(domainHistory.getId());
    restHistory.setPreviousValue(domainHistory.getPreviousValue());
    restHistory.setNewValue(domainHistory.getNewValue());
    restHistory.setUserId(domainHistory.getUser() != null ? domainHistory.getUser().getId() : null);
    restHistory.setModifiedAt(
        domainHistory.getModifiedAt() != null ? domainHistory.getModifiedAt() : null);
    restHistory.setEntityType(EnumMapper.mapEnum(domainHistory.getEntityType(), EntityType.class));
    restHistory.setEntityId(domainHistory.getEntityId());

    return restHistory;
  }

  public List<History> toRestHistories(List<com.example.demo.model.History> domainHistories) {
    return domainHistories.stream().map(this::toRestHistory).toList();
  }

  public com.example.demo.model.History.EntityType toDomainEntityType(EntityType restEntityType) {
    return EnumMapper.mapEnum(restEntityType, com.example.demo.model.History.EntityType.class);
  }
}
