package com.example.demo.service.money;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.money.MonetaryMovement;
import com.example.demo.repository.money.MonetaryMovementRepository;
import com.example.demo.service.utils.PageUtils;
import java.time.Instant;
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
public class MonetaryMovementService {

  private final MonetaryMovementRepository monetaryMovementRepository;

  public Optional<MonetaryMovement> findById(String id) {
    return monetaryMovementRepository.findById(id);
  }

  public Page<MonetaryMovement> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    return monetaryMovementRepository.findAll(pageable);
  }

  public List<MonetaryMovement> findAll() {
    return monetaryMovementRepository.findAll();
  }

  public Page<MonetaryMovement> findByAmountBetween(
      PageFromOne page, BoundedPageSize pageSize, Integer minAmount, Integer maxAmount) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    return monetaryMovementRepository.findByAmountBetween(minAmount, maxAmount, pageable);
  }

  public Long getTotalAmount() {
    return monetaryMovementRepository.getTotalAmount();
  }

  public Long getTotalAmountBetweenDates(Instant startDate, Instant endDate) {
    return monetaryMovementRepository.getTotalAmountBetweenDates(startDate, endDate);
  }

  @Transactional
  public MonetaryMovement create(MonetaryMovement monetaryMovement) {
    return monetaryMovementRepository.save(monetaryMovement);
  }

  @Transactional
  public MonetaryMovement update(MonetaryMovement monetaryMovement) {
    return monetaryMovementRepository.save(monetaryMovement);
  }

  @Transactional
  public List<MonetaryMovement> createOrUpdateAll(List<MonetaryMovement> movements) {
    return monetaryMovementRepository.saveAll(movements);
  }

  @Transactional
  public void deleteById(String id) {
    monetaryMovementRepository.deleteById(id);
  }
}
