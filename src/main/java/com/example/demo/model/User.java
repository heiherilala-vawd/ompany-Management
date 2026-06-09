package com.example.demo.model;

import com.example.demo.model.core.Department;
import com.example.demo.model.hr.EmployeeLeaveConfig;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "\"users\"")
@Getter
@Setter
@ToString(exclude = {"assignedJobs", "employeeLeaveConfig"})
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class User extends CreatAndUpdateEntity implements Serializable, UserDetails {
  @Id private String id;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private Role role;

  @NotBlank(message = "First name is mandatory")
  private String firstName;

  @NotBlank(message = "Last name is mandatory")
  private String lastName;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private Sex sex;

  @Email(message = "Email should be valid")
  @NotBlank(message = "Email is mandatory")
  private String email;

  @NotBlank(message = "Password is mandatory")
  private String password;

  @ManyToMany
  @JoinTable(
      name = "users_companies",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "company_id"))
  private Set<Company> companies = new HashSet<>();

  @ManyToMany(mappedBy = "responsibleUsers", fetch = FetchType.LAZY)
  private List<Job> assignedJobs = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_leave_config_id")
  private EmployeeLeaveConfig employeeLeaveConfig;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manager_id")
  private User manager;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  private Department department;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User user)) return false;
    return Objects.equals(getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  public enum Role {
    ADMIN,
    WAREHOUSE_WORKER,
    EMPLOYEE,
    ADMINISTRATION
  }

  public enum Sex {
    M,
    F
  }
}
