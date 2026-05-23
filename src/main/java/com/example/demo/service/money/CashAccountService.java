package com.example.demo.service.money;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.money.CashAccount;
import com.example.demo.repository.money.CashAccountRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashAccountService {

  private final CashAccountRepository cashAccountRepository;
  private final ModificationUtils modificationUtils;

  public Optional<CashAccount> findById(String id) {
    return cashAccountRepository.findById(id);
  }

  public Page<CashAccount> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return cashAccountRepository.findAll(pageable);
  }

  @Transactional
  public List<CashAccount> createOrUpdateAll(List<CashAccount> accounts) {
    List<CashAccount> processed = new ArrayList<>();
    for (CashAccount account : accounts) {
      CashAccount existing =
          account.getId() == null
              ? null
              : cashAccountRepository.findById(account.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          account, existing, account.getId(), modificationUtils.takePrimaryUser());
      processed.add(account);
    }
    return cashAccountRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    cashAccountRepository.deleteById(id);
  }
}
