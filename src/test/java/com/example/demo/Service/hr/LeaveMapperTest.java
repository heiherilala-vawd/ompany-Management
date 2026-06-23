package com.example.demo.Service.hr;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.hr.LeaveMapper;
import com.example.demo.service.UserService;
import com.example.demo.service.hr.LeaveTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaveMapperTest {

  @Mock private UserService userService;
  @Mock private LeaveTypeService leaveTypeService;
  @InjectMocks private LeaveMapper mapper;

  @Test
  void toDomain_null_returnsNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }
}
