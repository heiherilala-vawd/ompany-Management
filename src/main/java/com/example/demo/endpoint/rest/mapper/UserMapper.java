package com.example.demo.endpoint.rest.mapper;

import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.Role;
import com.example.demo.client.model.Sex;
import com.example.demo.client.model.User;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {

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

  public com.example.demo.model.User toDomain(CrupdateUser restUser) {
    return com.example.demo.model.User.builder()
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
        .build();
  }

  public User toRestUser(com.example.demo.model.User domainUser) {
    User restUser = new User();
    restUser.setId(domainUser.getId());
    restUser.setRole(EnumMapper.mapEnum(domainUser.getRole(), Role.class));
    restUser.setLastName(domainUser.getLastName());
    restUser.setFirstName(domainUser.getFirstName());
    restUser.setSex(EnumMapper.mapEnum(domainUser.getSex(), Sex.class));
    restUser.setEmail(domainUser.getEmail());
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

  public List<com.example.demo.model.User> toDomain(List<User> restUsers) {
    return restUsers.stream().map(this::toDomain).collect(java.util.stream.Collectors.toList());
  }
}
