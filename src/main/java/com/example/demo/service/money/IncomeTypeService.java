package com.example.demo.service.money;

import com.example.demo.model.money.IncomeType;
import com.example.demo.repository.money.IncomeTypeRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.validator.MoneyValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeTypeService {

  private final IncomeTypeRepository incomeTypeRepository;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<IncomeType> findById(String id) {
    return incomeTypeRepository.findById(id);
  }

  public List<IncomeType> findAllByCompanyId(String companyId) {
    return incomeTypeRepository.findByCompanyIdOrderByName(companyId);
  }

  @Transactional
  public List<IncomeType> createOrUpdateAll(List<IncomeType> incomeTypes) {
    moneyValidator.validateIncomeTypes(incomeTypes);
    List<IncomeType> processedIncomeTypes = new ArrayList<>();
    for (IncomeType incomeType : incomeTypes) {
      IncomeType existingIncomeType =
          incomeType.getId() == null
              ? null
              : incomeTypeRepository.findById(incomeType.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          incomeType, existingIncomeType, incomeType.getId(), modificationUtils.takePrimaryUser());
      processedIncomeTypes.add(incomeType);
    }
    return incomeTypeRepository.saveAll(processedIncomeTypes);
  }

  @Transactional
  public void deleteById(String id) {
    incomeTypeRepository.deleteById(id);
  }
}
