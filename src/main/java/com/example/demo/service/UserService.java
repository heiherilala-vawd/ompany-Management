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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository repository;
  private final ModificationUtils modificationUtils;
  private final CoreValidator coreValidator;

  public List<User> updateExistingUsers(List<User> users) {
    coreValidator.validateUsers(users);

    List<User> usersToSave = new ArrayList<>();

    for (User user : users) {

      User existingUser = repository.findByEmail(user.getEmail()).orElse(null);
      if (existingUser != null) {
        user.setPassword(existingUser.getPassword());
        user.setId(existingUser.getId());
      } else {
        throw new ForbiddenException("That endpoint can not create.");
      }
      modificationUtils.createOrUpdateModel(
          user, existingUser, user.getId(), modificationUtils.takePrimaryUser());
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
    return Specification.<User>where(equal(criteria.getCompanyId(), "company", "id"))
        .and(containsIgnoreCase(criteria.getFirstName(), "firstName"))
        .and(containsIgnoreCase(criteria.getLastName(), "lastName"))
        .and(containsIgnoreCase(criteria.getEmail(), "email"))
        .and(equal(criteria.getRole(), "role"));
  }
}
