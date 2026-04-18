package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.repository.movement.TravelEquipmentRepository;
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
public class TravelEquipmentService {

  private final TravelEquipmentRepository travelEquipmentRepository;

  public Optional<TravelEquipment> findById(String id) {
    return travelEquipmentRepository.findById(id);
  }

  public Page<TravelEquipment> findAll(
      PageFromOne page, BoundedPageSize pageSize, String travelId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    if (travelId != null) {
      return travelEquipmentRepository.findByTravelId(travelId, pageable);
    }

    return travelEquipmentRepository.findAll(pageable);
  }

  public List<TravelEquipment> findByTravelId(String travelId) {
    return travelEquipmentRepository.findByTravelId(travelId);
  }

  public Page<TravelEquipment> findByStatus(
      TravelEquipment.TransportStatus status, PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return travelEquipmentRepository.findByStatus(status, pageable);
  }

  @Transactional
  public TravelEquipment create(TravelEquipment travelEquipment) {
    return travelEquipmentRepository.save(travelEquipment);
  }

  @Transactional
  public TravelEquipment update(TravelEquipment travelEquipment) {
    return travelEquipmentRepository.save(travelEquipment);
  }

  @Transactional
  public List<TravelEquipment> createOrUpdateAll(List<TravelEquipment> equipmentList) {
    return travelEquipmentRepository.saveAll(equipmentList);
  }

  @Transactional
  public void deleteById(String id) {
    travelEquipmentRepository.deleteById(id);
  }

  @Transactional
  public void deleteByTravelId(String travelId) {
    travelEquipmentRepository.deleteByTravelId(travelId);
  }
}
