package com.example.demo.Service.movement;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.movement.CarMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarMapperTest {

  @InjectMocks private CarMapper mapper;

  @Test
  void toDomain_null_returnsNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }

}
