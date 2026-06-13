package com.example.demo.service.utils;

import com.example.demo.model.exception.BadRequestException;
import java.util.UUID;

public final class UuidValidator {

  private UuidValidator() {}

  public static void validate(String id, String fieldName) {
    if (id == null) {
      return;
    }
    try {
      UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(fieldName + " must be a valid UUID");
    }
  }
}
