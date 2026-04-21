package com.example.demo.endpoint.rest.controller;

import com.example.demo.client.model.EntityType;
import com.example.demo.client.model.History;
import com.example.demo.endpoint.rest.mapper.HistoryMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.HistoryCriteria;
import com.example.demo.service.HistoryService;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class HistoryController {

  private final HistoryService historyService;
  private final HistoryMapper historyMapper;

  @GetMapping("/histories")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<History> getHistories(
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "user_id", required = false) String userId,
      @RequestParam(name = "entity_type", required = false) EntityType entityType,
      @RequestParam(name = "entity_id", required = false) String entityId,
      @RequestParam(name = "date_from", required = false) OffsetDateTime dateFrom,
      @RequestParam(name = "date_to", required = false) OffsetDateTime dateTo) {

    HistoryCriteria criteria = new HistoryCriteria();
    criteria.setUserId(userId);
    criteria.setEntityType(
        entityType != null ? historyMapper.toDomainEntityType(entityType) : null);
    criteria.setEntityId(entityId);
    criteria.setDateFrom(dateFrom != null ? dateFrom.toInstant() : null);
    criteria.setDateTo(dateTo != null ? dateTo.toInstant() : null);

    return historyService.findAll(page, pageSize, criteria).stream()
        .map(historyMapper::toRestHistory)
        .toList();
  }
}
