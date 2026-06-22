package com.example.demo.Service.money;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.mapper.money.CashTransactionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.demo.service.money.CashAccountService;
import com.example.demo.endpoint.rest.mapper.money.CashAccountMapper;

@ExtendWith(MockitoExtension.class)
class CashTransactionMapperTest {

  @Mock private CashAccountService cashAccountService;
  @Mock private CashAccountMapper cashAccountMapper;
  @InjectMocks private CashTransactionMapper mapper;

  @Test
  void toDomain_null_returnsNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }

}
