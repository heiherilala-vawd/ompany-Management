package com.example.demo.service;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.criteria.UserCriteria;
import com.example.demo.model.exception.ForbiddenException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.CoreValidator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
  private static final String DEFAULT_PASSWORD = "0000";

  private final UserRepository repository;
  private final ModificationUtils modificationUtils;
  private final CoreValidator coreValidator;
  private final PasswordEncoder passwordEncoder;

  public List<User> updateExistingUsers(List<User> users) {
    coreValidator.validateUsers(users);
    User currentUser = modificationUtils.takePrimaryUser();
    com.example.demo.model.User.Role currentRole = currentUser.getRole();

    List<User> usersToSave = new ArrayList<>();

    for (User user : users) {

      User existingUser = repository.findByEmail(user.getEmail()).orElse(null);
      if (existingUser != null) {
        // Preserve existing password — ignore any password in payload
        user.setPassword(existingUser.getPassword());
        user.setId(existingUser.getId());
      } else {
        // New user: default password, ignore any password in payload
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setCreatedAt(Instant.now());
        user.setCreatedBy(currentUser);
      }

      if (existingUser != null
          && user.getRole() != existingUser.getRole()
          && !currentRole.canAssign(user.getRole())) {
        throw new ForbiddenException(
            "You cannot assign role "
                + user.getRole()
                + " which is higher than your own role "
                + currentRole);
      }

      modificationUtils.createOrUpdateModel(user, existingUser, user.getId(), currentUser);
      usersToSave.add(user);
    }
    return repository.saveAll(usersToSave);
  }

  public User getById(String userId) {
    return repository
        .findById(userId)
        .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
  }

  public Optional<User> getByEmail(String email) {
    return repository.findByEmail(email);
  }

  public List<User> getUsers(PageFromOne page, BoundedPageSize pageSize, UserCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return repository.findAll(toSpecification(criteria), pageable).getContent();
  }

  public void deleteById(String userId) {
    User user = getById(userId);
    repository.delete(user);
  }

  public User getByEmailOrThrow(String email) {
    return getByEmail(email)
        .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
  }

  private Specification<User> toSpecification(UserCriteria criteria) {
    return Specification.<User>where(
            (root, query, cb) -> {
              if (criteria.getCompanyId() == null) return cb.conjunction();
              var join = root.join("companies");
              return cb.equal(join.get("id"), criteria.getCompanyId());
            })
        .and(containsIgnoreCase(criteria.getFirstName(), "firstName"))
        .and(containsIgnoreCase(criteria.getLastName(), "lastName"))
        .and(containsIgnoreCase(criteria.getEmail(), "email"))
        .and(equal(criteria.getRole(), "role"))
        .and(
            (root, query, cb) -> {
              if (Boolean.TRUE.equals(criteria.getWithoutLeaveConfig())) {
                return cb.isNull(root.get("employeeLeaveConfig"));
              }
              return cb.conjunction();
            });
  }
}
