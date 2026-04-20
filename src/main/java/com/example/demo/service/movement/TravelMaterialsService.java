package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelMaterialsCriteria;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.repository.movement.TravelMaterialsRepository;
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
public class TravelMaterialsService {

  private final TravelMaterialsRepository travelMaterialsRepository;

  public Optional<TravelMaterials> findById(String id) {
    return travelMaterialsRepository.findById(id);
  }

  public Page<TravelMaterials> findAll(
      PageFromOne page, BoundedPageSize pageSize, TravelMaterialsCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return travelMaterialsRepository.findAll(toSpecification(criteria), pageable);
  }

  public List<TravelMaterials> findByTravelId(String travelId) {
    return travelMaterialsRepository.findByTravelId(travelId);
  }

  @Transactional
  public TravelMaterials create(TravelMaterials travelMaterials) {
    return travelMaterialsRepository.save(travelMaterials);
  }

  @Transactional
  public TravelMaterials update(TravelMaterials travelMaterials) {
    return travelMaterialsRepository.save(travelMaterials);
  }

  @Transactional
  public void updateQuantityReceived(String id, Integer quantityReceived) {
    travelMaterialsRepository.updateQuantityReceived(id, quantityReceived);
  }

  @Transactional
  public List<TravelMaterials> createOrUpdateAll(List<TravelMaterials> materialsList) {
    return travelMaterialsRepository.saveAll(materialsList);
  }

  @Transactional
  public void deleteById(String id) {
    travelMaterialsRepository.deleteById(id);
  }

  @Transactional
  public void deleteByTravelId(String travelId) {
    travelMaterialsRepository.deleteByTravelId(travelId);
  }

  private Specification<TravelMaterials> toSpecification(TravelMaterialsCriteria criteria) {
    return Specification.<TravelMaterials>where(equal(criteria.getTravelId(), "travel", "id"))
        .and(equal(criteria.getMaterialId(), "material", "id"))
        .and(equal(criteria.getQuantity(), "quantity"))
        .and(equal(criteria.getQuantityReceived(), "quantityReceived"));
  }
}
