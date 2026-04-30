package com.example.demo.endpoint.rest.controller;

import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.User;
import com.example.demo.endpoint.rest.mapper.UserMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.UserCriteria;
import com.example.demo.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;

  @PutMapping(value = "/users")
  @PreAuthorize("hasAnyRole(\"ADMIN\", \"ADMINISTRATION\")\n")
  public List<User> crupdateUsers(@RequestBody List<CrupdateUser> toWrite) {
    List<com.example.demo.model.User> saved =
        userService.updateExistingUsers(toWrite.stream().map(userMapper::toDomain).toList());
    return saved.stream().map(userMapper::toRestUser).toList();
  }

  @GetMapping("/users/{userId}")
  @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.id")
  public User getUserById(@PathVariable String userId) {
    return userMapper.toRestUser(userService.getById(userId));
  }

  @GetMapping("/users")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<User> getUsers(
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "first_name", required = false, defaultValue = "") String firstName,
      @RequestParam(name = "last_name", required = false, defaultValue = "") String lastName,
      @RequestParam(name = "email", required = false, defaultValue = "") String email,
      @RequestParam(name = "role", required = false) com.example.demo.model.User.Role role) {
    UserCriteria criteria = new UserCriteria();
    criteria.setFirstName(firstName);
    criteria.setLastName(lastName);
    criteria.setEmail(email);
    criteria.setRole(role);

    return userService.getUsers(page, pageSize, criteria).stream()
        .map(userMapper::toRestUser)
        .collect(Collectors.toList());
  }

  @DeleteMapping("/users/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteUserById(@PathVariable String id) {
    userService.deleteById(id);
  }
}
