package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.movement.EquipmentUsage;
import com.example.demo.repository.movement.EquipmentUsageRepository;
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
public class EquipmentUsageService {

  private final EquipmentUsageRepository equipmentUsageRepository;
  private final ModificationUtils modificationUtils;

  public Optional<EquipmentUsage> findById(String id) {
    return equipmentUsageRepository.findById(id);
  }

  public Page<EquipmentUsage> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return equipmentUsageRepository.findAll(pageable);
  }

  @Transactional
  public List<EquipmentUsage> createOrUpdateAll(List<EquipmentUsage> usages) {
    List<EquipmentUsage> processed = new ArrayList<>();
    for (EquipmentUsage usage : usages) {
      EquipmentUsage existing =
          usage.getId() == null
              ? null
              : equipmentUsageRepository.findById(usage.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          usage, existing, usage.getId(), modificationUtils.takePrimaryUser());
      processed.add(usage);
    }
    return equipmentUsageRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    equipmentUsageRepository.deleteById(id);
  }
}
