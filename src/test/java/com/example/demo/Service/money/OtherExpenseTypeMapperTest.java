package com.example.demo.Service.money;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.money.OtherExpenseTypeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.service.CompanyService;

@ExtendWith(MockitoExtension.class)
class OtherExpenseTypeMapperTest {

  @Mock private CompanyService companyService;
  @Mock private CompanyMapper companyMapper;
  @InjectMocks private OtherExpenseTypeMapper mapper;

  @Test
  void toDomain_null_returnsNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }

}
