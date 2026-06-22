package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelContainerCriteria;
import com.example.demo.model.movement.TravelContainer;
import com.example.demo.repository.movement.TravelContainerRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MovementValidator;
import java.util.ArrayList;
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
public class TravelContainerService {

  private final TravelContainerRepository travelContainerRepository;
  private final ModificationUtils modificationUtils;
  private final MovementValidator movementValidator;

  public Optional<TravelContainer> findById(String id) {
    return travelContainerRepository.findById(id);
  }

  public Page<TravelContainer> findAll(
      PageFromOne page, BoundedPageSize pageSize, TravelContainerCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return travelContainerRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<TravelContainer> createOrUpdateAll(List<TravelContainer> containers) {
    movementValidator.validateTravelContainers(containers);
    List<TravelContainer> processed = new ArrayList<>();
    for (TravelContainer container : containers) {
      TravelContainer existing = travelContainerRepository.findById(container.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          container, existing, container.getId(), modificationUtils.takePrimaryUser());
      processed.add(container);
    }
    return travelContainerRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    travelContainerRepository.deleteById(id);
  }

  private Specification<TravelContainer> toSpecification(TravelContainerCriteria criteria) {
    return Specification.<TravelContainer>where(equal(criteria.getTravelId(), "travel", "id"))
        .and(equal(criteria.getName(), "name"));
  }
}
