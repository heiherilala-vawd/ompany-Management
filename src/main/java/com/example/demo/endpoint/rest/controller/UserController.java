package com.example.demo.endpoint.rest.controller;

import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.User;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.UserMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.UserCriteria;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;

  @PutMapping("/users")
  @PreAuthorize("hasAnyRole(\"ADMIN\", \"ADMINISTRATION\")\n")
  public List<User> crupdateUsers(
      @RequestParam(name = "company_id") String companyId,
      @Valid @RequestBody List<CrupdateUser> toWrite) {
    List<com.example.demo.model.User> saved =
        userService.updateExistingUsers(
            toWrite.stream().map(u -> userMapper.toDomain(u, companyId)).toList());
    return saved.stream().map(userMapper::toRestUser).toList();
  }

  @GetMapping("/users/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_WORKER') or #id == authentication.principal.id")
  public User getUserById(@PathVariable String id) {
    return userMapper.toRestUser(userService.getById(id));
  }

  @GetMapping("/users")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public PaginatedResponse getUsers(
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "company_id", required = false) String companyId,
      @RequestParam(name = "first_name", required = false, defaultValue = "") String firstName,
      @RequestParam(name = "last_name", required = false, defaultValue = "") String lastName,
      @RequestParam(name = "email", required = false, defaultValue = "") String email,
      @RequestParam(name = "role", required = false) com.example.demo.model.User.Role role,
      @RequestParam(name = "without_leave_config", required = false) Boolean withoutLeaveConfig) {
    UserCriteria criteria = new UserCriteria();
    criteria.setCompanyId(companyId);
    criteria.setFirstName(firstName);
    criteria.setLastName(lastName);
    criteria.setEmail(email);
    criteria.setRole(role);
    criteria.setWithoutLeaveConfig(withoutLeaveConfig);

    var result = userService.getUsers(page, pageSize, criteria);
    var list = result.stream().map(userMapper::toRestUser).collect(Collectors.toList());
    return new PaginatedResponse(list, (int) result.getTotalElements());
  }

  @DeleteMapping("/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteUserById(@PathVariable String id) {
    userService.deleteById(id);
  }
}
