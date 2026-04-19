package com.example.demo.endpoint.rest.mapper;

import com.example.demo.model.CreatAndUpdateEntity;
import java.time.Instant;
import java.util.function.Consumer;

public final class RestAuditMapperUtils {

  private RestAuditMapperUtils() {}

  public static void mapAuditFields(
      CreatAndUpdateEntity source,
      Consumer<Instant> createdAtSetter,
      Consumer<Instant> updatedAtSetter,
      Consumer<String> commentSetter,
      Consumer<String> createdBySetter,
      Consumer<String> updatedBySetter) {
    if (source == null) {
      return;
    }

    createdAtSetter.accept(source.getCreatedAt());
    updatedAtSetter.accept(source.getUpdatedAt());
    commentSetter.accept(source.getComment());

    if (source.getCreatedBy() != null) {
      createdBySetter.accept(source.getCreatedBy().getId());
    }
    if (source.getUpdatedBy() != null) {
      updatedBySetter.accept(source.getUpdatedBy().getId());
    }
  }
}
