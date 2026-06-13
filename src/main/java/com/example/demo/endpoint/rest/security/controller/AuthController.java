package com.example.demo.endpoint.rest.security.controller;

import com.example.demo.client.model.AuthResponse;
import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.LoginRequest;
import com.example.demo.endpoint.rest.mapper.UserMapper;
import com.example.demo.endpoint.rest.security.dto.PasswordChangeRequest;
import com.example.demo.endpoint.rest.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserMapper userMapper;

  @PostMapping("/login")
  public AuthResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return authService.authenticateUser(loginRequest);
  }

  @PostMapping("/register")
  public AuthResponse registerUser(@Valid @RequestBody CrupdateUser user) {
    String companyId =
        user.getCompanyIds() != null && !user.getCompanyIds().isEmpty()
            ? user.getCompanyIds().get(0)
            : null;
    com.example.demo.model.User modelUser = userMapper.toDomain(user, companyId);
    String noEncodedPassword = user.getPassword();

    return authService.registerUser(modelUser, noEncodedPassword);
  }

  @GetMapping("/whoami")
  public AuthResponse whoami(HttpServletRequest request) {

    String authHeader = request.getHeader("Authorization");
    String token = "";

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);
    }
    return authService.whoami(token);
  }

  @PutMapping("/password")
  public void changePassword(@Valid @RequestBody PasswordChangeRequest request) {
    authService.changePassword(request.getOldPassword(), request.getNewPassword());
  }
}
