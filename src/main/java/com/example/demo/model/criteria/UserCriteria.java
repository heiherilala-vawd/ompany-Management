package com.example.demo.model.criteria;

import com.example.demo.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCriteria {
  private String firstName;
  private String lastName;
  private String email;
  private User.Role role;
}
