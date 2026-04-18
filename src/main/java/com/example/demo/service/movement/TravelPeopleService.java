package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.movement.TravelPeople;
import com.example.demo.repository.movement.TravelPeopleRepository;
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
public class TravelPeopleService {

  private final TravelPeopleRepository travelPeopleRepository;

  public Optional<TravelPeople> findById(String id) {
    return travelPeopleRepository.findById(id);
  }

  public Page<TravelPeople> findAll(PageFromOne page, BoundedPageSize pageSize, String travelId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    if (travelId != null) {
      return travelPeopleRepository.findByTravelId(travelId, pageable);
    }

    return travelPeopleRepository.findAll(pageable);
  }

  public List<TravelPeople> findByTravelId(String travelId) {
    return travelPeopleRepository.findByTravelId(travelId);
  }

  @Transactional
  public TravelPeople create(TravelPeople travelPeople) {
    return travelPeopleRepository.save(travelPeople);
  }

  @Transactional
  public TravelPeople update(TravelPeople travelPeople) {
    return travelPeopleRepository.save(travelPeople);
  }

  @Transactional
  public List<TravelPeople> createOrUpdateAll(List<TravelPeople> travelPeopleList) {
    return travelPeopleRepository.saveAll(travelPeopleList);
  }

  @Transactional
  public void deleteById(String id) {
    travelPeopleRepository.deleteById(id);
  }

  @Transactional
  public void deleteByTravelId(String travelId) {
    travelPeopleRepository.deleteByTravelId(travelId);
  }
}
