package com.example.demo.integration.conf;

import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.Role;
import com.example.demo.client.model.Sex;
import com.example.demo.client.model.User;
import java.time.Instant;

final class TestUserFixtures {

  private TestUserFixtures() {}

  static User admin1() {
    User user = new User();
    user.setId(TestUtils.ADMIN_ID);
    user.setRole(Role.ADMIN);
    user.setFirstName("Admin");
    user.setLastName("System");
    user.setSex(Sex.M);
    user.setEmail(TestUtils.ADMIN_EMAIL);
    user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    user.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    return user;
  }

  static User warehouseWorker1() {
    User user = new User();
    user.setId(TestUtils.WAREHOUSE_ID);
    user.setRole(Role.WAREHOUSE_WORKER);
    user.setFirstName("Warehouse");
    user.setLastName("Worker");
    user.setSex(Sex.M);
    user.setEmail(TestUtils.WAREHOUSE_EMAIL);
    user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    user.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    return user;
  }

  static User employee1() {
    User user = new User();
    user.setId(TestUtils.EMPLOYEE_ID);
    user.setRole(Role.EMPLOYEE);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setSex(Sex.M);
    user.setEmail(TestUtils.EMPLOYEE_EMAIL);
    user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    user.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    return user;
  }

  static User administration1() {
    User user = new User();
    user.setId(TestUtils.ADMINISTRATION_ID);
    user.setRole(Role.ADMINISTRATION);
    user.setFirstName("Admin");
    user.setLastName("Staff");
    user.setSex(Sex.F);
    user.setEmail(TestUtils.ADMINISTRATION_EMAIL);
    user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    user.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    return user;
  }

  static User user1() {
    User user = new User();
    user.setId(TestUtils.USER1_ID);
    user.setRole(Role.EMPLOYEE);
    user.setFirstName("Alice");
    user.setLastName("Martin");
    user.setSex(Sex.F);
    user.setEmail(TestUtils.USER1_EMAIL);
    user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    user.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    return user;
  }

  static User user2() {
    User user = new User();
    user.setId(TestUtils.USER2_ID);
    user.setRole(Role.EMPLOYEE);
    user.setFirstName("Bob");
    user.setLastName("Bernard");
    user.setSex(Sex.M);
    user.setEmail(TestUtils.USER2_EMAIL);
    user.setCreatedAt(Instant.parse(""));
    user.setUpdatedAt(Instant.parse(""));
    return user;
  }

  static CrupdateUser someCreatableUser() {
    CrupdateUser user = new CrupdateUser();
    user.setFirstName("Test");
    user.setLastName("User");
    user.setEmail(TestUtils.RANDOM_EMAIL);
    user.setPassword(TestUtils.PASSWORD);
    user.setSex(Sex.M);
    user.setRole(Role.EMPLOYEE);
    return user;
  }

  static CrupdateUser userToCrupdateUser(User user) {
    CrupdateUser crupdateUser = new CrupdateUser();
    crupdateUser.setFirstName(user.getFirstName());
    crupdateUser.setLastName(user.getLastName());
    crupdateUser.setEmail(user.getEmail());
    crupdateUser.setPassword(TestUtils.PASSWORD);
    crupdateUser.setSex(user.getSex());
    crupdateUser.setRole(user.getRole());
    return crupdateUser;
  }
}
