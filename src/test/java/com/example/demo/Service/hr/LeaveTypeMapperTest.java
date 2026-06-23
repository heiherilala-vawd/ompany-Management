package com.example.demo.Service.hr;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.endpoint.rest.mapper.hr.LeaveTypeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaveTypeMapperTest {

  @Mock private CompanyMapper companyMapper;
  @InjectMocks private LeaveTypeMapper mapper;

  @Test
  void toDomain_null_returnsNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }
}
