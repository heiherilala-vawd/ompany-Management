package com.example.demo.endpoint.rest.controller.hr;

import com.example.demo.client.model.CrupdateLeaveType;
import com.example.demo.client.model.LeaveType;
import com.example.demo.endpoint.rest.mapper.hr.LeaveTypeMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class LeaveTypeController {

  private final com.example.demo.service.hr.LeaveTypeService leaveTypeService;
  private final LeaveTypeMapper leaveTypeMapper;

  @GetMapping("/companies/{comp_id}/leave-types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<LeaveType> getLeaveTypes(
      @PathVariable String comp_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return leaveTypeService.findAll(page, pageSize, comp_id).stream()
        .map(leaveTypeMapper::toRestLeaveType)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/leave-types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<LeaveType> crupdateLeaveTypes(
      @PathVariable String comp_id, @RequestBody List<CrupdateLeaveType> toWrite) {
    List<com.example.demo.model.hr.LeaveType> saved =
        leaveTypeService.createOrUpdateAll(
            toWrite.stream().map(leaveTypeMapper::toDomain).toList());
    return saved.stream().map(leaveTypeMapper::toRestLeaveType).toList();
  }
}
