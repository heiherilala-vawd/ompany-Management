package com.example.demo.service.money;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.money.BudgetLine;
import com.example.demo.repository.money.BudgetLineRepository;
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
public class BudgetLineService {

  private final BudgetLineRepository budgetLineRepository;
  private final ModificationUtils modificationUtils;

  public Optional<BudgetLine> findById(String id) {
    return budgetLineRepository.findById(id);
  }

  public Page<BudgetLine> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return budgetLineRepository.findAll(pageable);
  }

  @Transactional
  public List<BudgetLine> createOrUpdateAll(List<BudgetLine> budgetLines) {
    List<BudgetLine> processed = new ArrayList<>();
    for (BudgetLine budgetLine : budgetLines) {
      BudgetLine existing =
          budgetLine.getId() == null
              ? null
              : budgetLineRepository.findById(budgetLine.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          budgetLine, existing, budgetLine.getId(), modificationUtils.takePrimaryUser());
      processed.add(budgetLine);
    }
    return budgetLineRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    budgetLineRepository.deleteById(id);
  }
}
