package com.example.demo.endpoint.rest.controller.hr;

import com.example.demo.client.model.CrupdateEmployeeLeaveConfig;
import com.example.demo.client.model.EmployeeLeaveConfig;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.hr.EmployeeLeaveConfigMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.utils.PageUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class EmployeeLeaveConfigController {

  private final com.example.demo.service.hr.EmployeeLeaveConfigService employeeLeaveConfigService;
  private final EmployeeLeaveConfigMapper employeeLeaveConfigMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/leave_configs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getEmployeeLeaveConfigs(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    var result = employeeLeaveConfigService.findByCompanyId(companyId, pageable);
    var list = result.stream().map(employeeLeaveConfigMapper::toRestEmployeeLeaveConfig).toList();
    return new PaginatedResponse(list, (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/leave_configs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<EmployeeLeaveConfig> crupdateEmployeeLeaveConfigs(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateEmployeeLeaveConfig> toWrite) {
    List<com.example.demo.model.hr.EmployeeLeaveConfig> saved =
        employeeLeaveConfigService.createOrUpdateAll(
            toWrite.stream()
                .map(dto -> employeeLeaveConfigMapper.toDomain(dto, companyId))
                .toList());
    return saved.stream().map(employeeLeaveConfigMapper::toRestEmployeeLeaveConfig).toList();
  }

  @GetMapping("/users/{userId}/companies/{companyId}/leave_configs/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public EmployeeLeaveConfig getEmployeeLeaveConfigById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return employeeLeaveConfigMapper.toRestEmployeeLeaveConfig(
        employeeLeaveConfigService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("EmployeeLeaveConfig " + id + " not found")));
  }
}
