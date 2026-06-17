package com.example.demo.repository.movement;

import com.example.demo.model.movement.TravelMaterialsArrivalLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelMaterialsArrivalLogRepository
    extends JpaRepository<TravelMaterialsArrivalLog, String> {

  List<TravelMaterialsArrivalLog> findByTravelMaterials_IdOrderByArrivalDateAsc(
      String travelMaterialsId);

  void deleteByTravelMaterials_Id(String travelMaterialsId);
}
