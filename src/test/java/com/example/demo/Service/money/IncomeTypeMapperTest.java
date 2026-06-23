package com.example.demo.Service.money;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.endpoint.rest.mapper.money.IncomeTypeMapper;
import com.example.demo.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IncomeTypeMapperTest {

  @Mock private CompanyService companyService;
  @Mock private CompanyMapper companyMapper;
  @InjectMocks private IncomeTypeMapper mapper;

  @Test
  void toDomain_null_returnsNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }
}
