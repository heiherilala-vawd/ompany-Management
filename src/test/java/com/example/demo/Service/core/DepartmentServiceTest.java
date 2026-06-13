package com.example.demo.Service.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.core.Department;
import com.example.demo.repository.core.DepartmentRepository;
import com.example.demo.service.core.DepartmentService;
import com.example.demo.service.utils.ModificationUtils;
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
class DepartmentServiceTest {

  @Mock private DepartmentRepository departmentRepository;
  @Mock private ModificationUtils modificationUtils;

  @InjectMocks private DepartmentService departmentService;

  private Department department;
  private User user;
  private String departmentId;

  @BeforeEach
  void setUp() {
    departmentId = "dept-123";
    user = User.builder().id("user-1").email("admin@example.com").build();

    department =
        Department.builder()
            .id(departmentId)
            .name("Engineering")
            .description("Engineering department")
            .comment("Initial")
            .build();
  }

  @Test
  void findById_ShouldReturnDepartment_WhenExists() {
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

    Optional<Department> result = departmentService.findById(departmentId);

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(departmentId);
    assertThat(result.get().getName()).isEqualTo("Engineering");
    verify(departmentRepository).findById(departmentId);
  }

  @Test
  void findById_ShouldReturnEmpty_WhenNotExists() {
    when(departmentRepository.findById("unknown")).thenReturn(Optional.empty());

    Optional<Department> result = departmentService.findById("unknown");

    assertThat(result).isEmpty();
    verify(departmentRepository).findById("unknown");
  }

  @Test
  void findById_ShouldReturnEmpty_WhenIdIsNull() {
    when(departmentRepository.findById(null)).thenReturn(Optional.empty());

    Optional<Department> result = departmentService.findById(null);

    assertThat(result).isEmpty();
    verify(departmentRepository).findById(null);
  }

  @Test
  void findAll_ShouldReturnPagedDepartments() {
    PageFromOne page = new PageFromOne("1");
    BoundedPageSize pageSize = new BoundedPageSize("10");
    Page<Department> expectedPage = new PageImpl<>(List.of(department));

    when(departmentRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

    Page<Department> result = departmentService.findAll(page, pageSize);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getId()).isEqualTo(departmentId);
    verify(departmentRepository).findAll(any(Pageable.class));
  }

  @Test
  void findAll_ShouldReturnEmptyPage_WhenNoDepartments() {
    PageFromOne page = new PageFromOne("1");
    BoundedPageSize pageSize = new BoundedPageSize("10");
    Page<Department> expectedPage = Page.empty();

    when(departmentRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

    Page<Department> result = departmentService.findAll(page, pageSize);

    assertThat(result.getContent()).isEmpty();
    verify(departmentRepository).findAll(any(Pageable.class));
  }

  @Test
  void findAll_ShouldUseDefaults_WhenNullParams() {
    Page<Department> expectedPage = new PageImpl<>(List.of(department));

    when(departmentRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

    Page<Department> result = departmentService.findAll(null, null);

    assertThat(result.getContent()).hasSize(1);
    verify(departmentRepository).findAll(any(Pageable.class));
  }

  @Test
  void createOrUpdateAll_ShouldCreateNewDepartment_WhenIdIsNull() {
    Department newDept =
        Department.builder().id(null).name("New Dept").description("New department").build();
    List<Department> toSave = List.of(newDept);

    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    doNothing().when(modificationUtils).createOrUpdateModel(any(), any(), any(), any());
    when(departmentRepository.saveAll(anyList())).thenReturn(List.of(newDept));

    List<Department> result = departmentService.createOrUpdateAll(toSave);

    assertThat(result).hasSize(1);
    verify(departmentRepository, never()).findById(anyString());
    verify(modificationUtils).createOrUpdateModel(eq(newDept), isNull(), isNull(), eq(user));
    verify(departmentRepository).saveAll(toSave);
  }

  @Test
  void createOrUpdateAll_ShouldCreateNewDepartment_WhenIdIsNotFound() {
    Department newDept = Department.builder().id("new-id").name("New Dept").build();
    List<Department> toSave = List.of(newDept);

    when(departmentRepository.findById("new-id")).thenReturn(Optional.empty());
    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    doNothing().when(modificationUtils).createOrUpdateModel(any(), any(), any(), any());
    when(departmentRepository.saveAll(anyList())).thenReturn(List.of(newDept));

    List<Department> result = departmentService.createOrUpdateAll(toSave);

    assertThat(result).hasSize(1);
    verify(departmentRepository).findById("new-id");
    verify(modificationUtils).createOrUpdateModel(eq(newDept), isNull(), eq("new-id"), eq(user));
    verify(departmentRepository).saveAll(toSave);
  }

  @Test
  void createOrUpdateAll_ShouldUpdateExistingDepartment_WhenIdFound() {
    Department existing =
        Department.builder()
            .id(departmentId)
            .name("Engineering")
            .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
            .build();
    Department updated = Department.builder().id(departmentId).name("Engineering Updated").build();
    List<Department> toSave = List.of(updated);

    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existing));
    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    doNothing().when(modificationUtils).createOrUpdateModel(any(), any(), any(), any());
    when(departmentRepository.saveAll(anyList())).thenReturn(List.of(updated));

    List<Department> result = departmentService.createOrUpdateAll(toSave);

    assertThat(result).hasSize(1);
    verify(modificationUtils)
        .createOrUpdateModel(eq(updated), eq(existing), eq(departmentId), eq(user));
    verify(departmentRepository).saveAll(toSave);
  }

