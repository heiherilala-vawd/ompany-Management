package com.example.demo.endpoint.rest.mapper.hr;

import com.example.demo.client.model.CrupdateLeave;
import com.example.demo.client.model.Leave;
import com.example.demo.client.model.LeaveStatus;
import com.example.demo.endpoint.rest.mapper.EnumMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.UserService;
import com.example.demo.service.hr.LeaveTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LeaveMapper {

  private final UserService userService;
  private final LeaveTypeService leaveTypeService;

  public com.example.demo.model.hr.Leave toDomain(CrupdateLeave rest) {
    if (rest == null) return null;

    return com.example.demo.model.hr.Leave.builder()
        .id(rest.getId())
        .user(rest.getUserId() != null ? userService.getById(rest.getUserId()) : null)
        .leaveType(
            rest.getLeaveTypeId() != null
                ? leaveTypeService.findById(rest.getLeaveTypeId()).orElse(null)
                : null)
        .startDate(rest.getStartDate())
        .endDate(rest.getEndDate())
        .durationDays(rest.getDurationDays())
        .status(
            EnumMapper.mapEnum(rest.getStatus(), com.example.demo.model.hr.Leave.LeaveStatus.class))
        .reason(rest.getReason())
        .comment(rest.getComment())
        .build();
  }

  public Leave toRestLeave(com.example.demo.model.hr.Leave domain) {
    if (domain == null) return null;

    Leave rest = new Leave();
    rest.setId(domain.getId());
    if (domain.getUser() != null) {
      rest.setUser(
          new com.example.demo.client.model.CrupdateUser()
              .id(domain.getUser().getId())
              .firstName(domain.getUser().getFirstName())
              .lastName(domain.getUser().getLastName())
              .email(domain.getUser().getEmail()));
    }
    if (domain.getLeaveType() != null) {
      com.example.demo.client.model.LeaveType restLeaveType =
          new com.example.demo.client.model.LeaveType();
      restLeaveType.setId(domain.getLeaveType().getId());
      restLeaveType.setName(domain.getLeaveType().getName());
      restLeaveType.setColor(domain.getLeaveType().getColor());
      rest.setLeaveType(restLeaveType);
    }
    rest.setStartDate(domain.getStartDate());
    rest.setEndDate(domain.getEndDate());
    rest.setDurationDays(domain.getDurationDays());
    rest.setStatus(EnumMapper.mapEnum(domain.getStatus(), LeaveStatus.class));
    rest.setReason(domain.getReason());
    if (domain.getApprovedBy() != null) {
      rest.setApprovedBy(
          new com.example.demo.client.model.CrupdateUser()
              .id(domain.getApprovedBy().getId())
              .firstName(domain.getApprovedBy().getFirstName())
              .lastName(domain.getApprovedBy().getLastName())
              .email(domain.getApprovedBy().getEmail()));
    }
    rest.setApprovedAt(domain.getApprovedAt());
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);

    return rest;
  }

  public CrupdateLeave toRestCrupdateLeave(com.example.demo.model.hr.Leave domain) {
    if (domain == null) return null;

    return new CrupdateLeave()
        .id(domain.getId())
        .userId(domain.getUser() != null ? domain.getUser().getId() : null)
        .leaveTypeId(domain.getLeaveType() != null ? domain.getLeaveType().getId() : null)
        .startDate(domain.getStartDate())
        .endDate(domain.getEndDate())
        .durationDays(domain.getDurationDays())
        .status(EnumMapper.mapEnum(domain.getStatus(), LeaveStatus.class))
        .reason(domain.getReason())
        .comment(domain.getComment());
  }
}
