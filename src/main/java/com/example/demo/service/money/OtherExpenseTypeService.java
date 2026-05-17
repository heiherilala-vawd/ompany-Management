package com.example.demo.service.money;

import com.example.demo.model.money.OtherExpenseType;
import com.example.demo.repository.money.OtherExpenseTypeRepository;
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
public class OtherExpenseTypeService {

  private final OtherExpenseTypeRepository otherExpenseTypeRepository;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<OtherExpenseType> findById(String id) {
    return otherExpenseTypeRepository.findById(id);
  }

  public List<OtherExpenseType> findAllByCompanyId(String companyId) {
    return otherExpenseTypeRepository.findByCompanyIdOrderByName(companyId);
  }

  @Transactional
  public List<OtherExpenseType> createOrUpdateAll(List<OtherExpenseType> otherExpenseTypes) {
    moneyValidator.validateOtherExpenseTypes(otherExpenseTypes);
    List<OtherExpenseType> processed = new ArrayList<>();
    for (OtherExpenseType otherExpenseType : otherExpenseTypes) {
      OtherExpenseType existing =
          otherExpenseType.getId() == null
              ? null
              : otherExpenseTypeRepository.findById(otherExpenseType.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          otherExpenseType,
          existing,
          otherExpenseType.getId(),
          modificationUtils.takePrimaryUser());
      processed.add(otherExpenseType);
    }
    return otherExpenseTypeRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    otherExpenseTypeRepository.deleteById(id);
  }
}
