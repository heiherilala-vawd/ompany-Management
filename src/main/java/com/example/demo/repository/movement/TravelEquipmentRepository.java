package com.example.demo.repository.movement;

import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelEquipment.TransportStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelEquipmentRepository
    extends JpaRepository<TravelEquipment, String>, JpaSpecificationExecutor<TravelEquipment> {
  Page<TravelEquipment> findByTravelId(String travelId, Pageable pageable);

  List<TravelEquipment> findByTravelId(String travelId);

  List<TravelEquipment> findByEquipmentId(String equipmentId);

  Page<TravelEquipment> findByStatus(TransportStatus status, Pageable pageable);

  List<TravelEquipment> findByTravelIdAndStatus(String travelId, TransportStatus status);

  void deleteByTravelId(String travelId);
}
