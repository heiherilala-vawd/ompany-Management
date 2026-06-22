package com.example.demo.Service.movement;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.movement.TravelContainerMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.demo.endpoint.rest.mapper.money.TravelExpenseMapper;
import com.example.demo.service.money.TravelExpenseService;

@ExtendWith(MockitoExtension.class)
class TravelContainerMapperTest {

  @Mock private TravelExpenseService travelExpenseService;
  @Mock private TravelExpenseMapper travelExpenseMapper;
  @InjectMocks private TravelContainerMapper mapper;

  @Test
  void toDomain_null_returnsNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }

}
