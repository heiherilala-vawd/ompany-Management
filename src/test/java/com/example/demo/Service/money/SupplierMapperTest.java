package com.example.demo.Service.money;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.money.SupplierMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.demo.endpoint.rest.mapper.CompanyMapper;

@ExtendWith(MockitoExtension.class)
class SupplierMapperTest {

  @Mock private CompanyMapper companyMapper;
  @InjectMocks private SupplierMapper mapper;

  @Test
  void toRest_null_returnsNull() {
    assertThat(mapper.toRest(null)).isNull();
  }

}
