package com.example.demo.endpoint.rest.controller.hr;

import com.example.demo.client.model.CrupdateLeaveType;
import com.example.demo.client.model.LeaveType;
import com.example.demo.endpoint.rest.mapper.hr.LeaveTypeMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class LeaveTypeController {

  private final com.example.demo.service.hr.LeaveTypeService leaveTypeService;
  private final LeaveTypeMapper leaveTypeMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/leave_types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<LeaveType> getLeaveTypes(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return leaveTypeService.findAll(page, pageSize, companyId).stream()
        .map(leaveTypeMapper::toRestLeaveType)
        .toList();
  }

  @PutMapping("/users/{userId}/companies/{companyId}/leave_types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<LeaveType> crupdateLeaveTypes(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateLeaveType> toWrite) {
    List<com.example.demo.model.hr.LeaveType> saved =
        leaveTypeService.createOrUpdateAll(
            toWrite.stream().map(rest -> leaveTypeMapper.toDomain(rest, companyId)).toList());
    return saved.stream().map(leaveTypeMapper::toRestLeaveType).toList();
  }

  @GetMapping("/users/{userId}/companies/{companyId}/leave_types/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public LeaveType getLeaveTypeById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return leaveTypeMapper.toRestLeaveType(
        leaveTypeService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("LeaveType " + id + " not found")));
  }
}
