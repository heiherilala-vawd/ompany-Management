package com.example.demo.repository.movement;

import com.example.demo.model.movement.TravelMaterials;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TravelMaterialsRepository extends JpaRepository<TravelMaterials, String> {
  Page<TravelMaterials> findByTravelId(String travelId, Pageable pageable);

  List<TravelMaterials> findByTravelId(String travelId);

  List<TravelMaterials> findByMaterialId(String materialId);

  @Modifying
  @Transactional
  @Query("UPDATE TravelMaterials tm SET tm.quantityReceived = :quantityReceived WHERE tm.id = :id")
  void updateQuantityReceived(
      @Param("id") String id, @Param("quantityReceived") Integer quantityReceived);

  void deleteByTravelId(String travelId);
}
