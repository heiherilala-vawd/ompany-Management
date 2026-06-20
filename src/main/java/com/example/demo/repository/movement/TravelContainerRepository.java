package com.example.demo.repository.movement;

import com.example.demo.model.movement.TravelContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TravelContainerRepository
    extends JpaRepository<TravelContainer, String>, JpaSpecificationExecutor<TravelContainer> {}
