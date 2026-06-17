package com.example.demo.endpoint.rest.mapper;

import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.Role;
import com.example.demo.client.model.Sex;
import com.example.demo.client.model.User;
import com.example.demo.endpoint.rest.mapper.core.DepartmentMapper;
import com.example.demo.model.Company;
import com.example.demo.model.core.Department;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {

  private final CompanyMapper companyMapper;
  private final DepartmentMapper departmentMapper;

  public com.example.demo.model.User toDomain(User restUser) {
    return com.example.demo.model.User.builder()
        .id(restUser.getId())
        .role(EnumMapper.mapEnum(restUser.getRole(), com.example.demo.model.User.Role.class))
        .firstName(restUser.getFirstName())
        .lastName(restUser.getLastName())
        .sex(EnumMapper.mapEnum(restUser.getSex(), com.example.demo.model.User.Sex.class))
        .email(restUser.getEmail())
        .comment(restUser.getComment())
        .build();
  }

  public com.example.demo.model.User toDomain(CrupdateUser restUser, String companyId) {
    var builder =
        com.example.demo.model.User.builder()
            .id(restUser.getId())
            .role(
                restUser.getRole() != null
                    ? com.example.demo.model.User.Role.valueOf(restUser.getRole().name())
                    : com.example.demo.model.User.Role.EMPLOYEE)
            .firstName(restUser.getFirstName())
            .lastName(restUser.getLastName())
            .sex(EnumMapper.mapEnum(restUser.getSex(), com.example.demo.model.User.Sex.class))
            .email(restUser.getEmail())
            .password(restUser.getPassword())
            .comment(restUser.getComment())
            .birthDate(restUser.getBirthDate())
            .manager(
                restUser.getManagerId() != null
                    ? com.example.demo.model.User.builder().id(restUser.getManagerId()).build()
                    : null)
            .department(
                restUser.getDepartmentId() != null
                    ? Department.builder().id(restUser.getDepartmentId()).build()
                    : null);
    if (companyId != null) {
      builder.companies(Set.of(Company.builder().id(companyId).build()));
    }
    return builder.build();
  }

  public User toRestUser(com.example.demo.model.User domainUser) {
    User restUser = new User();
    restUser.setId(domainUser.getId());
    restUser.setRole(EnumMapper.mapEnum(domainUser.getRole(), Role.class));
    restUser.setLastName(domainUser.getLastName());
    restUser.setFirstName(domainUser.getFirstName());
    restUser.setSex(EnumMapper.mapEnum(domainUser.getSex(), Sex.class));
    restUser.setEmail(domainUser.getEmail());
    if (domainUser.getCompanies() != null && !domainUser.getCompanies().isEmpty()) {
      restUser.setCompany(companyMapper.toRestCrupdateCompany(domainUser.getCompanies().iterator().next()));
    }
    restUser.setBirthDate(domainUser.getBirthDate());
    if (domainUser.getManager() != null) {
      restUser.setManager(new CrupdateUser().id(domainUser.getManager().getId()));
    }
    restUser.setDepartment(departmentMapper.toRestCrupdateDepartment(domainUser.getDepartment()));
    RestAuditMapperUtils.mapAuditFields(
        domainUser,
        restUser::setCreatedAt,
        restUser::setUpdatedAt,
        restUser::setComment,
        restUser::setCreatedBy,
        restUser::setUpdatedBy);

    return restUser;
  }

  public List<User> toRestUsers(List<com.example.demo.model.User> domainUsers) {
    return domainUsers.stream().map(this::toRestUser).collect(java.util.stream.Collectors.toList());
  }

  public CrupdateUser toRestCrupdateUser(com.example.demo.model.User domainUser) {
    if (domainUser == null) return null;
    CrupdateUser rest = new CrupdateUser();
    rest.setId(domainUser.getId());
    rest.setRole(EnumMapper.mapEnum(domainUser.getRole(), Role.class));
    rest.setLastName(domainUser.getLastName());
    rest.setFirstName(domainUser.getFirstName());
    rest.setSex(EnumMapper.mapEnum(domainUser.getSex(), Sex.class));
    rest.setEmail(domainUser.getEmail());
    rest.setCompanyIds(
        domainUser.getCompanies() != null && !domainUser.getCompanies().isEmpty()
            ? domainUser.getCompanies().stream().map(Company::getId).toList()
            : null);
    rest.setBirthDate(domainUser.getBirthDate());
    return rest;
  }

  public List<com.example.demo.model.User> toDomain(List<User> restUsers) {
    return restUsers.stream().map(this::toDomain).collect(java.util.stream.Collectors.toList());
  }
}
