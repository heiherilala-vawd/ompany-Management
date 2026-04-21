package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelPeopleCriteria;
import com.example.demo.model.movement.TravelPeople;
import com.example.demo.repository.movement.TravelPeopleRepository;
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
public class TravelPeopleService {

  private final TravelPeopleRepository travelPeopleRepository;

  public Optional<TravelPeople> findById(String id) {
    return travelPeopleRepository.findById(id);
  }

  public Page<TravelPeople> findAll(
      PageFromOne page, BoundedPageSize pageSize, TravelPeopleCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return travelPeopleRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<TravelPeople> createOrUpdateAll(List<TravelPeople> travelPeopleList) {
    return travelPeopleRepository.saveAll(travelPeopleList);
  }

  @Transactional
  public void deleteById(String id) {
    travelPeopleRepository.deleteById(id);
  }

  private Specification<TravelPeople> toSpecification(TravelPeopleCriteria criteria) {
    return Specification.<TravelPeople>where(equal(criteria.getTravelId(), "travel", "id"))
        .and(containsIgnoreCase(criteria.getPersonName(), "personName"));
  }
}
