package com.example.demo.Service.movement;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.movement.EquipmentUsageMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.endpoint.rest.mapper.movement.EquipmentMapper;
import com.example.demo.service.movement.WarehouseService;
import com.example.demo.service.UserService;
import com.example.demo.service.JobService;
import com.example.demo.service.movement.EquipmentService;

@ExtendWith(MockitoExtension.class)
class EquipmentUsageMapperTest {

  @Mock private EquipmentService equipmentService;
  @Mock private JobService jobService;
  @Mock private WarehouseService warehouseService;
  @Mock private UserService userService;
  @Mock private EquipmentMapper equipmentMapper;
  @Mock private JobMapper jobMapper;
  @InjectMocks private EquipmentUsageMapper mapper;

  @Test
  void toDomain_null_returnsNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }

}