  @Test
  void createOrUpdateAll_ShouldHandleMultipleDepartments_MixedCreateAndUpdate() {
    Department newDept = Department.builder().id(null).name("New Dept").build();
    Department existing =
        Department.builder()
            .id(departmentId)
            .name("Engineering")
            .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
            .build();
    Department updated = Department.builder().id(departmentId).name("Engineering Updated").build();
    List<Department> toSave = List.of(newDept, updated);

    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    doNothing().when(modificationUtils).createOrUpdateModel(any(), any(), any(), any());
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existing));
    when(departmentRepository.saveAll(anyList())).thenReturn(List.of(newDept, updated));

    List<Department> result = departmentService.createOrUpdateAll(toSave);

    assertThat(result).hasSize(2);
    verify(departmentRepository, never()).findById(isNull());
    verify(departmentRepository).findById(departmentId);
    verify(modificationUtils, times(2)).createOrUpdateModel(any(), any(), any(), any());
    verify(departmentRepository).saveAll(toSave);
  }

  @Test
  void createOrUpdateAll_ShouldReturnEmptyList_WhenInputIsEmpty() {
    List<Department> result = departmentService.createOrUpdateAll(List.of());

    assertThat(result).isEmpty();
    verify(departmentRepository, never()).findById(anyString());
    verify(departmentRepository).saveAll(List.of());
  }

  @Test
  void createOrUpdateAll_ShouldHandleDepartmentWithoutDescription() {
    Department minimalDept = Department.builder().id(null).name("Minimal").build();
    List<Department> toSave = List.of(minimalDept);

    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    doNothing().when(modificationUtils).createOrUpdateModel(any(), any(), any(), any());
    when(departmentRepository.saveAll(anyList())).thenReturn(List.of(minimalDept));

    List<Department> result = departmentService.createOrUpdateAll(toSave);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Minimal");
    assertThat(result.get(0).getDescription()).isNull();
    verify(departmentRepository).saveAll(toSave);
  }

  @Test
  void deleteById_ShouldDelete_WhenExists() {
    doNothing().when(departmentRepository).deleteById(departmentId);

    departmentService.deleteById(departmentId);

    verify(departmentRepository).deleteById(departmentId);
  }

  @Test
  void deleteById_ShouldHandleNullId() {
    doNothing().when(departmentRepository).deleteById(null);

    departmentService.deleteById(null);

    verify(departmentRepository).deleteById(null);
  }
}
