package com.example.demo.Service.money;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.endpoint.rest.mapper.money.PurchaseOrderMapper;
import com.example.demo.endpoint.rest.mapper.money.SupplierMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderMapperTest {

  @Mock private SupplierMapper supplierMapper;
  @Mock private CompanyMapper companyMapper;
  @Mock private JobMapper jobMapper;
  @InjectMocks private PurchaseOrderMapper mapper;

  @Test
  void toRest_null_returnsNull() {
    assertThat(mapper.toRest(null)).isNull();
  }
}
