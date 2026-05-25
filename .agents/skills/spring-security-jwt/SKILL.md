---
name: spring-security-jwt
description: Configure Spring Security with JWT authentication, role-based authorization, and self-matching in a Spring Boot project. Use when setting up authentication, JWT tokens, or securing API endpoints.
---

# Spring Security + JWT — Spring Boot

Set up a complete authentication and authorization system with Spring Security, JWT tokens, BCrypt passwords, role-based access control, and self-matching for user-owned resources.

## Architecture

```
Client → AuthController → AuthService → AuthenticationManager → CustomUserDetailsService → DB
    ↓                                                        →
    JWT token ← JwtUtils.generateJwtToken()                  ← User implements UserDetails
    ↓
[Request with Bearer token] → JwtAuthenticationFilter → SecurityContextHolder → Controller
```

## 1. Dépendances (`build.gradle.kts`)

```kotlin
implementation("org.springframework.boot:spring-boot-starter-security")
implementation("org.springframework.boot:spring-boot-starter-validation")
implementation("io.jsonwebtoken:jjwt-api:0.11.5")
runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
testImplementation("org.springframework.boot:spring-boot-starter-security-test")
```

## 2. Propriétés (`application.properties`)

```properties
jwt.secret.key=your-secret-key-at-least-256-bits-long-for-hs256-algorithm
jwt.expiration.time=86400000
security.bcrypt.strength=10
```

## 3. Migration SQL

```sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS password VARCHAR(255);
UPDATE users SET password = '' WHERE password IS NULL;
ALTER TABLE users ALTER COLUMN password SET NOT NULL;
```

## 4. Entity — `User implements UserDetails`

L'entité `User` doit implémenter `UserDetails` de Spring Security :

```java
package {basePackage}.model;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "\"users\"")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class User extends CreatAndUpdateEntity implements UserDetails {

  @Id
  private String id;

  private String firstName;
  private String lastName;
  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

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
    USER
  }
}
```

## 5. `CustomUserDetailsService.java`

Pont entre la DB et Spring Security :

```java
package {basePackage}.endpoint.rest.security.service;

import {basePackage}.model.User;
import {basePackage}.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
  }
}
```

## 6. `JwtUtils.java`

Création et validation des tokens JWT :

```java
package {basePackage}.endpoint.rest.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtils {

  @Value("${jwt.secret.key}")
  private String jwtSecret;

  @Value("${jwt.expiration.time}")
  private int jwtExpirationMs;

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  public String generateJwtToken(Authentication authentication) {
    UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
    return Jwts.builder()
        .setSubject(userPrincipal.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUserEmailFromJwtToken(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parse(authToken);
      return true;
    } catch (Exception e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    }
    return false;
  }
}
```

## 7. `JwtAuthenticationFilter.java`

Filtre qui intercepte chaque requête et authentifie via JWT :

```java
package {basePackage}.endpoint.rest.security.jwt;

import {basePackage}.endpoint.rest.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;
  private final CustomUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String jwt = parseJwt(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String email = jwtUtils.getUserEmailFromJwtToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }
    return null;
  }
}
```

## 8. `JwtAuthenticationEntryPoint.java`

Gestion des erreurs 401 (non authentifié) :

```java
package {basePackage}.endpoint.rest.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    log.error("Unauthorized error: {}", authException.getMessage());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    var exceptionResponse = new {basePackage}.model.exception.ExceptionResponse();
    exceptionResponse.setMessage("Not authorized");
    exceptionResponse.setType(authException.getMessage());

    new ObjectMapper().writeValue(response.getOutputStream(), exceptionResponse);
  }
}
```

## 9. `SecurityConfiguration.java`

Configuration principale de la sécurité :

