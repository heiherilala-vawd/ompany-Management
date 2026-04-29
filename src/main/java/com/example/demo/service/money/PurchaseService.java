package com.example.demo.service.money;

import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.PurchaseCriteria;
import com.example.demo.model.money.Purchase;
import com.example.demo.repository.money.PurchaseRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
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
public class PurchaseService {

  private final PurchaseRepository purchaseRepository;
  private final ModificationUtils modificationUtils;

  public Optional<Purchase> findById(String id) {
    return purchaseRepository.findById(id);
  }

  public Page<Purchase> findAll(
      PageFromOne page, BoundedPageSize pageSize, PurchaseCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return purchaseRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<Purchase> createOrUpdateAll(List<Purchase> purchases) {
    return purchaseRepository.saveAll(purchases);
  }

  @Transactional
  public void deleteById(String id) {
    purchaseRepository.deleteById(id);
  }

  private Specification<Purchase> toSpecification(PurchaseCriteria criteria) {
    return Specification.<Purchase>where(equal(criteria.getExpenseId(), "expense", "id"))
        .and(equal(criteria.getSupplierId(), "supplier", "id"))
        .and(equal(criteria.getIsEquipment(), "isEquipment"));
  }
}
