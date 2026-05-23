package com.example.demo.service.money;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.money.CashTransaction;
import com.example.demo.repository.money.CashTransactionRepository;
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
public class CashTransactionService {

  private final CashTransactionRepository cashTransactionRepository;
  private final ModificationUtils modificationUtils;

  public Optional<CashTransaction> findById(String id) {
    return cashTransactionRepository.findById(id);
  }

  public Page<CashTransaction> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return cashTransactionRepository.findAll(pageable);
  }

  @Transactional
  public List<CashTransaction> createOrUpdateAll(List<CashTransaction> transactions) {
    List<CashTransaction> processed = new ArrayList<>();
    for (CashTransaction transaction : transactions) {
      CashTransaction existing =
          transaction.getId() == null
              ? null
              : cashTransactionRepository.findById(transaction.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          transaction, existing, transaction.getId(), modificationUtils.takePrimaryUser());
      processed.add(transaction);
    }
    return cashTransactionRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    cashTransactionRepository.deleteById(id);
  }
}
