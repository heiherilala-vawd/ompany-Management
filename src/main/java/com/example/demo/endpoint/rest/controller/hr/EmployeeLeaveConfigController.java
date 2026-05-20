package com.example.demo.endpoint.rest.controller.hr;

import com.example.demo.client.model.CrupdateEmployeeLeaveConfig;
import com.example.demo.client.model.EmployeeLeaveConfig;
import com.example.demo.endpoint.rest.mapper.hr.EmployeeLeaveConfigMapper;
import com.example.demo.model.exception.NotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class EmployeeLeaveConfigController {

  private final com.example.demo.service.hr.EmployeeLeaveConfigService employeeLeaveConfigService;
  private final EmployeeLeaveConfigMapper employeeLeaveConfigMapper;

  @GetMapping("/companies/{comp_id}/leave_configs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<EmployeeLeaveConfig> getEmployeeLeaveConfigs(@PathVariable String comp_id) {
    return employeeLeaveConfigService.findByCompanyId(comp_id).stream()
        .map(employeeLeaveConfigMapper::toRestEmployeeLeaveConfig)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/leave_configs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<EmployeeLeaveConfig> crupdateEmployeeLeaveConfigs(
      @PathVariable String comp_id, @RequestBody List<CrupdateEmployeeLeaveConfig> toWrite) {
    List<com.example.demo.model.hr.EmployeeLeaveConfig> saved =
        employeeLeaveConfigService.createOrUpdateAll(
            toWrite.stream().map(dto -> employeeLeaveConfigMapper.toDomain(dto, comp_id)).toList());
    return saved.stream().map(employeeLeaveConfigMapper::toRestEmployeeLeaveConfig).toList();
  }

  @GetMapping("/companies/{comp_id}/leave_configs/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public EmployeeLeaveConfig getEmployeeLeaveConfigById(
      @PathVariable String comp_id, @PathVariable String id) {
    return employeeLeaveConfigMapper.toRestEmployeeLeaveConfig(
        employeeLeaveConfigService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("EmployeeLeaveConfig " + id + " not found")));
  }
}
