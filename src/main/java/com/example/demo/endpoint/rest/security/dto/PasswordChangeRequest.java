package com.example.demo.endpoint.rest.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequest {
  @NotBlank
  @JsonProperty("old_password")
  private String oldPassword;

  @NotBlank
  @Size(min = 8, max = 128)
  @JsonProperty("new_password")
  private String newPassword;
}
