package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Maintenance;
import com.example.demo.repository.movement.MaintenanceRepository;
import com.example.demo.service.money.ExpenseMoneyService;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MovementValidator;
import jakarta.persistence.criteria.Join;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaintenanceService {

  private final MaintenanceRepository maintenanceRepository;
  private final ExpenseMoneyService expenseMoneyService;
  private final ModificationUtils modificationUtils;
  private final MovementValidator movementValidator;

  public Optional<Maintenance> findById(String id) {
    return maintenanceRepository.findById(id);
  }

  public Page<Maintenance> findAll(
      String equipmentId, PageFromOne page, BoundedPageSize pageSize, String description) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    Specification<Maintenance> spec =
        Specification.<Maintenance>where(
                (root, query, cb) -> {
                  Join<Maintenance, Equipment> join = root.join("equipment");
                  return cb.equal(join.get("id"), equipmentId);
                })
            .and(containsIgnoreCase(description, "description"));
    return maintenanceRepository.findAll(spec, pageable);
  }

  @Transactional
  public List<Maintenance> createOrUpdateAll(List<Maintenance> maintenances) {
    movementValidator.validateMaintenances(maintenances);

    List<ExpenseMoney> expenses =
        maintenances.stream().map(Maintenance::getExpense).collect(Collectors.toList());
    expenseMoneyService.createOrUpdateAll(expenses);

    return maintenanceRepository.saveAll(maintenances);
  }

  @Transactional
  public void deleteById(String id) {
    maintenanceRepository
        .findById(id)
        .ifPresent(
            maintenance -> {
              if (maintenance.getExpense() != null) {
                maintenance.getExpense().setMaintenance(null);
              }
              maintenanceRepository.delete(maintenance);
            });
  }
}
