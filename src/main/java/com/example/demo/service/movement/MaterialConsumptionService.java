package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.movement.MaterialConsumption;
import com.example.demo.repository.movement.MaterialConsumptionRepository;
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
public class MaterialConsumptionService {

  private final MaterialConsumptionRepository materialConsumptionRepository;
  private final ModificationUtils modificationUtils;

  public Optional<MaterialConsumption> findById(String id) {
    return materialConsumptionRepository.findById(id);
  }

  public Page<MaterialConsumption> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return materialConsumptionRepository.findAll(pageable);
  }

  @Transactional
  public List<MaterialConsumption> createOrUpdateAll(List<MaterialConsumption> consumptions) {
    List<MaterialConsumption> processed = new ArrayList<>();
    for (MaterialConsumption consumption : consumptions) {
      MaterialConsumption existing =
          consumption.getId() == null
              ? null
              : materialConsumptionRepository.findById(consumption.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          consumption, existing, consumption.getId(), modificationUtils.takePrimaryUser());
      processed.add(consumption);
    }
    return materialConsumptionRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    materialConsumptionRepository.deleteById(id);
  }
}