```java
package {basePackage}.endpoint.rest.security;

import static org.springframework.http.HttpMethod.*;

import {basePackage}.endpoint.rest.security.jwt.JwtAuthenticationEntryPoint;
import {basePackage}.endpoint.rest.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationEntryPoint unauthorizedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> {})
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth
                    .requestMatchers("/auth/login", "/auth/register", "/ping")
                    .permitAll()
                    .requestMatchers("/auth/whoami")
                    .authenticated()

                    // Domain-specific rules
                    .requestMatchers(GET, "/{entity}", "/{entity}/*")
                    .authenticated()
                    .requestMatchers(PUT, "/{entity}")
                    .hasRole("ADMIN")
                    .requestMatchers(DELETE, "/{entity}/*")
                    .hasRole("ADMIN")

                    .anyRequest()
                    .authenticated());

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
```

## 10. `AuthService.java`

Logique métier d'authentification :

```java
package {basePackage}.endpoint.rest.security.service;

import {basePackage}.endpoint.rest.security.jwt.JwtUtils;
import {basePackage}.model.User;
import {basePackage}.model.exception.BadRequestException;
import {basePackage}.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthResponse authenticateUser(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    User user = userRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new BadRequestException("User not found"));

    return new AuthResponse()
        .email(user.getEmail())
        .id(user.getId())
        .type("Bearer")
        .role(user.getRole().name())
        .token(jwt);
  }

  public AuthResponse registerUser(User user, String noEncodedPassword) {
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new BadRequestException("Email is already in use");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    User savedUser = userRepository.save(user);

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            savedUser.getEmail(), noEncodedPassword));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    return new AuthResponse()
        .email(savedUser.getEmail())
        .id(savedUser.getId())
        .type("Bearer")
        .role(savedUser.getRole().name())
        .token(jwt);
  }

  public AuthResponse whoami(String token) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new BadRequestException("User not authenticated");
    }
    User user = userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new BadRequestException("User not found"));
    return new AuthResponse()
        .email(user.getEmail())
        .id(user.getId())
        .type("Bearer")
        .role(user.getRole().name())
        .token(token);
  }
}
```

## 11. `AuthController.java`

```java
package {basePackage}.endpoint.rest.security.controller;

import {basePackage}.endpoint.rest.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public AuthResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return authService.authenticateUser(loginRequest);
  }

  @PostMapping("/register")
  public AuthResponse registerUser(@Valid @RequestBody CrupdateUser user) {
    return authService.registerUser(/* ... */);
  }

  @GetMapping("/whoami")
  public AuthResponse whoami(HttpServletRequest request) {
    String token = "";
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);
    }
    return authService.whoami(token);
  }
}
```

## 12. `SelfMatcher.java` (optionnel)

Permet à un utilisateur d'accéder/modifier ses propres ressources :

```java
package {basePackage}.endpoint.rest.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;

@AllArgsConstructor
public class SelfMatcher implements RequestMatcher {

  private final HttpMethod method;
  private final String antPattern;

  private static final Pattern SELFABLE_URI_PATTERN =
      Pattern.compile("/[^/]+/(?<id>[^/]+)(/.*)?");

  @Override
  public boolean matches(HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }
    String userIdFromToken = ((User) authentication.getPrincipal()).getId();
    String userIdFromURL = getSelfId(request);
    return Objects.equals(userIdFromURL, userIdFromToken);
  }

  private String getSelfId(HttpServletRequest request) {
    Matcher uriMatcher = SELFABLE_URI_PATTERN.matcher(request.getRequestURI());
    return uriMatcher.find() ? uriMatcher.group("id") : null;
  }
}
```

## 13. Ordre d'initialisation

1. Base de données (migration SQL → ajouter `password`)
2. Modèle (`User` implémente `UserDetails`)
3. Dépendances Gradle
4. `CustomUserDetailsService`
5. `JwtUtils`
6. `JwtAuthenticationFilter`
7. `JwtAuthenticationEntryPoint`
8. `SecurityConfiguration`
9. `AuthService` + `AuthController`

## Vérification

1. Lancer `./gradlew build` — doit compiler sans erreur
2. `POST /auth/register` — créer un utilisateur
3. `POST /auth/login` — obtenir un token JWT
4. Appel sécurisé avec `Authorization: Bearer <token>` — doit fonctionner
5. Appel sans token — doit retourner 401
6. Tester les rôles (ex: ADMIN peut DELETE, USER ne peut pas)
