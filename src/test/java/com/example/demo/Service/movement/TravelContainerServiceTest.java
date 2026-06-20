package com.example.demo.Service.movement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.criteria.TravelContainerCriteria;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.TravelContainer;
import com.example.demo.repository.movement.TravelContainerRepository;
import com.example.demo.service.movement.TravelContainerService;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.validator.MovementValidator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class TravelContainerServiceTest {

  @Mock private TravelContainerRepository travelContainerRepository;
  @Mock private ModificationUtils modificationUtils;
  @Mock private MovementValidator movementValidator;

  @InjectMocks private TravelContainerService travelContainerService;

  private User user;
  private TravelContainer container;
  private TravelExpense travel;

  @BeforeEach
  void setUp() {
    user = User.builder().id("user-1").email("admin@example.com").build();
    travel = TravelExpense.builder().id("travel-1").build();
    container =
        TravelContainer.builder()
            .id("container-1")
            .travel(travel)
            .name("Blue Box")
            .description("Grande boîte plastique")
            .build();
  }

  @Test
  void findById_ShouldReturnContainer_WhenExists() {
    when(travelContainerRepository.findById("container-1")).thenReturn(Optional.of(container));

    Optional<TravelContainer> result = travelContainerService.findById("container-1");

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo("container-1");
    assertThat(result.get().getName()).isEqualTo("Blue Box");
    verify(travelContainerRepository).findById("container-1");
  }

  @Test
  void findById_ShouldReturnEmpty_WhenNotExists() {
    when(travelContainerRepository.findById("unknown")).thenReturn(Optional.empty());

    Optional<TravelContainer> result = travelContainerService.findById("unknown");

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_ShouldReturnPagedContainers() {
    PageFromOne page = new PageFromOne("1");
    BoundedPageSize pageSize = new BoundedPageSize("10");
    Page<TravelContainer> expectedPage = new PageImpl<>(List.of(container));

    when(travelContainerRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(expectedPage);

    Page<TravelContainer> result =
        travelContainerService.findAll(page, pageSize, new TravelContainerCriteria());

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getId()).isEqualTo("container-1");
  }

  @Test
  void createOrUpdateAll_ShouldCreateNewContainer_WhenNotExists() {
    List<TravelContainer> toSave = List.of(container);

    doNothing().when(movementValidator).validateTravelContainers(anyList());
    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    doNothing().when(modificationUtils).createOrUpdateModel(any(), any(), any(), any());
    when(travelContainerRepository.findById("container-1")).thenReturn(Optional.empty());
    when(travelContainerRepository.saveAll(anyList())).thenReturn(List.of(container));

    List<TravelContainer> result = travelContainerService.createOrUpdateAll(toSave);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo("container-1");
    verify(travelContainerRepository).saveAll(toSave);
  }

  @Test
  void createOrUpdateAll_ShouldUpdateExistingContainer_WhenExists() {
    TravelContainer existing =
        TravelContainer.builder()
            .id("container-1")
            .travel(travel)
            .name("Old Name")
            .build();

    doNothing().when(movementValidator).validateTravelContainers(anyList());
    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    doNothing().when(modificationUtils).createOrUpdateModel(any(), any(), any(), any());
    when(travelContainerRepository.findById("container-1")).thenReturn(Optional.of(existing));
    when(travelContainerRepository.saveAll(anyList())).thenReturn(List.of(container));

    List<TravelContainer> result = travelContainerService.createOrUpdateAll(List.of(container));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Blue Box");
    verify(travelContainerRepository).saveAll(anyList());
  }

  @Test
  void deleteById_ShouldDeleteContainer() {
    doNothing().when(travelContainerRepository).deleteById("container-1");

    travelContainerService.deleteById("container-1");

    verify(travelContainerRepository).deleteById("container-1");
  }
}
