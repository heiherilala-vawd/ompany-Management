package com.example.demo.Service.movement;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.movement.EquipmentIncidentMapper;
import com.example.demo.service.UserService;
import com.example.demo.service.money.TravelExpenseService;
import com.example.demo.service.movement.EquipmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EquipmentIncidentMapperTest {

  @Mock private EquipmentService equipmentService;
  @Mock private UserService userService;
  @Mock private TravelExpenseService travelExpenseService;
  @InjectMocks private EquipmentIncidentMapper mapper;

  @Test
  void toDomain_null_returnsNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }
}
