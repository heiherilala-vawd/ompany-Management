package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.movement.Material;
import com.example.demo.repository.movement.MaterialRepository;
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
public class MaterialService {

  private final MaterialRepository materialRepository;

  public Optional<Material> findById(String id) {
    return materialRepository.findById(id);
  }

  public Page<Material> findAll(PageFromOne page, BoundedPageSize pageSize, String warehouseId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    if (warehouseId != null) {
      return materialRepository.findByWarehouseId(warehouseId, pageable);
    }

    return materialRepository.findAll(pageable);
  }

  public List<Material> findAll() {
    return materialRepository.findAll();
  }

  public List<Material> findByWarehouseId(String warehouseId) {
    return materialRepository.findByWarehouseId(warehouseId);
  }

  @Transactional
  public Material create(Material material) {
    return materialRepository.save(material);
  }

  @Transactional
  public Material update(Material material) {
    return materialRepository.save(material);
  }

  @Transactional
  public List<Material> createOrUpdateAll(List<Material> materials) {
    return materialRepository.saveAll(materials);
  }

  @Transactional
  public void deleteById(String id) {
    materialRepository.deleteById(id);
  }

  public boolean existsById(String id) {
    return materialRepository.existsById(id);
  }
}
