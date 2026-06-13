package com.example.demo.service.utils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.model.exception.BadRequestException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UuidValidatorTest {

  @Test
  void validate_validUuid_doesNotThrow() {
    String validUuid = UUID.randomUUID().toString();
    assertThatCode(() -> UuidValidator.validate(validUuid, "Test id"))
        .doesNotThrowAnyException();
  }

  @Test
  void validate_null_doesNotThrow() {
    assertThatCode(() -> UuidValidator.validate(null, "Test id"))
        .doesNotThrowAnyException();
  }

  @Test
  void validate_invalidUuid_throws() {
    assertThatThrownBy(() -> UuidValidator.validate("not-a-uuid", "Test id"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Test id must be a valid UUID");
  }

  @Test
  void validate_emptyString_throws() {
    assertThatThrownBy(() -> UuidValidator.validate("", "Test id"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Test id must be a valid UUID");
  }

  @Test
  void validate_uuidWithoutDashes_throws() {
    String uuid = UUID.randomUUID().toString().replace("-", "");
    assertThatThrownBy(() -> UuidValidator.validate(uuid, "Test id"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Test id must be a valid UUID");
  }
}
