package com.example.demo.service.money;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.BankFeeCriteria;
import com.example.demo.model.money.BankFee;
import com.example.demo.repository.money.BankFeeRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MoneyValidator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankFeeService {

  private final BankFeeRepository bankFeeRepository;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<BankFee> findById(String id) {
    return bankFeeRepository.findById(id);
  }

  public Page<BankFee> findAll(
      PageFromOne page, BoundedPageSize pageSize, BankFeeCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return bankFeeRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<BankFee> createOrUpdateAll(List<BankFee> bankFees) {
    moneyValidator.validateBankFees(bankFees);
    return bankFeeRepository.saveAll(bankFees);
  }

  @Transactional
  public void deleteById(String id) {
    bankFeeRepository.deleteById(id);
  }

  private Specification<BankFee> toSpecification(BankFeeCriteria criteria) {
    return Specification.<BankFee>where(equal(criteria.getExpenseId(), "expense", "id"))
        .and(containsIgnoreCase(criteria.getBankName(), "bankName"))
        .and(containsIgnoreCase(criteria.getDescription(), "description"));
  }
}
