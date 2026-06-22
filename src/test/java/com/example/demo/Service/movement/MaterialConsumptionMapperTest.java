package com.example.demo.Service.movement;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.movement.MaterialConsumptionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.demo.endpoint.rest.mapper.movement.MaterialMapper;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.service.movement.WarehouseService;
import com.example.demo.service.JobService;
import com.example.demo.service.movement.MaterialService;
import com.example.demo.endpoint.rest.mapper.movement.WarehouseMapper;

@ExtendWith(MockitoExtension.class)
class MaterialConsumptionMapperTest {

  @Mock private MaterialService materialService;
  @Mock private WarehouseService warehouseService;
  @Mock private JobService jobService;
  @Mock private MaterialMapper materialMapper;
  @Mock private WarehouseMapper warehouseMapper;
  @Mock private JobMapper jobMapper;
  @InjectMocks private MaterialConsumptionMapper mapper;

  @Test
  void toDomain_null_returnsNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }

}
