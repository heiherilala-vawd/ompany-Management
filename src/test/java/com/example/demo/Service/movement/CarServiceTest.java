package com.example.demo.Service.movement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.movement.Car;
import com.example.demo.model.movement.CarId;
import com.example.demo.model.movement.CarStatus;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.FuelType;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.CarRepository;
import com.example.demo.service.movement.CarService;
import com.example.demo.service.movement.EquipmentService;
import com.example.demo.service.movement.WarehouseService;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.validator.MovementValidator;
import java.time.Instant;
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

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

  @Mock private CarRepository carRepository;
  @Mock private EquipmentService equipmentService;
  @Mock private WarehouseService warehouseService;
  @Mock private ModificationUtils modificationUtils;
  @Mock private MovementValidator movementValidator;

  @InjectMocks private CarService carService;

  private User user;
  private Car car;

  @BeforeEach
  void setUp() {
    user = User.builder().id("user-1").email("admin@example.com").build();

    CarId carId = new CarId("equip-1", "wh-1");
    Equipment equipment = Equipment.builder().id("equip-1").name("Test Equipment").build();
    Warehouse warehouse = Warehouse.builder().id("wh-1").name("Test Warehouse").build();

    car =
        Car.builder()
            .id(carId)
            .equipment(equipment)
            .warehouse(warehouse)
            .licensePlate("AB-123-CD")
            .fuelType(FuelType.DIESEL)
            .status(CarStatus.AVAILABLE)
            .brand("Toyota")
            .model("Hilux")
            .year(2020)
            .color("Blanc")
            .mileage(50000)
            .build();
  }

  @Test
  void findById_ShouldReturnCar_WhenExists() {
    when(carRepository.findByEquipment_IdAndWarehouse_Id("equip-1", "wh-1"))
        .thenReturn(Optional.of(car));

    Optional<Car> result = carService.findById("equip-1", "wh-1");

    assertThat(result).isPresent();
    assertThat(result.get().getId().getEquipmentId()).isEqualTo("equip-1");
    assertThat(result.get().getId().getWarehouseId()).isEqualTo("wh-1");
    verify(carRepository).findByEquipment_IdAndWarehouse_Id("equip-1", "wh-1");
  }

  @Test
  void findById_ShouldReturnEmpty_WhenNotExists() {
    when(carRepository.findByEquipment_IdAndWarehouse_Id("unknown", "unknown"))
        .thenReturn(Optional.empty());

    Optional<Car> result = carService.findById("unknown", "unknown");

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_ShouldReturnPagedCars() {
    PageFromOne page = new PageFromOne("1");
    BoundedPageSize pageSize = new BoundedPageSize("10");
    Page<Car> expectedPage = new PageImpl<>(List.of(car));

    when(carRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

    Page<Car> result = carService.findAll(page, pageSize);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getId().getEquipmentId()).isEqualTo("equip-1");
  }

  @Test
  void createOrUpdateAll_ShouldCreateNewCar_WhenNotExists() {
    List<Car> toSave = List.of(car);

    doNothing().when(movementValidator).validateCars(anyList());
    doNothing().when(movementValidator).validateCarCreation(any(), any());
    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    doNothing().when(modificationUtils).createOrUpdateModel(any(), any(), any(), any());
    when(carRepository.findById(car.getId())).thenReturn(Optional.empty());
    when(equipmentService.createOrUpdateAll(anyList())).thenReturn(List.of(car.getEquipment()));
    when(warehouseService.createOrUpdateAll(anyList())).thenReturn(List.of(car.getWarehouse()));
    when(carRepository.saveAll(anyList())).thenReturn(List.of(car));

    List<Car> result = carService.createOrUpdateAll(toSave);

    assertThat(result).hasSize(1);
    verify(equipmentService).createOrUpdateAll(anyList());
    verify(warehouseService).createOrUpdateAll(anyList());
    verify(carRepository).saveAll(toSave);
  }

  @Test
  void createOrUpdateAll_ShouldUpdateExistingCar_WhenExists() {
    Car existingCar =
        Car.builder()
            .id(car.getId())
            .equipment(car.getEquipment())
            .warehouse(car.getWarehouse())
            .licensePlate("OLD-PLATE")
            .fuelType(FuelType.DIESEL)
            .status(CarStatus.ON_MISSION)
            .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
            .build();

    when(carRepository.findById(car.getId())).thenReturn(Optional.of(existingCar));
    doNothing().when(movementValidator).validateCars(anyList());
    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    doNothing().when(modificationUtils).createOrUpdateModel(any(), any(), any(), any());
    when(equipmentService.createOrUpdateAll(anyList())).thenReturn(List.of(car.getEquipment()));
    when(warehouseService.createOrUpdateAll(anyList())).thenReturn(List.of(car.getWarehouse()));
    when(carRepository.saveAll(anyList())).thenReturn(List.of(car));

    List<Car> result = carService.createOrUpdateAll(List.of(car));

    assertThat(result).hasSize(1);
    verify(equipmentService).createOrUpdateAll(anyList());
    verify(warehouseService).createOrUpdateAll(anyList());
    verify(carRepository).saveAll(anyList());
    verify(movementValidator, never()).validateCarCreation(any(), any());
  }

  @Test
  void deleteById_ShouldDeleteCar() {
    doNothing().when(carRepository).deleteById(any(CarId.class));

    carService.deleteById("equip-1", "wh-1");

    verify(carRepository).deleteById(new CarId("equip-1", "wh-1"));
  }
}
