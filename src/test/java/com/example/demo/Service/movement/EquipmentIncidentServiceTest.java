package com.example.demo.Service.movement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.criteria.EquipmentIncidentCriteria;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.EquipmentIncident;
import com.example.demo.model.movement.IncidentType;
import com.example.demo.repository.movement.EquipmentIncidentRepository;
import com.example.demo.service.movement.EquipmentIncidentService;
import com.example.demo.service.utils.ModificationUtils;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class EquipmentIncidentServiceTest {

  @Mock private EquipmentIncidentRepository equipmentIncidentRepository;
  @Mock private ModificationUtils modificationUtils;

  @Captor private ArgumentCaptor<List<EquipmentIncident>> incidentListCaptor;

  private EquipmentIncidentService service;

  @BeforeEach
  void setUp() {
    service = new EquipmentIncidentService(equipmentIncidentRepository, modificationUtils);
  }

  @Test
  void findById_ShouldReturnIncident_WhenExists() {
    var incident = buildIncident("inc_001", IncidentType.DAMAGED);
    when(equipmentIncidentRepository.findById("inc_001")).thenReturn(Optional.of(incident));

    var result = service.findById("inc_001");

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo("inc_001");
    assertThat(result.get().getIncidentType()).isEqualTo(IncidentType.DAMAGED);
  }

  @Test
  void findById_ShouldReturnEmpty_WhenNotFound() {
    when(equipmentIncidentRepository.findById("unknown")).thenReturn(Optional.empty());

    var result = service.findById("unknown");

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_ShouldReturnPage() {
    var incident = buildIncident("inc_001", IncidentType.LOST);
    Page<EquipmentIncident> page = new PageImpl<>(List.of(incident));
    when(equipmentIncidentRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(page);

    var result =
        service.findAll(
            new PageFromOne("1"), new BoundedPageSize("20"), new EquipmentIncidentCriteria());

    assertThat(result).hasSize(1);
    assertThat(result.getContent().getFirst().getId()).isEqualTo("inc_001");
  }

  @Test
  void findAll_ShouldFilterByEquipmentId() {
    Page<EquipmentIncident> page = new PageImpl<>(List.of());
    when(equipmentIncidentRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(page);

    var criteria = new EquipmentIncidentCriteria();
    criteria.setEquipmentId("eq_001");
    service.findAll(new PageFromOne("1"), new BoundedPageSize("20"), criteria);

    verify(equipmentIncidentRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void createOrUpdateAll_ShouldCreateNewIncident_WhenNotExists() {
    when(modificationUtils.takePrimaryUser()).thenReturn(new User());
    var incident = buildIncident("inc_001", IncidentType.DAMAGED);
    when(equipmentIncidentRepository.findById("inc_001")).thenReturn(Optional.empty());
    when(equipmentIncidentRepository.saveAll(anyList())).thenReturn(List.of(incident));

    var result = service.createOrUpdateAll(List.of(incident));

    assertThat(result).hasSize(1);
    verify(equipmentIncidentRepository).saveAll(incidentListCaptor.capture());
    assertThat(incidentListCaptor.getValue()).hasSize(1);
    assertThat(incidentListCaptor.getValue().getFirst().getId()).isEqualTo("inc_001");
  }

  @Test
  void createOrUpdateAll_ShouldUpdateExistingIncident() {
    when(modificationUtils.takePrimaryUser()).thenReturn(new User());
    var existing = buildIncident("inc_001", IncidentType.DAMAGED);
    existing.setLocation("old_location");
    var updated = buildIncident("inc_001", IncidentType.DAMAGED);
    updated.setLocation("new_location");

    when(equipmentIncidentRepository.findById("inc_001")).thenReturn(Optional.of(existing));
    when(equipmentIncidentRepository.saveAll(anyList())).thenReturn(List.of(updated));

    var result = service.createOrUpdateAll(List.of(updated));

    assertThat(result).hasSize(1);
    verify(modificationUtils).createOrUpdateModel(eq(updated), eq(existing), eq("inc_001"), any());
  }

  @Test
  void deleteById_ShouldDelete() {
    service.deleteById("inc_001");

    verify(equipmentIncidentRepository).deleteById("inc_001");
  }

  private static EquipmentIncident buildIncident(String id, IncidentType type) {
    return EquipmentIncident.builder()
        .id(id)
        .incidentType(type)
        .equipment(Equipment.builder().id("eq_001").name("Test").build())
        .build();
  }
}
