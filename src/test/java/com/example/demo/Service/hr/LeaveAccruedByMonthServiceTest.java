package com.example.demo.Service.hr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.User;
import com.example.demo.model.hr.EmployeeLeaveConfig;
import com.example.demo.model.hr.LeaveAccruedByMonth;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.hr.LeaveAccruedByMonthRepository;
import com.example.demo.service.hr.LeaveAccruedByMonthService;
import com.example.demo.service.utils.ModificationUtils;
import java.math.BigDecimal;
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
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class LeaveAccruedByMonthServiceTest {

  @Mock private LeaveAccruedByMonthRepository repository;
  @Mock private UserRepository userRepository;
  @Mock private ModificationUtils modificationUtils;

  @InjectMocks private LeaveAccruedByMonthService service;

  private User user;
  private EmployeeLeaveConfig config;
  private LeaveAccruedByMonth entry;
  private LeaveAccruedByMonth existingEntry;
  private String userId;
  private String entryId;

  @BeforeEach
  void setUp() {
    userId = "user-001";
    entryId = "entry-001";

    config =
        EmployeeLeaveConfig.builder()
            .id("config-001")
            .vacationDaysPerMonth(new BigDecimal("2.5"))
            .build();

    user =
        User.builder()
            .id(userId)
            .email("user@test.com")
            .firstName("Test")
            .lastName("User")
            .role(User.Role.EMPLOYEE)
            .employeeLeaveConfig(config)
            .build();

    entry =
        LeaveAccruedByMonth.builder()
            .id(entryId)
            .user(user)
            .year(2026)
            .month(6)
            .accruedDays(new BigDecimal("2.5"))
            .build();

    existingEntry =
        LeaveAccruedByMonth.builder()
            .id(entryId)
            .user(user)
            .year(2026)
            .month(5)
            .accruedDays(new BigDecimal("2.0"))
            .build();
  }

  // ==================== TESTS POUR getAccruedDaysForYear ====================

  @Test
  void getAccruedDaysForYear_ShouldReturnSum_WhenDataExists() {
    when(repository.sumAccruedDaysByUserAndYear(userId, 2026))
        .thenReturn(Optional.of(new BigDecimal("12.5")));

    BigDecimal result = service.getAccruedDaysForYear(userId, 2026);

    assertThat(result).isEqualByComparingTo(new BigDecimal("12.5"));
    verify(repository).sumAccruedDaysByUserAndYear(userId, 2026);
  }

  @Test
  void getAccruedDaysForYear_ShouldReturnZero_WhenNoData() {
    when(repository.sumAccruedDaysByUserAndYear(userId, 2026))
        .thenReturn(Optional.empty());

    BigDecimal result = service.getAccruedDaysForYear(userId, 2026);

    assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    verify(repository).sumAccruedDaysByUserAndYear(userId, 2026);
  }

  // ==================== TESTS POUR findByUserIdAndYear ====================

  @Test
  void findByUserIdAndYear_ShouldReturnList() {
    List<LeaveAccruedByMonth> expected = List.of(entry);
    when(repository.findByUserIdAndYear(userId, 2026)).thenReturn(expected);

    List<LeaveAccruedByMonth> result = service.findByUserIdAndYear(userId, 2026);

    assertThat(result).hasSize(1).containsExactly(entry);
    verify(repository).findByUserIdAndYear(userId, 2026);
  }

  @Test
  void findByUserIdAndYear_ShouldReturnEmptyList_WhenNoData() {
    when(repository.findByUserIdAndYear(userId, 2026)).thenReturn(List.of());

    List<LeaveAccruedByMonth> result = service.findByUserIdAndYear(userId, 2026);

    assertThat(result).isEmpty();
    verify(repository).findByUserIdAndYear(userId, 2026);
  }

  // ==================== TESTS POUR createOrUpdateAll ====================

  @Test
  void createOrUpdateAll_ShouldCreateNewEntries_WhenIdNotFound() {
    when(repository.findById(entryId)).thenReturn(Optional.empty());
    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    when(repository.saveAll(List.of(entry))).thenReturn(List.of(entry));

    List<LeaveAccruedByMonth> result = service.createOrUpdateAll(List.of(entry));

    assertThat(result).hasSize(1).containsExactly(entry);
    verify(repository).findById(entryId);
    verify(modificationUtils).createOrUpdateModel(entry, null, entryId, user);
    verify(repository).saveAll(List.of(entry));
  }

  @Test
  void createOrUpdateAll_ShouldUpdateExistingEntries_WhenIdFound() {
    when(repository.findById(entryId)).thenReturn(Optional.of(existingEntry));
    when(modificationUtils.takePrimaryUser()).thenReturn(user);
    when(repository.saveAll(List.of(entry))).thenReturn(List.of(entry));

    List<LeaveAccruedByMonth> result = service.createOrUpdateAll(List.of(entry));

    assertThat(result).hasSize(1).containsExactly(entry);
    verify(repository).findById(entryId);
    verify(modificationUtils).createOrUpdateModel(entry, existingEntry, entryId, user);
    verify(repository).saveAll(List.of(entry));
  }

  @Test
  void createOrUpdateAll_ShouldHandleMultipleEntries() {
    LeaveAccruedByMonth entry2 =
        LeaveAccruedByMonth.builder()
            .id("entry-002")
            .user(user)
            .year(2026)
            .month(7)
            .accruedDays(new BigDecimal("2.5"))
            .build();

    when(repository.findById("entry-001")).thenReturn(Optional.empty());
    when(repository.findById("entry-002")).thenReturn(Optional.empty());
    when(modificationUtils.takePrimaryUser()).thenReturn(user).thenReturn(user);
    when(repository.saveAll(List.of(entry, entry2))).thenReturn(List.of(entry, entry2));

    List<LeaveAccruedByMonth> result = service.createOrUpdateAll(List.of(entry, entry2));

    assertThat(result).hasSize(2);
    verify(repository, times(2)).findById(anyString());
    verify(modificationUtils, times(2)).createOrUpdateModel(any(), any(), anyString(), eq(user));
    verify(repository).saveAll(anyList());
  }

  // ==================== TESTS POUR accrueForCurrentMonth ====================

  @Test
  void accrueForCurrentMonth_ShouldCreateAccruals_ForUsersWithConfig() {
    String userWithConfigId = "user-002";
    User userWithConfig =
        User.builder()
            .id(userWithConfigId)
            .email("user2@test.com")
            .role(User.Role.EMPLOYEE)
            .employeeLeaveConfig(config)
            .build();

    User userWithoutConfig =
        User.builder()
            .id("user-003")
            .email("user3@test.com")
            .role(User.Role.EMPLOYEE)
            .build();

    Page<User> userPage =
        new PageImpl<>(List.of(user, userWithConfig, userWithoutConfig));

    int year = 2026;
    int month = 6;

    when(userRepository.findAll(PageRequest.of(0, 100))).thenReturn(userPage);
    when(repository.existsByUserIdAndYearAndMonth(user.getId(), year, month)).thenReturn(true);
    when(repository.existsByUserIdAndYearAndMonth(userWithConfig.getId(), year, month))
        .thenReturn(false);
    when(repository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    service.accrueForCurrentMonth();

    verify(userRepository).findAll(PageRequest.of(0, 100));
    verify(repository).existsByUserIdAndYearAndMonth(user.getId(), year, month);
    verify(repository).existsByUserIdAndYearAndMonth(userWithConfig.getId(), year, month);
    verify(repository)
        .saveAll(
            argThat(
                list -> {
                  @SuppressWarnings("unchecked")
                  List<LeaveAccruedByMonth> accruals = (List<LeaveAccruedByMonth>) list;
                  return accruals.size() == 1
                      && accruals.get(0).getUser().getId().equals(userWithConfigId)
                      && accruals.get(0).getYear() == year
                      && accruals.get(0).getMonth() == month
                      && accruals.get(0).getAccruedDays().equals(new BigDecimal("2.5"));
                }));
  }

  @Test
  void accrueForCurrentMonth_ShouldSkipUser_WhenAlreadyAccrued() {
    Page<User> userPage = new PageImpl<>(List.of(user));
    int year = 2026;
    int month = 6;

    when(userRepository.findAll(PageRequest.of(0, 100))).thenReturn(userPage);
    when(repository.existsByUserIdAndYearAndMonth(user.getId(), year, month)).thenReturn(true);

    service.accrueForCurrentMonth();

    verify(repository, never()).saveAll(anyList());
  }

  @Test
  void accrueForCurrentMonth_ShouldSkipUser_WhenNoConfig() {
    User userWithoutConfig =
        User.builder()
            .id("user-no-config")
            .email("noconfig@test.com")
            .role(User.Role.EMPLOYEE)
            .build();

    Page<User> userPage = new PageImpl<>(List.of(userWithoutConfig));
    int year = 2026;
    int month = 6;

    when(userRepository.findAll(PageRequest.of(0, 100))).thenReturn(userPage);

    service.accrueForCurrentMonth();

    verify(repository, never())
        .existsByUserIdAndYearAndMonth(userWithoutConfig.getId(), year, month);
    verify(repository, never()).saveAll(anyList());
  }

  @Test
  void accrueForCurrentMonth_ShouldHandleMultiplePages() {
    Page<User> page1 =
        new PageImpl<>(List.of(user), PageRequest.of(0, 100), 150);

    Page<User> page2 =
        new PageImpl<>(List.of(), PageRequest.of(1, 100), 150);

    int year = 2026;
    int month = 6;

    when(userRepository.findAll(PageRequest.of(0, 100))).thenReturn(page1);
    when(userRepository.findAll(PageRequest.of(1, 100))).thenReturn(page2);
    when(repository.existsByUserIdAndYearAndMonth(user.getId(), year, month)).thenReturn(false);
    when(repository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    service.accrueForCurrentMonth();

    verify(userRepository).findAll(PageRequest.of(0, 100));
    verify(userRepository).findAll(PageRequest.of(1, 100));
    verify(repository).saveAll(anyList());
  }
}
