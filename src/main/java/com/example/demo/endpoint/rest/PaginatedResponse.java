package com.example.demo.endpoint.rest;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaginatedResponse {
  private final List<?> data;
  private final int total;
}
