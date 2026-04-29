package com.example.demo.repository.movement;

import com.example.demo.model.movement.TravelPeople;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelPeopleRepository
    extends JpaRepository<TravelPeople, String>, JpaSpecificationExecutor<TravelPeople> {
  Page<TravelPeople> findByTravelId(String travelId, Pageable pageable);

  List<TravelPeople> findByTravelId(String travelId);

  List<TravelPeople> findByUserId(String userId);

  void deleteByTravelId(String travelId);
}
