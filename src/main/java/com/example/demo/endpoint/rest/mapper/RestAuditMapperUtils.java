package com.example.demo.endpoint.rest.mapper;

import com.example.demo.client.model.AuditUser;
import com.example.demo.client.model.Role;
import com.example.demo.client.model.Sex;
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
      Consumer<AuditUser> createdBySetter,
      Consumer<AuditUser> updatedBySetter) {
    if (source == null) {
      return;
    }

    createdAtSetter.accept(source.getCreatedAt());
    updatedAtSetter.accept(source.getUpdatedAt());
    commentSetter.accept(source.getComment());

    if (source.getCreatedBy() != null) {
      createdBySetter.accept(toAuditUser(source.getCreatedBy()));
    }
    if (source.getUpdatedBy() != null) {
      updatedBySetter.accept(toAuditUser(source.getUpdatedBy()));
    }
  }

  private static AuditUser toAuditUser(com.example.demo.model.User source) {
    return new AuditUser()
        .id(source.getId())
        .role(EnumMapper.mapEnum(source.getRole(), Role.class))
        .firstName(source.getFirstName())
        .lastName(source.getLastName())
        .sex(EnumMapper.mapEnum(source.getSex(), Sex.class))
        .email(source.getEmail());
  }
}
