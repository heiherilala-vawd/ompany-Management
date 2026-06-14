package com.example.demo.service.money;

import com.example.demo.model.money.CompanyFixedCost;
import com.example.demo.repository.money.CompanyFixedCostRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.validator.MoneyValidator;
import java.time.LocalDate;
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
public class CompanyFixedCostService {

  private final CompanyFixedCostRepository companyFixedCostRepository;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<CompanyFixedCost> findById(String id) {
    return companyFixedCostRepository.findById(id);
  }

  public List<CompanyFixedCost> findAllByCompanyId(String companyId) {
    return companyFixedCostRepository.findByCompanyIdOrderByName(companyId);
  }

  public Page<CompanyFixedCost> findAllByCompanyId(String companyId, Pageable pageable) {
    return companyFixedCostRepository.findByCompanyIdOrderByName(companyId, pageable);
  }

  public List<CompanyFixedCost> findActiveByCompanyIdAtDate(String companyId, LocalDate date) {
    return companyFixedCostRepository.findActiveByCompanyIdAtDate(companyId, date);
  }

  @Transactional
  public List<CompanyFixedCost> createOrUpdateAll(List<CompanyFixedCost> fixedCosts) {
    moneyValidator.validateCompanyFixedCosts(fixedCosts);
    List<CompanyFixedCost> processed = new ArrayList<>();
    for (CompanyFixedCost fixedCost : fixedCosts) {
      CompanyFixedCost existing =
          fixedCost.getId() == null
              ? null
              : companyFixedCostRepository.findById(fixedCost.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          fixedCost, existing, fixedCost.getId(), modificationUtils.takePrimaryUser());
      processed.add(fixedCost);
    }
    return companyFixedCostRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    companyFixedCostRepository.deleteById(id);
  }
}
