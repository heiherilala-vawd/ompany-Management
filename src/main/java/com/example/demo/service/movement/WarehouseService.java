package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.WarehouseRepository;
import com.example.demo.service.utils.PageUtils;
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
public class WarehouseService {

  private final WarehouseRepository warehouseRepository;

  public Optional<Warehouse> findById(String id) {
    return warehouseRepository.findById(id);
  }

  public Page<Warehouse> findAll(PageFromOne page, BoundedPageSize pageSize, String jobId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    if (jobId != null) {
      return warehouseRepository.findByJobId(jobId, pageable);
    }

    return warehouseRepository.findAll(pageable);
  }

  public List<Warehouse> findAll() {
    return warehouseRepository.findAll();
  }

  public List<Warehouse> findByJobId(String jobId) {
    return warehouseRepository.findByJobId(jobId);
  }

  @Transactional
  public Warehouse create(Warehouse warehouse) {
    return warehouseRepository.save(warehouse);
  }

  @Transactional
  public Warehouse update(Warehouse warehouse) {
    return warehouseRepository.save(warehouse);
  }

  @Transactional
  public List<Warehouse> createOrUpdateAll(List<Warehouse> warehouses) {
    return warehouseRepository.saveAll(warehouses);
  }

  @Transactional
  public void deleteById(String id) {
    warehouseRepository.deleteById(id);
  }

  public boolean existsById(String id) {
    return warehouseRepository.existsById(id);
  }
}
